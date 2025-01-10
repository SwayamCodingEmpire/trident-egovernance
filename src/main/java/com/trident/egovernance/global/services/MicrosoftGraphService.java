package com.trident.egovernance.global.services;


import com.trident.egovernance.dto.BasicMSUserDto;
import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.naming.ldap.PagedResultsControl;
import java.nio.file.Paths;
import java.util.List;

@Service
public class MicrosoftGraphService {
    private final Logger logger = LoggerFactory.getLogger(MicrosoftGraphService.class);
    private final WebClient webClientGraph;
    private final StudentRepository studentRepository;
    private String token = "eyJ0eXAiOiJKV1QiLCJub25jZSI6ImVMNVRPOHkxQXFyVXk0amlhekVRdDhUYTBoX28zNDZNS2I4bWhpMzl3S28iLCJhbGciOiJSUzI1NiIsIng1dCI6InoxcnNZSEhKOS04bWdndDRIc1p1OEJLa0JQdyIsImtpZCI6InoxcnNZSEhKOS04bWdndDRIc1p1OEJLa0JQdyJ9.eyJhdWQiOiJodHRwczovL2dyYXBoLm1pY3Jvc29mdC5jb20iLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC8wYjI0ZjY3MS1kNjE3LTQxMzgtODE5ZC0xNTcyYmIzN2ZjNGQvIiwiaWF0IjoxNzM1OTg3MDQzLCJuYmYiOjE3MzU5ODcwNDMsImV4cCI6MTczNTk5MDk0MywiYWlvIjoiazJCZ1lBZzdzV0d5VmhlVDdYd1pPUmt4anlBbkFBPT0iLCJhcHBfZGlzcGxheW5hbWUiOiJuZXdFZ292ZXJuYW5jZSIsImFwcGlkIjoiMzRlOTdjMWQtMmJjZi00ZTgzLWE3NWEtYzhiNDI4OGRjZTJiIiwiYXBwaWRhY3IiOiIxIiwiaWRwIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvMGIyNGY2NzEtZDYxNy00MTM4LTgxOWQtMTU3MmJiMzdmYzRkLyIsImlkdHlwIjoiYXBwIiwib2lkIjoiODk5OTFiYTQtYThhNy00ODI5LWE5MWQtYzVmMjg3Njg1MDUzIiwicmgiOiIxLkFWUUFjZllrQ3hmV09FR0JuUlZ5dXpmOFRRTUFBQUFBQUFBQXdBQUFBQUFBQUFDaUFBQlVBQS4iLCJyb2xlcyI6WyJVc2VyLlJlYWRXcml0ZS5BbGwiXSwic3ViIjoiODk5OTFiYTQtYThhNy00ODI5LWE5MWQtYzVmMjg3Njg1MDUzIiwidGVuYW50X3JlZ2lvbl9zY29wZSI6IkFTIiwidGlkIjoiMGIyNGY2NzEtZDYxNy00MTM4LTgxOWQtMTU3MmJiMzdmYzRkIiwidXRpIjoiR1d0OHN4MUpxRUNPcHY5ZmpZR1NBQSIsInZlciI6IjEuMCIsIndpZHMiOlsiMDk5N2ExZDAtMGQxZC00YWNiLWI0MDgtZDVjYTczMTIxZTkwIl0sInhtc19pZHJlbCI6IjcgMTYiLCJ4bXNfdGNkdCI6MTQ4OTczMjkzOX0.ZBMWKACyoTHOvtuicmYEDZXP-KGODRNjkgJAnhO-3kEfIldMLWoLONvJMXcBkm6aCpCUf87uoBXEPepyyG3m__lW0Qh2iYQSg12mSTsKdEuRCDeJ7T0R5e9bOq0xfUowgPxrWKuxYy-Bk8bOP7BJgwND8xnwM0Vl5RVD1M0NY2TqoL3Dik_9WIJWMPFFJtlJjCFV7ZHSOGEEy7RSCFlMrh2JwHQ89fNyQnZ7hCws4UoiQys-VmywLGqWEyTtUtWNiB-YZ_dwPbBv5Uo9dVaLCmviIIOnLRa40lW8yEbE2G9rj6qOoVQk7FfwoB4ljfTkDGk3J_7A_RI_lC1TejxPOQ";

    public MicrosoftGraphService(StudentRepository studentRepository) {
        this.webClientGraph = WebClient.builder().baseUrl("https://graph.microsoft.com/v1.0/users").build();
        this.studentRepository = studentRepository;
    }

    public void startOperation(String regdNo1, String regdNo2){
        List<Student> students = studentRepository.findByRegdNoList(List.of(regdNo1,regdNo2));
        for(Student student : students){
            fetchUserJobInformation(student.getStudentAdmissionDetails().getJeeApplicationNo());
        }
    }


    public void fetchUserJobInformation(String jeeApplicationNumber) {
        logger.info("Fetching the user job information");
        String uri = UriComponentsBuilder.fromPath("")
                .queryParam("$filter", "surname eq \'"+ "2101310224669" +"\'")
                .queryParam("$select","userPrincipalName")
                .toUriString();
        webClientGraph.get()
                .uri(uri)
                .header("Authorization","Bearer "+token)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    public void photoUpload(UserPrincipalNameRecord userPrincipalNameRecord, String jeeApplicationNumber) {
        FileSystemResource fileResource = new FileSystemResource("sample_640×426.jpeg");
        // Use the result from the previous method
        String uri = UriComponentsBuilder.fromPath("/"+ userPrincipalNameRecord.userPrincipalName()+"/photo$value")
                .toUriString();
        webClientGraph.put()
                .uri(uri)
                .header("Authorization","Bearer "+token)
                .contentType(MediaType.IMAGE_JPEG)  // Set content type as image/jpeg
                .body(BodyInserters.fromResource(fileResource))  // Attach the file as the body
                .retrieve()
                .toBodilessEntity()
                .doOnError(throwable -> logger.error("Error uploading photo : {}", userPrincipalNameRecord.userPrincipalName()))
                .doOnSuccess(response -> logger.info("PhotoUpload success response: {}", userPrincipalNameRecord.userPrincipalName()))
                .block();
        // Add additional logic as needed
    }

    public void callServer(String number) {
    }
}
