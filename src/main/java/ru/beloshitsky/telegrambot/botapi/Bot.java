package ru.beloshitsky.telegrambot.botapi;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.beloshitsky.telegrambot.configuration.BotConfig;
import ru.beloshitsky.telegrambot.services.BotService;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Bot extends TelegramLongPollingBot {
  BotService botService;
  BotConfig botConfig;

  public Bot(BotService botService, BotConfig botConfig) {
    this.botService = botService;
    this.botConfig = botConfig;
  }

  // public Bot(DefaultBotOptions options, BotService botService, BotConfig botConfig) {
  //   super(options);
  //   this.botService = botService;
  //   this.botConfig = botConfig;
  // }

  @Override
  public String getBotUsername() {
    return botConfig.getUserName();
  }

  @Override
  public String getBotToken() {
    return botConfig.getToken();
  }

  @SneakyThrows
  @Override
  public void onUpdateReceived(Update update) {
    SendMessage message = botService.processUpdate(update);
    execute(message);
  }

  // @PostConstruct
  // public void registerBot() {
  //   try {
  //     DefaultBotOptions options = new DefaultBotOptions();
  //     options.setProxyType();
  //     TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
  //     botsApi.registerBot(this);
  //   } catch (TelegramApiException e) {
  //     log.error("Couldn't register a Bot");
  //     e.printStackTrace();
  //   }
  // }
}
