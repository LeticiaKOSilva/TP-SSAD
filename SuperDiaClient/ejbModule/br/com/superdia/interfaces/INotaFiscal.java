package br.com.superdia.interfaces;

import java.util.List;

import br.com.superdia.modelo.NotaFiscal;
import jakarta.ejb.Remote;

@Remote
public interface INotaFiscal {
	public void create(NotaFiscal notaFiscal);
	public void delete(NotaFiscal notaFiscal);
	public void update(NotaFiscal notaFiscal);
	public List<NotaFiscal>getProdutos();
}
