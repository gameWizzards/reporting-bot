package com.telegram.reporting.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Month {
    private final static Map<Integer, String> months = new ConcurrentHashMap<>(12);

    static {
        months.put(1, "ЯНВАРЬ");
        months.put(2, "ФЕВРАЛЬ");
        months.put(3, "МАРТ");
        months.put(4, "АПРЕЛЬ");
        months.put(5, "МАЙ");
        months.put(6, "ИЮНЬ");
        months.put(7, "ИЮЛЬ");
        months.put(8, "АВГУСТ");
        months.put(9, "СЕНТЯБРЬ");
        months.put(10, "ОКТЯБРЬ");
        months.put(11, "НОЯБРЬ");
        months.put(12, "ДЕКАБРЬ");
    }

    private Month() {
    }

    public static String  getNameByOrdinal(int ordinalNumb) {
        if (ordinalNumb < 1 || ordinalNumb > 12) {
            throw new IllegalArgumentException("Can't find month to request. Argument out of months range = %d".formatted(ordinalNumb));
        }
        return months.get(ordinalNumb);
    }
}
