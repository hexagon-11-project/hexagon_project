package jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class DBCPInitListener implements ServletContextListener {

	private String poolName;

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ServletContext servletContext = sce.getServletContext();

		/*
		 * web.xml에 설정한 properties 파일 경로를 가져온다.
		 */
		String configFile = servletContext.getInitParameter("poolConfigFile");

		if (configFile == null || configFile.trim().isEmpty()) {
			throw new IllegalStateException("web.xml의 poolConfigFile 설정이 없습니다.");
		}

		/*
		 * db.properties 파일을 읽는다.
		 */
		Properties properties = loadProperties(servletContext, configFile);

		/*
		 * Oracle JDBC 드라이버 로드
		 */
		loadJDBCDriver(properties);

		/*
		 * DBCP 커넥션 풀 생성 및 등록
		 */
		initConnectionPool(properties);

		servletContext.log("DBCP 커넥션 풀 등록 완료: " + poolName);
	}

	/**
	 * WEB-INF/db.properties 파일을 읽는다.
	 */
	private Properties loadProperties(ServletContext servletContext, String configFile) {

		Properties properties = new Properties();

		try (InputStream inputStream = servletContext.getResourceAsStream(configFile)) {

			if (inputStream == null) {
				throw new IllegalStateException("DB 설정 파일을 찾을 수 없습니다: " + configFile + "\n"
						+ "db.properties.example 파일을 복사해서 " + "db.properties 파일을 생성하세요.");
			}

			properties.load(inputStream);

			return properties;

		} catch (IOException e) {
			throw new RuntimeException("DB 설정 파일을 읽는 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * Oracle JDBC 드라이버를 로드한다.
	 */
	private void loadJDBCDriver(Properties properties) {

		String driverClass = getRequiredProperty(properties, "jdbcdriver");

		try {
			Class.forName(driverClass);

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("JDBC 드라이버를 찾을 수 없습니다: " + driverClass, e);
		}
	}

	/**
	 * Apache DBCP 커넥션 풀을 생성하고 등록한다.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initConnectionPool(Properties properties) {

		try {
			String jdbcUrl = getRequiredProperty(properties, "jdbcUrl");

			String username = getRequiredProperty(properties, "dbUser");

			String password = getRequiredProperty(properties, "dbPass");

			poolName = getRequiredProperty(properties, "poolName");

			String validationQuery = properties.getProperty("validationQuery", "SELECT 1 FROM DUAL");

			int minIdle = getIntProperty(properties, "minIdle", 3);

			int maxTotal = getIntProperty(properties, "maxTotal", 30);

			/*
			 * Oracle DB 커넥션 생성 정보
			 */
			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(jdbcUrl, username, password);

			/*
			 * 일반 JDBC Connection을 DBCP가 관리할 수 있는 Connection으로 변환
			 */
			PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,
					null);

			/*
			 * Oracle 연결 상태 확인용 쿼리
			 */
			poolableConnectionFactory.setValidationQuery(validationQuery);

			/*
			 * 커넥션 풀 설정
			 */
			GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

			poolConfig.setMinIdle(minIdle);
			poolConfig.setMaxTotal(maxTotal);
			poolConfig.setTestWhileIdle(true);

			/*
			 * 5분마다 유휴 커넥션 검사
			 */
			poolConfig.setTimeBetweenEvictionRunsMillis(1000L * 60L * 5L);

			/*
			 * 실제 커넥션 풀 생성
			 */
			GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<PoolableConnection>(
					poolableConnectionFactory, poolConfig);

			poolableConnectionFactory.setPool(connectionPool);

			/*
			 * Apache DBCP PoolingDriver 로드
			 */
			Class.forName("org.apache.commons.dbcp2.PoolingDriver");

			PoolingDriver poolingDriver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");

			/*
			 * ConnectionProvider에서 사용할 이름으로 풀 등록
			 */
			poolingDriver.registerPool(poolName, connectionPool);

			System.out.println("DBCP Pool 등록 완료: " + poolName);

		} catch (Exception e) {
			/*
			 * 예외를 출력만 하고 넘기면 나중에 Pool not registered 오류가 발생하므로 애플리케이션 시작 자체를 실패시킨다.
			 */
			throw new RuntimeException("DBCP 커넥션 풀 초기화에 실패했습니다.", e);
		}
	}

	/**
	 * 필수 properties 값을 읽는다.
	 */
	private String getRequiredProperty(Properties properties, String propertyName) {

		String value = properties.getProperty(propertyName);

		if (value == null || value.trim().isEmpty()) {
			throw new IllegalStateException("DB 설정값이 없습니다: " + propertyName);
		}

		return value.trim();
	}

	/**
	 * 숫자 properties 값을 읽는다.
	 */
	private int getIntProperty(Properties properties, String propertyName, int defaultValue) {

		String value = properties.getProperty(propertyName);

		if (value == null || value.trim().isEmpty()) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(value.trim());

		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(propertyName + " 값은 숫자여야 합니다: " + value, e);
		}
	}

	/**
	 * Tomcat 종료 또는 프로젝트 재배포 시 커넥션 풀과 JDBC 드라이버를 정리한다.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		ServletContext servletContext = sce.getServletContext();

		closeConnectionPool(servletContext);
		deregisterJDBCDrivers(servletContext);
	}

	/**
	 * 등록한 커넥션 풀을 종료한다.
	 */
	private void closeConnectionPool(ServletContext servletContext) {

		if (poolName == null || poolName.trim().isEmpty()) {
			return;
		}

		try {
			PoolingDriver poolingDriver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");

			poolingDriver.closePool(poolName);

			servletContext.log("DBCP 커넥션 풀 종료 완료: " + poolName);

		} catch (SQLException e) {
			servletContext.log("DBCP 커넥션 풀 종료 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * Tomcat 재배포 시 JDBC 드라이버 메모리 누수를 방지한다.
	 */
	private void deregisterJDBCDrivers(ServletContext servletContext) {

		ClassLoader webAppClassLoader = getClass().getClassLoader();

		Enumeration<Driver> drivers = DriverManager.getDrivers();

		while (drivers.hasMoreElements()) {

			Driver driver = drivers.nextElement();

			/*
			 * 현재 웹 애플리케이션이 등록한 드라이버만 제거한다.
			 */
			if (driver.getClass().getClassLoader() != webAppClassLoader) {
				continue;
			}

			try {
				DriverManager.deregisterDriver(driver);

				servletContext.log("JDBC 드라이버 해제 완료: " + driver.getClass().getName());

			} catch (SQLException e) {
				servletContext.log("JDBC 드라이버 해제 중 오류가 발생했습니다: " + driver.getClass().getName(), e);
			}
		}
	}
}