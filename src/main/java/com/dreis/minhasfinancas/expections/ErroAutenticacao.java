package com.dreis.minhasfinancas.expections;

public class ErroAutenticacao extends RuntimeException {
	
	public ErroAutenticacao(String mensagem) {
		super(mensagem);		
	}
}
