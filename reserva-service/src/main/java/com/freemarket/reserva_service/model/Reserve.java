package com.freemarket.reserva_service.model;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reserve")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Reserve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @Column 
    (nullable = false,length = 50)
    private Long idUser;

    @Column 
    (nullable = false,length = 50)
    private Long reserveDate;
    
    @Column
    (nullable = false,length = 255)
    private int totalPrice;    

    @OneToMany(mappedBy = "reserve", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ReserveDetails> reserveDetails;


}
