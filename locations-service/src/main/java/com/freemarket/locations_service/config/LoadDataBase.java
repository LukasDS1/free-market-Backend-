package com.freemarket.locations_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.freemarket.locations_service.model.Comuna;
import com.freemarket.locations_service.model.Location;
import com.freemarket.locations_service.model.Region;
import com.freemarket.locations_service.repository.ComunaRepository;
import com.freemarket.locations_service.repository.LocationRepository;
import com.freemarket.locations_service.repository.RegionRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LoadDataBase {

    @Bean
    CommandLineRunner initData(
            RegionRepository   regionRepo,
            ComunaRepository   comunaRepo,
            LocationRepository locationRepo) {

        return args -> {

            if (regionRepo.count() > 0) return;

            // ── Región ────────────────────────────────────────────────
            Region rm = new Region();
            rm.setNombreRegion("Región Metropolitana de Santiago");
            regionRepo.save(rm);

            // ── Comunas ───────────────────────────────────────────────
            Comuna providencia = new Comuna();
            providencia.setNombreComuna("Providencia");
            providencia.setRegion(rm);
            comunaRepo.save(providencia);

            Comuna santiago = new Comuna();
            santiago.setNombreComuna("Santiago");
            santiago.setRegion(rm);
            comunaRepo.save(santiago);

            Comuna lasCondes = new Comuna();
            lasCondes.setNombreComuna("Las Condes");
            lasCondes.setRegion(rm);
            comunaRepo.save(lasCondes);

            Location loc = new Location();
            loc.setUserId(2L);
            loc.setStreet("Avenida Providencia");
            loc.setStreetNumber("1234");
            loc.setStreetAddress("Avenida Providencia 1234, Providencia, Región Metropolitana de Santiago, Chile");
            loc.setLatitude(-33.4290115);
            loc.setLongitud(-70.6211027);
            loc.setComuna(providencia);
            locationRepo.save(loc);

            System.out.println("Datos iniciales de locations_service cargados.");
        };
    }
}