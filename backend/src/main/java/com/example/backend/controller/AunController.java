package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.sqlserver2.model.Aun;
import com.example.backend.sqlserver2.repository.AunRepository;

@RestController
@RequestMapping("/api/aun")
public class AunController {
    @Autowired
    private AunRepository aunRepository;

    //needed for select lists in mantinimiento general de articulos
    @GetMapping("/get-all/{ent}")
    public ResponseEntity<?> allGet (
        @PathVariable Integer ent
    ) {
        try {
            if (ent == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            List<Aun> tipos = aunRepository.findByENT(ent);
            if (tipos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sin resultado");
            }

            return ResponseEntity.ok(tipos);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }
}
