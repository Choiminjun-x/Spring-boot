package com.mj.choi.spring_boot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PostTest {
    @Test
    void 정상_생성() {
        assertThatCode(() -> new Post("제목", "내용"))
                .doesNotThrowAnyException();
    }

    @Test
    void 제목이_100자면_정상() {
        assertThatCode(() -> new Post("a".repeat(100), "내용"))
                .doesNotThrowAnyException();
    }

    @Test
    void 제목이_null이면_예외() {
        assertThatThrownBy(() -> new Post(null, "내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 내용이_null이면_예외() {
        assertThatThrownBy(() -> new Post("제목", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 제목이_101자면_예외() {
        assertThatThrownBy(() -> new Post("a".repeat(101), "내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void update에_null이면_예외() {
        Post post = new Post("제목", "내용");

        assertThatThrownBy(() -> post.update(null, "새 내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void update로_값이_변경된다() {
        Post post = new Post("제목", "내용");

        post.update("새 제목", "새 내용");

        assertThat(post.getTitle()).isEqualTo("새 제목");
        assertThat(post.getContent()).isEqualTo("새 내용");
    }
}
