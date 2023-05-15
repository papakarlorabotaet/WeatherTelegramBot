package com.fishev.vasyaev.config;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class Keyboard {

    public InlineKeyboardMarkup afterSettingsKeyboard() {
        // button 1
        InlineKeyboardButton currentForecastButton = new InlineKeyboardButton();
        currentForecastButton.setText("⌚️ текущий прогноз");
        currentForecastButton.setCallbackData("/current");
        // button 2
        InlineKeyboardButton futureForecastButton = new InlineKeyboardButton();
        futureForecastButton.setText("\uD83D\uDDD3 на 5 дней");
        futureForecastButton.setCallbackData("/future");
        // button 3
        InlineKeyboardButton settingsButton = new InlineKeyboardButton();
        settingsButton.setText("⚙ сменить город");
        settingsButton.setCallbackData("/settings");
        // row 1
        List<InlineKeyboardButton> row1 = new LinkedList<>();
        row1.add(currentForecastButton);
        row1.add(futureForecastButton);
        // row 2
        List<InlineKeyboardButton> row2 = new LinkedList<>();
        row2.add(settingsButton);
        // rows
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        rows.add(row1);
        rows.add(row2);
        // keyboard
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        return keyboard;
    }

    public InlineKeyboardMarkup futureForecastKeyboard(ArrayList<String> dates, String userChoice) {
        // button settings
        InlineKeyboardButton settingsButton = new InlineKeyboardButton();
        settingsButton.setText("⚙ сменить город");
        settingsButton.setCallbackData("/settings");
        // buttons
        InlineKeyboardButton btmBack = new InlineKeyboardButton();
        InlineKeyboardButton btmForward = new InlineKeyboardButton();
        // rows
        List<InlineKeyboardButton> row1 = new LinkedList<>();
        List<InlineKeyboardButton> row2 = new LinkedList<>();

        int userChoicePos = dates.indexOf(userChoice);

        if (userChoicePos > 0) {
            btmBack.setText("⬅️ " + dates.get(userChoicePos - 1));
            btmBack.setCallbackData(dates.get(userChoicePos - 1));
            row1.add(btmBack);
            if (userChoicePos < dates.size() - 1) {
                btmForward.setText("➡️️ " + dates.get(userChoicePos + 1));
                btmForward.setCallbackData(dates.get(userChoicePos + 1));
                row1.add(btmForward);
            }
        } else { // userChoicePos == 0 || userChoicePos == -1
            btmForward.setText("➡️️ " + dates.get(1));
            btmForward.setCallbackData(dates.get(1));
            row1.add(btmForward);
        }
        row2.add(settingsButton);
        // rows
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        rows.add(row1);
        rows.add(row2);
        // keyboard
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        return keyboard;
    }

}
