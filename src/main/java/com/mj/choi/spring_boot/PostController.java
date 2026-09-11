package com.mj.choi.spring_boot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;

    PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Post create(@RequestBody PostCreateRequest request) {
        Post post = new Post(request.getTitle(), request.getContent());
        return postRepository.save(post);
    }

    @GetMapping
    List<Post> findAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    Post findOne(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    Post Update(@PathVariable Long id, @RequestBody PostCreateRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        post.update(request.getTitle(), request.getContent());
        return post;
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        if (!postRepository.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}