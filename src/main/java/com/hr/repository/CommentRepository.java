package com.hr.repository;

import com.hr.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // ✅ 정확한 이름 (엔티티 필드 post의 id 참조)
    List<Comment> findByPost_IdOrderByCreateDateDesc(Long postId);

    long countByPostId(Long postId);
}
