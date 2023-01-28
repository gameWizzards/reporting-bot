package com.telegram.reporting.dialogs.general_dialogs;

import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.dialogs.DialogProcessor;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.service.MenuTemplateService;
import com.telegram.reporting.strategy.DialogProcessorStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeneralDialogHandlerImpl implements DialogHandler {

    private final Map<Long, DialogProcessor> activeDialogProcessors = new ConcurrentHashMap<>();

    private final DialogProcessorStrategy processorStrategy;
    private final MenuTemplateService menuTemplateService;

    @Override
    public void handleInlineButtonInput(Long chatId, ButtonLabelKey buttonLabelKey) {
        activeDialogProcessors.get(chatId).handleButtonClick(chatId, buttonLabelKey);
    }

    @Override
    public void handleTelegramUserInput(Long chatId, String input) {
        activeDialogProcessors.get(chatId).handleUserInput(chatId, input);
    }

    @Override
    public void createDialogProcessor(Long chatId, ButtonLabelKey buttonLabelKey) {
        DialogProcessor handler = processorStrategy.getProcessor(buttonLabelKey).initDialogProcessor(chatId);
        activeDialogProcessors.put(chatId, handler);
    }

    @Override
    public List<List<ButtonLabelKey>> getRootMenuTemplate() {
        return menuTemplateService.rootMenuTemplate(dialogHandlerAlias());
    }

    @Override
    public void removeDialogProcessor(Long chatId) {
        if (Objects.nonNull(activeDialogProcessors.get(chatId))) {
            activeDialogProcessors.remove(chatId).removeDialogData(chatId);
        }
    }

    @Override
    public List<Role> roleAccessibility() {
        return Arrays.asList(Role.values());
    }

    @Override
    public Integer displayOrder() {
        return 1;
    }

    @Override
    public DialogHandlerAlias dialogHandlerAlias() {
        return DialogHandlerAlias.GENERAL_DIALOGS;
    }
}
