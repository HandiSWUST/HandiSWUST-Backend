package org.shirakawatyu.handixikebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HandiXikeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HandiXikeBackendApplication.class, args);
    }

}
