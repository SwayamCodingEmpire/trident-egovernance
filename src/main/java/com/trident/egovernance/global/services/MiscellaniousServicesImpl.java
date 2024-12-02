package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.BasicFeeBatchDetails;
import com.trident.egovernance.dto.StudentRequiredFieldsDTO;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.helpers.TFWType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MiscellaniousServicesImpl implements MiscellaniousServices {
    private final Logger logger = LoggerFactory.getLogger(MiscellaniousServicesImpl.class);
    public String getBatchId(BasicFeeBatchDetails basicFeeBatchDetails) {
        StringBuilder batchId = new StringBuilder();
        batchId.append(basicFeeBatchDetails.course().getEnumName());
        batchId.append(basicFeeBatchDetails.admYear());
        batchId.append(basicFeeBatchDetails.branchCode());
        batchId.append(basicFeeBatchDetails.studentType().getEnumName());
        return batchId.toString();
    }
    private static final Pattern DATE_PATTERN = Pattern.compile("^(\\d{2}-\\d{2}-\\d{4})(?:_(\\d{2}-\\d{2}-\\d{4}))?$|^\\d{4}-\\d{4}$");
    @Override
    public List<String> getLastNumberOfDays(int days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return IntStream.range(0, days)
                .mapToObj(i -> LocalDate.now().minusDays(i))
                .map(date -> date.format(formatter))
                .collect(Collectors.toList());
    }
    @Override
    public List<String> getDatesFromStartOfWeekTillToday() {
        LocalDate today = LocalDate.now();
        // Get the first day of current week (Monday by default)
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return startOfWeek.datesUntil(today.plusDays(1))
                .map(date -> date.format(formatter))
                .collect(Collectors.toList());
    }
    @Override
    public List<String> getDatesFromStartOfMonthTillToday() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return startOfMonth.datesUntil(today.plusDays(1))
                .map(date -> date.format(formatter))
                .collect(Collectors.toList());
    }
    @Override
    public Date convertFromStringToDate(String input){
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");

            // Parse startDate and endDate in dd-MM-yyyy format
            java.util.Date startUtilDate = inputFormat.parse(input);
            return new Date(startUtilDate.getTime());
        }catch (ParseException e){
            throw new InvalidInputsException("Invalid Inputs format");
        }
    }
    @Override
    public Date[] convertToDates(String inputs){
        String[] parts = inputs.split("_");
        // Now you can use Date.valueOf() with the yyyy-MM-dd formatted strings
        return new Date[]{convertFromStringToDate(parts[0]), convertFromStringToDate(parts[1])};

    }
    @Override
    public int checkFormat(String input) {
        if (input == null || !DATE_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        if (input.contains("_")) {
            String[] parts = input.split("_");
            logger.info(Arrays.toString(parts));
            logger.info(parts[0]);
            logger.info(parts[1]);
            Date startDate = convertFromStringToDate(parts[0]);
            Date endDate = convertFromStringToDate(parts[1]);
            if (startDate.after(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            } else if (startDate.equals(endDate)) {
                throw new IllegalArgumentException("Start date cannot be equals to end date");
            }
            else{
                return 3;
            }
        }
        else if (input.contains("-") && input.indexOf("-") != input.lastIndexOf("-")) {
            // dd-MM-yyyy format validation
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            dateFormat.setLenient(false); // Strict parsing
            try {
                dateFormat.parse(input);
                return 1;
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format");
            }
        }
        else {
            // yyyy-yyyy format validation
            String[] parts = input.split("-");
            int startYear = Integer.parseInt(parts[0]);
            int endYear = Integer.parseInt(parts[1]);
            if (endYear == startYear + 1) {
                return 2;
            } else {
                throw new InvalidInputsException("Invalid Session Format");
            }
        }
    }

    @Override
    public boolean isCompulsoryFee(Fees fee, Boolean plPool, Boolean indusTraining) {
        return FeeTypesType.COMPULSORY_FEES.equals(FeeTypesType.fromDisplayName(fee.getFeeType().getType().getDisplayName())) &&
                ((fee.getDescription().equals("INDUSTRY-READY TRAINING FEE") && indusTraining) ||
                        (fee.getDescription().equals("PRE PLACEMENT TRAINING FEE") && plPool) ||
                        (!fee.getDescription().equals("INDUSTRY-READY TRAINING FEE") && !fee.getDescription().equals("PRE PLACEMENT TRAINING FEE")));
    }

    @Override
    public boolean isRelevantFee(Fees fee, StudentRequiredFieldsDTO student, Boolean plPool, Boolean indusTraining) {
        return (fee.getTfwType().equals(student.tfw()) || fee.getTfwType().equals(TFWType.ALL)) &&
                (isCompulsoryFee(fee, plPool, indusTraining) ||
                        (BooleanString.YES.equals(student.transportOpted()) && "TRANSPORTFEE".equals(fee.getFeeType().getFeeGroup())) ||
                        (BooleanString.YES.equals(student.hostelOption()) && "HOSTELFEE".equals(fee.getFeeType().getFeeGroup())));
    }
}
