package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.config.MissingColumnsInitializer;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Una base de datos creada antes del Sprint 2 debe quedar lista para ddl-auto=validate. */
class MissingColumnsInitializerTest {

    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:legacy" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE activities (id_activity INT PRIMARY KEY, name VARCHAR(255))");
            s.execute("CREATE TABLE users (id_user INT PRIMARY KEY, first_name VARCHAR(255))");
            s.execute("INSERT INTO activities VALUES (1, 'Yoga')");
            s.execute("INSERT INTO users VALUES (1, 'Ana')");
        }
    }

    @Test
    void addsMissingColumnsAndBackfillsExistingRows() throws SQLException {
        new MissingColumnsInitializer(dataSource).afterPropertiesSet();

        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            ResultSet activity = s.executeQuery("SELECT type FROM activities WHERE id_activity = 1");
            assertTrue(activity.next());
            assertEquals("CLASE", activity.getString(1));

            ResultSet user = s.executeQuery("SELECT active FROM users WHERE id_user = 1");
            assertTrue(user.next());
            assertTrue(user.getBoolean(1));
        }
    }

    @Test
    void runningTwiceDoesNothing() throws SQLException {
        MissingColumnsInitializer initializer = new MissingColumnsInitializer(dataSource);
        initializer.afterPropertiesSet();
        initializer.afterPropertiesSet();

        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM activities");
            rs.next();
            assertEquals(1, rs.getInt(1));
        }
    }
}
