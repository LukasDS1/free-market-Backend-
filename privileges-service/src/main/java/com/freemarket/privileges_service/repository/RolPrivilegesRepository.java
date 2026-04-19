package com.freemarket.privileges_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.privileges_service.model.rolPrivileges;

@Repository
public interface RolPrivilegesRepository extends JpaRepository<rolPrivileges, Long> {
    List<rolPrivileges> findByRoleId(Long roleId);
}
