package com.hibuz.ai.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import com.hibuz.ai.constant.ModelType;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatClientService {
    private final Map<ModelType, ChatModel> modelMap;

    private ModelType modelType;

    private ChatOptions chatOptions;

    @Getter
    private ChatClient client;

    ChatClientService(List<ChatModel> modelList) {
        ChatModel chatModel = modelList.get(0);
        this.modelType = ModelType.valueOf(chatModel);
        this.chatOptions = chatModel.getDefaultOptions();
        this.client = ChatClient.builder(chatModel).build();

        this.modelMap = modelList.stream().collect(Collectors.toMap(ModelType::valueOf, m2 -> m2));
    }

    public Builder builder() {
        return ChatClient.builder(modelMap.get(modelType)).defaultOptions(chatOptions);
    }

    public String chat(String userMessage) {
        return chat(new Prompt(userMessage));
    }

    public String chat(Prompt prompt) {
        return client.prompt(prompt).call().content();
    }

    public void changeChatModel(String modelTypeName, String modelName) {
        ModelType oldType = this.modelType;
        ModelType newType = ModelType.of(modelTypeName);

        this.modelType = newType;
        this.chatOptions = ChatOptions.builder().model(modelName).temperature(chatOptions.getTemperature()).build();
        this.client = builder().build();

        log.info("ChatClient changed! {} -> {}({})", oldType, newType, chatOptions.getModel());
    }

    public Map<String, Object> getModelInfo() {
        return Map.of(modelType.toString(), chatOptions);
    }
}
