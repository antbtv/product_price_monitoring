package com.example.repository;

import com.example.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Получить все категории с подкатегориями (fetch join)
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories")
    List<Category> findAllWithSubCategories();

    /**
     * Получить подкатегории по id родительской категории
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.parent.categoryId = :parentId")
    List<Category> findAllByParentId(@Param("parentId") Long parentId);
}