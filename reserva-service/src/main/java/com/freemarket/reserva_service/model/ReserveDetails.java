package com.freemarket.reserva_service.model;


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
@Table(name = "reserveDetails")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReserveDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idReserveDetails;

  @Column
  (nullable = false,length = 255)
  private int quanty; 
    
  @Column
  (nullable = false,length = 255)
  private int unitPrice;   

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "idProduct", nullable = false)
  private Product product;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "idReserva", nullable = false)
  private Reserve reserve;

}
