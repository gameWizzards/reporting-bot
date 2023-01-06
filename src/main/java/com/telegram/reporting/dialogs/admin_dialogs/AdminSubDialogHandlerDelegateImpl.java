package com.telegram.reporting.dialogs.admin_dialogs;

import com.telegram.reporting.dialogs.AbstractSubDialogHandlerDelegate;
import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.strategy.DialogProcessorStrategy;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.MenuStructureService;
import com.telegram.reporting.service.SendBotMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminSubDialogHandlerDelegateImpl extends AbstractSubDialogHandlerDelegate {

    public AdminSubDialogHandlerDelegateImpl(SendBotMessageService sendBotMessageService,
                                             I18nMessageService i18nMessageService,
                                             I18nButtonService i18nButtonService,
                                             DialogProcessorStrategy processorStrategy,
                                             MenuStructureService menuStructureService) {
        super(sendBotMessageService, i18nMessageService, i18nButtonService, processorStrategy, menuStructureService);
    }

    @Override
    public DialogHandlerAlias dialogHandlerAlias() {
        return DialogHandlerAlias.ADMIN_DIALOGS;
    }
}
