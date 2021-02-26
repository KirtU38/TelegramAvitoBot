package ru.beloshitsky.telegrambot.configuration;


import org.springframework.context.annotation.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.beloshitsky.telegrambot.botapi.Bot;
import ru.beloshitsky.telegrambot.messages.Message;
import ru.beloshitsky.telegrambot.services.AvgPriceMessageService;
import ru.beloshitsky.telegrambot.services.BotService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@ComponentScan("ru/beloshitsky/telegrambot")
@PropertySource("classpath:application.properties")
@Configuration
public class SpringConfig {

    @Bean("mapOfCities")
    public Map<String, String> mapOfCities(BotConfig botConfig)
            throws IOException {

        String citiesFile = botConfig.getCitiesFile();
        HashMap<String, String> mapOfCities = new HashMap<>();
        Files.readAllLines(Paths.get(citiesFile)).forEach(e -> {
            String[] tokens = e.split("=", 2);
            mapOfCities.put(tokens[0], tokens[1]);
        });
        return mapOfCities;
    }

    @Bean("mapOfMessages")
    public Map<String, Message> mapOfMessages(List<Message> listOfMessages) {

        return listOfMessages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
    }
}
