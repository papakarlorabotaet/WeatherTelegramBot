package org.fishev.vasyaev.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    final BotConfig config;
    Map<Long, User> users;
    @Autowired
    WeatherRequest weatherRequest;
    @Autowired
    Serializer serializer;
    @Autowired
    Keyboard keyboard;

    public Bot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;

        if (users == null) {
            users = serializer.deserialize();
        }
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getFrom().getId();
        } else {
            return;
        }
        if (users.get(chatId) == null) {
            users.put(chatId, new User());
        }
        if ((update.hasMessage() && update.getMessage().hasText()) || update.hasCallbackQuery()) {
            readUserCommand(update, chatId);
        }
        if (update.hasMessage()) {
            processBotState(update.getMessage(), chatId);
        } else if (update.hasCallbackQuery()) {
            processBotState(update.getCallbackQuery(), chatId);
        }
        serializer.serialize(users);
    }

    private void readUserCommand(Update update, Long chatId) {

        String userCommand;

        if (update.hasMessage() && update.getMessage().hasText()) {
            userCommand = update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            userCommand = update.getCallbackQuery().getData();
        } else {
            return;
        }

        if (users.get(chatId).getBotState() != BotState.DEFAULT &&
                (userCommand.equals("/current") || userCommand.equals("/future"))) {
            sendTextMessageToUser(chatId, "сначала отправь город, индекс или GPS координаты");
            return;
        }
        switch (userCommand) {
            case "/settings":
                users.get(chatId).clear();
                users.get(chatId).setBotState(BotState.CHANGE_SETTINGS);
                break;
            case "/current":
                users.get(chatId).setBotState(BotState.CURRENT_FORECAST);
                break;
            case "/future":
                users.get(chatId).setBotState(BotState.FUTURE_FORECAST);
                break;
            case "/start":
                users.get(chatId).clear();
                users.get(chatId).setBotState(BotState.BOT_START);
                break;
            case "/subscribe":
                users.get(chatId).setBotState(BotState.BOT_SUBSCRIBE);
                break;
            case "/unsubscribe":
                users.get(chatId).setBotState(BotState.BOT_UNSUBSCRIBE);
                break;
        }
    }

    public void sendTextMessageToUser(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.toString(), e);
        }
    }

    private void processBotState(Message message, Long chatId) {
        switch (users.get(chatId).getBotState()) {
            case DEFAULT:
                sendTextMessageToUser(chatId, "Не понимаю, выбери команду из меню \uD83D\uDE43");
                break;
            case CHANGE_SETTINGS:
                sendTextMessageToUser(chatId, "Отправь название города на русском или английском языке, " +
                        "6 цифр индекса города РФ или геолокацию");
                users.get(chatId).setBotState(BotState.READ_SETTINGS);
                break;
            case READ_SETTINGS:
                readSettings(message, chatId);
                break;
            case CURRENT_FORECAST:
                requestCurrentForecast(chatId);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
            case FUTURE_FORECAST:
                requestFutureForecast(chatId, null);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
            case BOT_START:
                sendTextMessageToUser(chatId, "Привет, я умею в погоду \uD83D\uDE42 \nВведи город, индекс или GPS координаты");
                users.get(chatId).setBotState(BotState.READ_SETTINGS);
                break;
            case BOT_SUBSCRIBE:
//                sendTextMessageToUser(chatId, "Вы успешно подписались \uD83D\uDE42");
                subscriptionActivity(message, chatId);
                users.get(chatId).setBotState(BotState.CHANGE_SETTINGS);
                break;

            case BOT_UNSUBSCRIBE:
//                sendTextMessageToUser(chatId, "Вы так же успешно отписались \uD83D\uDE42");
                subscriptionDeactivity(message, chatId);
                users.get(chatId).setBotState(BotState.CHANGE_SETTINGS);
                break;


        }

    }

    private void processBotState(CallbackQuery callbackQuery, Long chatId) {
        switch (users.get(chatId).getBotState()) {
            case CHANGE_SETTINGS:
                sendTextMessageToUser(chatId, "Отправь название города на русском или английском языке, " +
                        "6 цифр индекса города РФ или геолокацию");
                users.get(chatId).setBotState(BotState.READ_SETTINGS);
                break;
            case CURRENT_FORECAST:
                requestCurrentForecast(chatId);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
            case FUTURE_FORECAST:
                requestFutureForecast(chatId, null);
                users.get(chatId).setBotState(BotState.DEFAULT);
                break;
            case DEFAULT:
                requestFutureForecast(chatId, callbackQuery);
        }
    }

    private void subscriptionActivity(Message message, Long chatId) {
        try {
            if (!users.get(chatId).getIsSubscriptions()) { // Если false то подписываемся на рассылку
                if (users.get(chatId).getIsCity()) {
                    sendTextMessageToUser(chatId, "Вы успешно подписались ");
                    users.get(chatId).setSubscriptions(); // и меняем значение isSubscriptions на TRUE c помощью метода setSubscriptions
                    foo();
                } else {
                    sendTextMessageToUser(chatId, "Перед тем как подписаться на рассылку, выбери город ");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void subscriptionDeactivity(Message message, Long chatId) {
        try {
            if (users.get(chatId).getIsSubscriptions()) { // Если true то может позволить отписаться от рассылки
                sendTextMessageToUser(chatId, "Вы также успешно отписались ");
                users.get(chatId).setSubscriptions(); // и меняем значение isSubscriptions на FALSE c помощью метода setSubscriptions

            } else {
                sendTextMessageToUser(chatId, "Перед тем как отписаться от рассылки, выбери город, да, даже это я предусмотрел, негодник");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 5000L)
    public void foo() {
        System.out.println("прошло 5 секунд с момента подписки ");

    }


    private void readSettings(Message message, Long chatId) {

        if (message.hasLocation()) {
            users.get(chatId).setLocation(message.getLocation());
            sendMessageToUserWithKeyboard(chatId, users.get(chatId).getSettings(), keyboard.afterSettingsKeyboard());
        } else if (message.hasText()) {
            String userInput = message.getText();
            if (userInput.equals("/current") || userInput.equals("/future") || userInput.equals("/subscribe") || userInput.equals("/unsubscribe")) {
                return;
            }
            if (isIndex(userInput)) {
                users.get(chatId).setIndex(Integer.parseInt(userInput));
                sendMessageToUserWithKeyboard(chatId, users.get(chatId).getSettings(), keyboard.afterSettingsKeyboard());
            } else if (isCity(userInput)) {
                users.get(chatId).setCity(userInput);
                sendMessageToUserWithKeyboard(chatId, users.get(chatId).getSettings(), keyboard.afterSettingsKeyboard());
            } else {
                sendTextMessageToUser(chatId, "Что-то не похоже на индекс или название города \uD83E\uDD13");
            }
        }
    }

    private void sendMessageToUserWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboard);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.toString(), e);
        }
    }


    private void requestCurrentForecast(Long chatId) {

        String messageText = null;
        try {
            if (users.get(chatId).getCity() != null) {
                messageText = weatherRequest.getForecast(users.get(chatId).getCity(), "weather", null);
            } else if (users.get(chatId).getIndex() != null) {
                messageText = weatherRequest.getForecast(users.get(chatId).getIndex(), "weather", null);
            } else if (users.get(chatId).getLocation() != null) {
                messageText = weatherRequest.getForecast(users.get(chatId).getLocation(), "weather", null);
            }
        } catch (PlaceNotFoundException e) {
            messageText = "Такой город по названию или индексу не найден, измени настройки";
        } catch (IOException | URISyntaxException e) {
            messageText = "Произошла критическая ошибка на при запросе прогноза погоды от сервера. Попробуйте позже";
            users.get(chatId).setBotState(BotState.DEFAULT);
            log.error(e.toString(), e);
        }

        sendTextMessageToUser(chatId, messageText);
    }

    private void requestFutureForecast(Long chatId, CallbackQuery callbackQuery) {

        String messageText = null;
        String userChoice;
        if (callbackQuery == null) {
            userChoice = null;
        } else {
            userChoice = callbackQuery.getData();
        }

        try {
            if (users.get(chatId).getCity() != null) {
                messageText = weatherRequest.getForecast(users.get(chatId).getCity(), "forecast", userChoice);
            } else if (users.get(chatId).getIndex() != null) {
                messageText = weatherRequest.getForecast(users.get(chatId).getIndex(), "forecast", userChoice);
            } else if (users.get(chatId).getLocation() != null) {
                messageText = weatherRequest.getForecast(users.get(chatId).getLocation(), "forecast", userChoice);
            }
        } catch (PlaceNotFoundException e) {
            messageText = "Такой город по названию или индексу не найден, измени настройки";
        } catch (IOException | URISyntaxException e) {
            messageText = "Произошла критическая ошибка на при запросе прогноза погоды от сервера. Попробуйте позже";
            users.get(chatId).setBotState(BotState.DEFAULT);
            log.error(e.toString(), e);
        }

        ArrayList<String> dates = weatherRequest.getForecastDates();

        if (callbackQuery != null) {
            editMessage(chatId, messageText, keyboard.futureForecastKeyboard(dates, userChoice),
                    callbackQuery.getMessage().getMessageId());
        } else
            sendMessageToUserWithKeyboard(chatId, messageText, keyboard.futureForecastKeyboard(dates, null));
    }

    private void editMessage(Long chatId, String text, InlineKeyboardMarkup keyboard, Integer messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId.toString());
        editMessageText.setText(text);
        editMessageText.setMessageId(messageId);
        editMessageText.setReplyMarkup(keyboard);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error(e.toString());
        }
    }

    private boolean isIndex(String userInput) {
        for (char c : userInput.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        int index;
        try {
            index = Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            return false;
        }
        return Integer.toString(index).length() == 6;
    }

    private boolean isCity(String userInput) {
        for (char c : userInput.toCharArray()) {
            if (Character.isDigit(c)) {
                return false;
            }
        }
        for (char c : userInput.toCharArray()) {
            if (!Character.isAlphabetic(c) && c != ' ' && c != '-' && c != '\'') {
                return false;
            }
        }
        return userInput.length() > 1;
    }
}