package com.telegram.reporting.strategy;

import com.telegram.reporting.dialogs.DialogHandlerAlias;
import com.telegram.reporting.dialogs.SubDialogHandlerDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SubDialogHandlerDelegateStrategyImpl implements SubDialogHandlerDelegateStrategy {
    private final Map<DialogHandlerAlias, SubDialogHandlerDelegate> delegates;

    public SubDialogHandlerDelegateStrategyImpl(List<SubDialogHandlerDelegate> handlers) {
        delegates = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(SubDialogHandlerDelegate::dialogHandlerAlias, Function.identity()));
    }

    public SubDialogHandlerDelegate getDelegate(DialogHandlerAlias alias) {
        if (!delegates.containsKey(alias)) {
            throw new IllegalArgumentException("Can't find appropriate subDialogHandlerDelegate by alias. Alias=" + alias);
        }
        return delegates.get(alias);
    }

}
