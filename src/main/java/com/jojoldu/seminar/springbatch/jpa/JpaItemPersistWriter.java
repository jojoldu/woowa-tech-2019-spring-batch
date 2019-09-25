package com.jojoldu.seminar.springbatch.jpa;

import org.springframework.batch.item.database.JpaItemWriter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * 스프링 배치가 기본으로 제공하는 JpaItemWriter는 항상 merge를 호출한다. 새로운 객체인 경우 persist에 비해 merge는 성능저하가 발생할 수 있다.
 *
 * - 연관된 객체가 proxy면 proxy객체를 확인하려고 select 쿼리를 조회한다.
 *
 * 이 클래스는 merge 대신 persist를 호출한다. 따라서 항상 새로운 객체를 저장할 때만 사용해야 한다.
 * - 저장할 엔티티가 영속성 컨텍스트에 있다. -> dirty checking
 * - 저장할 엔티티가 영속성 컨텍스트에 없다. -> persist 호출
 */
public class JpaItemPersistWriter<T> extends JpaItemWriter<T> {

    public JpaItemPersistWriter() {
    }

    public JpaItemPersistWriter(EntityManagerFactory entityManagerFactory) {
        setEntityManagerFactory(entityManagerFactory);
    }

    @Override
    protected void doWrite(EntityManager entityManager, List<? extends T> items) {
        if (logger.isDebugEnabled()) {
            logger.debug("Writing to JPA with " + items.size() + " items.");
        }

        if (!items.isEmpty()) {
            long persistCount = 0;
            for (T item : items) {
                if (!entityManager.contains(item)) {
                    entityManager.persist(item);
                    persistCount++;
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(persistCount + " entities persisted.");
                logger.debug((items.size() - persistCount) + " entities found in persistence context.");
            }
        }

    }
}
