package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Asu;
import com.example.backend.sqlserver2.model.AsuId;

import java.util.List;

@Repository
public interface AsuRepository extends JpaRepository<Asu, AsuId> {
    //needed to add an articulo for consulta general
    List<Asu> findByENTAndAFACOD(Integer ent, String afacod);
}