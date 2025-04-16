package org.sanyuankexie.attendance.model;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "attendance")
@Component
@Data
public class SystemInfo {

    String week;
    String term;
    int grade;
    String password;
    Long mailTarget;

}
