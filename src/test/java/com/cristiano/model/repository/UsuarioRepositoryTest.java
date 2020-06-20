package com.cristiano.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.cristiano.model.entity.Usuario;

@SuppressWarnings("unused")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") //@SpringBootTest
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarExistenciadeUmEmail() {
		// cenario
		Usuario usuario = criarUsuario();
		
		//usuarioRepository.save(usuario);
		entityManager.persist(usuario);
		
		
		// ação/execução
		boolean result = usuarioRepository
				.existsByEmail(usuario.getEmail());
		
		// verificação
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail() {
		// cenario
		//usuarioRepository.deleteAll();
		
		// ação
		boolean result = usuarioRepository.existsByEmail("usuario@email.com");
		
		// verificacao
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//cenario
		Usuario usuario = criarUsuario();
		
		//ação
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		//verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test @Transactional
	public void deveBuscarUmUsuarioPorEmail() {
		// cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		// ação
		Optional<Usuario> result = usuarioRepository
				.findByEmail(usuario.getEmail());
		
		
		// verificação
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	public static Usuario criarUsuario() {
		return Usuario
				.builder()
				.nome("usuario")
				.senha("senha")
				.email("email@email.com")
				.build();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
		// ação/execução
		boolean result = usuarioRepository
				.existsByEmail("email@email.com");
		
		// verificação
		Assertions.assertThat(result).isFalse();
	}
}
