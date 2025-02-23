package com.hibuz.ai.util;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class BikeJsonReader {

    private final Resource resource;

    BikeJsonReader(@Value("classpath:/docs/bikes.json") Resource resource) {
        this.resource = resource;
    }

    public List<Document> loadJsonAsDocuments() {
        JsonReader jsonReader = new JsonReader(this.resource, "name", "price", "shortDescription", "description");
        return jsonReader.get();
	}
}
