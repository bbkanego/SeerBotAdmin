package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.CustomIntentUtterance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bkane on 1/28/19.
 * https://www.baeldung.com/spring-data-criteria-queries
 * https://github.com/eugenp/tutorials/blob/5e3eb4ae41bf58c419ffd1b70409c66e93a07243/persistence-modules/spring-jpa/src/main/java/org/baeldung/persistence/dao/BookRepositoryImpl.java
 */
@Repository
public class CustomIntentSearchRepositoryImpl implements CustomIntentSearchRepository {
    private EntityManager em;

    public CustomIntentSearchRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<CustomIntentUtterance> findIntentsAndUtterances(SearchIntents searchIntents) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<CustomIntentUtterance> criteriaQuery = criteriaBuilder.createQuery(CustomIntentUtterance.class);

        Root<CustomIntentUtterance> predefinedIntentUtterancesRoot = criteriaQuery.from(CustomIntentUtterance.class);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(searchIntents.getUtterance())) {
            predicates.add(criteriaBuilder.like(predefinedIntentUtterancesRoot.get("utterance"),
                    "%" + searchIntents.getUtterance() + "%"));
        }
        if (StringUtils.isNotBlank(searchIntents.getIntentName())) {
            predicates.add(criteriaBuilder.equal(predefinedIntentUtterancesRoot.get("intent"),
                    searchIntents.getIntentName()));
        }
        if (searchIntents.getCategory() != null) {
            predicates.add(criteriaBuilder.equal(predefinedIntentUtterancesRoot.get("category"),
                    searchIntents.getCategory()));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        return em.createQuery(criteriaQuery).getResultList();
    }
}
