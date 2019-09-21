package com.example.accountclient.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "address", url = "${client-service.server-url}")
public interface CustomFeignClient {

    @GetMapping("/account/{id}")
    public Long getValue(@PathVariable(name = "id") Integer id);

    @PutMapping("/account/{id}/replenishment/{amount}")
    public void putValue(@PathVariable(name = "id") Integer id,
                         @PathVariable(name = "amount") Long amount);

}
