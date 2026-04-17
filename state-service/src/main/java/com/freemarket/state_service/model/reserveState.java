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
@Table(name = "reserve_state")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class reserveState {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long reserveStateId;

    @Column
    (nullable = false,length = 50,unique = true)
    private String reserveStateName;

    @Column
    (nullable = false,length = 50,unique = true)
    private String reserveStateDescription;

}
