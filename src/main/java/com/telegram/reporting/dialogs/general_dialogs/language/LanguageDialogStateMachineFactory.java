package com.telegram.reporting.dialogs.general_dialogs.language;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "LanguageDialogStateMachineFactory")
public class LanguageDialogStateMachineFactory extends EnumStateMachineConfigurerAdapter<LanguageState, LanguageEvent> {
    private final LanguageActions languageActions;

    public LanguageDialogStateMachineFactory(@Lazy LanguageActions languageActions) {
        this.languageActions = languageActions;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<LanguageState, LanguageEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<LanguageState, LanguageEvent> states) throws Exception {
        states.withStates()
                .initial(LanguageState.START_LANGUAGE_DIALOG)
                .end(LanguageState.END_LANGUAGE_DIALOG)
                .states(EnumSet.allOf(LanguageState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<LanguageState, LanguageEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(LanguageState.START_LANGUAGE_DIALOG)
                .event(LanguageEvent.RUN_LANGUAGE_DIALOG)
                .target(LanguageState.USER_LANGUAGE_CHOOSING)
                .action(languageActions::sendListLanguages)

                .and().withExternal()
                .source(LanguageState.USER_LANGUAGE_CHOOSING)
                .event(LanguageEvent.HANDLE_USER_CHANGE_LOCALE)
                .target(LanguageState.END_LANGUAGE_DIALOG)
                .action(languageActions::handleUserLanguageChoice);
    }
}
