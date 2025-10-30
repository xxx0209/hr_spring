package com.hr.controller;

import com.hr.entity.Category;
import com.hr.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 카테고리 조회
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("list")
    public Page<Category> getAllPagingCategories(Pageable pageable) {
        return categoryService.getAllCategories(pageable);
    }

    // 카테고리 등록
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryService.save(category);
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.update(id, category);
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}

