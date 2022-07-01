package com.telegram.reporting.utils;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import org.apache.commons.lang3.Validate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class KeyboardUtils {

    private KeyboardUtils() {}

    public static KeyboardRow createButton(String name) {
        Validate.notBlank(name, "Can't create button without name");
        return createRowButtons(name);
    }

    public static KeyboardRow createRowButtons(String... names) {
        Validate.noNullElements(names, "Can't create row of buttons. Array of names contains NULL element");
        Stream.of(names).forEach(name -> Validate.notBlank(name, "Can't create row of button. There is no name for the button"));
        KeyboardRow button = new KeyboardRow();
        button.addAll(Arrays.asList(names));
        return button;
    }

    public static KeyboardRow[] createButtonsWithRows(List<String> buttonNames, int buttonsInRow) {
        Validate.notEmpty(buttonNames, "Can't create buttons with rows. List of names empty or NULL");
        Validate.noNullElements(buttonNames, "Can't create buttons with rows. List of names contains NULL element");

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<String> listBN = new ArrayList<>(buttonNames);

        final int requiredMinQuantity = 2;
        if (buttonsInRow < requiredMinQuantity) {
            buttonsInRow = requiredMinQuantity;
        }

        int cycle = listBN.size() / 2;
        int lastRowButtons = listBN.size() % buttonsInRow;

        for (int i = 0; i < cycle; i++) {
            List<String> subBNs = new ArrayList<>(listBN.subList(0, buttonsInRow));
            KeyboardRow button = KeyboardUtils.createRowButtons(subBNs.toArray(new String[0]));
            subBNs.forEach(listBN::remove);

            keyboardRows.add(button);
        }

        if (lastRowButtons != 0) {
            keyboardRows.add(KeyboardUtils.createRowButtons(listBN.toArray(new String[0])));
        }

        return keyboardRows.toArray(new KeyboardRow[0]);
    }

    public static ReplyKeyboardMarkup createMainMenuButtonMarkup() {
        return createKeyboardMarkup(true, new KeyboardRow());
    }

    public static ReplyKeyboardMarkup createKeyboardMarkup(boolean addMainMenuButton, KeyboardRow... rows) {
        Objects.requireNonNull(rows, "Can't create keyboard markup without keyboard rows");
        List<KeyboardRow> keyboardRows = new ArrayList<>(List.of(rows));
        if (addMainMenuButton) {
            KeyboardRow mainMenuButton = createButton(ButtonValue.MAIN_MENU.text());
            keyboardRows.add(mainMenuButton);
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static SendMessage createRootMenuMessage(String chatId) {
        Objects.requireNonNull(chatId, "Can't create root menu buttons! ChatId is required");
        final String startFlowMessage = """
                Окей.
                Выбери диалог.
                """;

        SendMessage sendMessage = new SendMessage(chatId, startFlowMessage);
        KeyboardRow firstRow = KeyboardUtils.createButton(ButtonValue.CREATE_REPORT_START_DIALOG.text());
        KeyboardRow secondRow = KeyboardUtils.createRowButtons(ButtonValue.EDIT_REPORT_START_DIALOG.text(), ButtonValue.DELETE_REPORT_START_DIALOG.text());
        KeyboardRow thirdRow = KeyboardUtils.createRowButtons(ButtonValue.STATISTIC_START_DIALOG.text());
        sendMessage.setReplyMarkup(createKeyboardMarkup(false, firstRow, secondRow, thirdRow));

        return sendMessage;
    }

    public static List<String> getAvailableCategoryButtons(String timeRecordsJson) {
        return ButtonValue.categoryButtons().stream()
                .map(ButtonValue::text)
                .filter(Predicate.not(Optional.ofNullable(timeRecordsJson).orElse("")::contains))
                .toList();
    }

    public static String[] getButtonsByTimeRecordOrdinalNumber(List<TimeRecordTO> timeRecordTOS) {
        Validate.notEmpty(timeRecordTOS, "Can't create buttons for TimeRecordsTOs. List of TimeRecordTOs is empty or NULL");
        timeRecordTOS.forEach(tr -> Validate.notNull(tr.getOrdinalNumber(), "TimeRecords must contain ordinal number to creating buttons for them. TimeRecord = %s".formatted(tr)));

        List<String> buttons = new ArrayList<>(timeRecordTOS.size());
        timeRecordTOS.forEach(tr -> buttons.add(String.valueOf(tr.getOrdinalNumber())));
        return buttons.toArray(new String[timeRecordTOS.size()]);
    }
}
