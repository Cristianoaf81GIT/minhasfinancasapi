package com.cristiano.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cristiano.exception.RegraNegocioException;
import com.cristiano.model.entity.Lancamento;
import com.cristiano.model.entity.Usuario;
import com.cristiano.model.enums.StatusLancamento;
import com.cristiano.model.repository.LancamentoRepository;
import com.cristiano.model.repository.LancamentoRepositoryTest;
import com.cristiano.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmUsuario () {
		
		Lancamento lancamentoSalvar = LancamentoRepositoryTest
				.criarLancamento();
		
		Mockito
		.doNothing()
		.when( service )
		.validar( lancamentoSalvar );
		
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest
				.criarLancamento();
		
		lancamentoSalvo.setId( 1l );
		lancamentoSalvo.setStatus( StatusLancamento.PENDENTE );
		
		Mockito
		.when( repository.save( lancamentoSalvar ) )
		.thenReturn(lancamentoSalvo);
		
		
		Lancamento lancamento = service.salvar( lancamentoSalvar );
		
		
		Assertions
		.assertThat( lancamento.getId() )
		.isEqualTo( lancamentoSalvo.getId() );
		
		Assertions
		.assertThat( lancamento.getStatus() )
		.isEqualTo( StatusLancamento.PENDENTE );
		
		
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroValidacao () {
		Lancamento lancamentoSalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito
		.doThrow( RegraNegocioException.class )
		.when( service )
		.validar( lancamentoSalvar );
		
				
		Assertions
		.catchThrowableOfType( 
				() -> service.salvar(lancamentoSalvar) ,
				RegraNegocioException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoSalvar);
		
	}
	
	@Test
	public void deveAtualizarUmUsuario () {

		Lancamento lancamentoSalvo = LancamentoRepositoryTest
				.criarLancamento();
		
		lancamentoSalvo.setId( 1l );
		lancamentoSalvo.setStatus( StatusLancamento.PENDENTE );
						
		Mockito
		.doNothing()
		.when( service )
		.validar( lancamentoSalvo );
		
		
		
		Mockito
		.when( repository.save( lancamentoSalvo ) )
		.thenReturn(lancamentoSalvo);
		
		
		service.atualizar( lancamentoSalvo );		
		
		Mockito
		.verify(repository, Mockito.times(1))
		.save( lancamentoSalvo );		
		
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo () {
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
				
				
		Assertions
		.catchThrowableOfType( 
				() -> service.atualizar(lancamento) ,
				NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamento);
		
	}
	
	@Test
	public void deveDeletarUmLancamento () {
		
		Lancamento lancamento = LancamentoRepositoryTest
				.criarLancamento();
		
		lancamento.setId(1l);
		
		service.deletar( lancamento );
		
		Mockito.verify(repository ).delete( lancamento );
	} 
	
	
	@Test
	public void deveLancarErroAoTentarDeltarUmLancamentoQueAindaNaoFoiSalvo () {
		
		Lancamento lancamento = LancamentoRepositoryTest
				.criarLancamento();
		
		Assertions
		.catchThrowableOfType( () -> service.deletar( lancamento ),
				NullPointerException.class );
		
		Mockito
		.verify(repository, Mockito.never() )
		.delete( lancamento );
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveFiltrarlancamentos () {
		 
		Lancamento lancamento = LancamentoRepositoryTest
				.criarLancamento();
		
		lancamento.setId(1l);
		
		 List<Lancamento> lista = Arrays.asList(lancamento);
		
		 Mockito
		 .when( repository.findAll( Mockito.any( Example.class )) )
		 .thenReturn(lista);
		 
		 List<Lancamento> resultado = service.buscar( lancamento );
		 
		 Assertions
		 .assertThat( resultado )
		 .isNotEmpty()
		 .hasSize( 1 )
		 .contains( lancamento );
	}
	
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento () {
		Lancamento lancamento = LancamentoRepositoryTest
				.criarLancamento();
		
		lancamento.setId(1l);
		
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		service.atualizarStatus(lancamento, novoStatus);
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		
		Mockito.verify(service).atualizar(lancamento);
	}
	
	
	@Test
	public void deveObterUmLancamentoPorId () {
		
		long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest
				.criarLancamento();
		
		
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		Assertions.assertThat(resultado.isPresent()).isTrue();
		
	}
	
	
	@Test
	public void deveRetornarVazioQuandoUmLancamentoNaoExiste () {
		
		long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest
				.criarLancamento();
		
		
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		Assertions.assertThat(resultado.isPresent()).isFalse();
		
	}
	
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions
				.catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe uma Descrição válida");
		
		lancamento.setDescricao("");
		
		erro = Assertions
				.catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe uma Descrição válida");
		
		lancamento.setDescricao("Salario");
		
		erro = Assertions
				.catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("informe um Mês válido");
		
		lancamento.setAno(0);
		
		erro = catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("informe um Mês válido");
		
		lancamento.setAno(13);
		
		erro = catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("informe um Mês válido");
		
		lancamento.setMes(1);
		
		erro = catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("informe um Ano válido");
		
		lancamento.setAno(202);
		
		erro = catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("informe um Ano válido");
		
		lancamento.setAno(2020);
		
		erro = catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Usuário");
		
		lancamento.setUsuario(new Usuario());
		
		erro = catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Usuário");
		
		lancamento.getUsuario().setId(1l);
		
		erro = catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Valor válido");
		
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = catchThrowable( () -> service.validar(lancamento) );
		
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Valor válido");
		
		lancamento.setValor(BigDecimal.valueOf(1));
		
		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro)
		.isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Tipo de lançamento");
		
	}
}
