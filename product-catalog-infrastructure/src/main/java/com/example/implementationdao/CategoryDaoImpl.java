package com.example.implementationdao;

import com.example.dao.CategoryDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.Category;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDaoImpl implements CategoryDao {

    private final String SELECT_ALL = "SELECT c FROM Category c LEFT JOIN FETCH c.subCategories";

    private final HibernateSessionFactory hibernateSessionFactory;

    public CategoryDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

//  Стоит ли в таких методах именовать сущность как entity?
//  Увидел в других кодах, будет хорошей практикой?
    @Override
    public void create(Category category) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.persist(category);
    }

    @Override
    public Category findById(int id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        return session.get(Category.class, id);
    }

    @Override
    public void update(Category category) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.merge(category);
    }

    @Override
    public void delete(int id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        Category category = session.get(Category.class, id);
        if (category != null) {
            session.remove(category);
        }
    }

    @Override
    public List<Category> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();

        return session.createQuery(SELECT_ALL, Category.class).list();
    }
}
