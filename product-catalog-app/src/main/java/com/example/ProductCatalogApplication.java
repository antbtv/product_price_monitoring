package com.example;

import com.example.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ProductCatalogApplication {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    }
}
//@SpringBootApplication
//public class ProductCatalogApplication extends SpringBootServletInitializer {
//    public static void main(String[] args) {
//        SpringApplication.run(ProductCatalogApplication.class, args);
//    }
//}
