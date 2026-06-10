package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.ArticleProjection;
import com.example.backend.sqlserver2.repository.ArtRepository;
import com.example.backend.sqlserver2.repository.AsuRepository;
import com.example.backend.sqlserver2.repository.MeaRepository;

@RestController
@RequestMapping("/api/art")
public class ArtController {
    @Autowired
    private ArtRepository artRepository;
    @Autowired
    private AsuRepository asuRepository;
    @Autowired
    private MeaRepository meaRepository;
    
    private static final String SIN_RESULTADO = "Sin resultado";
    private static final String ERROR = "Error :";
    private static final int PAGE_SIZE = 20;

    //fetching articulos for consulta general
    @GetMapping("/fetch-consulta-general/{ent}")
    public ResponseEntity<?> fetchConsultaGeneral(
        @PathVariable Integer ent,
        @RequestParam(defaultValue = "0") int page
    ) {
        try {
            List<ArticleProjection> articles = artRepository.findByENT(ent, PageRequest.of(page, PAGE_SIZE));
            if (articles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SIN_RESULTADO);
            }
            return ResponseEntity.ok(articles);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR + ex.getMessage());
        }
    }
    
    //getting pagination number
    @GetMapping("/get-pag/{ent}")
    public ResponseEntity<?> getPag(
        @PathVariable Integer ent
    ) {
        try {
            Integer pagination = artRepository.countByENT(ent);
            if (pagination == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SIN_RESULTADO);
            }

            return ResponseEntity.ok(pagination);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR + ex.getMessage());
        }
    }
    
    //search in articulos general
    @GetMapping("/search/{ent}")
    public ResponseEntity<?> searchArticles(
        @PathVariable Integer ent,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String afacod,
        @RequestParam(required = false) String asucod,
        @RequestParam(defaultValue = "todos") String bloqueado
    ) {
        try {
            Page<ArticleProjection> articles = artRepository.searchArticles(
                ent, search, afacod, asucod, bloqueado, PageRequest.of(page, PAGE_SIZE)
            );
            if (articles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SIN_RESULTADO);
            }
            return ResponseEntity.ok(articles.getContent());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }
}