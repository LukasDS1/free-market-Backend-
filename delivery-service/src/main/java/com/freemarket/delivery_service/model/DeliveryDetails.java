package com.freemarket.delivery_service.model;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="delivery_details")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class DeliveryDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDeliveryDetails;

    @Column 
    (nullable = false)
    private LocalDate deliveryBeginDate;

    @Column 
    (nullable = false)
    private LocalDate deliveryEndDate ;

    @Column 
    (nullable = false)
    private Long idReserva;

    @Column 
    (nullable = false)
    private Long idUsuario;

    @Column
    (nullable = true)
    private Long idRepartidor; 

   @OneToOne(mappedBy = "deliveryDetails", fetch = FetchType.LAZY)
    private Delivery delivery;



}