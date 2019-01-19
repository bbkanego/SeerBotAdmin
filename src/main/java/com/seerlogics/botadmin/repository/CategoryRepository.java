package com.seerlogics.botadmin.repository;

import com.seerlogics.botadmin.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by bkane on 11/1/18.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    //@Query("select o from Category cat where cat.code = :code" )
    //Category findCategoryByCode(@Param("code") String code);

    Category findByCode(String code);
}
