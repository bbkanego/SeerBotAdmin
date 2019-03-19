package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.dto.SearchIntents;
import com.seerlogics.botadmin.model.Category;
import com.seerlogics.botadmin.model.Intent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by bkane on 3/14/19.
 */
@Repository
public interface IntentRepository extends JpaRepository<Intent, Long>, IntentSearchRepository {
    List<Intent> findByCategory(Category cat);

    @Query("select pu from Intent pu where pu.category.code = :code")
    List<Intent> findIntentsByCode(@Param("code") String code);

    @Query("select pu from Intent pu where pu.category.code = :code and pu.intentType = :intentType")
    List<Intent> findIntentsByCodeAndType(@Param("code") String code, @Param("intentType") String intentType);
}
