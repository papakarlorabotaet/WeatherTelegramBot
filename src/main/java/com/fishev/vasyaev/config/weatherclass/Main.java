package com.fishev.vasyaev.config.weatherclass;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Main {
    float temp;
    float feels_like;
    float temp_min;
    float temp_max;
    float pressure;
    int humidity;
}