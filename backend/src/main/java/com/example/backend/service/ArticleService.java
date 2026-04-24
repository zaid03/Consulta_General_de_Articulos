package com.example.backend.service;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.backend.dto.ArticleProjection;
import com.example.backend.dto.ArticleProjectionDTO;
import com.example.backend.sqlserver2.model.Art;
import com.example.backend.sqlserver2.repository.ArtRepository;

@Service
public class ArticleService {
    private final ArtRepository artRepository;
    
    public ArticleService(ArtRepository artRepository) {
        this.artRepository = artRepository;
    }
    
    public Page<ArticleProjection> searchArticles(
            Integer ent, String search, String afacod, String asucod, 
            String bloqueado, int page) {
        
        Specification<Art> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(cb.equal(root.get("ent"), ent));
            
            if (search != null && !search.isEmpty()) {
                if (search.length() < 11) {
                    predicates.add(cb.equal(root.get("artcod"), search));
                } else if (search.length() < 31) {
                    Predicate artdesLike = cb.like(root.get("artdes"), "%" + search + "%");
                    Predicate artrefEqual = cb.equal(root.get("artref"), search);
                    predicates.add(cb.or(artdesLike, artrefEqual));
                } else {
                    predicates.add(cb.like(root.get("artdes"), "%" + search + "%"));
                }
            }
            
            if (afacod != null && !afacod.isEmpty()) {
                predicates.add(cb.equal(root.get("afacod"), afacod));
            }
            
            if (asucod != null && !asucod.isEmpty()) {
                predicates.add(cb.equal(root.get("asucod"), asucod));
            }
            
            if (bloqueado != null && !bloqueado.equals("todos")) {
                if (bloqueado.equals("bloqueado")) {
                    predicates.add(cb.equal(root.get("artblo"), 0));
                } else if (bloqueado.equals("nobloqueado")) {
                    predicates.add(cb.notEqual(root.get("artblo"), 0));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Art> results = artRepository.findAll(spec, PageRequest.of(page, 20));
        
        return results.map(art -> new ArticleProjectionDTO(
            art.getAFACOD(), art.getAfa().getAFADES(), 
            art.getASUCOD(), art.getAsu().getASUDES(),
            art.getARTCOD(), art.getARTDES(), art.getARTREF(),
            art.getARTBLO(), art.getARTUNI(), art.getARTSOL(), art.getARTREC(),
            art.getAun().getAUNDES(), art.getARTUCO(), art.getARTUEM(),
            art.getARTPMI(), art.getARTPMP(), art.getARTMIN(), art.getARTOPT()
        ));
    }
}