package main;

import main.model.ApplicationUser;
import main.repository.*;
import main.service.Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Properties;

@Configuration
public class AppConfig {

    @Bean(name="service")
    public Service createService(){ return new Service();}

    @Bean(name="passwordEncoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


}