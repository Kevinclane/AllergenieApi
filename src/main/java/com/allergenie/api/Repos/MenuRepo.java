package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepo extends JpaRepository<Menu, Integer> {
}
