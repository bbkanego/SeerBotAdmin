package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.model.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by bkane on 11/1/18.
 */
@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {
}
