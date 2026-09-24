package com.gabrielriguiti.buscahibrida.indexing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Reindexa o catalogo assim que o app sobe, depois da migration Flyway - sem isso, um
 * `docker compose up` de clone limpo deixa embedding/texto_busca NULL e os modos
 * vetorial/hibrido voltam vazios (ver release-packaging spec: "sem nenhum outro passo de
 * setup manual necessario"). Seguro rodar em todo restart porque reindex() e idempotente
 * (sempre UPDATE por id). */
@Component
public class StartupIndexer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupIndexer.class);

    private final CatalogIndexer catalogIndexer;

    public StartupIndexer(CatalogIndexer catalogIndexer) {
        this.catalogIndexer = catalogIndexer;
    }

    @Override
    public void run(ApplicationArguments args) {
        int indexed = catalogIndexer.reindex();
        log.info("Catalogo reindexado no startup: {} produtos", indexed);
    }
}
