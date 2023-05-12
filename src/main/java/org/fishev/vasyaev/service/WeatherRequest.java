package org.fishev.vasyaev.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.fishev.vasyaev.exception.PlaceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Component
@Import(ObjectMapper.class)
public class WeatherRequest {

    final WeatherConfig config;
    CurrentWeather currentWeather;
    FutureWeather futureWeather;
    @Autowired
    ObjectMapper objectMapper;

    public WeatherRequest(WeatherConfig config) {
        this.config = config;
    }

    public String getForecast(String city, String forecastType, String dateUserChoice)
            throws PlaceNotFoundException, IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("api.openweathermap.org")
                .setPath("data/2.5/" + forecastType)
                .setParameter("q", city)
                .setParameter("appid", config.getWeatherKey())
                .setParameter("lang", "ru")
                .setParameter("units", "metric")
                .build();

        if (forecastType.equals("weather"))
            return callCurrentForecast(uri);
        else
            return callFutureForecast(uri, dateUserChoice);
    }

    public String getForecast(Integer index, String forecastType, String dateUserChoice)
            throws PlaceNotFoundException, IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("api.openweathermap.org")
                .setPath("data/2.5/" + forecastType)
                .setParameter("zip", index.toString() + ",ru")
                .setParameter("appid", config.getWeatherKey())
                .setParameter("lang", "ru")
                .setParameter("units", "metric")
                .build();

        if (forecastType.equals("weather"))
            return callCurrentForecast(uri);
        else
            return callFutureForecast(uri, dateUserChoice);
    }

    public String getForecast(Location location, String forecastType, String dateUserChoice)
            throws PlaceNotFoundException, IOException, URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("api.openweathermap.org")
                .setPath("data/2.5/" + forecastType)
                .setParameter("lat", location.getLatitude().toString())
                .setParameter("lon", location.getLongitude().toString())
                .setParameter("appid", config.getWeatherKey())
                .setParameter("lang", "ru")
                .setParameter("units", "metric")
                .build();

        if (forecastType.equals("weather"))
            return callCurrentForecast(uri);
        else
            return callFutureForecast(uri, dateUserChoice);
    }

    private String callCurrentForecast(URI uri) throws PlaceNotFoundException, IOException {

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            currentWeather = objectMapper.readValue(uri.toURL(), CurrentWeather.class);
        } catch (FileNotFoundException e) {
            throw new PlaceNotFoundException();
        }
        return currentWeather.currentForecast();
    }

    private String callFutureForecast(URI uri, String dateUserChoice) throws PlaceNotFoundException, IOException {

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            futureWeather = objectMapper.readValue(uri.toURL(), FutureWeather.class);
        } catch (FileNotFoundException e) {
            throw new PlaceNotFoundException();
        }
        return futureWeather.forecastForChosenDate(dateUserChoice);
    }

    public ArrayList<String> getForecastDates(){
        return futureWeather.getForecastDates();
    }
}
