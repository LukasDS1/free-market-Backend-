package com.freemarket.locations_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;


@Configuration
public class RestConfig {

   @Bean
public RestTemplate RestTemplateNormal() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(5000);
    
    RestTemplate rt = new RestTemplate(factory);
    
    rt.getInterceptors().add((request, body, execution) -> {
        request.getHeaders().set("User-Agent", "freemarket-locations-service/1.0");
        return execution.execute(request, body);
    });
    
    return rt;
}

}
