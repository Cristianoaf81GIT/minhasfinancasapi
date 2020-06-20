package com.cristiano.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cristiano.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
