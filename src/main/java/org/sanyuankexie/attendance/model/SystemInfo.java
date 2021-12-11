package org.sanyuankexie.attendance.model;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "attendance")
@Component
@Data
public class SystemInfo {

    //
    String week;
    String term;
    int leve;
    String password;
    String[] isLeve;
}
