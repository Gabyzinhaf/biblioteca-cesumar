/*
 * Sistema Web - Biblioteca Universidade Cesumar
 * Desenvolvido com Java Servlet, JSP e JSF seguindo o padrão MVC
 * Funcionalidades: Cadastro, Listagem e Exclusão de Livros
 */

// ===== MODEL =====
// Livro.java
package model;

public class Livro {
    private String titulo;
    private String autor;
    private int ano;
    private String isbn;

    public Livro(String titulo, String autor, int ano, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.isbn = isbn;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAno() { return ano; }
    public String getIsbn() { return isbn; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setAno(int ano) { this.ano = ano; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}

// LivroDAO.java
package model;

import java.util.ArrayList;
import java.util.List;

public class LivroDAO {
    private static List<Livro> livros = new ArrayList<>();

    public static void adicionar(Livro livro) {
        livros.add(livro);
    }

    public static List<Livro> listar() {
        return new ArrayList<>(livros);
    }

    public static void excluirPorISBN(String isbn) {
        livros.removeIf(l -> l.getIsbn().equals(isbn));
    }
}

// ===== CONTROLLER =====
// LivroServlet.java
package controller;

import model.Livro;
import model.LivroDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/LivroServlet")
public class LivroServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        if ("cadastrar".equals(acao)) {
            String titulo = request.getParameter("titulo");
            String autor = request.getParameter("autor");
            String anoStr = request.getParameter("ano");
            String isbn = request.getParameter("isbn");

            if (titulo == null || autor == null || anoStr == null || isbn == null ||
                titulo.isBlank() || autor.isBlank() || anoStr.isBlank() || isbn.isBlank()) {
                request.setAttribute("erro", "Preencha todos os campos corretamente.");
                request.getRequestDispatcher("cadastro.jsp").forward(request, response);
                return;
            }

            try {
                int ano = Integer.parseInt(anoStr);
                Livro livro = new Livro(titulo.trim(), autor.trim(), ano, isbn.trim());
                LivroDAO.adicionar(livro);
                response.sendRedirect("listar.jsp");
            } catch (NumberFormatException e) {
                request.setAttribute("erro", "Ano inválido.");
                request.getRequestDispatcher("cadastro.jsp").forward(request, response);
            }

        } else if ("excluir".equals(acao)) {
            String isbn = request.getParameter("isbn");
            LivroDAO.excluirPorISBN(isbn);
            response.sendRedirect("listar.jsp");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("livros", LivroDAO.listar());
        request.getRequestDispatcher("listar.jsp").forward(request, response);
    }
}

// ===== VIEW =====
// cadastro.jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cadastro de Livro</title>
    <style>
        body { font-family: Arial; background: #f9f9f9; padding: 20px; }
        form { background: #fff; padding: 20px; border-radius: 8px; width: 400px; box-shadow: 0 0 10px #ccc; }
        input[type=text], input[type=submit] {
            width: 100%; padding: 10px; margin: 8px 0; border: 1px solid #ccc; border-radius: 4px;
        }
        input[type=submit] {
            background-color: #007bff; color: white; cursor: pointer;
        }
        .erro { color: red; font-weight: bold; }
    </style>
</head>
<body>
    <h2>Cadastro de Livro</h2>
    <form action="LivroServlet" method="post">
        <input type="hidden" name="acao" value="cadastrar">
        <label>Título:</label>
        <input type="text" name="titulo">

        <label>Autor:</label>
        <input type="text" name="autor">

        <label>Ano de Publicação:</label>
        <input type="text" name="ano">

        <label>ISBN:</label>
        <input type="text" name="isbn">

        <input type="submit" value="Cadastrar">
    </form>

    <div class="erro">
        <% String erro = (String) request.getAttribute("erro");
           if (erro != null) { out.print(erro); } %>
    </div>

    <br>
    <a href="listar.jsp">Ver lista de livros</a>
</body>
</html>

// listar.jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Livro, model.LivroDAO" %>
<!DOCTYPE html>
<html>
<head>
    <title>Lista de Livros</title>
    <style>
        body { font-family: Arial; padding: 20px; background: #f0f0f0; }
        table { width: 100%; border-collapse: collapse; background: #fff; box-shadow: 0 0 10px #ccc; }
        th, td { padding: 10px; border: 1px solid #ddd; text-align: center; }
        th { background-color: #007bff; color: white; }
        form { margin: 0; }
        input[type=submit] {
            background-color: #dc3545; border: none; padding: 5px 10px; color: white; border-radius: 4px; cursor: pointer;
        }
    </style>
</head>
<body>
    <h2>Livros Cadastrados</h2>
    <table>
        <tr>
            <th>Título</th>
            <th>Autor</th>
            <th>Ano</th>
            <th>ISBN</th>
            <th>Ações</th>
        </tr>
        <% for (Livro livro : LivroDAO.listar()) { %>
        <tr>
            <td><%= livro.getTitulo() %></td>
            <td><%= livro.getAutor() %></td>
            <td><%= livro.getAno() %></td>
            <td><%= livro.getIsbn() %></td>
            <td>
                <form action="LivroServlet" method="post">
                    <input type="hidden" name="acao" value="excluir">
                    <input type="hidden" name="isbn" value="<%= livro.getIsbn() %>">
                    <input type="submit" value="Excluir">
                </form>
            </td>
        </tr>
        <% } %>
    </table>
    <br>
    <a href="cadastro.jsp">Cadastrar novo livro</a>
</body>
</html>
