package main;

import main.model.ApplicationUser;
import main.repository.*;
import main.service.Service;
import main.websocket.WebsocketServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
public class AppConfig {

    @Bean(name="service")
    public Service createService(){ return new Service();}

    @Bean(name="passwordEncoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
/*
    @Bean(name="websocket")
    public WebsocketServer startWebsocket() {
        return new WebsocketServer();
    }


    @Bean(name="websocket")
    public WebsocketServer createWebsocketServer(){
        return new WebsocketServer();
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000");
            }
        };
    }*/

}