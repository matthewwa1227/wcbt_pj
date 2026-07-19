package com.casualapp.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class JobDateFormatter {

    private static final Locale DISPLAY_LOCALE =
            Locale.forLanguageTag("zh-HK");

    private JobDateFormatter() {
        // Utility class
    }

    public static String formatMonth(String rawJobDate) {
        Date date = parseDate(rawJobDate);

        if (date == null) {
            return "--";
        }

        return new SimpleDateFormat(
                "M月",
                DISPLAY_LOCALE
        ).format(date);
    }

    public static String formatDay(String rawJobDate) {
        Date date = parseDate(rawJobDate);

        if (date == null) {
            return "--";
        }

        return new SimpleDateFormat(
                "d",
                DISPLAY_LOCALE
        ).format(date);
    }

    public static String formatFullDate(String rawJobDate) {
        Date date = parseDate(rawJobDate);

        if (date == null) {
            return "日期待定";
        }

        return new SimpleDateFormat(
                "yyyy年M月d日 (EEEE)",
                DISPLAY_LOCALE
        ).format(date);
    }

    public static String formatStartTime(String rawJobDate) {
        if (rawJobDate == null || rawJobDate.length() < 16) {
            return "時間待定";
        }

        try {
            return rawJobDate.substring(11, 16) + " 開始";
        } catch (IndexOutOfBoundsException exception) {
            return "時間待定";
        }
    }

    private static Date parseDate(String rawJobDate) {
        if (rawJobDate == null || rawJobDate.length() < 10) {
            return null;
        }

        try {
            String datePart = rawJobDate.substring(0, 10);

            SimpleDateFormat parser = new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
            );

            parser.setLenient(false);

            return parser.parse(datePart);

        } catch (Exception exception) {
            return null;
        }
    }
}