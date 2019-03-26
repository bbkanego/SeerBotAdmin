package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.dto.SearchBots;
import com.seerlogics.botadmin.model.Bot;
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
 * Created by bkane on 1/31/19.
 */
@Repository
public class BotSearchRepositoryImpl implements BotSearchRepository {

    private EntityManager em;

    public BotSearchRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Bot> findBots(SearchBots searchBots) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Bot> criteriaQuery = criteriaBuilder.createQuery(Bot.class);

        Root<Bot> botRoot = criteriaQuery.from(Bot.class);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(searchBots.getName())) {
            predicates.add(criteriaBuilder.like(botRoot.get("name"), "%" + searchBots.getName() + "%"));
        }
        if (StringUtils.isNotBlank(searchBots.getDisplayName())) {
            predicates.add(criteriaBuilder.equal(botRoot.get("displayName"), "%" + searchBots.getDisplayName() + "%"));
        }
        if (StringUtils.isNotBlank(searchBots.getDescription())) {
            predicates.add(criteriaBuilder.equal(botRoot.get("description"), "%" + searchBots.getDescription() + "%"));
        }
        if (searchBots.getCategory() != null) {
            predicates.add(criteriaBuilder.equal(botRoot.get("category"), searchBots.getCategory()));
        }
        if (StringUtils.isNotBlank(searchBots.getStatusCode())) {
            predicates.add(criteriaBuilder.equal(botRoot.get("status").get("code"), searchBots.getStatusCode()));
        }
        if (searchBots.getOwnerAccount() != null) {
            predicates.add(criteriaBuilder.equal(botRoot.get("owner"), searchBots.getOwnerAccount()));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        return em.createQuery(criteriaQuery).getResultList();
    }
}
