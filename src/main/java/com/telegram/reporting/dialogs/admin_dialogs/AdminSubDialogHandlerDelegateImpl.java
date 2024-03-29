package com.telegram.reporting.dialogs.admin_dialogs;

import com.telegram.reporting.dialogs.AbstractSubDialogHandlerDelegate;
import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.MenuTemplateService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.strategy.DialogProcessorStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminSubDialogHandlerDelegateImpl extends AbstractSubDialogHandlerDelegate {

    public AdminSubDialogHandlerDelegateImpl(SendBotMessageService sendBotMessageService,
                                             I18nMessageService i18nMessageService,
                                             @Lazy I18nButtonService i18nButtonService,
                                             DialogProcessorStrategy processorStrategy,
                                             MenuTemplateService menuTemplateService) {
        super(sendBotMessageService, i18nMessageService, i18nButtonService, processorStrategy, menuTemplateService);
    }

    @Override
    public DialogHandlerAlias dialogHandlerAlias() {
        return DialogHandlerAlias.ADMIN_DIALOGS;
    }

    @Override
    public MessageKey getStartFlowMessageKey() {
        return MessageKey.ASD_START_SUB_DIALOG_FLOW;
    }
}
