package com.mj.choi.spring_boot;

public class PostResponse {
    private final Long id;
    private final String title;
    private final String content;


    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }
}
