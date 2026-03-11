package org.example.ForumHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.ForumHub.model.Topico;

public class TopicoRequest {

    @NotBlank
    @Size(max = 200)
    private String titulo;

    @NotBlank
    @Size(max = 10000)
    private String mensagem;

    @NotBlank
    @Size(max = 100)
    private String autor;

    @NotBlank
    @Size(max = 100)
    private String curso;

    public TopicoRequest() {
    }

    public TopicoRequest(String titulo, String mensagem, String autor, String curso) {
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.autor = autor;
        this.curso = curso;
    }

    public Topico toEntity() {
        // Normaliza espaços nas extremidades
        return new Topico(
                titulo == null ? null : titulo.trim(),
                mensagem == null ? null : mensagem.trim(),
                autor == null ? null : autor.trim(),
                curso == null ? null : curso.trim()
        );
    }

    // getters e setters

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
}

