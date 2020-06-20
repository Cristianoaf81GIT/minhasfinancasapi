package com.cristiano.service;

import com.cristiano.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario salvarUsuario(Usuario usuario);
	
	Usuario autenticar(String email, String senha);
	
	void validarEmail(String email);
}
