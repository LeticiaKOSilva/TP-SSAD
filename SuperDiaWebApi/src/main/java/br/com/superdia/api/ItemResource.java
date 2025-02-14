package br.com.superdia.api;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import br.com.superdia.interfaces.IItem;
import br.com.superdia.interfaces.IUsuario;
import br.com.superdia.modelo.Item;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/item")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped  
public class ItemResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private IItem itemService;

    @EJB
    private IUsuario usuarioService;

    public boolean isAuthenticated(String login, String senha) {
        return usuarioService.authentication(login, senha) != null;
    }

    @POST
    @Path("/create")
    public Response create(AuthRequest authRequest) {
        try {
            Item item = authRequest.getItem();
            Item createdItem = itemService.create(item);
            
            return Response.status(Response.Status.CREATED).entity(createdItem).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao criar item").build();
        }
    }

    @PUT
    @Path("/update")
    public Response update(AuthRequest authRequest) {
        try {
            Item item = authRequest.getItem();
            Item itemBanco = itemService.getItemById(item.getId());
            
            merge(item, itemBanco);
            
            itemService.update(itemBanco);
            return Response.ok(itemBanco).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar item").build();
        }
    }

    @POST
    @Path("/delete")
    public Response delete(AuthRequest authRequest) {
        try {            
            Item item = authRequest.getItem();
            itemService.delete(item);
            return Response.ok().entity("Item removido com sucesso!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao remover item").build();
        }
    }

    @POST
    @Path("/getItens")
    public Response getItens(AuthRequest authRequest) {
        try {
            if (!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
                return Response.status(Response.Status.UNAUTHORIZED).entity("Acesso Negado! Autenticação necessária").build();
            
            List<Item> itens = itemService.getProdutos();
            return Response.ok(itens).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar itens").build();
        }
    }

    public static class AuthRequest {
        private String login;
        private String senha;
        private Item item;

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public Item getItem() { return item; }
        public void setItem(Item item) { this.item = item; }
    }
    
    public static <T> void merge(T source, T target) throws IllegalAccessException {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target objects cannot be null");
        }

        Class<?> clazz = source.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            
            field.setAccessible(true);
            Object sourceValue = field.get(source);
            if (sourceValue != null) {
                field.set(target, sourceValue);
            }
        }
    }
}
