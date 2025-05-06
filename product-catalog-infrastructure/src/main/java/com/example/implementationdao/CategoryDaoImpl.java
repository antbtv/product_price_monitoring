package com.example.implementationdao;

import com.example.MessageSources;
import com.example.dao.CategoryDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.Category;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDaoImpl implements CategoryDao {

    private static final String SELECT_ALL = "SELECT c FROM Category c LEFT JOIN FETCH c.subCategories";

    private final HibernateSessionFactory hibernateSessionFactory;
    private static final Logger logger = LogManager.getLogger(CategoryDaoImpl.class);

    public CategoryDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public Category create(Category category) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.persist(category);
        logger.info(MessageSources.SUCCESS_CREATE);
        return category;
    }

    @Override
    public Category findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        Category category = session.get(Category.class, id);

        if (category == null) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        } else {
            logger.info(MessageSources.SUCCESS_READ_ONE);
        }

        return category;
    }

    @Override
    public void update(Category category) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.merge(category);
        logger.info(MessageSources.SUCCESS_UPDATE);
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        Category category = session.get(Category.class, id);

        if (category != null) {
            session.remove(category);
            logger.info(MessageSources.SUCCESS_DELETE);
        } else {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Override
    public List<Category> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<Category> categories = session.createQuery(SELECT_ALL, Category.class).list();
        logger.info(MessageSources.SUCCESS_READ_MANY);

        return categories;
    }
}

