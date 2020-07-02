package com.cristiano.service;

import java.util.Optional;

import com.cristiano.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario salvarUsuario(Usuario usuario);
	
	Usuario autenticar(String email, String senha);
	
	void validarEmail(String email);
	
	Optional<Usuario> obterPorId(Long id);
}
