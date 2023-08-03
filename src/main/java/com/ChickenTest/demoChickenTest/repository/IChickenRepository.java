package com.ChickenTest.demoChickenTest.repository;

import com.ChickenTest.demoChickenTest.entity.Chicken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChickenRepository extends JpaRepository<Chicken, Long> {
}
