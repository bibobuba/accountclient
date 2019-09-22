package com.example.accountclient.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "client-service")
@Getter
@Setter
public class ClientConfiguration {

    private int amountOfRequestsForGettingProcess;
    private int amountOfRequestsForPuttingProcess;
    private int amountOfThreads;
    private List<Integer> listOfIds;

}
