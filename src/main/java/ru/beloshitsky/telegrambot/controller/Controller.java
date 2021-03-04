package ru.beloshitsky.telegrambot.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.beloshitsky.telegrambot.Bot;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class Controller {
  Bot bot;

  @PostMapping("/")
  public BotApiMethod<?> receiveUpdate(@RequestBody Update update) {
    log.info(
        "Controller: {}, {}",
        update.getMessage().getText(),
        update.getMessage().getFrom().getFirstName());
    return bot.onWebhookUpdateReceived(update);
  }
}