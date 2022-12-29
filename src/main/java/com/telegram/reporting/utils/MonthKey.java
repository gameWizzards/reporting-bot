package com.telegram.reporting.utils;

import com.telegram.reporting.dialogs.I18nKey;

import java.util.stream.Stream;

public enum MonthKey implements I18nKey {

    JANUARY(1, "month.january"),
    FEBRUARY(2, "month.february"),
    MARCH(3, "month.march"),
    APRIL(4, "month.april"),
    MAY(5, "month.may"),
    JUNE(6, "month.june"),
    JULY(7, "month.july"),
    AUGUST(8, "month.august"),
    SEPTEMBER(9, "month.september"),
    OCTOBER(10, "month.october"),
    NOVEMBER(11, "month.november"),
    DECEMBER(12, "month.december");

    private final int ordinalNumb;
    private final String key;

    MonthKey(int ordinalNumb, String key) {
        this.ordinalNumb = ordinalNumb;
        this.key = key;
    }

    public static MonthKey getMonthByOrdinal(int ordinalNumb) {
        if (ordinalNumb < 1 || ordinalNumb > 12) {
            throw new IllegalArgumentException("Can't find month to request. Argument out of months range = %d".formatted(ordinalNumb));
        }
        return Stream.of(values())
                .filter(month -> month.ordinalNumb == ordinalNumb)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public String value() {
        return this.key;
    }
}
