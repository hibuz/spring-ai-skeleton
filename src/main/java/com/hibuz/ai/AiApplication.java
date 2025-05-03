package com.hibuz.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class AiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiApplication.class, args);
	}
	@Bean
    public ApplicationRunner run(ChatModel model) {
        return args -> log.info("Default chatModel: {}", model.getDefaultOptions().getModel());
	}
}
