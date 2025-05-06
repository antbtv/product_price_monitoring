package com.example.implementationdao;

import com.example.MessageSources;
import com.example.dao.PriceHistoryDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.PriceHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PriceHistoryDaoImpl implements PriceHistoryDao {

    private static final String SELECT_ALL = "from PriceHistory";
    private static final String SELECT_PRICES_BY_PRODUCT_IN_DATA_RANGE = "FROM PriceHistory ph " +
            "WHERE ph.product.id = :productId AND ph.store.storeId = :storeId " +
            "AND ph.recordedAt BETWEEN :startDate AND :endDate";

    private final HibernateSessionFactory hibernateSessionFactory;
    private static final Logger logger = LogManager.getLogger(PriceHistoryDaoImpl.class);

    public PriceHistoryDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public PriceHistory create(PriceHistory priceHistory) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.persist(priceHistory);
        logger.info(MessageSources.SUCCESS_CREATE);
        return priceHistory;
    }

    @Override
    public PriceHistory findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        PriceHistory priceHistory = session.get(PriceHistory.class, id);

        if (priceHistory == null) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        } else {
            logger.info(MessageSources.SUCCESS_READ_ONE);
        }

        return priceHistory;
    }

    @Override
    public void update(PriceHistory priceHistory) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.merge(priceHistory);
        logger.info(MessageSources.SUCCESS_UPDATE);
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        PriceHistory priceHistory = session.get(PriceHistory.class, id);

        if (priceHistory != null) {
            session.remove(priceHistory);
            logger.info(MessageSources.SUCCESS_DELETE);
        } else {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Override
    public List<PriceHistory> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<PriceHistory> priceHistories = session.createQuery(SELECT_ALL, PriceHistory.class).list();
        logger.info(MessageSources.SUCCESS_READ_MANY);

        return priceHistories;
    }

    @Override
    public List<PriceHistory> findPriceHistoryByProductAndDateRange(Long productId,
                                                                    Long storeId,
                                                                    LocalDate startDate,
                                                                    LocalDate endDate) {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<PriceHistory> priceHistories = session.createQuery(SELECT_PRICES_BY_PRODUCT_IN_DATA_RANGE, PriceHistory.class)
                .setParameter("productId", productId)
                .setParameter("storeId", storeId)
                .setParameter("startDate", startDate.atStartOfDay())
                .setParameter("endDate", endDate.atTime(23, 59, 59))
                .list();
        logger.info(MessageSources.SUCCESS_READ_MANY);

        return priceHistories;
    }
}
