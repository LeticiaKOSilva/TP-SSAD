package br.com.superdia.interfaces;

import java.util.List;

import br.com.superdia.modelo.Usuario;

public interface IUsuario {
	public void create(Usuario usuario);
	public void update(Usuario usuario);
	public void remove(Usuario usuario);
	public Usuario autentication(Usuario usuario);
	public List<Usuario>getUsuarios();
}
