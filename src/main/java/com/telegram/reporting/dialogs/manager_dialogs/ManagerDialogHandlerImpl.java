package com.telegram.reporting.dialogs.manager_dialogs;

import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.dialogs.SubDialogHandlerDelegate;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.strategy.SubDialogHandlerDelegateStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManagerDialogHandlerImpl implements DialogHandler {

    private final SubDialogHandlerDelegateStrategy subDialogHandlerDelegateStrategy;

    private SubDialogHandlerDelegate subDialogDelegate;

    @PostConstruct
    public void configure() {
        this.subDialogDelegate = subDialogHandlerDelegateStrategy.getDelegate(dialogHandlerAlias());
    }

    @Override
    public void handleInlineButtonInput(Long chatId, ButtonLabelKey buttonLabelKey) {

        // return to admin submenu when click 'manager menu' button
        if (ButtonLabelKey.COMMON_RETURN_MANAGER_MENU.equals(buttonLabelKey)) {
            subDialogDelegate.startSubDialogFlow(chatId);
            return;
        }

        if (!subDialogDelegate.isProcessorCreated(chatId) && subDialogDelegate.belongToSubMenuButtons(buttonLabelKey)) {
            subDialogDelegate.createDialogProcessor(chatId, buttonLabelKey);
        }

        if (subDialogDelegate.isProcessorCreated(chatId)) {
            subDialogDelegate.handleInlineButtonInput(chatId, buttonLabelKey);
        }
    }

    @Override
    public void handleTelegramUserInput(Long chatId, String input) {
        if (subDialogDelegate.isProcessorCreated(chatId)) {
            subDialogDelegate.handleTelegramUserInput(chatId, input);
        }
    }

    @Override
    public void createDialogProcessor(Long chatId, ButtonLabelKey buttonLabelKey) {
        subDialogDelegate.startSubDialogFlow(chatId);
    }

    @Override
    public List<List<ButtonLabelKey>> getRootMenuTemplate() {
        return subDialogDelegate.getRootMenuTemplate();
    }

    @Override
    public void removeDialogProcessor(Long chatId) {
        subDialogDelegate.removeDialogProcessor(chatId);
    }


    @Override
    public List<Role> roleAccessibility() {
        return List.of(Role.MANAGER_ROLE, Role.ADMIN_ROLE);
    }

    @Override
    public Integer displayOrder() {
        return 2;
    }

    @Override
    public DialogHandlerAlias dialogHandlerAlias() {
        return DialogHandlerAlias.MANAGER_DIALOGS;
    }
}
