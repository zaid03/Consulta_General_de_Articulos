package com.example.backend.sqlserver2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import com.example.backend.dto.ArticleProjection;
import com.example.backend.sqlserver2.model.Art;
import com.example.backend.sqlserver2.model.ArtId;

import java.util.List;

@Repository
public interface ArtRepository extends JpaRepository<Art, ArtId>, JpaSpecificationExecutor<Art> {
    List<ArticleProjection> findByENT(Integer ent, Pageable pageable);
}