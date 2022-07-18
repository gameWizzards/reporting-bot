package com.telegram.reporting.dialogs.guards;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.LockUpdateReportService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.DateTimeUtils;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.Month;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;

@Slf4j
@Service
public class GuardValidatorImpl implements GuardValidator {

    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService userService;
    private final LockUpdateReportService lockService;

    public GuardValidatorImpl(SendBotMessageService sendBotMessageService, TelegramUserService userService,
                              LockUpdateReportService lockService) {
        this.sendBotMessageService = sendBotMessageService;
        this.userService = userService;
        this.lockService = lockService;
    }

    @Override
    public <S, E> boolean validateDate(StateContext<S, E> context) {
        String regexDay = "^([1-9]|0[1-9]|1\\d|2\\d|3[01])$";
        String regexDayMonth = "^([1-9]|0[1-9]|1\\d|2\\d|3[01]).(0[1-9]|1[0-2])$";
        String regexFullDate = "^([1-9]|0[1-9]|1\\d|2\\d|3[01]).(0[1-9]|1[0-2]).(19|20)\\d{2}$";

        Long chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.DATE);

        if (userInput.matches(regexDay) || userInput.matches(regexDayMonth) || userInput.matches(regexFullDate)) {
            LocalDate reportDate = DateTimeUtils.convertUserInputToDate(userInput);
            User user = userService.findByChatId(chatId).orElseThrow(() -> new TelegramUserException("Can't find user with chatId =%d".formatted(chatId)));
            boolean existManipulateReportDataLock = lockService.lockExist(user.getId(), reportDate);

            if (existManipulateReportDataLock) {
                String lockMessage = """
                        %s, ты не можешь создавать/удалять/изменять отчеты за %s.
                        Этот период закрыт для изменеий.
                        Введи другой месяц!
                        """.formatted(user.getName(), Month.getNameByOrdinal(reportDate.getMonthValue()));
                sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), lockMessage),
                        KeyboardUtils.createMainMenuButtonMarkup());
                return false;
            }
            return true;
        }

        String dateErrorMessage = """
                Ты не верно ввел дату!
                Попробуй еще раз в формате - 29.08.1997. Также допустимо - 29 или 29.08
                """;
        sendBotMessageService.sendMessage(chatId, dateErrorMessage);

        return false;
    }

    @Override
    public <S, E> boolean validateMonthDate(StateContext<S, E> context) {
        String regexMonth = "^([1-9]|1[0-2])$";
        String regexMonthYear = "^([1-9]|1[0-2]).(19|20)\\d{2}$";

        Long chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.DATE);

        if (userInput.matches(regexMonth) || userInput.matches(regexMonthYear)) {
            return true;
        }

        String dateErrorMessage = """
                Ты не верно ввел месяц!
                Допустимо - месяц или месяц.год!
                Попробуй еще раз в формате - 8 или 8.1997
                """;
        sendBotMessageService.sendMessage(chatId, dateErrorMessage);

        return false;
    }

    @Override
    public <S, E> boolean validateTime(StateContext<S, E> context) {
        Long chatId = TelegramUtils.currentChatId(context);
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
        Long chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_NOTE);
        String lastButtonText = (String) context.getExtendedState().getVariables().get(ContextVariable.BUTTON_VALUE);
        boolean isSkipNote = ButtonValue.SKIP_NOTE.text().equals(lastButtonText.trim());

        if (isSkipNote || (userInput != null && userInput.trim().length() >= minNoteLength)) {
            return true;
        }

        String noteErrorMessage = """
                Твое примечание слишком лаконичное))
                Попробуй написать больше БУКАВ))
                Минимальное количество -> %s""".formatted(minNoteLength);

        sendBotMessageService.sendMessage(chatId, noteErrorMessage);
        return false;
    }

    private boolean isWithinDay(String userInput) {
        var hours = Integer.parseInt(userInput);
        return hours > 0 && hours <= 24;
    }
}
