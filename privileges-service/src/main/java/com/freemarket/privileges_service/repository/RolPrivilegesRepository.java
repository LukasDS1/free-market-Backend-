package com.freemarket.privileges_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.freemarket.privileges_service.model.rolPrivileges;

@Repository
public interface RolPrivilegesRepository extends JpaRepository<rolPrivileges, Long> {
@Query("SELECT rp FROM rolPrivileges rp JOIN FETCH rp.privilege p JOIN FETCH p.modulo WHERE rp.roleId = :roleId")
    List<rolPrivileges> findByRoleId(Long roleId);
}
