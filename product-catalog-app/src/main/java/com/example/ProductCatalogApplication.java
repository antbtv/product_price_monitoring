package com.example;

import com.example.config.AppConfig;
import com.example.entity.Category;
import com.example.entity.Price;
import com.example.entity.Product;
import com.example.entity.Store;
import com.example.service.CategoryService;
import com.example.service.PriceService;
import com.example.service.ProductService;
import com.example.service.StoreService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class ProductCatalogApplication {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        StoreService storeService = context.getBean(StoreService.class);
        ProductService productService = context.getBean(ProductService.class);
        PriceService priceService = context.getBean(PriceService.class);
        CategoryService categoryService = context.getBean(CategoryService.class);

        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            System.out.println(product);
        }

        List<Price> prices = priceService.getAllPrices();
        for (Price price : prices) {
            System.out.println(price);
        }

        List<Store> stores = storeService.getAllStores();
        for (Store store : stores) {
            System.out.println(store);
        }

        List<Category> categories = categoryService.getAllCategories();
        for (Category category : categories) {
            System.out.println(category);
        }
    }
}
//@SpringBootApplication
//public class ProductCatalogApplication extends SpringBootServletInitializer {
//    public static void main(String[] args) {
//        SpringApplication.run(ProductCatalogApplication.class, args);
//    }
//}
