package ru.beloshitsky.telegrambot.messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.beloshitsky.telegrambot.services.AvgPriceMessageService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class AveragePriceMessage implements Message {

    AvgPriceMessageService avgPriceMessageService;
    Map<String, String> mapOfCities;
    final String ROOT_URL = "https://www.avito.ru/";

    @Autowired
    public AveragePriceMessage(AvgPriceMessageService avgPriceMessageService, Map<String, String> mapOfCities) {
        this.avgPriceMessageService = avgPriceMessageService;
        this.mapOfCities = mapOfCities;
    }

    @SneakyThrows
    public SendMessage getMessage(String text, String chatId) {
        System.out.println("AveragePriceMessage");

        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        String[] tokens = text.toLowerCase(Locale.ROOT).trim().split("\\s", 2);
        String city = tokens[0];
        String product = tokens[1];
        System.out.println(Arrays.toString(tokens));
        // String product = mapOfCities.containsKey(city) ? tokens[1] : "";

        if (mapOfCities.containsKey(city)) {
            String cityInEnglish = mapOfCities.get(city);
            // String URL = "https://www.avito.ru/" + cityInEnglish + "?q=" + product;
            String URL = ROOT_URL + cityInEnglish + "?q=" + product;

            double averagePrice = avgPriceMessageService.calculateAvgPrice(city, cityInEnglish, product, URL);

            message.setText(String.format("Средняя цена в городе %s = %,.0f ₽", city, averagePrice));
            message.setReplyMarkup(getInlineKeyboardMarkup(URL));
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
