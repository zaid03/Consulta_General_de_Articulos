package com.example.backend.controller;

import com.example.backend.dto.ExistenciasMeaProjection;
import com.example.backend.dto.ExistenciasMeaProjectionDTO;
import com.example.backend.sqlserver2.model.Mea;
import com.example.backend.sqlserver2.repository.MeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mea")
public class MeaController {
    @Autowired
    private MeaRepository meaRepository;

    private static final String SIN_RESULTADO = "Sin resultado";
    private static final String ERROR = "Error :";

    //selecting existencias for articles
    @GetMapping("/existencias-por-articulo/{ent}/{afacod}/{asucod}/{artcod}")
    public ResponseEntity<?> existenciasPorArticulo(
        @PathVariable Integer ent,
        @PathVariable String afacod,
        @PathVariable String asucod,
        @PathVariable String artcod
    ) {
        try {
            List<Mea> existencias = meaRepository.findByENTAndAFACODAndASUCODAndARTCOD(ent, afacod, asucod, artcod);

            if (existencias.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SIN_RESULTADO);
            }

            List<ExistenciasMeaProjection> result = existencias.stream()
            .map(mea -> new ExistenciasMeaProjectionDTO(
                mea.getMAGCOD(),
                mea.getMag() != null ? mea.getMag().getDEPCOD() : null,
                mea.getMag() != null && mea.getMag().getDep() != null ? mea.getMag().getDep().getDEPDES() : null,
                mea.getMEAUNI(),
                mea.getMEALOC()
            ))
            .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR + ex.getMessage());
        }
    }
}