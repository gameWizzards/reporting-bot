package com.telegram.reporting.dialogs.tariff_dialogs.list_tarrifs;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "ListTariffsDialogStateMachineFactory")
public class ListTariffsDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<ListTariffsState, ListTariffsEvent> {
    private final ListTariffsActions listTariffsActions;

    public ListTariffsDialogStateMachineFactory(@Lazy ListTariffsActions listTariffsActions) {
        this.listTariffsActions = listTariffsActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ListTariffsState, ListTariffsEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<ListTariffsState, ListTariffsEvent> states) throws Exception {
        states.withStates()
                .initial(ListTariffsState.START_LIST_TARIFFS_DIALOG)
                .end(ListTariffsState.END_LIST_TARIFFS_DIALOG)
                .states(EnumSet.allOf(ListTariffsState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ListTariffsState, ListTariffsEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(ListTariffsState.START_LIST_TARIFFS_DIALOG)
                .event(ListTariffsEvent.LIST_TARIFFS_DIALOG_STARTED)
                .target(ListTariffsState.OVERRIDDEN_TARIFF_DISPLAYING_TYPE_CHOICE)
                .action(listTariffsActions::sendExistedTariffs)
                .action(listTariffsActions::sendOverriddenTariffDisplayingButtons)

                .and().withExternal()
                .source(ListTariffsState.OVERRIDDEN_TARIFF_DISPLAYING_TYPE_CHOICE)
                .event(ListTariffsEvent.DISPLAYING_OVERRIDDEN_TARIFFS_CHOSEN)
                .target(ListTariffsState.TARIFF_OPTION_CHOICE)
                .action(listTariffsActions::handleDisplayingTypeChoice)

                // displaying tariffs by category
                .and().withExternal()
                .source(ListTariffsState.TARIFF_OPTION_CHOICE)
                .event(ListTariffsEvent.DISPLAYING_BY_CATEGORY_CHOSEN)
                .target(ListTariffsState.TARIFF_CATEGORY_CHOICE)
                .action(listTariffsActions::sendOverriddenTariffsByChosenCategory)

                .and().withExternal()
                .source(ListTariffsState.TARIFF_CATEGORY_CHOICE)
                .event(ListTariffsEvent.RESEND_CATEGORY_BUTTONS)
                .target(ListTariffsState.TARIFF_OPTION_CHOICE)
                .action(listTariffsActions::resendCategoryButtons)


                // displaying tariffs by employee
                .and().withExternal()
                .source(ListTariffsState.TARIFF_OPTION_CHOICE)
                .event(ListTariffsEvent.DISPLAYING_BY_EMPLOYEE_CHOSEN)
                .target(ListTariffsState.TARIFF_EMPLOYEE_CHOICE)
                .action(listTariffsActions::sendOverriddenTariffsByChosenEmployee)

                .and().withExternal()
                .source(ListTariffsState.TARIFF_EMPLOYEE_CHOICE)
                .event(ListTariffsEvent.RESEND_EMPLOYEE_BUTTONS)
                .target(ListTariffsState.TARIFF_OPTION_CHOICE)
                .action(listTariffsActions::resendEmployeeButtons)
        ;
    }
}
