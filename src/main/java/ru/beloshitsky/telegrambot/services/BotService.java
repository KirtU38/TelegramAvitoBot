package ru.beloshitsky.telegrambot.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.beloshitsky.telegrambot.messages.AveragePriceMessage;
import ru.beloshitsky.telegrambot.messages.HelpMessage;
import ru.beloshitsky.telegrambot.messages.Message;
import ru.beloshitsky.telegrambot.messages.StartMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class BotService {


    @Autowired
    @Qualifier("mapOfMessages")
    Map<String, Message> mapOfMessages;

    public SendMessage processUpdate(Update update)
            throws InterruptedException, TelegramApiException, IOException {

        mapOfMessages.values().forEach(System.out::println);
        mapOfMessages.keySet().forEach(System.out::println);

        SendMessage message = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println("BotService start");
            String text = update.getMessage().getText().toLowerCase(Locale.ROOT);
            String chatId = String.valueOf(update.getMessage().getChatId());
            String command = text;
            System.out.println(text);
            System.out.println(command);

            if (matchesCityAndProduct(command)) {
                System.out.println("DA");
                command = "найти среднюю цену";
            }
            System.out.println("В мапу");
            message = mapOfMessages.get(command).getMessage(text, chatId);
            System.out.println(mapOfMessages.get(command));

        }
        System.out.println("END");
        return message;
    }

    private boolean matchesCityAndProduct(String text) {
        return text.matches("[а-яА-Я]+-*\\s*[а-яА-Я]*-*[а-яА-Я]*\\s+.*");
    }
}
