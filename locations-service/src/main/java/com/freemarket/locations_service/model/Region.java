package com.freemarket.locations_service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="region")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Region {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long regionId;

    @Column
    (nullable=false)
    private String nombreRegion;

   
    @OneToMany(mappedBy = "region")
    private List<Comuna> comunas;


}
