package com.freemarket.privileges_service.model;

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
@Table(name = "rolPrivileges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class rolPrivileges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roleId; 

    @ManyToOne
    @JoinColumn(name = "privilegesId")
    private Privileges privilege;

}
