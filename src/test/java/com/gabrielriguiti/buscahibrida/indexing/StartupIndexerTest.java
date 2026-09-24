package com.gabrielriguiti.buscahibrida.indexing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StartupIndexerTest {

    private final CatalogIndexer catalogIndexer = mock(CatalogIndexer.class);
    private final StartupIndexer startupIndexer = new StartupIndexer(catalogIndexer);

    @Test
    void reindexesCatalogOnRun() {
        when(catalogIndexer.reindex()).thenReturn(22);

        startupIndexer.run(mock(ApplicationArguments.class));

        verify(catalogIndexer, times(1)).reindex();
    }
}
