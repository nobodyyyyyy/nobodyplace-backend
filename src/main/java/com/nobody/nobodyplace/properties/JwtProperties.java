package com.nobody.nobodyplace.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nobodyplace.jwt")
@Data
public class JwtProperties {

    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;
}
