package com.hr.service;

import com.hr.entity.Category;
import com.hr.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 전체 카테고리 조회
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 전체 카테고리 조회(페이징적용)
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    // 카테고리 등록
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    // 카테고리 수정
    public Category update(Long id, Category updateCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(updateCategory.getName());
        category.setColor(updateCategory.getColor());
        category.setActive(updateCategory.getActive());
        return categoryRepository.save(category);
    }

    // 카테고리 삭제
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setActive(false);
        categoryRepository.save(category);
    }
}
