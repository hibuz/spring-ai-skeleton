package com.hibuz.ai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgVectorStoreBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class VectorStoreConfig {

    //@Profile({ "dev", "prod" })
    //@Bean
    public VectorStore pgVectorStore(PgVectorStoreBuilder builder) {
        return builder.build();
    }

    //@Profile("!dev & !prod")
    //@Bean
    public VectorStore simplVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
