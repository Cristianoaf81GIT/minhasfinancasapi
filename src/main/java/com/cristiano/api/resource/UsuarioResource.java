package com.cristiano.api.resource;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cristiano.api.dto.UsuarioDTO;
import com.cristiano.exception.ErroAutenticacao;
import com.cristiano.exception.RegraNegocioException;
import com.cristiano.model.entity.Usuario;
import com.cristiano.service.LancamentoService;
import com.cristiano.service.UsuarioService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {
	
	private final UsuarioService service;
	private final LancamentoService lancamentoService;
		
	
	@PostMapping("/autenticar")
	public ResponseEntity<Object> autenticar(@RequestBody UsuarioDTO dto){
		
		try {
			
			Usuario autenticado = service
					.autenticar(dto.getEmail(), dto.getSenha());
			
			return ResponseEntity.ok(autenticado);
			
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}		
		
	}
	
	@PostMapping
	public ResponseEntity<Object> salvar( @RequestBody UsuarioDTO dto ) {
		Usuario usuario = Usuario
				.builder()
				.nome( dto.getNome() )
				.email( dto.getEmail() )
				.senha( dto.getSenha() )
				.build();
		
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity<Object>(usuarioSalvo,HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}		
		
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity<Object> obterSaldo (@PathVariable("id") Long id) {
		
		if ( !service.obterPorId(id).isPresent() ) {
			
			return new ResponseEntity<Object>(
					HttpStatus.NOT_FOUND);
			
		}
		
		BigDecimal saldo = lancamentoService
				.obterSaldoPorUsuario(id);
		
		return new ResponseEntity<Object>(saldo,HttpStatus.OK);
	}
	
}
