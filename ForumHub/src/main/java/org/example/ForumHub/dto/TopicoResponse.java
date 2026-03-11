package org.example.ForumHub.dto;

import org.example.ForumHub.model.StatusTopico;

import java.time.LocalDateTime;

public class TopicoResponse {
    private Long id;
    private String titulo;
    private String mensagem;
    private LocalDateTime dataCriacao;
    private StatusTopico status;
    private String autor;
    private String curso;

    public TopicoResponse() {
    }

    public TopicoResponse(Long id, String titulo, String mensagem, LocalDateTime dataCriacao, StatusTopico status, String autor, String curso) {
        this.id = id;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.autor = autor;
        this.curso = curso;
    }

    // getters

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public StatusTopico getStatus() {
        return status;
    }

    public String getAutor() {
        return autor;
    }

    public String getCurso() {
        return curso;
    }

    // converter da entidade para DTO
    public static TopicoResponse fromEntity(org.example.ForumHub.model.Topico topico) {
        return new TopicoResponse(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensagem(),
                topico.getDataCriacao(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso()
        );
    }
}

