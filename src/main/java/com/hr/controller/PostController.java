package com.hr.controller;

import com.hr.entity.Post;
import com.hr.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // React 개발 서버
public class PostController {

    private final PostService postService;

    // 목록: 검색(q) + 카테고리(category) + 정렬(sort=key,dir) + 페이지(page, size)
    @GetMapping
    public Page<Post> list(
            @RequestParam(defaultValue = "ALL") String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,       // 프론트 1부터 -> 0으로 보정
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createDate,desc") String sort
    ) {
        String[] sp = sort.split(",");
        String sortKey = sp.length > 0 ? sp[0] : "createDate";
        Sort.Direction dir = (sp.length > 1 && "asc".equalsIgnoreCase(sp[1])) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(dir, sortKey));
        return postService.list(q, category, pageable);
    }

    // 상세(+조회수 증가 addView=true 시)
    @GetMapping("/{id}")
    public Post get(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean addView) {
        if (addView) return postService.increaseViews(id);
        return postService.get(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    // 생성
    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    // 수정
    @PutMapping("/{id}")
    public Post update(@PathVariable Long id, @RequestBody Post post) {
        return postService.update(id, post);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }
}
