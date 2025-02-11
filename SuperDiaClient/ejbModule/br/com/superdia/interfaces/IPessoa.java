package br.com.superdia.interfaces;

import java.util.List;

import br.com.superdia.modelo.Pessoa;

public interface IPessoa {
	Pessoa create(Pessoa pessoa);
	void delete(Pessoa pessoa);
	void update(Pessoa pessoa);
	List<Pessoa> getPessoas();

}
