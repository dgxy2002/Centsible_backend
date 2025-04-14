package com.example.backendapi.config;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dipmlrzfc",
                "api_key", "227166733379629",
                "api_secret", "gnZXu0awAPwBRPfrjNXCmyS5uo4"
        ));
    }
}
