package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.magcodOnly;
import com.example.backend.sqlserver2.model.Asu;
import com.example.backend.sqlserver2.model.AsuId;

import java.util.List;

@Repository
public interface AsuRepository extends JpaRepository<Asu, AsuId> {
    //needed to add an articulo for consulta general
    List<Asu> findByENTAndAFACOD(Integer ent, String afacod);

    @Query("""
        SELECT m.MAGCOD AS MAGCOD
        FROM Asu a
        JOIN Mat m
            ON a.ENT = m.ENT
        AND a.MTACOD = m.MTACOD
        WHERE a.ENT = :ent
        AND a.AFACOD = :afacod
        AND a.ASUCOD = :asucod
        """)
    List<magcodOnly> findMagcods(Integer ent, String afacod, String asucod);
}