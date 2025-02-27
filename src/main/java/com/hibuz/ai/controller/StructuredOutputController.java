package com.hibuz.ai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hibuz.ai.service.ChatClientService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("structured")
@RestController
@Slf4j
@Tag(name = "step3. 구조화된 출력 구성", externalDocs = @ExternalDocumentation(description = "Structured Output API",
	url = "https://docs.spring.io/spring-ai/reference/concepts.html#_structured_output"))
public class StructuredOutputController {

    private final ChatClientService service;

    public StructuredOutputController(ChatClientService service) {
        this.service = service;
    }

	@GetMapping("3-1/generic/map")
	public Map<String, Object> map(@RequestParam(defaultValue = "an array of numbers from 1 to 9 under they key name 'numbers'") String subject) {
		String text = "Provide me a List of {subject}";
		log.info("chat> {}", text.replace("{subject}", subject));

		return service.getClient().prompt()
				.user(u -> u.text(text).param("subject", subject))
        		.call()
        		.entity(new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	@GetMapping("3-2/generic/bean")
	public List<ActorsFilms> generic(@RequestParam(defaultValue = "Tom Hanks and Bill Murray") String actor) {
		String userMessage = "Generate the filmography of 5 movies for {actor}.";
		log.info("chat> {}", userMessage.replace("{actor}", actor));
		PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("actor", actor));
		Prompt prompt = promptTemplate.create();

		return service.getClient().prompt(prompt)
				.call()
				.entity(new ParameterizedTypeReference<List<ActorsFilms>>() {});
	}

	@GetMapping("3-3/converter/list")
	public List<String> list(@RequestParam(defaultValue = "ice cream flavors") String subject) {
		String text = "List five {subject}";
		log.info("chat> {}", text.replace("{subject}", subject));

		return service.getClient().prompt()
                .user(u -> u.text(text).param("subject", subject))
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));
	}

	@GetMapping("3-4/converter/bean")
	public ActorsFilms convertor(@Schema(description = "template: Generate the filmography for the actor {actor}.\n<br/>{format}",
                                defaultValue = "Jeff Bridges") @RequestParam String actor) {
		var outputParser = new BeanOutputConverter<>(ActorsFilms.class);

		String format = outputParser.getFormat();
		log.info("format: {}", format);
		String userMessage = """
				Generate the filmography for the actor {actor}.
				{format}
				""";
		PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("actor", actor, "format", format));
		Prompt prompt = promptTemplate.create();

		return outputParser.convert(service.chat(prompt));
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class ActorsFilms {
		private String actor;
		private List<String> movies;
	}
	
}
