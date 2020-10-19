package rbi.codingtest.utils;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {
    public static Date getLastWeek(Date date) {
        GregorianCalendar dayBeforeThisWeek = new GregorianCalendar();
        dayBeforeThisWeek.setGregorianChange(date);
        int dayFromMonday = (dayBeforeThisWeek.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7;
        dayBeforeThisWeek.add(Calendar.DATE, -dayFromMonday-1);
        return dayBeforeThisWeek.getTime();
    }

    public static int getDayOfTheWeek(Date date){
        LocalDate localDate = LocalDate.parse(date.toString());
        return  localDate.getDayOfWeek().getValue();
    }
}
