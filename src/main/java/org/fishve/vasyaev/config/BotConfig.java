package org.fishve.vasyaev.config;

@Configuration
@Getter
@PropertySource("classpath:application.properties")
public class BotConfig {

    @Value("${botUserName}")
    String botUserName;

    @Value("${token}")
    String token;

}