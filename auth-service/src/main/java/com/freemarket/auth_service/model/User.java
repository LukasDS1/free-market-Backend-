package com.freemarket.auth_service.model;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data

public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long userId;

    @Column 
    (nullable = false,length = 50,unique = true)
    private String email;

    @Column
    (nullable = false,length = 255)
    private String password;    

    @Column
    (nullable = false,length = 50,unique = true)
    private  String username;

    @Column
    (nullable = false,length = 50)
    private  String firstName;

    @Column
    (nullable = false,length = 50)
    private String lastName;

    @Column
    (nullable = true,length = 20)
    private String genre;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idRol")
    @JsonIgnoreProperties("usuarios")
    private Rol rol;

    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

     @Override
    public boolean isAccountNonExpired() {
       return true;
    }
     @Override
    public boolean isAccountNonLocked() {
       return true;
    }
    
     @Override
    public boolean isCredentialsNonExpired() {
       return true;
    }
    
     @Override
    public boolean isEnabled() {
       return true;
    }
    
    @Override
    public String getUsername() {
    return this.username;
    }
    @Override
    public String getPassword() {
    return this.password;
}
    
}
