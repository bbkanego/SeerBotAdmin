package com.seerlogics.chatbot.repository.botadmin;


import com.seerlogics.chatbot.model.botadmin.Category;
import com.seerlogics.chatbot.model.botadmin.CustomIntentUtterance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by bkane on 11/16/18.
 */
@Repository
public interface CustomPredefinedIntentRepository extends JpaRepository<CustomIntentUtterance, Long> {
    List<CustomIntentUtterance> findByCategory(Category cat);

    @Query("select pu from CustomIntentUtterance pu where pu.category.code = :code " +
            "and pu.owner.userName = :userName")
    List<CustomIntentUtterance> findIntentsByCategoryCodeAndAccount(@Param("code") String code,
                                                                    @Param("userName") String userName);

    @Query("select pu from CustomIntentUtterance pu where pu.intent = :intent " +
            "and pu.utterance = :utterance")
    List<CustomIntentUtterance> findResponseForIntentAndUtterance(@Param("utterance") String utterance,
                                                                    @Param("intent") String intent);
}
