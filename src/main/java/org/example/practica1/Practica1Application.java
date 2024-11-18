package org.example.practica1;

import org.example.practica1.funko.repository.FunkoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class Practica1Application  {

	@Autowired
	private FunkoRepository repository;

	public static void main(String[] args) {

		SpringApplication.run(Practica1Application.class, args);
	}

	/*
	@Override
	public void run(String... args) throws Exception {

			repository.saveAll(DemoData.FUNKOS_DEMO);
	}

	 */

}
