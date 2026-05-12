package com.proyecto.administrativo.controler;

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

import com.proyecto.administrativo.model.AdministrativoModel;
import com.proyecto.administrativo.service.AdministrativoService;


@RestController
@RequestMapping("/api/v1/administrativo")
public class AdministrativoControler {

    private final AdministrativoService administrativoService;

    public AdministrativoControler(AdministrativoService administrativoService){
        this.administrativoService = administrativoService;
    }

    @GetMapping
    public List<AdministrativoModel> listAll(){
        return administrativoService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrativoModel> findById(@PathVariable Long id){
        Optional<AdministrativoModel> administrativo = administrativoService.findById(id);
        return administrativo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/rut/{rut}")
    public ResponseEntity<AdministrativoModel> findByRut(@PathVariable String rut){
        Optional<AdministrativoModel> administrativo = administrativoService.findByRut(rut);
        return administrativo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<AdministrativoModel> save(@RequestBody AdministrativoModel administrativo){
        return ResponseEntity.status(HttpStatus.CREATED).body(administrativoService.save(administrativo));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delet(@PathVariable Long id){
        administrativoService.delet(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{charge}")
    public List<AdministrativoModel> findByCharge(@PathVariable String charge){
        return administrativoService.findByCharge(charge);
    }
    
}

