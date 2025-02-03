package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergenRepo extends JpaRepository<Allergen, Integer> {

}
