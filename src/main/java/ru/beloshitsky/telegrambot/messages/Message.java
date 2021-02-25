package ru.beloshitsky.telegrambot.messages;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface Message {

    public SendMessage getMessage(String text, String chatId);
}
