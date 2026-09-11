package com.mj.choi.spring_boot;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {

    private final Map<Long, Post> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    // Put
    public Post save(Post post) {
        Long id = sequence.incrementAndGet();
        post.assignId(id);
        store.put(id, post);
        return post;
    }

    // Get all
    public List<Post> findAll() {
        return new ArrayList<>( store.values());
    }

    // Get {id}
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}
