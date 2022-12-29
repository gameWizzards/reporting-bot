package com.telegram.reporting.dialogs;

import java.util.List;
//TODO change impl to auto setting (store buttons in handlers,
// create method with List<Integer> where every element provide number of buttons in row)
public final class RootMenuStructure {
    public final static List<List<ButtonLabelKey>> GENERAL_ROOT_MENU_STRUCTURE = List.of(
            List.of(ButtonLabelKey.GCR_START_DIALOG),
            List.of(ButtonLabelKey.GER_START_DIALOG, ButtonLabelKey.GDR_START_DIALOG),
            List.of(ButtonLabelKey.GS_START_DIALOG),
            List.of(ButtonLabelKey.GL_START_DIALOG)
    );

    public final static List<List<ButtonLabelKey>> MANAGER_ROOT_MENU_STRUCTURE = List.of(
            List.of(ButtonLabelKey.MSD_MANAGER_START_SUB_DIALOG)
    );
    public final static List<List<ButtonLabelKey>> ADMIN_ROOT_MENU_STRUCTURE = List.of(
            List.of(ButtonLabelKey.ASD_ADMIN_START_SUB_DIALOG)
    );

    private RootMenuStructure() {
    }
}
