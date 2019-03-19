package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.model.IntentUtterance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by bkane on 3/14/19.
 */
@Repository
public interface IntentUtteranceRepository extends JpaRepository<IntentUtterance, Long> {
}
