package com.example.implementationdao;

import com.example.MessageSources;
import com.example.dao.PriceDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.Price;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceDaoImpl implements PriceDao {

    private static final String SELECT_ALL = "SELECT p FROM Price p JOIN FETCH p.product JOIN FETCH p.store";
    private static final String SELECT_BY_PRODUCT_ID = "SELECT p FROM Price p WHERE p.product.productId = :productId";

    private final HibernateSessionFactory hibernateSessionFactory;
    private static final Logger logger = LogManager.getLogger(PriceDaoImpl.class);

    public PriceDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public Price create(Price price) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            session.persist(price);
            logger.debug(MessageSources.SUCCESS_CREATE);
            return price;
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_CREATE);
            return null;
        }
    }

    @Override
    public Price findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        Price price = null;

        try {
            price = session.get(Price.class, id);

            if (price == null) {
                logger.error(MessageSources.FAILURE_READ_ONE);
            } else {
                logger.debug(MessageSources.SUCCESS_READ_ONE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        }
        return price;
    }

    @Override
    public void update(Price price) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            session.merge(price);
            logger.debug(MessageSources.SUCCESS_UPDATE);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_UPDATE);
        }
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            Price price = session.get(Price.class, id);

            if (price != null) {
                session.remove(price);
                logger.debug(MessageSources.SUCCESS_DELETE);
            } else {
                logger.error(MessageSources.FAILURE_DELETE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Override
    public List<Price> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<Price> prices = null;

        try {
            prices = session.createQuery(SELECT_ALL, Price.class).list();
            logger.debug(MessageSources.SUCCESS_READ_MANY);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
        }
        return prices;
    }

    @Override
    public List<Price> findByProductId(Long productId) {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<Price> prices = null;

        try {
            prices = session.createQuery(SELECT_BY_PRODUCT_ID, Price.class)
                    .setParameter("productId", productId)
                    .list();
            logger.debug(MessageSources.SUCCESS_READ_MANY);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
        }
        return prices;
    }
}
