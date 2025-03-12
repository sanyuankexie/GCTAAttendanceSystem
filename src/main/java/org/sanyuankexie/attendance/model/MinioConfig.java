package org.sanyuankexie.attendance.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinioConfig {
    String endPoint;
    String accessKey;
    String secretKey;
    String bucketName;
}
