package com.freemarket.privileges_service.model;

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
@Table(name="privilegios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Privileges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privilegesId;

    @Column 
    (nullable = false,length = 50,unique = true)
    private String privilegeName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moduloId")
    @JsonIgnoreProperties("privilegios")
    private Modulo modulo;


}
