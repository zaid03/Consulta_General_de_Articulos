package com.example.backend.sqlserver2.repository;

import com.example.backend.sqlserver2.model.Mag;
import com.example.backend.sqlserver2.model.MagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagRepository extends JpaRepository<Mag, MagId> {
    
}