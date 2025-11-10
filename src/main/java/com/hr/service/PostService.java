package com.hr.service;

import com.hr.entity.Comment;
import com.hr.entity.Like;
import com.hr.entity.Post;
import com.hr.repository.CommentRepository;
import com.hr.repository.LikeRepository;
import com.hr.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;


//    /** ✅ 게시글 목록 */
//    public Page<Post> list(String q, String category, Pageable pageable) {
//        if (category != null && !category.trim().isEmpty()) {
//            // ✅ category 값이 있을 경우 해당 카테고리 게시글만
//            return postRepository.findByCategory(category, pageable);
//        } else if (q != null && !q.trim().isEmpty()) {
//            // ✅ 검색어(q)가 있을 경우 제목/내용 검색
//            return postRepository.findByTitleContainingOrContentContaining(q, q, pageable);
//        } else {
//            // ✅ 아무 조건도 없을 때 전체 조회
//            return postRepository.findAll(pageable);
//        }
//    }

    public Page<Post> list(String q, String category, Pageable pageable) {
        Page<Post> posts;

        if (category != null && !category.trim().isEmpty()) {
            posts = postRepository.findByCategory(category, pageable);
        } else if (q != null && !q.trim().isEmpty()) {
            posts = postRepository.findByTitleContainingOrContentContaining(q, q, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }

        // 🔹 각 게시글의 댓글 수 세기
        posts.forEach(post -> {
            long count = commentRepository.countByPostId(post.getId());
            post.setCommentCount(count);
        });

        return posts;
    }



    /** ✅ 게시글 상세 */
    public Optional<Post> get(Long id) {
        return postRepository.findById(id);
    }

    /** ✅ 좋아요 토글 (계정당 1회만 가능) */
    @Transactional
    public boolean toggleLike(Long postId, String memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Optional<Like> existing = likeRepository.findByPost_IdAndMemberId(postId, memberId);

        if (existing.isPresent()) {
            // 이미 좋아요 눌렀으면 취소
            likeRepository.delete(existing.get());
            post.setLikes(post.getLikes() - 1);
            return false; // 좋아요 취소 상태
        } else {
            // 처음 누름
            Like like = new Like();
            like.setPost(post);
            like.setMemberId(memberId);
            likeRepository.save(like);
            post.setLikes(post.getLikes() + 1);
            return true; // 좋아요 상태
        }
    }
    public boolean hasUserLiked(Long postId, String memberId) {
        return likeRepository.findByPost_IdAndMemberId(postId, memberId).isPresent();
    }

    /** ✅ 댓글 추가 */
    @Transactional
    public Comment addComment(Long postId, String writer, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setWriter(writer);
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    /** ✅ 댓글 조회 */
    public List<Comment> getCommentsByPostId(Long postId) {
        // ✅ 정확한 메서드명으로 수정
        return commentRepository.findByPost_IdOrderByCreateDateDesc(postId);
    }
    /** ✅ 게시글 저장 */
    @Transactional
    public Post save(Post post) {
        return postRepository.save(post);
    }
    //조회수
    @Transactional
    public void increaseViews(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        int current = post.getViews() == null ? 0 : post.getViews();
        post.setViews(current + 1);
        postRepository.save(post);
    }


    @Transactional
    public Post getAndIncreaseViews(Long id, boolean increaseViews) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 조회수 증가 여부를 파라미터로 제어
        if (increaseViews) {
            int currentViews = (post.getViews() == null) ? 0 : post.getViews();
            post.setViews(currentViews + 1);
        }

        postRepository.save(post);
        return post;
    }

    // 게시글 수정
    public Post updatePost(Long id, Post updatedPost) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        existing.setTitle(updatedPost.getTitle());
        existing.setContent(updatedPost.getContent());
        return postRepository.save(existing);
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("게시글이 존재하지 않습니다.");
        }
        postRepository.deleteById(id);
    }

    // ✅ 댓글 수정 기능
    public Comment updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        comment.setContent(newContent);
        return commentRepository.save(comment); // DB에 UPDATE 실행
    }

    // ✅ 댓글 삭제 서비스 메서드
    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("댓글이 존재하지 않습니다.");
        }
        commentRepository.deleteById(commentId);
    }

}
