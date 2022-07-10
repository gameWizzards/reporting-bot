package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.ButtonValue;

public interface SubDialogHandler {
    boolean belongToSubDialogStarter(ButtonValue buttonValue);
}
