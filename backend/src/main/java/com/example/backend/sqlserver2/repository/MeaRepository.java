package com.example.backend.sqlserver2.repository;

import com.example.backend.sqlserver2.model.Mea;
import com.example.backend.sqlserver2.model.MeaId;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeaRepository extends JpaRepository<Mea, MeaId> {
    //selecting existencias for articles with eager loading of relationships
    @EntityGraph(attributePaths = {"mag", "mag.dep"})
    List<Mea> findByENTAndAFACODAndASUCODAndARTCOD(Integer ent, String afacod, String asucod, String artcod);
}