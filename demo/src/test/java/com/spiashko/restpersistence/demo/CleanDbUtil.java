package com.spiashko.restpersistence.demo;

import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

public class CleanDbUtil {

    public static void cleanStore(final TransactionTemplate transactionTemplate, final EntityManager entityManager) {
        transactionTemplate.execute(transactionStatus -> {
            entityManager.createQuery("delete from Cat").executeUpdate();
            entityManager.createQuery("delete from Person ").executeUpdate();
            entityManager.flush();
            return null;
        });
    }
}
