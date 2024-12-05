package com.hibuz.ai.service;

import java.util.function.Function;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

@Component("currentWeather")
@Description("Get the weather in location")
public class WeatherService implements Function<WeatherService.Request, WeatherService.Response> {
	
    public enum Unit { CELSIUS, FAHRENHEIT }

	public record Request(String location, Unit unit) {}

	public record Response(double temp, Unit unit) {}

    @Override
	public Response apply(Request request) {
		return new Response(30.0, Unit.CELSIUS);
	}

	@Description("Get weather information for a city")
	public static String getWeatherStatic(String city, Unit unit) {

		double temperature = 0;
		if (city.contains("Paris")) {
			temperature = 30;
		}
		else if (city.contains("Seoul")) {
			temperature = 20;
		}
		else if (city.contains("San Francisco")) {
			temperature = 10;
		}

		return "temperature: " + temperature + " unit: " + unit;
	}
}
