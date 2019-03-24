package com.seerlogics.chatbot.repository.botadmin;

import com.seerlogics.chatbot.model.botadmin.Account;
import com.seerlogics.chatbot.model.botadmin.Category;
import com.seerlogics.chatbot.model.botadmin.Intent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by bkane on 3/14/19.
 */
@Repository
public interface IntentRepository extends JpaRepository<Intent, Long> {
    List<Intent> findByCategory(Category cat);

    @Query("select pu from Intent pu where pu.category.code = :code")
    List<Intent> findIntentsByCode(@Param("code") String code);

    @Query("select pu from Intent pu where pu.category.code = :code and pu.intentType = :intentType")
    List<Intent> findIntentsByCodeAndType(@Param("code") String code, @Param("intentType") String intentType);

    @Query("select pu from Intent pu where pu.category.code = :code and pu.intentType = :intentType " +
            "and pu.owner = :owner")
    List<Intent> findIntentsByCodeTypeAndOwner(@Param("code") String code, @Param("intentType") String intentType,
                                               @Param("owner") Account owner);


    @Query("select pu from Intent pu where pu.category.code = :botTypeCode and pu.intentType = :intentType " +
            "and pu.utterances.locale = :locale and pu.utterances.utterance = :utterance")
    Intent findIntentsByUtterance(@Param("botTypeCode") String botTypeCode, @Param("intentType") String intentType,
                                        @Param("utterance") String utterance,
                                        @Param("locale") String locale);
}
