package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.dto.SearchIntentsDto;
import com.seerlogics.botadmin.model.PredefinedIntentUtterances;
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
 */
@Repository
public class IntentSearchRepositoryImpl implements IntentSearchRepository {
    EntityManager em;

    @Override
    public List<PredefinedIntentUtterances> findIntentsAndUtterances(SearchIntentsDto searchIntentsDto) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PredefinedIntentUtterances> criteriaQuery = criteriaBuilder.createQuery(PredefinedIntentUtterances.class);

        Root<PredefinedIntentUtterances> predefinedIntentUtterancesRoot = criteriaQuery.from(PredefinedIntentUtterances.class);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(searchIntentsDto.getUtterance())) {
            predicates.add(criteriaBuilder.like(predefinedIntentUtterancesRoot.get("utterance"),
                    "%" + searchIntentsDto.getUtterance() + "%"));
        }
        if (StringUtils.isNotBlank(searchIntentsDto.getIntentName())) {
            predicates.add(criteriaBuilder.equal(predefinedIntentUtterancesRoot.get("intent"), searchIntentsDto.getIntentName()));
        }
        if (searchIntentsDto.getCategory() != null) {
            predicates.add(criteriaBuilder.equal(predefinedIntentUtterancesRoot.get("category"), searchIntentsDto.getCategory()));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        return em.createQuery(criteriaQuery).getResultList();
    }
}
