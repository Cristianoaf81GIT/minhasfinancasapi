package com.cristiano.service.impl;



import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cristiano.exception.ErroAutenticacao;
import com.cristiano.exception.RegraNegocioException;
import com.cristiano.model.entity.Usuario;
import com.cristiano.model.repository.UsuarioRepository;
import com.cristiano.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	
	private UsuarioRepository repository;
		
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		
		validarEmail(usuario.getEmail());
		
		return repository.save(usuario);
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository
				.findByEmail(email);
		
		if(!usuario.isPresent())
			throw new ErroAutenticacao("Usuário não encontrado "
					+ "para o email informado!");
		
		if(!usuario.get().getSenha().equals(senha.trim()))
			throw new ErroAutenticacao("Senha inválida");
		
		return usuario.get();
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		
		if(existe)
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
			
		
	}

}
