package com.hibuz.ai.controller;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.ChatClientService;
import com.hibuz.ai.util.WeatherTools;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("tool")
@RestController
@Slf4j
@Tag(name = "step4", description = "Tool Calling API")
public class ToolController {

    private final ChatClientService service;

    public ToolController(ChatClientService service) {
        this.service = service;
    }

    @GetMapping("object")
    @Operation(summary = "Information Retrieval & Taking Actions(Can you set an alarm 10 minutes from now?)")
    public ChatResponse object(@RequestParam(defaultValue = "What day is today?") String message) {
        log.info("chat> {}", message);
        /**
         * ToolCallback[] dateTimeTools = ToolCallbacks.from(new DateTimeTools());
         * service.builder().defaultTools(new DateTimeTools() or dateTimeTools)
         * .build().prompt(message).call().chatResponse();
         */

         return service.getClient().prompt(message).tools(new DateTimeTools())
            .call().chatResponse();
    }

    @GetMapping("bean")
    public ChatResponse bean(@RequestParam(defaultValue = "What's the weather like in Seoul?") String message) {
        log.info("chat> {}", message);
        
        return service.getClient().prompt(message).tools("currentWeather")
        .call().chatResponse();
    }

    @GetMapping("function")
    public ChatResponse function(@RequestParam(defaultValue = "What's the weather like in Seoul?") String message) {
        log.info("chat> {}", message);

        ToolCallback toolCallback = FunctionToolCallback
            .builder("functionTool", new WeatherTools())
            .inputType(WeatherTools.Request.class)
            .description("Get the weather in location")
            .build();

        return service.getClient().prompt(message).tools(toolCallback).call().chatResponse();
    }

    @GetMapping("method")
    public ChatResponse method(@RequestParam(defaultValue = "What's the weather like in Seoul, Paris and San Francisco?") String message) {
        log.info("chat> {}", message);

        Method method = ReflectionUtils.findMethod(WeatherTools.class, "getWeatherStatic");

        ToolCallback toolCallback = MethodToolCallback.builder()
            .toolDefinition(ToolDefinition.builder(method)
                    .description("Get the current date and time in the user's timezone")
                    .build())
            .toolMethod(method)
            .build();

        return service.getClient().prompt(message).tools(toolCallback).call().chatResponse();
    }

    class DateTimeTools {

        @Tool(description = "Get the current date and time in the user's timezone")
        String getCurrentDateTime() {
            return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
        }

        @Tool(description = "Set a user alarm for the given time")
        void setAlarm(@ToolParam(description = "Time in ISO-8601 format") String time) {
            LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
            log.info("Alarm set for {}", alarmTime);
        }
    }
}
