package com.nexdom.estoque.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegracaoTestBase {

	@DynamicPropertySource
	static void registrarDatasource(DynamicPropertyRegistry registry) {
		PostgreSQLProperties.aplicar(registry);
	}
}
