package com.hibuz.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.WeatherService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class FunctionController {

    private final ChatModel chatModel;

    @Autowired
    public FunctionController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("func/bean")
    public ChatResponse bean(@RequestParam(value = "message", defaultValue = "What's the weather like in San Francisco, Seoul, and Paris?") String message) {
        log.info("chat> {}", message);
        return ChatClient.create(chatModel).prompt(message)
            .functions("currentWeather")
            .call().chatResponse();
    }

    @GetMapping("func/invoke")
    public ChatResponse client(@RequestParam(value = "message", defaultValue = "What's the weather like in San Francisco, Seoul, and Paris?") String message) {
        log.info("chat> {}", message);
        return ChatClient.create(chatModel).prompt(message)
            .functions(FunctionCallback.builder()
            .description("Get the weather in location")
            .function("currentWeather", (WeatherService.Request request) -> new WeatherService.Response(20.0, WeatherService.TemperatureUnit.FAHRENHEIT))
            .inputType(WeatherService.Request.class)
            .build())
            .call().chatResponse();
    }
}
