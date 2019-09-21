package com.example.accountclient.controller;

import com.example.accountclient.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @GetMapping("/start")
    public void startProcess() {
        service.startProcess();
    }

}
