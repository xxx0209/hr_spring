package com.hr.service;

import com.hr.entity.Category;
import com.hr.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 전체 카테고리 조회
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 카테고리 등록
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    // 카테고리 삭제
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
