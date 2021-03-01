package ru.beloshitsky.telegrambot.messages;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        String[] tokens = text.toLowerCase(Locale.ROOT).trim().split("\\s", 2);
        String city = tokens[0];
        String product = tokens[1];
        log.info("city: {}, product: {}", city, product);

        if (mapOfCities.containsKey(city)) {
            String cityInEnglish = mapOfCities.get(city);
            log.info("cityInEnglish: {}", cityInEnglish);
            double averagePrice = avgPriceMessageService.getAvgOnAllPages(cityInEnglish, product);
            message.setText(String.format("Средняя цена в городе %s = %,.0f ₽", city, averagePrice));
            message.setReplyMarkup(getInlineKeyboardMarkup(cityInEnglish, product));
        } else {
            message.setText("Нет такого города");
        }
        message.setChatId(chatId);
        return message;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(String cityInEnglish, String product) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Перейти на Avito");
        button.setCallbackData("Button has been pressed");
        String URL = botConfig.getRootURL() + cityInEnglish + "?q=" + product;
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

