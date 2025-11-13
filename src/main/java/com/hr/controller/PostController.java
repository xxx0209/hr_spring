package com.hr.controller;

import com.hr.dto.MemberDto;
import com.hr.entity.Comment;
import com.hr.entity.Post;
import com.hr.security.CustomUserDetails;
import com.hr.service.LikeService;
import com.hr.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    /** ✅ 게시글 상세 조회 (조회수 증가 여부 선택 가능) */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id,
                                     @RequestParam(required = false, defaultValue = "true") boolean view,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 조회수 증가 여부를 파라미터로 전달
        Post post = postService.getAndIncreaseViews(id, view);

        boolean liked = false;
        if (userDetails != null) {
            liked = postService.hasUserLiked(id, userDetails.getName()); // principal.getName() 대신 userDetails.getUsername()
        }

        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("liked", liked);

        return ResponseEntity.ok(response);
    }

    /** ✅ 게시글 목록 조회 */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                postService.list(q, category, PageRequest.of(page, size))
        );
    }

    /** ✅ 게시글 좋아요 (1인 1회 토글 방식) */
    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberId = (userDetails != null) ? userDetails.getName() : "익명"; // principal.getName() 대신 userDetails.getUsername()
        boolean liked = postService.toggleLike(id, memberId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    /** ✅ 댓글 등록 */
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal CustomUserDetails userDetails  // @AuthenticationPrincipal 추가
    ) {
        try {
            // 로그인된 사용자가 있다면, 그 사용자의 이름을 가져옴
            String writer = (userDetails != null) ? userDetails.getName() : "익명"; // userDetails.getName() 사용
            String writerId = (userDetails != null) ? userDetails.getMemberId() : "-";
            String content = payload.get("content") != null ? payload.get("content").toString().trim() : "";

            if (content.isEmpty()) throw new RuntimeException("댓글 내용이 비어있습니다.");

            Comment saved = postService.addComment(id, writer,content,writerId);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    // 댓글 목록 조회 (GET)
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long id) {
        List<Comment> comments = postService.getCommentsByPostId(id);
        return ResponseEntity.ok(comments);
    }

    /** ✅ 게시글 등록 */
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String writerName = (userDetails != null) ? userDetails.getName() : post.getMemberName(); // userDetails.getName() 사용
            if (writerName == null || writerName.trim().isEmpty()) writerName = "익명";
            post.setMemberName(writerName);
            Post saved = postService.save(post);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/like-status")
    public ResponseEntity<?> checkLikeStatus(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberId = (userDetails != null) ? userDetails.getName() : "익명"; // userDetails.getUsername() 사용
        boolean liked = postService.hasUserLiked(id, memberId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @Autowired
    private LikeService likeService;  // LikeService 사용

    // 게시글의 좋아요 수 조회 API
    @GetMapping("/getLikesCount")
    public int getLikesCount(@RequestParam("postId") Long postId) {
        return likeService.getLikesCount(postId);  // LikeService 호출
    }

    /** ✅ 좋아요 누른 사용자 목록 조회 */
    @GetMapping("/{id}/likes")
    public ResponseEntity<List<String>> getLikes(@PathVariable Long id) {
        List<String> usersWhoLiked = likeService.getUsersWhoLikedPost(id);
        return ResponseEntity.ok(usersWhoLiked);
    }

    /** ✅ 게시글 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        try {
            Post updated = postService.updatePost(id, updatedPost);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** ✅ 게시글 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok(Map.of("message", "게시글이 삭제되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** ✅ 댓글 수정 */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> payload
    ) {
        try {
            String content = payload.get("content");
            if (content == null || content.trim().isEmpty()) {
                throw new RuntimeException("댓글 내용이 비어있습니다.");
            }

            Comment updated = postService.updateComment(commentId, content.trim());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** ✅ 댓글 삭제 */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            postService.deleteComment(commentId);
            return ResponseEntity.ok(Map.of("message", "댓글이 삭제되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
