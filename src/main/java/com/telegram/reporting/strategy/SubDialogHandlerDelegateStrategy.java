package com.telegram.reporting.strategy;

import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.dialogs.SubDialogHandlerDelegate;

public interface SubDialogHandlerDelegateStrategy {
    SubDialogHandlerDelegate getDelegate(DialogHandlerAlias alias);
}
