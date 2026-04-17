package com.freemarket.state_service.model;


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
@Table(name = "delivery_state")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class deliveryState {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long deliveryStateId;

    @Column
    (nullable = false,length = 50,unique = true)
    private String deliveryStateName ;

    @Column
    (nullable = false,length = 50,unique = true)
    private String deliveryStateDescription;

   




}
