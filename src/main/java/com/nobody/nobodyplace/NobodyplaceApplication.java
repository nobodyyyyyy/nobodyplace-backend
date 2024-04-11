package com.nobody.nobodyplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
public class NobodyplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NobodyplaceApplication.class, args);
    }

}
