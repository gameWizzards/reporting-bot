package com.telegram.reporting.strategy;

import com.telegram.reporting.dialogs.DialogProcessor;
import com.telegram.reporting.i18n.ButtonLabelKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class DialogProcessorStrategy {
    private final Map<ButtonLabelKey, DialogProcessor> dialogProcessors;

    public DialogProcessorStrategy(List<DialogProcessor> handlers) {
        dialogProcessors = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(DialogProcessor::startDialogButtonKey, Function.identity()));
    }

    public DialogProcessor getProcessor(ButtonLabelKey startDialogButtonKey) {
        if (!dialogProcessors.containsKey(startDialogButtonKey)) {
            throw new IllegalArgumentException("Can't find mapping of button to dialog processor. Button=" + startDialogButtonKey);
        }
        return dialogProcessors.get(startDialogButtonKey);
    }

    public Stream<DialogProcessor> allProcessors() {
        return dialogProcessors.values().stream();
    }
}
