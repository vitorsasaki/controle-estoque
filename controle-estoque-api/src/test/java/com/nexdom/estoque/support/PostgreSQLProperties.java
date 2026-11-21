package com.nexdom.estoque.support;

import org.springframework.test.context.DynamicPropertyRegistry;

final class PostgreSQLProperties {

	private PostgreSQLProperties() {
	}

	static void aplicar(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", PostgresContainerHolder.CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", PostgresContainerHolder.CONTAINER::getUsername);
		registry.add("spring.datasource.password", PostgresContainerHolder.CONTAINER::getPassword);
		registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
	}
}
