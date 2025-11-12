package com.hr.repository;

import com.hr.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    // ✅ 카테고리별 게시글 조회
    Page<Post> findByCategory(String category, Pageable pageable);

    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

}
