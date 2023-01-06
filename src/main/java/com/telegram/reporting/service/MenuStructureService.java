package com.telegram.reporting.service;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.dialogs.DialogHandlerAlias;

import java.util.List;

public interface MenuStructureService {
    List<List<ButtonLabelKey>> rootMenuButtons(DialogHandlerAlias dialogHandlerAlias);

    List<List<ButtonLabelKey>> subMenuButtons(DialogHandlerAlias dialogHandlerAlias);
}