package com.telegram.reporting.service;

import org.springframework.statemachine.StateContext;

public interface GuardService {
    <S, E> boolean validateDate(StateContext<S, E> context);

    <S, E> boolean validateTime(StateContext<S, E> context);

    <S, E> boolean validateNote(StateContext<S, E> context);
}
