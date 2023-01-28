package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.i18n.ButtonLabelKey;

import java.util.List;

public interface MenuTemplateService {
    List<List<ButtonLabelKey>> rootMenuTemplate(DialogHandlerAlias dialogHandlerAlias);

    List<List<ButtonLabelKey>> subMenuTemplate(DialogHandlerAlias dialogHandlerAlias);
}