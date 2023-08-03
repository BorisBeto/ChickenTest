package com.ChickenTest.demoChickenTest.repository;

import com.ChickenTest.demoChickenTest.entity.Egg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEggRepository extends JpaRepository<Egg, Long> {
}
