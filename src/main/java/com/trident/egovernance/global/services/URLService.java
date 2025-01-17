package com.trident.egovernance.global.services;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;

@Service
public class URLService {
    private final MoneyReceiptTokenGeneratorService tokenGeneratorService;
    @Value("${frontend.ip}")
    private String frontEndIP;

    public URLService(MoneyReceiptTokenGeneratorService tokenGeneratorService) {
        this.tokenGeneratorService = tokenGeneratorService;
    }

    public String generateUrl(long number, String paymentReceiver) throws UnsupportedEncodingException {
        // Generate the JWT token
        String token = tokenGeneratorService.generateToken(number, paymentReceiver);

        // URL encode the JWT token to make it URL-safe
        String encodedToken = URLEncoder.encode(token, "UTF-8");

//        String ipAddress = "172.16.9.202";
//        String port = ":3000";
        // Return the URL with the encoded token as a query parameter
        return frontEndIP + "/verifymr?key=" + encodedToken;

    }

    private String getLocalIpAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "localhost"; // Fallback to localhost if IP cannot be determined
        }
    }

    // Method to extract the long number from the token in the URL
    public Pair<Long, String> getNumberFromUrl(String token){
        // URL decode the token
        String decodedToken;
        try {
            decodedToken = java.net.URLDecoder.decode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // Extract and return the original long number from the decoded token
        long mrNo =  tokenGeneratorService.extractNumber(decodedToken);
        String paymentReceiver = tokenGeneratorService.extractPaymentReceiver(decodedToken);
        return Pair.of(mrNo, paymentReceiver);
    }
}
