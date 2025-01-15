package com.trident.egovernance.global.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.domains.accountsSectionHandler.services.FeeCollectionTransactionsServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.repositories.permanentDB.BranchRepository;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.FeesRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.services.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public")
public class PublicController {
    private final S3ServiceImpl s3Service;
    private final WebClient webClientGraph;
    private final DuesDetailsRepository duesDetailsRepository;
    private final AppBearerTokenService appBearerTokenService;
    private final AuthenticationServiceImpl authenticationService;
    @PersistenceContext
    private final EntityManager entityManager;
    private final StudentRepository studentRepository;
    private final CustomJwtServiceImpl customJwtService;
    private final Logger logger = LoggerFactory.getLogger(PublicController.class);
    private final FeesRepository feesRepository;
    private final BranchRepository branchRepository;
    private final URLService urlService;
    private final FeeCollectionTransactionsServiceImpl feeCollectionTransactionsServiceImpl;
    private final MicrosoftGraphService microsoftGraphService;

    public PublicController(S3ServiceImpl s3Service, DuesDetailsRepository duesDetailsRepository, AppBearerTokenService appBearerTokenService, AuthenticationServiceImpl authenticationService, EntityManager entityManager, StudentRepository studentRepository, CustomJwtServiceImpl customJwtService,
                            FeesRepository feesRepository, BranchRepository branchRepository, URLService urlService, FeeCollectionTransactionsServiceImpl feeCollectionTransactionsServiceImpl, MicrosoftGraphService microsoftGraphService) {
        this.s3Service = s3Service;
        this.duesDetailsRepository = duesDetailsRepository;
        this.appBearerTokenService = appBearerTokenService;
        this.authenticationService = authenticationService;
        this.entityManager = entityManager;
        this.studentRepository = studentRepository;
        this.customJwtService = customJwtService;
        this.feesRepository = feesRepository;
        this.branchRepository = branchRepository;
        this.urlService = urlService;
        this.feeCollectionTransactionsServiceImpl = feeCollectionTransactionsServiceImpl;
        this.webClientGraph = WebClient.builder()
                .baseUrl("https://graph.microsoft.com/v1.0/users")
                .build();
        this.microsoftGraphService = microsoftGraphService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Login login){
        logger.info(login.toString());
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticationService.authenticate(login);
        logger.info(customUserDetails.toString());
        return ResponseEntity.ok(new LoginResponse(customJwtService.generateToken(customUserDetails), customJwtService.getExpirationTime()));
    }

    @GetMapping("/count")
    public long count(){
        return feesRepository.count();
    }
    @GetMapping("/view")
    public ResponseEntity<List<CurrentSessionDto>> view(){
        String sql = "SELECT SESSIONID " +
                "FROM FEECDEMO.CURRENT_SESSION WHERE REGDNO = :regdNo";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("regdNo","REGD1234");
        return query.getResultList().isEmpty() ? ResponseEntity.ok(null) : ResponseEntity.ok(query.getResultList());
    }

    @GetMapping("/branches")
    public ResponseEntity<List<BranchGroup>> getAllBranches(){
        List<BranchRecord> branches = branchRepository.findAllNow();
        Map<String, List<BranchRecord>> groupedBranches = branches.stream()
                .collect(Collectors.groupingBy(BranchRecord::course));

        // Convert grouped data into BranchGroup DTO
        List<BranchGroup> branchGroups = groupedBranches.entrySet().stream()
                .map(entry -> new BranchGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        logger.info(branchGroups.toString());
        return branchGroups.isEmpty() ? ResponseEntity.ok(null) : ResponseEntity.ok(branchGroups);
    }

    @PostMapping("/profile-test/{regdNo}")
    public void setProfilePicture(@PathVariable("regdNo") String regdNo) {
        String userId = "sweety.dash.csaiml2028@codingEmpire.onmicrosoft.com";
        String appToken = appBearerTokenService.getAppBearerToken("defaultKey");
        try {
            String key = regdNo + "/" + regdNo + "-Passport-Photo";
            logger.info(key);
            byte[] profilePicture = s3Service.getFileAsBytes("nsrdocbucket",key);
            logger.info(profilePicture.toString());
            webClientGraph.put()
                    .uri("/{userId}/photo/$value", userId)
                    .header("Authorization", "Bearer " + appToken)
                    .header("Content-Type", "image/jpeg")
                    .bodyValue(profilePicture)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class).flatMap(body -> {
                        logger.error("Error setting profile picture: {}", body);
                        return Mono.error(new RuntimeException("Failed to set profile picture"));
                    }))
                    .toBodilessEntity()
                    .block();

            logger.info("Profile picture set successfully for user ID: {}", userId);
        } catch (Exception e) {
            logger.error("Error setting profile picture for user ID: {}", userId, e);
            throw new IllegalStateException("Error setting profile picture for user ID: " + userId, e);
        }
    }

    @PostMapping("/resource")
    public ResponseEntity<MoneyReceipt> uploadFile(@RequestBody LoginResponse key) {
        logger.info(key.token());
        HttpHeaders headers = new HttpHeaders();
        Pair<Long,String> decodedValue = urlService.getNumberFromUrl(key.token());
        headers.add("Payment-Receiver", decodedValue.getRight());
        headers.add("Access-Control-Expose-Headers", "Payment-Receiver");
        return ResponseEntity.ok().headers(headers).body(feeCollectionTransactionsServiceImpl.getMoneyReceiptByMrNo(decodedValue.getLeft()));

    }

    @GetMapping("/photo-upload/{regd1}/{regd2}")
    public void uploadPhoto(@PathVariable("regd1") String regd1, @PathVariable("regd2") String regd2) {
        microsoftGraphService.startOperation(regd1, regd2);
    }
//
    @GetMapping("/testOne")
    public void doOne(){
        microsoftGraphService.callServer("210310224669");
    }
}
