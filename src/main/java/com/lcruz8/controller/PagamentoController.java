package com.lcruz8.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping
    public List<Pagamento> list() {
        return pagamentoRepository.findAll();
    }
}

