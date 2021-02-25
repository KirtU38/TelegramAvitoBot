package ru.beloshitsky.telegrambot.configuration;


import org.springframework.context.annotation.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.beloshitsky.telegrambot.botapi.Bot;
import ru.beloshitsky.telegrambot.services.AvgPriceMessageService;
import ru.beloshitsky.telegrambot.services.BotService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan("ru/beloshitsky/telegrambot")
@PropertySource("classpath:application.properties")
public class SpringConfig {

    @Bean
    public Bot bot(BotService botService, BotConfig botConfig)
            throws TelegramApiException {

        Bot bot = new Bot();
        bot.setBotService(botService);
        bot.setBotToken(botConfig.getToken());
        bot.setBotUserName(botConfig.getUserName());

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
        return bot;
    }

    @Bean
    public AvgPriceMessageService avgPriceMessageService(BotConfig botConfig){
        AvgPriceMessageService avgPriceMessageService = new AvgPriceMessageService();

        avgPriceMessageService.setPriceThreshold(botConfig.getPriceThreshold());
        avgPriceMessageService.setPagesLimit(botConfig.getPagesLimit());
        avgPriceMessageService.setDelayBetweenConnections(botConfig.getDelayBetweenConnections());
        return avgPriceMessageService;
    }

    @Bean
    public Map<String, String> mapOfCities(BotConfig botConfig)
            throws IOException {

        String citiesFile = botConfig.getCitiesFile();
        List<String> strings = Files.readAllLines(Paths.get(citiesFile));

        HashMap<String, String> mapOfCities = new HashMap<>();
        strings.forEach(e -> {
            String[] tokens = e.split("=", 2);
            mapOfCities.put(tokens[0], tokens[1]);
        });
        System.out.println(mapOfCities.size());
        return mapOfCities;
    }
}
