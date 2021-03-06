package org.sayem.webdriver.selenium;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by sayem on 1/22/16.
 */
public class DatePicker {

    public String todayDate() {
        LocalDate date = LocalDate.now();
        return date.format(DateTimeFormatter.ofPattern("M/d/yyyy"));
    }

    public String addNumberOfDayToDate(int days) {
        LocalDate date = LocalDate.now().plusDays(days);
        return date.format(DateTimeFormatter.ofPattern("M/d/yyyy"));
    }

    public String addOneWeekToDate() {
        LocalDate date = LocalDate.now().plusWeeks(1);
        return date.format(DateTimeFormatter.ofPattern("M/d/yyyy"));
    }

    public String addOneMonthToDate() {
        LocalDate date = LocalDate.now().plusMonths(1);
        return date.format(DateTimeFormatter.ofPattern("M/d/yyyy"));
    }

    public String addNumberOfWeekDay(int workdays) {
        LocalDate result = LocalDate.now();
        int addedDays = 0;
        while (addedDays < workdays) {
            result = result.plusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++addedDays;
            }
        }
        return result.format(DateTimeFormatter.ofPattern("M/d/yyyy"));
    }

    public String addNumberOfWeekDayInReverse(int workdays) {
        LocalDate result = LocalDate.now();
        int addedDays = 0;
        while (addedDays < workdays) {
            result = result.plusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++addedDays;
            }
        }
        return result.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}