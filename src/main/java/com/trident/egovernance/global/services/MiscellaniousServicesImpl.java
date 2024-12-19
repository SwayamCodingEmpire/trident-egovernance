package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.helpers.TFWType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.allegro.finance.tradukisto.MoneyConverters;
import pl.allegro.finance.tradukisto.ValueConverters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MiscellaniousServicesImpl implements MiscellaniousServices {
    private final MenuBladeFetcherService menuBladeFetcherService;
    private final UserDataFetcherFromMS userDataFetcherFromMS;
    private final Logger logger = LoggerFactory.getLogger(MiscellaniousServicesImpl.class);

    public MiscellaniousServicesImpl(MenuBladeFetcherService menuBladeFetcherService, UserDataFetcherFromMS userDataFetcherFromMS) {
        this.menuBladeFetcherService = menuBladeFetcherService;
        this.userDataFetcherFromMS = userDataFetcherFromMS;
    }

    public String generateBatchId(BasicFeeBatchDetails basicFeeBatchDetails) {
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

    @Override
    public String getMoneyIntoWords(BigDecimal input) {
        MoneyConverters converter = MoneyConverters.ENGLISH_BANKING_MONEY_VALUE;
        return converter.asWords(input);
    }
    @Override
    public MoneyDTO convertMoneyToWords(BigDecimal input) {
        var converters = ValueConverters.ENGLISH_INTEGER;
        String amountInWords = new String();
        if(input.signum() < 0) {
            BigDecimal absTatAmount = input.abs();
            BigDecimal integralPart = absTatAmount.setScale(0, RoundingMode.DOWN);
            BigDecimal fractionalPart = absTatAmount.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
            String rupees = converters.asWords(integralPart.intValue());
            String paise = converters.asWords(fractionalPart.intValue());
            if(paise.compareTo("zero")==0){
                amountInWords = "Excess Amount Paid of " + "Rupees " + rupees + " Only";
            }
            else{
                amountInWords = "Excess Amount Paid of " + "Rupees " + rupees + " Paise " + paise + " Only";
            }

        }
        else{
            BigDecimal integralPart = input.setScale(0, RoundingMode.DOWN);
            BigDecimal fractionalPart = input.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
            String rupees = converters.asWords(integralPart.intValue());
            String paise = converters.asWords(fractionalPart.intValue());
            if(paise.compareTo("zero")==0){
                amountInWords = "Rupees " + rupees + " Only";
            }
            else{
                amountInWords = "Rupees " + rupees + " Paise " + paise + " Only";
            }
        }
        return new MoneyDTO(input,amountInWords);
    }

    public UserJobInformationDto getUserJobInformation() {
        try{
            JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                List<GrantedAuthority> authorities =
                        authentication.getAuthorities().stream().toList();

                if (!authorities.isEmpty() && authorities.size() > 0) {
                    // First authority is jobTitle with "ROLE_" prefix
                    return new UserJobInformationDto(jwt.getClaimAsString("name"), authorities.get(0).getAuthority().substring(5), authorities.get(1).getAuthority(), authorities.get(2).getAuthority());
                }
            }
            return null;
        }catch (ClassCastException e){
            throw new InvalidInputsException("Invalid Authentication Token");
        }
    }

    public RoleDetails getMenuItems() {
        UserJobInformationDto userJobInformationDto = getUserJobInformation();
        NavigationMenu navigationMenu = menuBladeFetcherService.getNavigationMenu();
        String role = userJobInformationDto.jobTitle();

        logger.info(navigationMenu.toString());
        return switch (role) {
            case "STUDENT" -> navigationMenu.menus().get("STUDENT");
            case "OFFICE" -> navigationMenu.menus().get("OFFICE");
            case "ACCOUNTS" -> navigationMenu.menus().get("ACCOUNTS");
            default -> throw new InvalidInputsException("Invalid role: " + role);
        };
    }
}
