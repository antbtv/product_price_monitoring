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

        try {
            session.persist(category);
            logger.debug(MessageSources.SUCCESS_CREATE);
            return category;
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_CREATE);
            return null;
        }
    }

    @Override
    public Category findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        Category category = null;

        try {
            category = session.get(Category.class, id);

            if (category == null) {
                logger.error(MessageSources.FAILURE_READ_ONE);
            } else {
                logger.debug(MessageSources.SUCCESS_READ_ONE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        }
        return category;
    }

    @Override
    public void update(Category category) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            session.merge(category);
            logger.debug(MessageSources.SUCCESS_UPDATE);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_UPDATE);
        }
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            Category category = session.get(Category.class, id);

            if (category != null) {
                session.remove(category);
                logger.debug(MessageSources.SUCCESS_DELETE);
            } else {
                logger.error(MessageSources.FAILURE_DELETE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Override
    public List<Category> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<Category> categories = null;

        try {
            categories = session.createQuery(SELECT_ALL, Category.class).list();
            logger.debug(MessageSources.SUCCESS_READ_MANY);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
        }
        return categories;
    }
}

