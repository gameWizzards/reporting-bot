package com.telegram.reporting.service;

import org.springframework.statemachine.StateContext;

public interface GeneralActionService {
    <S, E> void sendRootMenuButtons(StateContext<S, E> context);

    <S, E> void handleUserDateInput(StateContext<S, E> context);

    <S, E> void handleUserTimeInput(StateContext<S, E> context);

    <S, E> void requestInputNote(StateContext<S, E> context);

    <S, E> void handleUserNoteInput(StateContext<S, E> context);

    <S, E> void prepareTimeRecord(StateContext<S, E> context);

    <S, E> void declinePersistReport(StateContext<S, E> context);
}
