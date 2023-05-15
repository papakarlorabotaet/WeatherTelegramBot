package com.fishev.vasyaev.config;

import lombok.Setter;
import com.fishev.vasyaev.config.weatherclass.City;
import com.fishev.vasyaev.config.weatherclass.WeatherList;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

@Setter
@Component
public class FutureWeather {

    private WeatherList[] list;
    private City city;

    public ArrayList<String> getForecastDates(){
        String dateSting;
        ArrayList<String> dates = new ArrayList<>();
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(city.getTimezone());
        DateTimeFormatter date = DateTimeFormatter.ofPattern("E d MMM", Locale.forLanguageTag("ru"));

        for (var i : list) {
            dateSting = LocalDateTime.ofEpochSecond(i.getDt(), 0, zoneOffset).format(date);
            if (!dates.contains(dateSting)) {
                dates.add(dateSting);
            }
        }
        return dates;
    }

    public String forecastForChosenDate(String dateUserChoice) {

        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(city.getTimezone());
        DateTimeFormatter date = DateTimeFormatter.ofPattern("E d MMM", Locale.forLanguageTag("ru"));
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter hour = DateTimeFormatter.ofPattern("h");
        ArrayList<String> dates = getForecastDates();
        String temperature;
        String result;
        String dateToCompare;
        LocalDateTime localDateTime;

        final Icons icons = new Icons();

        if (dateUserChoice != null) {
            dateToCompare = dateUserChoice;
        } else {
            dateToCompare = dates.get(0);
        }

        result = city.getName() + "\n\uD83D\uDDD3 " + dateToCompare +
                " \uD83C\uDF05 " +
                LocalDateTime.ofEpochSecond(city.getSunrise(), 0, zoneOffset).format(time) +
                " \uD83C\uDF06 " +
                LocalDateTime.ofEpochSecond(city.getSunset(), 0, zoneOffset).format(time) + "\n\n";

        for (var i : list) {
            localDateTime  = LocalDateTime.ofEpochSecond(i.getDt(), 0, zoneOffset);
            if (localDateTime.format(date).equals(dateToCompare)) {
                temperature = String.format("\uD83C\uDF21️ %+.0f°C ", i.getMain().getTemp());

                result = String.join(" ",
                        result,
                        icons.iconsMap.get(localDateTime.format(hour)),
                        localDateTime.format(time),
                        icons.iconsMap.get(i.getWeather()[0].getIcon()),
                        i.getWeather()[0].getDescription(),
                        "\n",
                        temperature,
                        "\uD83D\uDCA8",
                        Integer.toString(Math.round(i.getWind().getSpeed())),
                        "м/с",
                        i.getWind().getDirection(),
                        "☔",
                        (int) (i.getPop() * 100) + "%",
                        "\n\n");
            }
        }
        return result;
    }
}