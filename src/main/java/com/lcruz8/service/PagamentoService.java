package com.lcruz8.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.http.ResponseEntity;

import com.lcruz8.model.Pagamento;
import com.lcruz8.model.PagamentoStatus;
import com.lcruz8.model.PagamentoTipo;
import com.lcruz8.repository.PagamentoRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    @PersistenceContext
    EntityManager entityManager;

    public List<Pagamento> list(Pagamento pagamento) {

        if(
            pagamento != null &&
            ( pagamento.getCodigo() != null ||
              pagamento.getCpfCnpj() != null ||
              pagamento.getStatus() != null )
        ) {

            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Pagamento> query = builder.createQuery(Pagamento.class);
            Root<Pagamento> root = query.from(Pagamento.class);

            List<Predicate> predicates = new ArrayList<Predicate>();

            if(pagamento.getCodigo() != null)
                predicates.add(builder.like(root.get("codigo"), pagamento.getCodigo().toString()));

            if(pagamento.getCpfCnpj() != null)
                predicates.add(builder.like(root.get("cpfCnpj"), pagamento.getCpfCnpj()));

            if(pagamento.getStatus() != null)
                predicates.add(builder.like(root.get("status"), pagamento.getStatus()));


            query.where(predicates.toArray(new Predicate[predicates.size()]));

            return entityManager.createQuery(query).getResultList();
        } 

        return pagamentoRepository.findAll();
    }

    public Pagamento create(Pagamento pagamento) {
        if( pagamento.getMetodoPagamento().equals(PagamentoTipo.PG_PIX) || 
            pagamento.getMetodoPagamento().equals(PagamentoTipo.PG_BOLETO))
            pagamento.setNumeroCartao(null);

        return pagamentoRepository.save(pagamento);
    }

    public ResponseEntity<Pagamento> update(Pagamento pagamento) {
        return pagamentoRepository.findById(pagamento.getId())
        .map(registro -> {

            if( 
                ( (registro.getStatus().equals(PagamentoStatus.PROC_PENDENTE) && !pagamento.getStatus().equals(PagamentoStatus.PROC_PENDENTE)) ||
                  (registro.getStatus().equals(PagamentoStatus.PROC_FALHA) && pagamento.getStatus().equals(PagamentoStatus.PROC_PENDENTE)) ) &&
                ( registro.getStatus().equals(PagamentoStatus.PROC_FALHA) || registro.getStatus().equals(PagamentoStatus.PROC_PENDENTE) ||
                  registro.getStatus().equals(PagamentoStatus.PROC_SUCESSO) )
             ) {
                registro.setStatus(pagamento.getStatus());
                Pagamento updated = pagamentoRepository.save(registro);

                return ResponseEntity.ok().body(updated);
            } 

            return ResponseEntity.ok().body(registro);
        })
        .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> delete(Pagamento pagamento) {
        return pagamentoRepository.findById(pagamento.getId())
        .map(registro -> {
    
            if(registro.getStatus().equals(PagamentoStatus.PROC_PENDENTE))
                pagamentoRepository.deleteById(pagamento.getId());
    
            return ResponseEntity.noContent().<Void>build();
        })
        .orElse(ResponseEntity.notFound().build());
    }
    
}
