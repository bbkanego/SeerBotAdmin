package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.model.Account;
import com.seerlogics.botadmin.model.TrainedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainedModelRepository extends JpaRepository<TrainedModel, Long> {
    TrainedModel findByName(String name);
    List<TrainedModel> findByOwner(Account owner);
}
