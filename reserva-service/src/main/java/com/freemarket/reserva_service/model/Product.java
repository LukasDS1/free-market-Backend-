package com.freemarket.reserva_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    @Column 
    (nullable = false,length = 50,unique = true)
    private String productname;

    @Column 
    (nullable = false)
    private String url;
    
    @Column 
    (nullable = false,length = 50)
    private int productprice;

    @Column 
    (nullable = false,length = 50)
    private int productStock;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idReserveDetails")
    @JsonIgnoreProperties("product")
    private ReserveDetails reserveDetails;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "providerId", nullable = false)
    private Provider provider;




}
