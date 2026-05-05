package com.proyecto.gestionArchivo.controler;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.gestionArchivo.model.PacienteModel;
import com.proyecto.gestionArchivo.service.PacienteService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteControler {

    private final PacienteService pacienteService;

    public PacienteControler(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public List<PacienteModel> ListAll(){
        return pacienteService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteModel> findById(@PathVariable Long id){
        Optional<PacienteModel> paciente = pacienteService.findById(id);
        return paciente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<PacienteModel> findByRut(@PathVariable String rut) {
        Optional<PacienteModel> paciente = pacienteService.findByRut(rut);
        return paciente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PacienteModel> save(@Valid @RequestBody PacienteModel paciente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.save(paciente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delet(@PathVariable Long id) {
        pacienteService.delet(id);
        return ResponseEntity.noContent().build();
    }




}
