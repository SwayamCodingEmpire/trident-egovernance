package com.trident.egovernance.global.services;

import com.trident.egovernance.domains.student.services.StudentDashBoardsService;
import com.trident.egovernance.dto.OtherProfileDTO;
import com.trident.egovernance.dto.ProfileDTO;
import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.exceptions.UserNotLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProfileFetcherService {
    private final MiscellaniousServices miscellaniousServices;
    private final Logger logger = LoggerFactory.getLogger(ProfileFetcherService.class);
    private final StudentDashBoardsService studentDashBoardsService;
    public ProfileFetcherService(MiscellaniousServices miscellaniousServices, StudentDashBoardsService studentDashBoardsService) {
        this.miscellaniousServices = miscellaniousServices;
        this.studentDashBoardsService = studentDashBoardsService;
    }

    @Cacheable(key = "#authentication.name", value = "profileDTO")
    public ProfileDTO getUserJobInformation(Authentication authentication){
        logger.info("Running getUserJobInformation" + authentication.getName());
        if(authentication.isAuthenticated()){
            UserJobInformationDto userJobInformationDto = miscellaniousServices.getUserJobInformation().getLeft();
            if(userJobInformationDto.jobTitle().equals("STUDENT")){
                return studentDashBoardsService.getStudentProfile(userJobInformationDto);
            }
            else {
                return new OtherProfileDTO(miscellaniousServices.getUserJobInformation().getLeft());
            }
        }
        throw new UserNotLoggedInException("User not logged in");
    }


//
}
