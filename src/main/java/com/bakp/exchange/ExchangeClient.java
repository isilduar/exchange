package com.bakp.exchange;

import com.bakp.config.FeignConfiguration;
import com.bakp.dto.ExchangeResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-client", url = "${exchange-client.url}", configuration = FeignConfiguration.class)
public interface ExchangeClient {

    @GetMapping("/convert")
    ExchangeResultDto convertFromToWithPlaces(@RequestParam String from,
                                              @RequestParam String to,
                                              @RequestParam float amount,
                                              @RequestParam int places);
}
