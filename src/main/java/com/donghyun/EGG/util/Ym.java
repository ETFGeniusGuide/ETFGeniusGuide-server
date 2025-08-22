package com.donghyun.EGG.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public final class Ym {
    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");
    private Ym(){}

    /** "YYYY-MM" → 그 달의 1일(LocalDate) */
    public static LocalDate firstDay(String yearMonth) {
        return YearMonth.parse(yearMonth, YM).atDay(1);
    }

    /** "YYYY-MM-DD" → LocalDate */
    public static LocalDate parseDate(String yyyyMmDd) {
        return LocalDate.parse(yyyyMmDd);
    }

    /** date가 yearMonth(1일) 범위 내인지 체크 */
    public static boolean inSameMonth(LocalDate yearMonthFirstDay, LocalDate date) {
        LocalDate start = yearMonthFirstDay;
        LocalDate endExcl = yearMonthFirstDay.plusMonths(1);
        return !date.isBefore(start) && date.isBefore(endExcl);
    }
}