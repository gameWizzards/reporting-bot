package com.telegram.reporting.service;

import org.springframework.statemachine.StateContext;

public interface GeneralActionService {
    <S, E> void generalRequestInputDate(StateContext<S, E> context);

    <S, E> void sendListTimeRecords(StateContext<S, E> context);

    <S, E> void handleChoiceTimeRecord(StateContext<S, E> context);

    <S, E> void handleUserDateInput(StateContext<S, E> context);

    <S, E> void handleUserTimeInput(StateContext<S, E> context);

    <S, E> void handleCategory(StateContext<S, E> context);

    <S, E> void handleUserNoteInput(StateContext<S, E> context);

    <S, E> void sendRootMenuButtons(StateContext<S, E> context);

}
