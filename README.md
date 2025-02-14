# TP-SSAD
### Trabalho desenvolvido em grupo para a matéria de  serviços de suporte a aplicações distribuídas

 ## Sobre o jogo
 
<p><b>SuperDia</b>é um sistema desenvolvido para automatizar os processos do centro de distribuição (CD) do Supermercado Dia. O objetivo é gerenciar estoque, vendas e compras de maneira eficiente, integrando-se com unidades físicas, site e aplicativo móvel.
</p>
<p>Toda a lógica de negócio está implementada em EJB (Enterprise Java Beans) e disponibilizada por um servidor de aplicação. As camadas de interação com o usuário são responsáveis apenas pelo consumo da lógica de negócio, garantindo separação de responsabilidades. </p>
  
## O que é necessário?
  - Para montar esse projeto é necessário que se tenha algumas coisas:
    
    || TIPO | LINK | INFORMAÇÕES ||
    | --- | --- | --- | --- | --- |
    || Banco de dados | [PgAdmin](https://www.pgadmin.org/download/) | Este ambiente irá gravar os dados corretamento, gravando esses dados neste caso com o JPA ||
    || IDE | [EclipseEE](https://www.eclipse.org/downloads/packages/release/2023-12/r) | Escolha a opção Eclipse IDE for Enterprise Java and Web Developers ||
    || JDK |  [JDK](https://www.oracle.com/br/java/technologies/downloads/) | No EclipseEE e nos projetos desse repositório utilizaremos a versão 17 mais você pode instalar uma versão mais nova que não trará problema nenhum é só não mudar as configurações dos projetos ||
    || Versão Java | [Java](https://www.java.com/en/download/) | No EclipseEE e nos projetos utilizaremos a versão 17. ||
    || Servidor | [WildFly](https://www.wildfly.org/downloads/) | Instale a versão WildFly Distribution 34.0.1 final	zip Este servidor é extremamente necessário para realizarmos a visualização do java na web e executarmos o EJB. usados para o desenvolvimento e implantação de aplicações distribuídas baseadas em componentes que são escaláveis, transacionais, e seguros. ||
    || EJB | [EJB](https://youtu.be/527dcam45iE?si=30MFb5ADw-EYaQTf) | O EJB (Enterprise JavaBeans) contém a lógica de negócio que atua sobre os dados de negócio.  ||
    || Maven | [Maven](https://www.devmedia.com.br/introducao-ao-maven/25128) | Maven é uma ferramenta de gerenciamento e automação de construção de projetos. Ele fornece diversas funcionalidades adicionais através do uso de plugins e estimula o emprego de melhores práticas de organização, desenvolvimento e manutenção de projetos. | 

## Proposta de Projeto
<p>O projeto consiste em: </p>

 | | ÁREA | DESCRIÇÃO | |
   | --- | --- | --- | --- |
   || Projeto EJB SuperDia  | Implementação das 3 interfaces criadas no projeto SuperDiaClient no pacote br.com.superdia.sessionbean ||
   || ProjetoEJB SuperDiaClient | - Implementação no pacote br.com.superdia.modelo as classes Produto, Pessoa e Usuário (Teremos 3 perfis: Administrador, Caixa e Cliente). <br><br> - Implementação no pacote br.com.superdia.interfaces as interfaces para a persistencia de dados das 3 classes criadas acima. ||
   || Implementação JAX-RS |  Implementar o JAX-RS para realizar as operações CRUD nas 3 classes modelos ||
   || Validar Autenticação | Validar a autenticação dos usuários e permitir que o: <br> <br> <strong>- Administrador:</strong> Gerencia o estoque.  <br> <strong>- Caixa:</strong> Efetue vendas. <br> <strong>- Cliente: </strong> Compre online. ||
   || Aplicação Web | Responsável por permitir que os usuários  de perfil Administrador gerenciamento de estoque (cadatro ou importação de produtos). ||
   || Aplicação Desktop | Responsável por permitir aos usuários de perfil Caixa a cadastrarem as compras efetuadas pelo cliente. ||
   || Loja online | Responsável por permitir aos usuários de perfil Cliente adicionem produtos no seu carrinho de compras e finalizam a compra. ||
   || Aplicação Mobile | Responsável por permitir aos usuários de perfil Cliente adicionem produtos no seu carrinho de compras e finalizam a compra. ||
   

## Vídeo de apresentação

<p> A seguir temos um vídeo de apresentação das interfaces do projeto, tanto desktop quanto a web</p>

https://github.com/user-attachments/assets/1c84ac4f-5256-45ff-a60f-c31f249c76ac

## Colaboradores do Projeto

  - A seguir a tabela contém um link para o repositório de todos os colaboradores desse projeto.

    || NOME | LINK DO  GITHUB ||
      | --- | --- | --- | --- |
      || Letícia Oliveira |[LeticiaKOSilva](https://github.com/LeticiaKOSilva) ||
      || Vinícius José | [ViniciusJPSilva](https://github.com/ViniciusJPSilva) ||
      || Vitor Samuel | [VitorSVNascimento](https://github.com/VitorSVNascimento) ||
      || Vitor Silvestre | [VitorST1](https://github.com/VitorST1) ||
      || Yury Oliveira | [YuryOAraujo](https://github.com/YuryOAraujo) ||

