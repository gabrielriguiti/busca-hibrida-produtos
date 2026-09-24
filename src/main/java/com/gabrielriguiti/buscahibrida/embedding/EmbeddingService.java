package com.gabrielriguiti.buscahibrida.embedding;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Gera embeddings de 384 dimensoes com o e5-small rodando em processo na JVM (ONNX
 * Runtime via DJL), sem depender de um servico Python externo.
 */
@Service
public class EmbeddingService {

    private static final String MODEL_URL =
            "https://huggingface.co/intfloat/e5-small/resolve/main/model.onnx";
    private static final String TOKENIZER_HUB_ID = "intfloat/e5-small";

    @Value("${embedding.cache-dir:.cache/e5-small}")
    private String cacheDir;

    private HuggingFaceTokenizer tokenizer;
    private Model model;
    private Predictor<String, float[]> predictor;

    @PostConstruct
    public void init() throws IOException, MalformedModelException {
        Path modelPath = downloadModelIfMissing();
        tokenizer = HuggingFaceTokenizer.newInstance(TOKENIZER_HUB_ID,
                Map.of("maxLength", "512", "truncation", "true"));
        model = Model.newInstance("e5-small", "OnnxRuntime");
        model.load(modelPath);
        predictor = model.newPredictor(new E5Translator());
    }

    public synchronized float[] embed(String text) {
        try {
            return predictor.predict(text);
        } catch (TranslateException e) {
            throw new RuntimeException("Falha ao gerar embedding", e);
        }
    }

    @PreDestroy
    public void close() {
        if (predictor != null) {
            predictor.close();
        }
        if (model != null) {
            model.close();
        }
    }

    private Path downloadModelIfMissing() throws IOException {
        Path dir = Path.of(cacheDir);
        Files.createDirectories(dir);
        Path modelFile = dir.resolve("model.onnx");
        if (Files.exists(modelFile) && Files.size(modelFile) > 0) {
            return modelFile;
        }
        Path tempFile = dir.resolve("model.onnx.tmp");
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(MODEL_URL)).GET().build();
            HttpResponse<Path> response =
                    client.send(request, HttpResponse.BodyHandlers.ofFile(tempFile));
            if (response.statusCode() != 200) {
                throw new IOException("Download do modelo e5-small falhou: HTTP "
                        + response.statusCode());
            }
            Files.move(tempFile, modelFile);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Download do modelo e5-small interrompido", e);
        } finally {
            Files.deleteIfExists(tempFile);
        }
        return modelFile;
    }

    /**
     * Tokeniza com o prefixo "query: " exigido pelo e5, roda o grafo ONNX e faz mean
     * pooling + normalizacao L2 manualmente (o engine OnnxRuntime do DJL nao suporta
     * as operacoes de NDArray usadas para isso, so a execucao do grafo em si).
     */
    private final class E5Translator implements Translator<String, float[]> {

        @Override
        public NDList processInput(TranslatorContext ctx, String input) {
            Encoding encoding = tokenizer.encode("query: " + input);
            NDManager manager = ctx.getNDManager();
            Shape shape = new Shape(1, encoding.getIds().length);
            NDArray inputIds = manager.create(encoding.getIds(), shape);
            inputIds.setName("input_ids");
            NDArray attentionMask = manager.create(encoding.getAttentionMask(), shape);
            attentionMask.setName("attention_mask");
            NDArray tokenTypeIds = manager.create(encoding.getTypeIds(), shape);
            tokenTypeIds.setName("token_type_ids");
            ctx.setAttachment("attentionMask", encoding.getAttentionMask());
            return new NDList(inputIds, attentionMask, tokenTypeIds);
        }

        @Override
        public float[] processOutput(TranslatorContext ctx, NDList list) {
            NDArray lastHiddenState = list.get(0);
            long[] shape = lastHiddenState.getShape().getShape();
            int seqLen = (int) shape[1];
            int hidden = (int) shape[2];
            float[] flat = lastHiddenState.toFloatArray();
            long[] attentionMask = (long[]) ctx.getAttachment("attentionMask");

            float[] pooled = new float[hidden];
            float maskSum = 0f;
            for (int t = 0; t < seqLen; t++) {
                float m = attentionMask[t];
                maskSum += m;
                int base = t * hidden;
                for (int h = 0; h < hidden; h++) {
                    pooled[h] += flat[base + h] * m;
                }
            }
            if (maskSum <= 0f) {
                maskSum = 1f;
            }
            double normSq = 0;
            for (int h = 0; h < hidden; h++) {
                pooled[h] /= maskSum;
                normSq += (double) pooled[h] * pooled[h];
            }
            double norm = Math.sqrt(normSq);
            if (norm > 0) {
                for (int h = 0; h < hidden; h++) {
                    pooled[h] = (float) (pooled[h] / norm);
                }
            }
            return pooled;
        }

        @Override
        public Batchifier getBatchifier() {
            return null;
        }
    }
}
