package com.lcruz8.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.lcruz8.model.Pagamento;
import com.lcruz8.repository.PagamentoRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/pagamentos")
@AllArgsConstructor
public class PagamentoController {
    private final PagamentoRepository pagamentoRepository;
    private final String PENDENTE = "PENDENTE";
    private final String FALHA = "FALHA";

    @GetMapping
    public List<Pagamento> list() {
        return pagamentoRepository.findAll();
    }
    
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Pagamento create(@RequestBody Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }
    @PutMapping()
    public ResponseEntity<Pagamento> update(@RequestBody Pagamento pagamento) {
        return pagamentoRepository.findById(pagamento.getId())
                .map(registro -> {

                    if( (registro.getStatus().equals(PENDENTE) && !pagamento.getStatus().equals(PENDENTE)) ||
                        (registro.getStatus().equals(FALHA) && pagamento.getStatus().equals(PENDENTE)) ) {

                        registro.setStatus(pagamento.getStatus());
                        Pagamento updated = pagamentoRepository.save(registro);

                        return ResponseEntity.ok().body(updated);
                    } 

                    return ResponseEntity.ok().body(registro);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping()
    public ResponseEntity<Void> delete(@RequestBody Pagamento pagamento) {

        return pagamentoRepository.findById(pagamento.getId())
                .map(registro -> {
                    
                    if(registro.getStatus().equals(PENDENTE))
                        pagamentoRepository.deleteById(pagamento.getId());

                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}

