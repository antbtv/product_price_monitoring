package com.example.implementationdao;

import com.example.MessageSources;
import com.example.dao.ProductDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    private static final String SELECT_ALL = "SELECT p FROM Product p JOIN FETCH p.category c";
    private static final String SELECT_BY_CATEGORY = "from Product p where p.category.categoryId = :categoryId";

    private final HibernateSessionFactory hibernateSessionFactory;
    private static final Logger logger = LogManager.getLogger(ProductDaoImpl.class);

    public ProductDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public Product create(Product product) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            session.persist(product);
            logger.debug(MessageSources.SUCCESS_CREATE);
            return product;
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_CREATE);
            return null;
        }
    }

    @Override
    public Product findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        Product product = null;

        try {
            product = session.get(Product.class, id);

            if (product == null) {
                logger.error(MessageSources.FAILURE_READ_ONE);
            } else {
                logger.debug(MessageSources.SUCCESS_READ_ONE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        }
        return product;
    }

    @Override
    public void update(Product product) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            session.merge(product);
            logger.debug(MessageSources.SUCCESS_UPDATE);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_UPDATE);
        }
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            Product product = session.get(Product.class, id);

            if (product != null) {
                session.remove(product);
                logger.debug(MessageSources.SUCCESS_DELETE);
            } else {
                logger.error(MessageSources.FAILURE_DELETE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Override
    public List<Product> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<Product> products = null;

        try {
            products = session.createQuery(SELECT_ALL, Product.class).list();
            logger.debug(MessageSources.SUCCESS_READ_MANY);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
        }
        return products;
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<Product> products = null;

        try {
            products = session.createQuery(SELECT_BY_CATEGORY, Product.class)
                    .setParameter("categoryId", categoryId)
                    .list();
            logger.debug(MessageSources.SUCCESS_READ_MANY);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
        }
        return products;
    }
}
