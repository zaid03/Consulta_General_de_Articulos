package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Asu;
import com.example.backend.sqlserver2.model.AsuId;

@Repository
public interface AsuRepository extends JpaRepository<Asu, AsuId> {
    
}