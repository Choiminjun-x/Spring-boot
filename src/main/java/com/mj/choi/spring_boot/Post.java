package com.mj.choi.spring_boot;

public class Post {
    private Long id;
    private String title;
    private String content;

    Post(String title, String content) {
        validate(title, content);
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void update(String title, String content) {
        validate(title, content);
        this.title = title;
        this.content = content;
    }

    private void validate(String title, String content) {
        if (title == null) {
            throw new IllegalArgumentException("제목에 null이 올 수 없음");
        }

        if (content == null) {
            throw new IllegalArgumentException("컨텐츠에 null이 올 수 없음");
        }

        if (title.length() > 100) {
            throw new IllegalArgumentException("제목 100자 넘음");
        }
    }
}
