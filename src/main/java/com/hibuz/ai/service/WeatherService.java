package com.hibuz.ai.service;

import java.util.function.Function;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

@Component("currentWeather")
@Description("Get the weather in location")
public class WeatherService implements Function<WeatherService.Request, WeatherService.Response> {

    public enum TemperatureUnit { CELSIUS, FAHRENHEIT }

	public record Request(String location, TemperatureUnit unit) {}

	public record Response(double temp, TemperatureUnit unit) {}

    @Override
	public Response apply(Request request) {
		return new Response(30.0, TemperatureUnit.CELSIUS);
	}
}
