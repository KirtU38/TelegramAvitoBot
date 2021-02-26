package ru.beloshitsky.telegrambot.messages;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.beloshitsky.telegrambot.configuration.BotConfig;
import ru.beloshitsky.telegrambot.services.AvgPriceMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class AveragePriceMessage implements Message {

    Map<String, String> mapOfCities;
    AvgPriceMessageService avgPriceMessageService;
    BotConfig botConfig;

    @SneakyThrows
    public SendMessage getMessage(String text, String chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        String[] tokens = text.toLowerCase(Locale.ROOT).trim().split("\\s", 2);
        String city = tokens[0];
        String product = tokens[1];
        // String product = mapOfCities.containsKey(city) ? tokens[1] : "";

        if (mapOfCities.containsKey(city)) {
            String cityInEnglish = mapOfCities.get(city);
            double averagePrice = avgPriceMessageService.calculateAvgPrice(cityInEnglish, product);
            String URLCityAndProduct = botConfig.getRootURL() + cityInEnglish + "?q=" + product;
            message.setText(String.format("Средняя цена в городе %s = %,.0f ₽", city, averagePrice));
            message.setReplyMarkup(getInlineKeyboardMarkup(URLCityAndProduct));
        } else {
            message.setText("Нет такого города");
        }
        return message;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(String URL) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Перейти на Avito");
        button.setCallbackData("Button has been pressed");
        button.setUrl(URL);

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(button);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        keyboard.setKeyboard(rowList);
        return keyboard;
    }

    @Override
    public String getId() {
        return "найти среднюю цену";
    }
}

