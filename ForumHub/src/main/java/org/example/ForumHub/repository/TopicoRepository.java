package org.example.ForumHub.repository;

import org.example.ForumHub.model.Topico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    boolean existsByTituloAndMensagem(String titulo, String mensagem);

    // Método case-insensitive que usa lower() para comparar título e mensagem
    @Query("select count(t) > 0 from Topico t where lower(t.titulo) = lower(:titulo) and lower(t.mensagem) = lower(:mensagem)")
    boolean existsByTituloAndMensagemIgnoreCase(@Param("titulo") String titulo, @Param("mensagem") String mensagem);

    // Busca um tópico por título e mensagem (case-insensitive) para checar duplicatas na atualização
    @Query("select t from Topico t where lower(t.titulo) = lower(:titulo) and lower(t.mensagem) = lower(:mensagem)")
    Optional<Topico> findByTituloAndMensagemIgnoreCase(@Param("titulo") String titulo, @Param("mensagem") String mensagem);
}
