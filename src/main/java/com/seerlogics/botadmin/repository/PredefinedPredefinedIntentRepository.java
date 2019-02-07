package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.model.Category;
import com.seerlogics.botadmin.model.PredefinedIntentUtterances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by bkane on 11/16/18.
 */
@Repository
public interface PredefinedPredefinedIntentRepository extends JpaRepository<PredefinedIntentUtterances, Long>,
        PredefinedIntentSearchRepository {
    List<PredefinedIntentUtterances> findByCategory(Category cat);

    @Query("select pu from PredefinedIntentUtterances pu where pu.category.code = :code")
    List<PredefinedIntentUtterances> findIntentsByCode(@Param("code") String code);
}
