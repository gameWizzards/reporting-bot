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
        String userInput = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.DATE);

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
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), dateErrorMessage), KeyboardUtils.createMainMenuButtonMarkup());

        return false;
    }

    @Override
    public <S, E> boolean validateMonthDate(StateContext<S, E> context) {
        String regexMonth = "^([1-9]|1[0-2])$";
        String regexMonthYear = "^([1-9]|1[0-2]).(19|20)\\d{2}$";

        String userInput = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.DATE);

        if (userInput.matches(regexMonth) || userInput.matches(regexMonthYear)) {
            return true;
        }

        String dateErrorMessage = """
                Ты не верно ввел месяц!
                Допустимо - месяц или месяц.год!
                Попробуй еще раз в формате - 8 или 8.1997
                """;
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), dateErrorMessage), KeyboardUtils.createMainMenuButtonMarkup());

        return false;
    }

    @Override
    public <S, E> boolean validateTime(StateContext<S, E> context) {
        String userInput = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.REPORT_TIME);

        if (userInput.matches("\\d+") && isWithinDay(userInput)) {
            return true;
        }

        String timeErrorMessage = """
                Ты не верно ввел время.
                Допустимые значения - положительные числа в пределах 24 часов
                """;
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), timeErrorMessage), KeyboardUtils.createMainMenuButtonMarkup());
        return false;
    }

    @Override
    public <S, E> boolean validateNote(StateContext<S, E> context) {
        int minNoteLength = 5;
        String userInput = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.REPORT_NOTE);
        String lastButtonText = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.BUTTON_VALUE);
        boolean isSkipNote = ButtonValue.SKIP_NOTE.text().equals(lastButtonText.trim());

        if (isSkipNote || (userInput != null && userInput.trim().length() >= minNoteLength)) {
            return true;
        }

        String noteErrorMessage = """
                Твое примечание слишком лаконичное))
                Попробуй написать больше БУКАВ))
                Минимальное количество -> %s""".formatted(minNoteLength);

        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), noteErrorMessage), KeyboardUtils.createMainMenuButtonMarkup());
        return false;
    }

    @Override
    public <S, E> boolean validatePhoneInput(StateContext<S, E> context) {
        String specSymbolsRegex = "[()\\-+ ]";
        String fullFormatPhoneRegex = "^380[0-9]{9}";

        String userInput = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.PHONE);
        String clearedInput = userInput.replaceAll(specSymbolsRegex, "");
        String fullFormatPhone = clearedInput.startsWith("0") ? "38" + clearedInput : clearedInput;
        boolean isCorrectPhoneFormat = fullFormatPhone.matches(fullFormatPhoneRegex);

        if (isCorrectPhoneFormat) {
            User user = userService.findByPhone(fullFormatPhone).orElse(null);
            if (user != null) {
                String userInfo = user.getActivated() != null ? user.getFullName() : "нет имени т.к. пользователь еще не активировал свой аккаунт";
                String errMessage = """
                Ты не можешь добавить этот номер т.к. пользователь с таким номером телефона уже существует!
                Инфо о пользователе:
                Телефон: +%s
                Имя: %s
                """.formatted(fullFormatPhone, userInfo);
                sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), errMessage), KeyboardUtils.createMainMenuButtonMarkup());
                return false;
            }
        }

        if (isCorrectPhoneFormat) {
            context.getExtendedState().getVariables().put(ContextVariable.PHONE, fullFormatPhone);
            return true;
        }

        String errMessage = """
                Не верный формат номера!
                Допустимые форматы:
                <code>067 1112233 или 38 067 1112233</code>
                Также допустимо использовать символы:
                <b> + () пробелы и тире</b>
                """;

        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), errMessage), KeyboardUtils.createMainMenuButtonMarkup());
        return false;
    }

    private boolean isWithinDay(String userInput) {
        var hours = Integer.parseInt(userInput);
        return hours > 0 && hours <= 24;
    }
}
