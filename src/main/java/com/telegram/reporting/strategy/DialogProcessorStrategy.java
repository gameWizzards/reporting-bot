package com.telegram.reporting.strategy;

import com.telegram.reporting.dialogs.DialogProcessor;
import com.telegram.reporting.i18n.ButtonLabelKey;

public interface DialogProcessorStrategy {
    DialogProcessor getProcessor(ButtonLabelKey startDialogButtonKey);
}
