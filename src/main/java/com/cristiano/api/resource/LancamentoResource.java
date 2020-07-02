package com.cristiano.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cristiano.api.dto.AtualizaStatusDTO;
import com.cristiano.api.dto.LancamentoDTO;
import com.cristiano.exception.RegraNegocioException;
import com.cristiano.model.entity.Lancamento;
import com.cristiano.model.entity.Usuario;
import com.cristiano.model.enums.StatusLancamento;
import com.cristiano.model.enums.TipoLancamento;
import com.cristiano.service.LancamentoService;
import com.cristiano.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {
	
	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity<Object> buscar (
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "tipo", required = false) String tipo,
			@RequestParam("usuario") Long idUsuario )	{
		
		Lancamento lancamentoFiltro = new Lancamento();
		
		lancamentoFiltro.setDescricao(descricao);
		
		lancamentoFiltro.setMes(mes);
		
		lancamentoFiltro.setAno(ano);
		
		if(!tipo.equals(""))
			lancamentoFiltro.setTipo(TipoLancamento.valueOf( tipo.toUpperCase() ));
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		
		if( !usuario.isPresent() )
			return new ResponseEntity<Object>(
					"Não foi possível realizar a consulta. " + 
					"Usuário não encontrado para o id informado",
					HttpStatus.BAD_REQUEST);
		else
			lancamentoFiltro.setUsuario( usuario.get() );
		
		List<Lancamento> lancamentos = service.buscar( lancamentoFiltro );
		
		return new ResponseEntity<Object>( lancamentos, HttpStatus.OK );
	}
	
	
	@PostMapping
	public ResponseEntity<Object> salvar( @RequestBody LancamentoDTO dto ) {
		try {
			Lancamento entidade = converter( dto );
			entidade = service.salvar( entidade );
			ResponseEntity<Object> responseEntity = 
					new ResponseEntity<Object>( entidade, HttpStatus.CREATED );
			return responseEntity;
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	} 
	
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}")
	public ResponseEntity atualizar( 
			@PathVariable("id") long id,
			@RequestBody LancamentoDTO dto ) {
		
		return service.obterPorId(id).map( entity -> {
			
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch ( RegraNegocioException e ) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
		}).orElseGet(
			
			() -> new ResponseEntity<Object>(
					
					"Lancamento não encontrado na base de dados",
					HttpStatus.BAD_REQUEST
					
			)
			
		);
	}
	
	@PutMapping("{id}/atualizar-status")
	public ResponseEntity<Object> atualizarStatus (
		@PathVariable Long id,	@RequestBody AtualizaStatusDTO dto ) {
		 // aula 78 implementando o status do lancamento
		boolean estaAtualizadoLancamento = false;
		
		Lancamento lancamento = service
				.obterPorId(id)
				.orElse(null);
		 
		 if ( lancamento == null ) {
			 return new ResponseEntity<Object>(
					 "Lancamento não encontrado na base de dados!",
					 HttpStatus.BAD_REQUEST); 
		} else {
			
			StatusLancamento statusSelecionado = StatusLancamento
					.valueOf( dto.getStatus() );
			
			if( statusSelecionado == null ) {
				
				return new ResponseEntity<Object>(
						"Não foi possível atualizar o status"
						+ " do lançamento, envie um status válido.",
						HttpStatus.BAD_REQUEST);
			} else {
				
				lancamento.setStatus( statusSelecionado );
				
				try {
					
					service.atualizar( lancamento );
					estaAtualizadoLancamento = true;
					
				} catch (RegraNegocioException e) {
					
					return new ResponseEntity<Object>(
							e.getMessage(),
							HttpStatus.BAD_REQUEST);
					
				}
			}
		} 
		 
		if ( estaAtualizadoLancamento ) 
			
			return new ResponseEntity<Object>(
					lancamento, HttpStatus.OK );
		
		else
			
			return new ResponseEntity<Object>(
					"Não foi possível atualizar o lançamento "
					+ "verifique o id e o status",
					HttpStatus.BAD_REQUEST);
	}
	
	
	private Lancamento converter(LancamentoDTO dto) {
		
		Lancamento lancamento = new Lancamento();
		
		lancamento.setId(dto.getId());
		
		lancamento.setDescricao(dto.getDescricao());
		
		lancamento.setAno(dto.getAno());
		
		lancamento.setMes(dto.getMes());
		
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService
		.obterPorId(dto.getUsuario()).orElseThrow(
			
			()-> new RegraNegocioException(
					
				"Usuario não encontrado para o id informado"
					
			)
					
		);
		
		lancamento.setUsuario(usuario);
		
		if ( dto.getTipo() != null )
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		
		if ( dto.getStatus() != null )
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		
		return lancamento;
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<Object> deletar( @PathVariable("id") Long id ){
		return service.obterPorId(id).map( entidade -> {
			
			service.deletar(entidade);
			
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
			
		}).orElseGet(
				
			() -> new ResponseEntity<Object>(
				"Lancamento não encontrado na base de dados",
				HttpStatus.BAD_REQUEST
			)
			
		);
	}
	
	
}
