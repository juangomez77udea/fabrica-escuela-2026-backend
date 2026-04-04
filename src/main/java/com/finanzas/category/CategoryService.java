package com.finanzas.category;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(String name, String descripcion, String type) {
        if (categoryRepository.existsByNameAndType(name, type)) {
            throw new IllegalArgumentException("Esta categoría ya existe");
        }

        Category category = new Category(name, descripcion, type);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
    }

    public Category updateCategory(Long id, String name, String descripcion, String type) {
        Category category = getCategoryById(id);
        category.setName(name);
        category.setDescripcion(descripcion);
        category.setType(type);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
