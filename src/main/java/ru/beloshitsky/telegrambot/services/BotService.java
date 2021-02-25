package ru.beloshitsky.telegrambot.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.beloshitsky.telegrambot.messages.AveragePriceMessage;
import ru.beloshitsky.telegrambot.messages.HelpMessage;
import ru.beloshitsky.telegrambot.messages.StartMessage;

import java.io.IOException;
import java.util.Locale;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class BotService {

    AveragePriceMessage averagePriceMessage;
    HelpMessage helpMessage;
    StartMessage startMessage;

    @Autowired
    public BotService(AveragePriceMessage averagePriceMessage, HelpMessage helpMessage, StartMessage startMessage) {
        this.averagePriceMessage = averagePriceMessage;
        this.helpMessage = helpMessage;
        this.startMessage = startMessage;
    }

    public SendMessage processUpdate(Update update)
            throws InterruptedException, TelegramApiException, IOException {

        SendMessage message = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println("BotService start");
            String text = update.getMessage().getText().toLowerCase(Locale.ROOT);
            String chatId = String.valueOf(update.getMessage().getChatId());

            message = helpMessage.getMessage(text, chatId);

            if (text.equals("/start")) {
                System.out.println("START");
                message = startMessage.getMessage(text, chatId);
            }
            if (matchesCityAndProduct(text)) {
                System.out.println("CITY");
                message = averagePriceMessage.getMessage(text, chatId);
            }
        }
        return message;
    }

    private boolean matchesCityAndProduct(String text) {
        return text.matches("[а-яА-Я]+-*\\s*[а-яА-Я]*-*[а-яА-Я]*\\s+.*");
    }
}
