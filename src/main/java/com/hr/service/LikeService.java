package com.hr.service;

import com.hr.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;  // LikeRepository 주입

    // 특정 게시글의 좋아요 수 조회
    public int getLikesCount(Long postId) {
        return likeRepository.countLikesByPostId(postId);  // Repository에서 좋아요 수를 조회
    }

    /** ✅ 특정 게시글을 좋아요한 사용자 목록 조회 */
    public List<String> getUsersWhoLikedPost(Long postId) {
        return likeRepository.findUsersWhoLikedPost(postId);
    }
}
