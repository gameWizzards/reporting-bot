package com.telegram.reporting.dialogs;

import java.util.List;

public final class SubMenuStructure {

    public final static List<List<ButtonLabelKey>> ADMIN_SUB_MENU_STRUCTURE = List.of(
            List.of(ButtonLabelKey.ALU_START_DIALOG)
    );

    public final static List<List<ButtonLabelKey>> MANAGER_SUB_MENU_STRUCTURE = List.of(
            List.of(ButtonLabelKey.MES_START_DIALOG),
            List.of(ButtonLabelKey.MAE_START_DIALOG, ButtonLabelKey.MESTATUS_START_DIALOG)
    );

    private SubMenuStructure() {
    }
}
