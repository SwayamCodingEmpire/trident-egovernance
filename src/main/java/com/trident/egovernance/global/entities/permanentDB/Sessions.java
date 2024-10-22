package com.trident.egovernance.global.entities.permanentDB;


import com.trident.egovernance.global.helpers.SessionIdId;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(SessionIdId.class)
@Entity(name = "SESSIONS")
@Table(name = "SESSIONS")
public class Sessions implements Serializable {
    @Column(name = "SESSIONID")
    private String sessionId;
    @Column(name = "STARTDATE")
    private Date startDate;
    @Column(name = "ENDDATE")
    private Date endDate;
    @Column(name = "COURSE")
    @Id
    private String course;
    @Column(name = "REGDYEAR")
    @Id
    private int regdYear;
    @Column(name = "PREVSESSIONSID",length = 10)
    private String prevSessionId;
    @Column(name = "ADMISSIONYEAR")
    @Id
    private int admissionYear;
    @Id
    private String studentType;
}
