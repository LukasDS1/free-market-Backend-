package com.freemarket.locations_service.model;

import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="location")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Location {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long locationId;

    @Column
    (nullable=false)
    private Long userId;

    @Column(nullable = false)
    private String street;
    
    @Column
    (nullable=false) 
    private String streetAddress;

    @Column(nullable = false)
    private String streetNumber;

    @Column
    (nullable=false)
    private double longitud;

    @Column
    (nullable=false)
    private double latitude;

    @Column(name = "address_type")
    private String addressType; 

    @Column(name = "is_active")
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "idComuna")
    private Comuna comuna;
   

    

}
