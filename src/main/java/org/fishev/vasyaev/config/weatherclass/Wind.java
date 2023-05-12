package org.fishev.vasyaev.config.weatherclass;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Wind {
    float speed;
    float deg;
    float gust;

    public String getDirection() {
        if (deg < 11.25)
            return "⬆️ C";
        else if (deg < 33.75)
            return "⬆️↗️ CСВ";
        else if (deg < 56.25)
            return "↗️ CВ";
        else if (deg < 78.75)
            return "↗️➡️️ ВCВ";
        else if (deg < 101.25)
            return "➡️ В";
        else if (deg < 123.75)
            return "➡️↘️ ВЮВ";
        else if (deg < 146.25)
            return "↘️ ЮВ";
        else if (deg < 168.75)
            return "↘️⬇️ ЮЮВ";
        else if (deg < 191.25)
            return "⬇️ Ю";
        else if (deg < 213.75)
            return "⬇️↙️️ ЮЮЗ";
        else if (deg < 236.25)
            return "️↙️ ЮЗ";
        else if (deg < 258.75)
            return "↖️⬅️ ЗЮЗ";
        else if (deg < 281.25)
            return "⬅️ З";
        else if (deg < 303.75)
            return "⬅️↖️ ЗСЗ";
        else if (deg < 326.25)
            return "↖️ СЗ";
        else if (deg < 348.75)
            return "↖️ CСЗ";
        //else if (deg >= 348.75)
        return "⬆️ C";
    }
}
