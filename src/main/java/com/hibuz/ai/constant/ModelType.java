package com.hibuz.ai.constant;

import org.springframework.ai.chat.model.ChatModel;

public enum ModelType {
    OLLAMA, OPENAI;

    public static ModelType of(String name) {
        for (ModelType type : ModelType.values()) {
            if (type.toString().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return OLLAMA;
    }

    public static ModelType valueOf(ChatModel chatModel) {
        for (ModelType type : ModelType.values()) {
            if (chatModel.getClass().getSimpleName().toUpperCase().startsWith(type.toString())) {
                return type;
            }
        }

        throw new RuntimeException("Not found ModelType for " + chatModel.getClass().getSimpleName());
    }
}
