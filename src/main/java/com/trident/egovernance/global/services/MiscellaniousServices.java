package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.BasicFeeBatchDetails;
import com.trident.egovernance.dto.MoneyDTO;
import com.trident.egovernance.dto.StudentRequiredFieldsDTO;
import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.global.entities.permanentDB.Fees;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface MiscellaniousServices {
    UserJobInformationDto getUserJobInformation();
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
}
