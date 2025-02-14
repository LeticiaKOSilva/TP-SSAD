package br.com.superdia.api;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import br.com.superdia.interfaces.ICarrinho;
import br.com.superdia.interfaces.IUsuario;
import br.com.superdia.modelo.Carrinho;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/carrinho")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped  
public class CarrinhoResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ICarrinho carrinhoService;

    @EJB
    private IUsuario usuarioService;

    public boolean isAuthenticated(String login, String senha) {
        return usuarioService.authentication(login, senha) != null;
    }

    @POST
    @Path("/create")
    public Response create(AuthRequest authRequest) {
        try {
            Carrinho carrinho = authRequest.getCarrinho();
            Carrinho createdCarrinho = carrinhoService.create(carrinho);
            return Response.status(Response.Status.CREATED).entity(createdCarrinho).build();
        } catch (Exception e) {
        	System.err.println(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao criar carrinho").build();
        }
    }
    
    @PUT
    @Path("/update")
    public Response update(AuthRequest authRequest) {
        try {
            Carrinho carrinho = authRequest.getCarrinho();
            Carrinho carrinhoBanco = carrinhoService.getCarrinhoById(carrinho.getId());
            
            merge(carrinho, carrinhoBanco);
            
            carrinhoService.update(carrinhoBanco);
            return Response.ok(carrinhoBanco).build();
        } catch (Exception e) {
        	System.err.println(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar carrinho").build();
        }
    }


    @POST
    @Path("/delete")
    public Response delete(AuthRequest authRequest) {
        try {
            if (!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
                return Response.status(Response.Status.UNAUTHORIZED).entity("Acesso Negado! Autenticação necessária").build();
            
            Carrinho carrinho = authRequest.getCarrinho();
            carrinhoService.delete(carrinho);
            return Response.ok().entity("Carrinho removido com sucesso!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao remover carrinho").build();
        }
    }

    @POST
    @Path("/{usuarioId}")
    public Response getCarrinhoByUsuario(@PathParam("usuarioId") Long usuarioId, AuthRequest authRequest) {
        try {
            if (!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
                return Response.status(Response.Status.UNAUTHORIZED).entity("Acesso Negado! Autenticação necessária").build();
            
            Carrinho carrinho = carrinhoService.getCarrinhoByUsuario(usuarioId);
            
            if (carrinho == null)
            	return Response.status(Response.Status.NO_CONTENT).build();
            
            return Response.ok().entity(carrinho).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar carrinho").build();
        }
    }
    
    @POST
    @Path("/delete/{usuarioId}")
    public Response deleteCarrinhoByUsuario(@PathParam("usuarioId") Long usuarioId, AuthRequest authRequest) {
        try {
            if (!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
                return Response.status(Response.Status.UNAUTHORIZED).entity("Acesso Negado! Autenticação necessária").build();
            
            Carrinho carrinho = carrinhoService.getCarrinhoByUsuario(usuarioId);
            
            if (carrinho == null)
            	return Response.status(Response.Status.NO_CONTENT).build();
            
            return Response.ok().entity(carrinho).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar carrinho").build();
        }
    }
    
    public static class AuthRequest {
        private String login;
        private String senha;
        private Carrinho carrinho;

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public Carrinho getCarrinho() { return carrinho; }
        public void setCarrinho(Carrinho carrinho) { this.carrinho = carrinho; }
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
