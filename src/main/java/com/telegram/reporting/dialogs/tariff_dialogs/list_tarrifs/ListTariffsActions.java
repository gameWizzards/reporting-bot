package com.telegram.reporting.dialogs.tariff_dialogs.list_tarrifs;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.dto.EmployeeTO;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.mapper.EmployeeMapper;
import com.telegram.reporting.mapper.EmployeeTariffMessageMapper;
import com.telegram.reporting.repository.EmployeeTariffRepository;
import com.telegram.reporting.repository.TariffRepository;
import com.telegram.reporting.repository.UserRepository;
import com.telegram.reporting.service.*;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListTariffsActions {
    private final SendBotMessageService sendBotMessageService;
    private final CategoryService categoryService;
    private final I18nMessageService i18NMessageService;
    private final I18nButtonService i18nButtonService;
    private final CacheService cacheService;
    private final TariffRepository tariffRepository;
    private final EmployeeTariffRepository employeeTariffRepository;
    private final EmployeeTariffMessageMapper employeeTariffMessageMapper;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;

    public void sendExistedTariffs(StateContext<ListTariffsState, ListTariffsEvent> context) {
        var chatId = CommonUtils.currentChatId(context);
        var tariffs = tariffRepository.findAll();

        sendBotMessageService.sendMessage(chatId, i18NMessageService.convertToCompanyTariffsMessage(chatId, tariffs));
    }

    public void sendOverriddenTariffDisplayingButtons(StateContext<ListTariffsState, ListTariffsEvent> context) {
        var chatId = CommonUtils.currentChatId(context);

        var existsAnyOverriddenTariffs = employeeTariffRepository.existsAnyOverriddenTariffs();

        var message = new SendMessage(chatId.toString(), i18NMessageService.getMessage(chatId, MessageKey.TLT_SHOW_OVERRIDDEN_TARIFFS));
        sendBotMessageService.sendMessageWithKeys(message,
                i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.TARIFF_MENU,
                        ButtonLabelKey.TL_SHOW_OVERRIDDEN_TARIFFS_BY_EMPLOYEE,
                        ButtonLabelKey.TL_SHOW_OVERRIDDEN_TARIFFS_BY_CATEGORY));
//
//        if (existsAnyOverriddenTariffs) {
//            var message = new SendMessage(chatId.toString(), i18NMessageService.getMessage(chatId, MessageKey.TLT_SHOW_OVERRIDDEN_TARIFFS));
//            sendBotMessageService.sendMessageWithKeys(message,
//                    i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.TARIFF_MENU,
//                            ButtonLabelKey.TL_SHOW_OVERRIDDEN_TARIFFS_BY_EMPLOYEE,
//                            ButtonLabelKey.TL_SHOW_OVERRIDDEN_TARIFFS_BY_CATEGORY));
//        } else {
//            var message = new SendMessage(chatId.toString(), i18NMessageService.getMessage(chatId, MessageKey.TLT_NO_OVERRIDDEN_TARIFFS));
//            sendBotMessageService.sendMessageWithKeys(message, i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.TARIFF_MENU));
//        }
    }

    public void handleDisplayingTypeChoice(StateContext<ListTariffsState, ListTariffsEvent> context) {
        var chatId = CommonUtils.currentChatId(context);
        var buttonCallback = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);
        var chosenDisplayingType = ButtonLabelKey.getByKey(buttonCallback);

        switch (chosenDisplayingType) {
            case TL_SHOW_OVERRIDDEN_TARIFFS_BY_EMPLOYEE -> sendOrdinalEmployeeButtonsWithExistedOverriddenTariffs(chatId);
            case TL_SHOW_OVERRIDDEN_TARIFFS_BY_CATEGORY -> sendCategoryButtonsWithExistedOverriddenTariffs(chatId);
        }
        // depends on sorting type send list employees or categories to choose by whom display tariffs
    }




    public void sendOverriddenTariffsByChosenCategory(StateContext<ListTariffsState, ListTariffsEvent> context) {
        var chatId = CommonUtils.currentChatId(context);
        var buttonCallback = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);
        var tariffCategory = ButtonLabelKey.getByKey(buttonCallback);

        var overriddenTariffsMessage = i18NMessageService.convertToOverriddenTariffsByCategoryMessage(chatId, tariffCategory);

        var message = new SendMessage(chatId.toString(), overriddenTariffsMessage);

        sendBotMessageService.sendMessageWithKeys(message,
                i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.TARIFF_MENU, ButtonLabelKey.TL_CHOOSE_ANOTHER_TARIFF));
    }

    public void resendCategoryButtons(StateContext<ListTariffsState, ListTariffsEvent> context) {
        var chatId = CommonUtils.currentChatId(context);
        sendCategoryButtonsWithExistedOverriddenTariffs(chatId);
    }

    public void sendOverriddenTariffsByChosenEmployee(StateContext<ListTariffsState, ListTariffsEvent> context) {
        var chatId = CommonUtils.currentChatId(context);
        var employeeOrdinalNumber = CommonUtils.getContextVar(context, Long.class, ContextVarKey.EMPLOYEE_ORDINAL);
        sendBotMessageService.sendMessage(chatId, "some tariffs belong some employee by ordinal number: " + employeeOrdinalNumber);
    }

    public void sendTariffsByEmployee(StateContext<ListTariffsState, ListTariffsEvent> context) {

    }

    private void sendCategoryButtonsWithExistedOverriddenTariffs(Long chatId) {
        var categoryButtons = categoryService.getAllWithExistedOverriddenTariffs(false).stream()
                .map(category -> ButtonLabelKey.getByKey(category.getNameKey()))
                .toArray(ButtonLabelKey[]::new);
        SendMessage sendMessage = new SendMessage(chatId.toString(), i18NMessageService.getMessage(chatId, MessageKey.TLT_CHOOSE_OVERRIDDEN_TARIFF_BY_CATEGORY));
        sendBotMessageService.sendMessageWithKeys(sendMessage, i18nButtonService.createInlineMarkup(chatId, MenuButtons.TARIFF_MENU, 2, categoryButtons));
    }

    private void sendOrdinalEmployeeButtonsWithExistedOverriddenTariffs(Long chatId) {
            var employeesWithExistOverriddenTariffs = userRepository.findUsersWithExistOverriddenTariffs().stream()
                    .map(employeeMapper::toDto)
                    .sorted(Comparator.comparing(EmployeeTO::getFullName))
                    .toList();


        employeesWithExistOverriddenTariffs
                .forEach(etm -> etm.setOrdinalNumber(employeesWithExistOverriddenTariffs.indexOf(etm) + 1L));

        var listEmployeesMessage = convertEmployeesToMessage(employeesWithExistOverriddenTariffs);

        SendMessage sendMessage = new SendMessage(chatId.toString(),
                i18NMessageService.getMessage(chatId, MessageKey.TLT_CHOOSE_OVERRIDDEN_TARIFF_BY_EMPLOYEE, listEmployeesMessage));
        sendBotMessageService.sendMessageWithKeys(sendMessage,
                i18nButtonService.createOrdinalButtonsInlineMarkup(chatId, MenuButtons.TARIFF_MENU, employeesWithExistOverriddenTariffs, 10));
    }

    private String convertEmployeesToMessage(List<EmployeeTO> employeesWithExistOverriddenTariffs) {
        var message = "%d. %s";
        return employeesWithExistOverriddenTariffs.stream()
                .map(employee -> message.formatted(employee.getOrdinalNumber(), employee.getFullName()))
                .collect(Collectors.joining("\n"));
    }

    public void resendEmployeeButtons(StateContext<ListTariffsState, ListTariffsEvent> context) {

    }

//    var employeeTariffMessageTOs = employeeTariffRepository.getEmployeeTariffWithExistedOverriddenTariffs(false).stream()
//            .map(employeeTariffMessageMapper::toDto)
//            .sorted(Comparator.comparing(EmployeeTariffMessageTO::getFullName))
//            .toList();
}
