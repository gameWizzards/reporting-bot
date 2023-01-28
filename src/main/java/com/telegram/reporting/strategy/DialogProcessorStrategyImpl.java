package com.telegram.reporting.strategy;

import com.telegram.reporting.dialogs.DialogProcessor;
import com.telegram.reporting.i18n.ButtonLabelKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DialogProcessorStrategyImpl implements DialogProcessorStrategy {
    private final Map<ButtonLabelKey, DialogProcessor> dialogProcessors;

    public DialogProcessorStrategyImpl(List<DialogProcessor> handlers) {
        dialogProcessors = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(DialogProcessor::startDialogButtonKey, Function.identity()));
    }

    @Override
    public DialogProcessor getProcessor(ButtonLabelKey startDialogButtonKey) {
        if (!dialogProcessors.containsKey(startDialogButtonKey)) {
            // TODO change to relevant exception and add aspect handling to send info to chat
            throw new IllegalArgumentException("Can't find mapping of button to dialog processor. Button=" + startDialogButtonKey);
        }
        return dialogProcessors.get(startDialogButtonKey);
    }
}
