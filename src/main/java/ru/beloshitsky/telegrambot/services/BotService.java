package ru.beloshitsky.telegrambot.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.beloshitsky.telegrambot.messages.AveragePriceMessage;
import ru.beloshitsky.telegrambot.messages.HelpMessage;
import ru.beloshitsky.telegrambot.messages.Message;
import ru.beloshitsky.telegrambot.messages.StartMessage;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BotService {

    Map<String, Message> mapOfMessages;

    @Autowired
    public BotService(@Qualifier("mapOfMessages") Map<String, Message> mapOfMessages) {
        this.mapOfMessages = mapOfMessages;
    }

    public SendMessage processUpdate(Update update) {

        SendMessage message = null;
        if (update.hasMessage() && update.getMessage().hasText()) {

            String text = update.getMessage().getText().toLowerCase(Locale.ROOT);
            String chatId = String.valueOf(update.getMessage().getChatId());
            String command = text;
            if (matchesCityAndProduct(command)) {
                command = "найти среднюю цену";
            }
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    update.getMessage().getFrom(), chatId, text);
            message = mapOfMessages.get(command).getMessage(text, chatId);
        }
        return message;
    }

    private boolean matchesCityAndProduct(String text) {
        return text.matches("[а-яА-Я]+-*\\s*[а-яА-Я]*-*[а-яА-Я]*\\s+.*");
    }
}
