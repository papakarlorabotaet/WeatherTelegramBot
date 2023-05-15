package com.fishev.vasyaev.config.weatherclass;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Sun {
    private long sunrise;
    private long sunset;
}
