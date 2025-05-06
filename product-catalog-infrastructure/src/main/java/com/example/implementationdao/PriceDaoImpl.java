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

        session.persist(price);
        logger.info(MessageSources.SUCCESS_CREATE);
        return price;
    }

    @Override
    public Price findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        Price price = session.get(Price.class, id);

        if (price == null) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        } else {
            logger.info(MessageSources.SUCCESS_READ_ONE);
        }

        return price;
    }

    @Override
    public void update(Price price) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.merge(price);
        logger.info(MessageSources.SUCCESS_UPDATE);
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        Price price = session.get(Price.class, id);

        if (price != null) {
            session.remove(price);
            logger.info(MessageSources.SUCCESS_DELETE);
        } else {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Override
    public List<Price> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<Price> prices = session.createQuery(SELECT_ALL, Price.class).list();
        logger.info(MessageSources.SUCCESS_READ_MANY);

        return prices;
    }

    @Override
    public List<Price> findByProductId(Long productId) {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<Price> prices = session.createQuery(SELECT_BY_PRODUCT_ID, Price.class)
                .setParameter("productId", productId)
                .list();
        logger.info(MessageSources.SUCCESS_READ_MANY);

        return prices;
    }
}
