package com.cristiano.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cristiano.model.entity.Lancamento;
import com.cristiano.model.enums.StatusLancamento;
import com.cristiano.model.enums.TipoLancamento;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	
	@Test
	public void deveSalvarUmLancamento () {
		Lancamento lancamento = criarLancamento();
		
		lancamento = repository.save( lancamento );
		
		assertThat(lancamento.getId()).isNotNull();
		
	}
		
	@Test
	public void  deletarUmLancamento () {
		
		Lancamento lancamento = criarEPersistirLancamento();
		
		lancamento = entityManager
				.find(Lancamento.class, lancamento.getId());
		
		repository.delete( lancamento );
		
		Lancamento lancamentoInexistente = entityManager
				.find(Lancamento.class, lancamento.getId());
		
		
		assertThat( lancamentoInexistente ).isNull();
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarEPersistirLancamento();
		lancamento.setAno(2019);
		lancamento.setDescricao("teste atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager
				.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno())
		.isEqualTo( 2019 );
		
		assertThat(lancamentoAtualizado.getDescricao())
		.isEqualTo("teste atualizar");
		
		assertThat(lancamentoAtualizado.getStatus())
		.isEqualTo(StatusLancamento.CANCELADO);
	}

	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarEPersistirLancamento();
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	public static Lancamento criarLancamento() {
		return Lancamento
				.builder()
				.ano(2020)
				.mes(7)
				.descricao("lancamento de crédito")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
	}
	
	
	private Lancamento criarEPersistirLancamento() {
		Lancamento lancamento = criarLancamento();
		
		entityManager.persist( lancamento );
		return lancamento;
	}
}
