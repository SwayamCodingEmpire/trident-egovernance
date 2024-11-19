package com.trident.egovernance.global.services;

import java.sql.Date;
import java.util.List;

public interface DateConverterServices {
    List<String> getLastNumberOfDays(int days);
    List<String> getDatesFromStartOfWeekTillToday();
    List<String> getDatesFromStartOfMonthTillToday();
    Date convertFromStringToDate(String input);
    Date[] convertToDates(String inputs);
    int checkFormat(String input);

}
