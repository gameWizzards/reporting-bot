package com.telegram.reporting.dialogs.general_dialogs;

import com.telegram.reporting.dialogs.ButtonLabelKey;
import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.dialogs.RootMenuStructure;
import com.telegram.reporting.dialogs.StateMachineHandler;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.service.I18nButtonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class GeneralDialogHandlerImpl implements DialogHandler {

    private final Map<Long, StateMachineHandler> stateMachineHandlers = new ConcurrentHashMap<>();

    private final StateMachineHandler createReportHandler;
    private final StateMachineHandler deleteReportHandler;
    private final StateMachineHandler editReportHandler;
    private final StateMachineHandler statisticHandler;
    private final StateMachineHandler languageHandler;

    private final I18nButtonService i18nButtonService;


    public GeneralDialogHandlerImpl(@Qualifier("CreateReportStateMachineHandler") StateMachineHandler createReportHandler,
                                    @Qualifier("DeleteReportStateMachineHandler") StateMachineHandler deleteReportHandler,
                                    @Qualifier("EditReportStateMachineHandler") StateMachineHandler editReportHandler,
                                    @Qualifier("StatisticStateMachineHandler") StateMachineHandler statisticHandler,
                                    @Qualifier("LanguageStateMachineHandler") StateMachineHandler languageHandler,
                                    I18nButtonService i18nButtonService, ApplicationEventPublisher eventPublisher) {
        this.createReportHandler = createReportHandler;
        this.deleteReportHandler = deleteReportHandler;
        this.editReportHandler = editReportHandler;
        this.statisticHandler = statisticHandler;
        this.languageHandler = languageHandler;
        this.i18nButtonService = i18nButtonService;
    }

    @Override
    public void handleInlineButtonInput(Long chatId, ButtonLabelKey buttonLabelKey) {
        stateMachineHandlers.get(chatId).handleButtonClick(chatId, buttonLabelKey);
    }

    @Override
    public void handleTelegramUserInput(Long chatId, String input) {
        stateMachineHandlers.get(chatId).handleUserInput(chatId, input);
    }

    @Override
    public void createStateMachineHandler(Long chatId, ButtonLabelKey buttonLabelKey) {
        StateMachineHandler handler = switch (buttonLabelKey) {
            case GCR_START_DIALOG -> createReportHandler.initStateMachine(chatId);
            case GDR_START_DIALOG -> deleteReportHandler.initStateMachine(chatId);
            case GER_START_DIALOG -> editReportHandler.initStateMachine(chatId);
            case GS_START_DIALOG -> statisticHandler.initStateMachine(chatId);
            case GL_START_DIALOG -> languageHandler.initStateMachine(chatId);
            default ->
                    throw new IllegalArgumentException("Can't find mapping of button to stateMachine handler. Button=" + buttonLabelKey);
        };
        stateMachineHandlers.put(chatId, handler);
    }

    @Override
    public List<List<ButtonLabelKey>> getRootMenuButtons() {
        return RootMenuStructure.GENERAL_ROOT_MENU_STRUCTURE;
    }

    @Override
    public void removeStateMachineHandler(Long chatId) {
        if (Objects.nonNull(stateMachineHandlers.get(chatId))) {
            stateMachineHandlers.remove(chatId).removeDialogData(chatId);
        }
    }

    @Override
    public boolean belongToDialogStarter(ButtonLabelKey buttonLabelKey) {
        return RootMenuStructure.GENERAL_ROOT_MENU_STRUCTURE.stream()
                .flatMap(Collection::stream)
                .anyMatch(buttonLabelKey::equals);
    }

    @Override
    public List<Role> roleAccessibility() {
        return Arrays.asList(Role.values());
    }

    @Override
    public Integer displayOrder() {
        return 1;
    }
}
