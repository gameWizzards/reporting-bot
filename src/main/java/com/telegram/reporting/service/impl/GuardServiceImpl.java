package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.Message;
import com.telegram.reporting.service.GuardService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GuardServiceImpl implements GuardService {

    private final SendBotMessageService sendBotMessageService;

    public GuardServiceImpl(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public <S, E> boolean validateDate(StateContext<S, E> context) {
        String regexDay = "^([1-9]|0[1-9]|1\\d|2\\d|3[01])$";
        String regexDayMonth = "^([1-9]|0[1-9]|1\\d|2\\d|3[01]).(0[1-9]|1[0-2])$";
        String regexFullDate = "^([1-9]|0[1-9]|1\\d|2\\d|3[01]).(0[1-9]|1[0-2]).(19|20)\\d{2}$";

        String chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.DATE);

        if (userInput.matches(regexDay) || userInput.matches(regexDayMonth) || userInput.matches(regexFullDate)) {
            return true;
        }

        String dateErrorMessage = """
                Ты не верно ввел дату.
                Попробуй еще раз в формате - 29.08.1997.
                Также допустимо - 29 или 29.08
                """;
        sendBotMessageService.sendMessage(chatId, dateErrorMessage);

        return false;
    }

    @Override
    public <S, E> boolean validateTime(StateContext<S, E> context) {
        String chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_TIME);

        if (userInput.matches("\\d+") && isWithinDay(userInput)) {
            return true;
        }

        String timeErrorMessage = """
                Ты не верно ввел время.
                Допустимые значения - положительные числа в пределах 24 часов
                """;
        sendBotMessageService.sendMessage(chatId, timeErrorMessage);
        return false;
    }

    @Override
    public <S, E> boolean validateNote(StateContext<S, E> context) {
        int minNoteLength = 5;
        String chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_NOTE);
        String lastButtonText = (String) context.getExtendedState().getVariables().get(ContextVariable.MESSAGE);
        boolean isSkipNote = ButtonValue.SKIP_NOTE.text().equals(lastButtonText.trim());

        if (isSkipNote || (userInput != null && userInput.trim().length() >= minNoteLength)) {
            return true;
        }

        String noteErrorMessage = """
                Ваше примечание слишком лаконичное))
                попробуйте написать больше БУКАВ))
                Минимальное количество ->""" + minNoteLength;

        sendBotMessageService.sendMessage(chatId, noteErrorMessage);
        return false;
    }

    private boolean isWithinDay(String userInput) {
        var hours = Integer.parseInt(userInput);
        return hours > 0 && hours <= 24;
    }
}
