package com.fishev.vasyaev.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Icons {

    Map<String, String> iconsMap = new HashMap<>();

    public Icons() {
        //weather icon
        iconsMap.put("01d", "☀️");
        iconsMap.put("01n", "\uD83C\uDF0C");
        iconsMap.put("02d", "\uD83C\uDF24");
        iconsMap.put("02n", "\uD83C\uDF0C");
        iconsMap.put("03d", "⛅");
        iconsMap.put("03n", "☁️");
        iconsMap.put("04d", "\uD83C\uDF25");
        iconsMap.put("04n", "☁️");
        iconsMap.put("09d", "\uD83C\uDF27");
        iconsMap.put("09n", "\uD83C\uDF27");
        iconsMap.put("10d", "\uD83C\uDF26");
        iconsMap.put("10n", "\uD83C\uDF27");
        iconsMap.put("11d", "\uD83C\uDF29");
        iconsMap.put("11n", "\uD83C\uDF29");
        iconsMap.put("13d", "❄️");
        iconsMap.put("13n", "❄️");
        iconsMap.put("50d", "\uD83C\uDF2B");
        iconsMap.put("50n", "\uD83C\uDF2B");
        //clocks
        iconsMap.put("1", "\uD83D\uDD50️");
        iconsMap.put("2", "\uD83D\uDD51");
        iconsMap.put("3", "\uD83D\uDD52️");
        iconsMap.put("4", "\uD83D\uDD53️");
        iconsMap.put("5", "\uD83D\uDD54");
        iconsMap.put("6", "\uD83D\uDD55️");
        iconsMap.put("7", "\uD83D\uDD56️");
        iconsMap.put("8", "\uD83D\uDD57️");
        iconsMap.put("9", "\uD83D\uDD58️");
        iconsMap.put("10", "\uD83D\uDD59️");
        iconsMap.put("11", "\uD83D\uDD5A️");
        iconsMap.put("12", "\uD83D\uDD5B️");
    }
}
