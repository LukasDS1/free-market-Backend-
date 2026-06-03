package com.freemarket.reserva_service.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.freemarket.reserva_service.model.Product;
import com.freemarket.reserva_service.model.Provider;
import com.freemarket.reserva_service.repository.ProductRepository;
import com.freemarket.reserva_service.repository.ProviderRepository;


import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LoadDataBase {

    @Bean
    CommandLineRunner initData(
            ProviderRepository    providerRepo,
            ProductRepository     productRepo) {

        return args -> {

            if (providerRepo.count() > 0) return; 
            Provider p1 = new Provider();
            p1.setProvidername("TechSupplies S.A.");

            Provider p2 = new Provider();
            p2.setProvidername("ElectroDistrib Ltda.");

            providerRepo.save(p1);
            providerRepo.save(p2);

            Product prod1 = new Product();
            prod1.setProductname("Teclado Mecánico RGB");
            prod1.setUrl("https://images.example.com/teclado-rgb.jpg");
            prod1.setProductprice(29990);
            prod1.setProductStock(50);
            prod1.setActive(true);
            prod1.setProvider(p1);

            Product prod2 = new Product();
            prod2.setProductname("Monitor 24 pulgadas FHD");
            prod2.setUrl("https://images.example.com/monitor-24.jpg");
            prod2.setProductprice(149990);
            prod2.setProductStock(20);
            prod2.setActive(true);
            prod2.setProvider(p1);

            Product prod3 = new Product();
            prod3.setProductname("Mouse Inalámbrico");
            prod3.setUrl("https://images.example.com/mouse-wireless.jpg");
            prod3.setProductprice(14990);
            prod3.setProductStock(100);
            prod3.setActive(true);
            prod3.setProvider(p2);

            productRepo.save(prod1);
            productRepo.save(prod2);
            productRepo.save(prod3);

            System.out.println("Datos iniciales de reserva_service cargados.");
        };
    }
}