package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.model.TrainedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainedModelRepository extends JpaRepository<TrainedModel, Long> {
    TrainedModel findByName(String name);
}
