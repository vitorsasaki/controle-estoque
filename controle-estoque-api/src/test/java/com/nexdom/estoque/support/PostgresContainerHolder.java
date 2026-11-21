package com.nexdom.estoque.support;

import org.testcontainers.postgresql.PostgreSQLContainer;

public final class PostgresContainerHolder {

	public static final PostgreSQLContainer CONTAINER;

	static {
		CONTAINER = new PostgreSQLContainer("postgres:16-alpine");
		CONTAINER.start();
	}

	private PostgresContainerHolder() {
	}
}
