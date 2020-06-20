package com.cristiano.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cristiano.exception.ErroAutenticacao;
import com.cristiano.exception.RegraNegocioException;
import com.cristiano.model.entity.Usuario;
import com.cristiano.model.repository.UsuarioRepository;
import com.cristiano.service.impl.UsuarioServiceImpl;

//@SpringBootTest // sobe toda a aplicação
@SuppressWarnings("unused")
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest // sobe apenas instancia do bd para testes
@AutoConfigureTestDatabase(replace = Replace.NONE) // evita sobrescrita de configuracoes
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	@MockBean // cria instancia mock de usuario repository
	UsuarioRepository repository;
	
	//@BeforeEach
	public void setUp() {
		// não necessario por causa da anotation @MockBean
		// repository = Mockito.mock(UsuarioRepository.class);
		//Mockito.spy(UsuarioServiceImpl.class);
		//service = new UsuarioServiceImpl(repository);
	}
	
	@Test
	public void deveAutenticarUsuarioComSucesso() {
		// cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
		Mockito.when( repository.findByEmail(email) ).thenReturn(Optional.of(usuario));
		
		// acao
		Usuario result = service.autenticar(email, senha);
		
		// verificacao
		Assertions.assertThat( result ).isNotNull();
	}
	
	@Test
	public void deveValidarEmail() {
		// cenario
		Mockito.when(
			repository.existsByEmail( Mockito.anyString() )
		).thenReturn(false);
		
		// acao
		service.validarEmail("email@email.com");
		
	}
	
	@Test
	public void deveLancarErroAoValidarEmail() {
		Mockito.when(
			repository.existsByEmail( Mockito.anyString() )
		).thenReturn(true);
		
		assertThrows(RegraNegocioException.class, () -> {
			service.validarEmail("email@email.com");
		}, "RegraNegocioExpetion lançada com sucesso!");
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioComEmailCadastrado() {
		// cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		// acao-verificacao
		/*assertThrows(ErroAutenticacao.class, () -> {
			service.autenticar("email@email.com", "senha");
		}, "erro de autenticação lançado!");*/
		
		Throwable exception = Assertions
				.catchThrowable(()-> service
						.autenticar("email@email.com", "senha") );
		
		Assertions.assertThat(exception)
		.isInstanceOf(ErroAutenticacao.class)
		.hasMessage("Usuário não encontrado para o email informado!");
	}
	
	@Test
	public void deveLancarErroQuandoASenhaNaoBater() {
		String senha = "senha";
		Usuario usuario = Usuario
				.builder()
				.email("email@email.com")
				.senha(senha)
				.build();
		
		Mockito
		.when( repository.findByEmail( Mockito.anyString() ) )
		.thenReturn( Optional.of(usuario) );
		
		// acao-verificacao
		/*assertThrows(ErroAutenticacao.class, () -> {
			service.autenticar("email@email.com", "123");
		}, "erro de autenticação lançado!");*/
		
		Throwable exception = Assertions
				.catchThrowable(() -> service
						.autenticar("email@email.com", "123"));
		
		Assertions.assertThat(exception)
		.isInstanceOf(ErroAutenticacao.class)
		.hasMessage("Senha inválida");
	}
	
	@Test
	public void deveSalvarUsuario() {
		// cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario
				.builder()
				.id(1L)
				.nome("usuario")
				.email("email@email.com")
				.senha("senha")
				.build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		// acao
		Usuario UsuariSalvo = service.salvarUsuario(new Usuario());
		
		Assertions.assertThat(UsuariSalvo).isNotNull();
		Assertions.assertThat(UsuariSalvo.getId()).isEqualTo(1L);
		Assertions.assertThat(UsuariSalvo.getNome()).isEqualTo("usuario");
		Assertions.assertThat(UsuariSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(UsuariSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		// cenario
		String email = "email@email.com";
		
		Usuario usuario = Usuario
				.builder()
				.email(email)
				.build();
		
		Mockito
		.doThrow(RegraNegocioException.class)
		.when(service)
		.validarEmail(email);
		
		// acao
		assertThrows(RegraNegocioException.class,() ->{
			service.salvarUsuario(usuario);
		});
				
		// verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
	}
	
}


