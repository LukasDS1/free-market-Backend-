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
@Table(name = "state")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class state {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long stateId;

    @Column
    (nullable = false,length = 50,unique = true)
    private String stateName;

    @Column
    (nullable = false,length = 50,unique = true)
    private String stateDescription;


}
