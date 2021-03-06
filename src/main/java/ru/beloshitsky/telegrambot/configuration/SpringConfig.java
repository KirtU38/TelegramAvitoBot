package ru.beloshitsky.telegrambot.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.beloshitsky.telegrambot.botapi.Bot;
import ru.beloshitsky.telegrambot.messages.Message;
import ru.beloshitsky.telegrambot.services.BotService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@ComponentScan("ru/beloshitsky/telegrambot")
@PropertySource("classpath:application.properties")
@Configuration
public class SpringConfig {

  @Bean("mapOfCities")
  public Map<String, String> mapOfCities(BotConfig botConfig) throws IOException {
    String citiesFile = botConfig.getCitiesFile();
    HashMap<String, String> mapOfCities = new HashMap<>();

    Files.readAllLines(Paths.get(citiesFile))
        .forEach(
            e -> {
              String[] tokens = e.split("=", 2);
              mapOfCities.put(tokens[0], tokens[1]);
            });
    return mapOfCities;
  }

  @Bean("mapOfMessages")
  public Map<String, Message> mapOfMessages(List<Message> listOfMessages) {
    return listOfMessages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
  }

  @Bean
  public Bot bot(BotService botService, BotConfig botConfig) {
    Bot bot = null;
    try {
      // DefaultBotOptions options = new DefaultBotOptions();
      // options.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
      // options.setProxyHost(botConfig.getProxyHost());
      // options.setProxyPort(botConfig.getProxyPort());
      //
      // bot = new Bot(options, botService, botConfig);
      bot = new Bot(botService, botConfig);
      TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
      botsApi.registerBot(bot);
      log.info("Bot Registered");
    } catch (TelegramApiException e) {
      log.error("Couldn't register a Bot");
      e.printStackTrace();
    }
    return bot;
  }
}
