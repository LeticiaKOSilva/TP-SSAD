package br.com.superdia.interfaces;

import java.util.List;

import br.com.superdia.modelo.Item;
import jakarta.ejb.Remote;

@Remote
public interface IItem {
	public void create(Item item);
	public void delete(Item item);
	public void update(Item item);
	public List<Item>getProdutos();
}
