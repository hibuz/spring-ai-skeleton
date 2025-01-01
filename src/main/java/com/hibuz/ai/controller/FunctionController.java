package com.hibuz.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.WeatherService;
import com.hibuz.ai.service.WeatherService.Unit;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("func")
@RestController
@Slf4j
public class FunctionController {

    private static final String DEFAULT_VALUE = "What's the weather like in San Francisco, Seoul, and Paris?";

    private final ChatModel chatModel;

    public FunctionController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("bean")
    public ChatResponse bean(@RequestParam(value = "message", defaultValue = DEFAULT_VALUE) String message) {
        log.info("chat> {}", message);

        return ChatClient.create(chatModel).prompt(message)
            .functions("currentWeather")
            .call().chatResponse();
    }

    @GetMapping("callback/function-invoke")
    public ChatResponse func(@RequestParam(value = "message", defaultValue = DEFAULT_VALUE) String message) {
        log.info("chat> {}", message);
        FunctionCallback callback = FunctionCallback.builder()
            .function("currentWeather", (WeatherService.Request request) ->
                 new WeatherService.Response(20.0, Unit.FAHRENHEIT))
            .inputType(WeatherService.Request.class)
            .build();

        return ChatClient.create(chatModel).prompt(message)
            .functions(callback)
            .call().chatResponse();
    }

    @GetMapping("callback/static-method-invoke")
    public ChatResponse method(@RequestParam(value = "message", defaultValue = DEFAULT_VALUE + " Use Celsius.") String message) {
        log.info("chat> {}", message);
        FunctionCallback callback = FunctionCallback.builder()
            .method("getWeatherStatic", String.class, Unit.class)
            .targetClass(WeatherService.class)
            .build();

        return ChatClient.create(chatModel).prompt(message)
            .functions(callback)
            .call().chatResponse();
    }
}
