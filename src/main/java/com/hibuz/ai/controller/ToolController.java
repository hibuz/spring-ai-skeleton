package com.hibuz.ai.controller;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.ChatClientService;
import com.hibuz.ai.util.WeatherTools;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("tool")
@RestController
@Slf4j
@Tag(name = "step4. 어플리케이션 도구와 연결(정보조회, 기능수행)", externalDocs = @ExternalDocumentation(description = "Tool Calling API",
    url = "https://docs.spring.io/spring-ai/reference/concepts.html#concept-fc"))
public class ToolController {

    private final ChatClientService service;
    private final ToolCallback[] toolCallbacks;

    public ToolController(ChatClientService service, AsyncMcpToolCallbackProvider toolCallbackProvider) {
        this.service = service;
        this.toolCallbacks = toolCallbackProvider.getToolCallbacks();
        if (toolCallbacks != null) {
            log.info("toolNames={}", Arrays.asList(toolCallbacks).stream().map(callback -> callback.getToolDefinition().name()).toList());
        }
    }

    @GetMapping("4-1/object")
    public ChatResponse object(@Schema(description = "Information Retrieval & Taking Actions(Can you set an alarm 10 minutes from now?)",
                                defaultValue = "What day is today?") @RequestParam String message) {
        log.info("chat> {}", message);
        /**
         * ToolCallback[] dateTimeTools = ToolCallbacks.from(new DateTimeTools());
         * service.builder().defaultTools(new DateTimeTools() or dateTimeTools)
         * .build().prompt(message).call().chatResponse();
         */

         return service.getClient().prompt(message).tools(new DateTimeTools())
            .call().chatResponse();
    }

    @GetMapping("4-2/bean")
    public ChatResponse bean(@RequestParam(defaultValue = "What's the weather like in Seoul?") String message) {
        log.info("chat> {}", message);
        
        return service.getClient().prompt(message).toolNames("currentWeather")
        .call().chatResponse();
    }

    @GetMapping("4-3/function")
    public ChatResponse function(@RequestParam(defaultValue = "What's the weather like in Seoul in fahrenheit?") String message) {
        log.info("chat> {}", message);

        ToolCallback toolCallback = FunctionToolCallback
            .builder("currentWeather", new WeatherTools())
            .inputType(WeatherTools.Request.class)
            .build();

        return service.getClient().prompt(message).toolCallbacks(toolCallback).call().chatResponse();
    }

    @GetMapping("4-4/method")
    public ChatResponse method(@RequestParam(defaultValue = "What's the weather like in Seoul, Paris and San Francisco? in Celsius") String message) {
        log.info("chat> {}", message);

        Method method = Arrays.stream(WeatherTools.class.getMethods()).filter(m -> "getWeatherStatic".equals(m.getName())).findFirst().get();

        ToolCallback toolCallback = MethodToolCallback.builder()
            .toolDefinition(ToolDefinition.builder()
                    .inputSchema(JsonSchemaGenerator.generateForMethodInput(method))
                    .description("Get the weather in location")
                    .build())
            .toolMethod(method)
            .build();

        return service.getClient().prompt(message).toolCallbacks(toolCallback).call().chatResponse();
    }

    @SuppressWarnings("null")
    @GetMapping("4-5/mcp")
    public Generation mcp(@RequestParam(defaultValue = "qwen2.5-coder:1.5b") String modelName,
                            @RequestParam(defaultValue = "spring-ai github 프로젝트의 UserMessageTests.java 를 찾아서 비슷하게 채팅 테스트코드 작성해줘") String message,
                            @RequestParam(defaultValue = "false") boolean useMcpClient) {
        log.info("chat> {}", message);

        if (useMcpClient) {
            ChatOptions chatOptions = ChatOptions.builder().model(modelName).temperature(0.7).build();
            return service.getClient().prompt(message).options(chatOptions).toolCallbacks(toolCallbacks).call().chatResponse().getResult();
        } else {
            return service.getClient().prompt(message).call().chatResponse().getResult();
        }
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
