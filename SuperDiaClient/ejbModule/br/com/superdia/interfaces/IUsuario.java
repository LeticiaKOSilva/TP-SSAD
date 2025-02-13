package br.com.superdia.interfaces;

import java.util.List;

import br.com.superdia.modelo.Usuario;

public interface IUsuario {
	public void create(Usuario usuario);
	public void update(Usuario usuario);
	public void delete(Usuario usuario);
	public Usuario authentication(String email, String senha);
	public List<Usuario>getUsuarios();
	public Usuario getByEmail(String email);
}
