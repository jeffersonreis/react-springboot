package com.dreis.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dreis.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{
	
}
