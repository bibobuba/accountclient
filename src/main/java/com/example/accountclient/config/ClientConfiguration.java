package com.example.accountclient.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "client-service")
@Getter
@Setter
public class ClientConfiguration {

    private int amountOfUsersForGettingProcess;
    private int amountOfUsersForPuttingProcess;
    private List<Integer> listOfIds;

}
