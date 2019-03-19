package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.model.IntentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by bkane on 3/14/19.
 */
@Repository
public interface IntentResponseRepository extends JpaRepository<IntentResponse, Long> {
}
