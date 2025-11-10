package com.hr.repository;

import com.hr.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPost_IdAndMemberId(Long postId, String memberId);
    int countLikesByPostId(Long postId);

    @Query("SELECT l.memberId FROM Like l WHERE l.post.id = :postId")
    List<String> findUsersWhoLikedPost(@Param("postId") Long postId);

}
