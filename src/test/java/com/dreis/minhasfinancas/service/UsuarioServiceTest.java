package com.dreis.minhasfinancas.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dreis.minhasfinancas.expections.ErroAutenticacao;
import com.dreis.minhasfinancas.expections.RegraNegocioExpection;
import com.dreis.minhasfinancas.model.entity.Usuario;
import com.dreis.minhasfinancas.model.repository.UsuarioRepository;
import com.dreis.minhasfinancas.service.impl.UsuarioServiceImpl;

import javassist.compiler.ast.Variable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")

public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() {
		// cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
							.id(1l)
							.nome("nome")
							.email("email@email.com")
							.senha("senha").build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);				
		
		// acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		// verificacao
		Assertions.assertNotNull(usuarioSalvo);
		Assertions.assertEquals(usuarioSalvo.getId(), 1l);
		Assertions.assertEquals(usuarioSalvo.getNome(), "nome");
		Assertions.assertEquals(usuarioSalvo.getEmail(), "email@email.com");
		Assertions.assertEquals(usuarioSalvo.getSenha(), "senha");		
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioExpection.class, () -> {
			// cenario 
			String email = "email@email.com";
			Usuario usuario = Usuario.builder().email(email).build();
			Mockito.doThrow(RegraNegocioExpection.class).when(service).validarEmail(email);
	
			// acao - tentar salvar (n pode)
			service.salvarUsuario(usuario);
			
			// verificacao - espero que nunca tenha salvado
			Mockito.verify(repository, Mockito.never()).save(usuario);
		});		
	}
	
	@Test
	public void deveAutenticasUmUsuarioComSucesso() {

		// cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		// acao
		Usuario result = service.autenticar(email, senha);
		
		// verificacao
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		Throwable exception = Assertions.assertThrows(ErroAutenticacao.class, () -> {
			// cenario
			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
			
			// acao
			service.autenticar("email@email.com", "senha");
		});
		
		String errorMessage = exception.getMessage();
		Assertions.assertTrue(errorMessage.equals("Usuário não encontrado para o email informado."));	
	}
	
	public String lorem() {
		String a = "oi"; 
		return a;
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {	
		
		// cenario
		String email = "email@email.com";
		String senha = "senha";
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		// acao
		Throwable exception =  Assertions.assertThrows( ErroAutenticacao.class, () -> {
			service.autenticar("email@email.com", "senhaErrada123");
		});
		
		String errorMessage = exception.getMessage();
		Assertions.assertTrue(errorMessage.equals("Senha inválida."));
	}
	
	
	@Test
	public void deveValidarEmail() {	
		// cenario (fake)
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		// acao
		service.validarEmail("email@email.com");
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrato() {
		Assertions.assertThrows(RegraNegocioExpection.class, () -> {
			//cenario
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);	
			
			//acao
			service.validarEmail("email@email.com");
		});
	}
}