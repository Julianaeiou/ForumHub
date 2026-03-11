package org.example.ForumHub.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.ForumHub.dto.TopicoRequest;
import org.example.ForumHub.dto.TopicoResponse;
import org.example.ForumHub.model.Topico;
import org.example.ForumHub.repository.TopicoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    private final TopicoRepository topicoRepository;

    public TopicoController(TopicoRepository topicoRepository) {
        this.topicoRepository = topicoRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Void> criarTopico(@Valid @RequestBody TopicoRequest request, UriComponentsBuilder uriBuilder) {
        // Normaliza e verifica duplicata (case-insensitive)
        String titulo = request.getTitulo() == null ? null : request.getTitulo().trim();
        String mensagem = request.getMensagem() == null ? null : request.getMensagem().trim();

        if (topicoRepository.existsByTituloAndMensagemIgnoreCase(titulo, mensagem)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Topico topico = request.toEntity();
        try {
            Topico saved = topicoRepository.save(topico);
            var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(saved.getId()).toUri();
            return ResponseEntity.created(uri).build();
        } catch (DataIntegrityViolationException ex) {
            // Protege contra condições de corrida onde a constraint única do DB falhe
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TopicoResponse>> listarTopicos() {
        List<Topico> topicos = topicoRepository.findAll();
        List<TopicoResponse> responses = topicos.stream()
                .map(TopicoResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponse> detalharTopico(@PathVariable Long id) {
        return topicoRepository.findById(id)
                .map(topico -> ResponseEntity.ok(TopicoResponse.fromEntity(topico)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoResponse> atualizarTopico(@PathVariable Long id, @Valid @RequestBody TopicoRequest request) {
        Optional<Topico> optionalTopico = topicoRepository.findById(id);
        if (optionalTopico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String titulo = request.getTitulo() == null ? null : request.getTitulo().trim();
        String mensagem = request.getMensagem() == null ? null : request.getMensagem().trim();

        // Checa duplicata case-insensitive: se existir outro topico com mesmo titulo+mensagem e id diferente -> conflict
        Optional<Topico> existing = topicoRepository.findByTituloAndMensagemIgnoreCase(titulo, mensagem);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Topico topico = optionalTopico.get();
        topico.setTitulo(titulo);
        topico.setMensagem(mensagem);
        topico.setAutor(request.getAutor() == null ? null : request.getAutor().trim());
        topico.setCurso(request.getCurso() == null ? null : request.getCurso().trim());

        Topico saved = topicoRepository.save(topico);
        return ResponseEntity.ok(TopicoResponse.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluirTopico(@PathVariable Long id) {
        Optional<Topico> optionalTopico = topicoRepository.findById(id);
        if (optionalTopico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        topicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
