package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by bkane on 11/3/18.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUserName(String userName);
}
