package com.freemarket.config_service.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuration")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConfig;

    
    @Column 
    (nullable = false,unique = true)
    private String commerceName ;

    @Column 
    (nullable = false,unique = true)
    private Long idUser ;

    @Column 
    (nullable = false)
    private String LogoUrl ;

    @Column 
    (nullable = false)
    private String favicomUrl ;

    @Column 
    (nullable = false)
    private String principalfont ;

    @Column 
    (nullable = false)
    private String primarColor ;
    
    @Column 
    (nullable = false)
    private String secondaryColor ;

    @Column 
    (nullable = false)
    private Date updateAt ;
}
