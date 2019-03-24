package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.Intent;
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
 * Created by bkane on 3/14/19.
 */
@Repository
public class IntentSearchRepositoryImpl implements IntentSearchRepository {
    private EntityManager em;

    public IntentSearchRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    public List<Intent> findIntentsAndUtterances(SearchIntents searchIntents) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Intent> criteriaQuery = criteriaBuilder.createQuery(Intent.class);

        Root<Intent> intentRoot = criteriaQuery.from(Intent.class);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(searchIntents.getUtterance())) {
            predicates.add(criteriaBuilder.like(intentRoot.get("utterances"),
                    "%" + searchIntents.getUtterance() + "%"));
        }
        if (StringUtils.isNotBlank(searchIntents.getIntentName())) {
            predicates.add(criteriaBuilder.equal(intentRoot.get("intent"), searchIntents.getIntentName()));
        }
        if (searchIntents.getCategory() != null) {
            predicates.add(criteriaBuilder.equal(intentRoot.get("category"), searchIntents.getCategory()));
        }
        if (searchIntents.getIntentType() != null) {
            predicates.add(criteriaBuilder.equal(intentRoot.get("intentType"), searchIntents.getIntentType()));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        return em.createQuery(criteriaQuery).getResultList();
    }
}
