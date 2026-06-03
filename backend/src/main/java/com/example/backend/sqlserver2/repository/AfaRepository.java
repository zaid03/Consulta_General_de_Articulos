package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.sqlserver2.model.Afa;
import com.example.backend.sqlserver2.model.AfaId;

import java.util.List;

@Repository
public interface AfaRepository extends JpaRepository<Afa, AfaId> {
    //needed to add an articulo for consulta general
    List<Afa> findByENT(Integer ent);
}