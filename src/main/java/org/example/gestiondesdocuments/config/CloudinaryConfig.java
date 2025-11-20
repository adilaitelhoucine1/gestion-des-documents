package org.example.gestiondesdocuments.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String cloudApiKey;

    @Value("${cloudinary.api_secret}")
    private String cloudSecretKey;

    @Bean
    public Cloudinary cloudinary() {


        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dbap6a4vd",
                "api_key", "957975616391753",
                "api_secret", "pzP9UzlcRR_sliK5Mqjs2RZreXY"
        ));
    }
}
