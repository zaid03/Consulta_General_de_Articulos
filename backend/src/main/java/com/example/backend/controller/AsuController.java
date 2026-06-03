package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.sqlserver2.model.Asu;
import com.example.backend.sqlserver2.repository.AsuRepository;

@RestController
@RequestMapping("/api/asu")
public class AsuController {

    @Autowired
    private AsuRepository asuRepository;

    private static final String SIN_RESULTADO = "Sin resultado";
    private static final String ERROR = "Error :";

    //needed to add an articulo for consulta general
    @GetMapping("/fetching-subs/{ent}/{afacod}")
    public ResponseEntity<?> subsFetching (
        @PathVariable Integer ent,
        @PathVariable String afacod
    ) {
        try {
            if (ent == null || afacod == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios.");
            }

            List<Asu> subfamilias = asuRepository.findByENTAndAFACOD(ent, afacod);
            if (subfamilias.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SIN_RESULTADO);
            }

            return ResponseEntity.ok(subfamilias);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ERROR + ex.getMostSpecificCause().getMessage());
        }
    }
}
