package com.hr.service;

import com.hr.entity.Post;
import com.hr.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Page<Post> list(String q, String category, Pageable pageable) {
        Specification<Post> spec = (root, query, cb) -> cb.conjunction();

        if (category != null && !"ALL".equalsIgnoreCase(category)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (q != null && !q.isBlank()) {
            String like = "%" + q + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(root.get("title"), like),
                    cb.like(root.get("content"), like),
                    cb.like(root.get("createId"), like)
            ));
        }
        return postRepository.findAll(spec, pageable);
    }

    public Optional<Post> get(Long id) {
        return postRepository.findById(id);
    }

    public Post create(Post post) {
        post.setId(null);
        if (post.getViews() == null) post.setViews(0);
        return postRepository.save(post);
    }

    public Post update(Long id, Post req) {
        return postRepository.findById(id).map(p -> {
            p.setTitle(req.getTitle());
            p.setContent(req.getContent());
            p.setCategory(req.getCategory());
            // 작성자 변경은 일반적으로 막음(원하면 해제)
            return postRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    public Post increaseViews(Long id) {
        Post p = postRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        p.setViews(p.getViews() + 1);
        return postRepository.save(p);
    }
}
