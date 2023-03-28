package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.service.MenuTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DefaultMenuTemplateServiceImpl implements MenuTemplateService {
    private final Map<DialogHandlerAlias, List<List<ButtonLabelKey>>> rootMenuStructure = new HashMap<>(3);
    private final Map<DialogHandlerAlias, List<List<ButtonLabelKey>>> subMenuStructure = new HashMap<>(2);

    @PostConstruct
    public void configureMenuStructures() {
        rootMenuStructure.put(DialogHandlerAlias.ADMIN_DIALOGS, adminRootMenuStructure());
        rootMenuStructure.put(DialogHandlerAlias.GENERAL_DIALOGS, generalRootMenuStructure());
        rootMenuStructure.put(DialogHandlerAlias.MANAGER_DIALOGS, managerRootMenuStructure());

        subMenuStructure.put(DialogHandlerAlias.ADMIN_DIALOGS, adminSubMenuStructure());
        subMenuStructure.put(DialogHandlerAlias.MANAGER_DIALOGS, managerSubMenuStructure());
    }

    @Override
    public List<List<ButtonLabelKey>> rootMenuTemplate(DialogHandlerAlias dialogHandlerAlias) {
        return rootMenuStructure.get(dialogHandlerAlias);
    }

    @Override
    public List<List<ButtonLabelKey>> subMenuTemplate(DialogHandlerAlias dialogHandlerAlias) {
        return subMenuStructure.get(dialogHandlerAlias);
    }

    @Override
    public boolean belongToRootMenu(ButtonLabelKey buttonLabelKey) {
        return rootMenuStructure.values().stream()
                .flatMap(List::stream)
                .anyMatch(buttonLabels -> buttonLabels.contains(buttonLabelKey));
    }

    private List<List<ButtonLabelKey>> generalRootMenuStructure() {
        return List.of(
                List.of(ButtonLabelKey.GCR_START_DIALOG),
                List.of(ButtonLabelKey.GER_START_DIALOG, ButtonLabelKey.GDR_START_DIALOG),
                List.of(ButtonLabelKey.GS_START_DIALOG),
                List.of(ButtonLabelKey.GL_START_DIALOG));
    }

    private List<List<ButtonLabelKey>> adminRootMenuStructure() {
        return List.of(
                List.of(ButtonLabelKey.ASD_ADMIN_START_SUB_DIALOG));
    }

    private List<List<ButtonLabelKey>> managerRootMenuStructure() {
        return List.of(
                List.of(ButtonLabelKey.MSD_MANAGER_START_SUB_DIALOG));
    }

    private List<List<ButtonLabelKey>> adminSubMenuStructure() {
        return List.of(
                List.of(ButtonLabelKey.ALU_START_DIALOG));
    }

    private List<List<ButtonLabelKey>> managerSubMenuStructure() {
        return List.of(
                List.of(ButtonLabelKey.MES_START_DIALOG),
                List.of(ButtonLabelKey.MAE_START_DIALOG, ButtonLabelKey.MESTATUS_START_DIALOG));
    }
}
