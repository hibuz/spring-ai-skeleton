package com.hibuz.ai.util;

import java.util.function.Function;

import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration(proxyBeanMethods = false)
public class WeatherTools implements Function<WeatherTools.Request, WeatherTools.Response> {
	
	@Bean("currentWeather")
	@Description("Get the weather in location")
	Function<Request, Response> currentWeather() {
		return req -> new Response(11.0, Unit.C);
	}	

    public enum Unit { C, F }

	public record Request(@ToolParam(description = "The name of a city or a country") String location, Unit unit) {}

	public record Response(double temp, Unit unit) {}

    @Override
	public Response apply(Request request) {
		return new Response(22.0, Unit.C);
	}

	public static String getWeatherStatic(String city, Unit unit) {

		double temperature = 0;
		if (city.contains("Seoul")) {
			temperature = 10;
		}
		else if (city.contains("Paris")) {
			temperature = 20;
		}
		else if (city.contains("San Francisco")) {
			temperature = 30;
		}

		return "temperature: " + temperature + " unit: " + unit;
	}
}
