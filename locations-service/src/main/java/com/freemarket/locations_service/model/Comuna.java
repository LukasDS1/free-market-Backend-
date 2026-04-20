package com.freemarket.locations_service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
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
@Table(name="comuna")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comuna {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idComuna;

    @Column
    (nullable=false)
    private String nombreComuna;

    
    @ManyToOne
    @JoinColumn(name = "regionId")
    private Region region;

    @OneToMany(mappedBy = "comuna")
    private List<Location> locations;


}
