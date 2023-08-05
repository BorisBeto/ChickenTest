package com.ChickenTest.demoChickenTest;

import com.ChickenTest.demoChickenTest.entity.Farm;
import com.ChickenTest.demoChickenTest.entity.LifeCycle;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoChickenTestApplication implements CommandLineRunner {
	@Autowired
	IFarmRepository farmRepository;

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		SpringApplication.run(DemoChickenTestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Farm farm = new Farm();

		farm.setNombre("SUPER FARM");
		farm.setGranjero("Brian Duran");
		farm.setDinero(500);
		farm.setCantPollos(0);
		farm.setCantHuevos(0);
		farm.setLimitePollos(10);
		farm.setLimiteHuevos(10);
		farm.setDias(LifeCycle.DAY_OF_LIFE_FARMER);
		farmRepository.save(farm);
	}
}
