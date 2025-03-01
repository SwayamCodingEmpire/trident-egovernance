package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface MiscellaniousServices {
    Pair<UserJobInformationDto, UserIdAndOriginalToken> getUserJobInformation();
    String generateBatchId(BasicFeeBatchDetails basicFeeBatchDetails);
    List<String> getLastNumberOfDays(int days);
    List<String> getDatesFromStartOfWeekTillToday();
    List<String> getDatesFromStartOfMonthTillToday();
    Date convertFromStringToDate(String input);
    Date[] convertToDates(String inputs);
    int checkFormat(String input);
    boolean isCompulsoryFee(Fees fee, Boolean plPool, Boolean indusTraining);
    boolean isRelevantFee(Fees fee, StudentRequiredFieldsDTO student, Boolean plPool, Boolean indusTraining);
    String getMoneyIntoWords(BigDecimal input);
    MoneyDTO convertMoneyToWords(BigDecimal input);
    RoleDetails getMenuItems();
    String incrementYearRange(String yearRange);
}
