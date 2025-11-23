package com.example.acess_request_manager;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Generated
@SpringBootApplication
@EnableSpringDataWebSupport(
    pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class AcessRequestManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(AcessRequestManagerApplication.class, args);
  }
}
