package com.telegram.reporting.dialogs.guards;

import org.springframework.statemachine.StateContext;

public interface GuardValidator {
    <S, E> boolean validateDate(StateContext<S, E> context);

    <S, E> boolean validateMonthDate(StateContext<S, E> context);

    <S, E> boolean validateTime(StateContext<S, E> context);

    <S, E> boolean validateNote(StateContext<S, E> context);

    <S, E> boolean validatePhoneInput(StateContext<S, E> context);
}
