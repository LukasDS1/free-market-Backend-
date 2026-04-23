package com.freemarket.reserva_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.freemarket.reserva_service.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

}
