package com.freemarket.auth_service.model;


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
@Table(name = "rol")
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Rol {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long rolId;

     @Column
    (nullable = false,length = 50)
    private  String rolName;

    @Column
    (nullable = false,length = 50)
    private  String Description;

  
    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    @JsonIgnore
    List<User> users;

}

