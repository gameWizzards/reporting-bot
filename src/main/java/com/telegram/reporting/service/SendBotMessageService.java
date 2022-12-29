package com.telegram.reporting.service;

import com.telegram.reporting.repository.dto.EmployeeTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface SendBotMessageService {

    void sendMessageWithKeys(SendMessage message, ReplyKeyboard keyboardMarkup);

    void sendMessage(Long chatId, String message);

    void sendMessage(String chatId, String message);

    void sendLink2UserChat(Long chatId, EmployeeTO employee);
    // TODO remove after some time when all old based dialogs migrate to new button's schema
    void removeReplyKeyboard (Long chatId, String message);

}
