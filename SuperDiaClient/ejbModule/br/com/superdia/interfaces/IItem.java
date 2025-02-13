package br.com.superdia.interfaces;

import java.util.List;

import br.com.superdia.modelo.Item;
import jakarta.ejb.Remote;

@Remote
public interface IItem {
	public Item create(Item item);
	public void delete(Item item);
	public void update(Item item);
	public List<Item>getProdutos();
	public Item getItemById(Long id);
}
