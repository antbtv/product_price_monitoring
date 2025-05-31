package com.example.dao;

import com.example.entity.Category;

import java.util.List;

public interface CategoryDao extends GenericDao<Category> {

    /**
     * Нахождение всех дочерних категорий
     *
     * @param parentId id родительской категории
     * @return список дочерних категорий
     */
    List<Category> findAllByParentId(Long parentId);
}
