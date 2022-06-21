package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.service.GuardService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class GuardServiceImpl implements GuardService {

    private final SendBotMessageService sendBotMessageService;

    public GuardServiceImpl(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public <S, E> boolean validateDate(StateContext<S, E> context) {

        String chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.DATE);

        if (userInput.matches(".*\\d+.*")) {
            return true;
        }
        String dateExample = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        sendBotMessageService.sendMessage(chatId, "Вы не верно ввели дату. Попробуйте еще раз в формате - " + dateExample);

        return false;
    }

    @Override
    public <S, E> boolean validateTime(StateContext<S, E> context) {
        String chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_TIME);

        if (userInput.matches("\\d+") && Integer.parseInt(userInput) > 0) {
            return true;
        }

        sendBotMessageService.sendMessage(chatId, "Вы не верно ввели время. Допустимые значения - положительные числа");
        return false;
    }

    @Override
    public <S, E> boolean validateNote(StateContext<S, E> context) {
        int minNoteLength = 5;
        String chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_NOTE);
        String lastButtonText = (String) context.getExtendedState().getVariables().get(ContextVariable.MESSAGE);
        boolean isSkipNote = Message.SKIP_NOTE.text().equals(lastButtonText.trim());

        if (isSkipNote || (userInput != null && userInput.trim().length() >= minNoteLength)) {
            return true;
        }

        sendBotMessageService.sendMessage(chatId, "Ваше примечание слишком лаконичное)) попробуйте написать больше БУКАВ)) Минимальное количество - " + minNoteLength);
        return false;
    }
}
