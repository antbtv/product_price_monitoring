package com.example.dao.impl;

import com.example.dao.ProductDao;
import com.example.db.connection.HibernateSessionFactory;
import com.example.entity.Product;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    private static final String SELECT_ALL = "SELECT p FROM Product p JOIN FETCH p.category c";
    private static final String SELECT_BY_CATEGORY = "from Product p where p.category.categoryId = :categoryId";

    private final HibernateSessionFactory hibernateSessionFactory;

    public ProductDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public Product create(Product product) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.persist(product);
        return product;
    }

    @Override
    public Product findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        return session.get(Product.class, id);
    }

    @Override
    public void update(Product product) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.merge(product);
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        Product product = session.get(Product.class, id);

        if (product != null) {
            session.remove(product);
        }
    }

    @Override
    public List<Product> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        return session.createQuery(SELECT_ALL, Product.class).list();
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        Session session = hibernateSessionFactory.getCurrentSession();
        return session.createQuery(SELECT_BY_CATEGORY, Product.class)
                .setParameter("categoryId", categoryId)
                .list();
    }
}
