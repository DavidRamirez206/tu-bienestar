package edu.co.icesi.proyectofinal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Crea la columna users.active si la base de datos todavia no la tiene (HU9.1).
 *
 * <p>Asi nadie tiene que correr la migracion a mano: la aplicacion se encarga al arrancar.
 * Se ejecuta antes de que Hibernate valide el esquema, porque con ddl-auto=validate la
 * aplicacion no arranca si a la tabla le falta una columna de la entidad.</p>
 */
public class UsersActiveColumnInitializer implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(UsersActiveColumnInitializer.class);

    private static final String TABLE = "users";
    private static final String COLUMN = "active";

    private final DataSource dataSource;

    public UsersActiveColumnInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() {
        try (Connection connection = dataSource.getConnection()) {
            String table = findTable(connection);

            // En las pruebas Hibernate crea el esquema despues: no hay nada que parchar todavia.
            if (table == null || hasActiveColumn(connection, table)) {
                return;
            }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + COLUMN
                        + " BOOLEAN DEFAULT TRUE NOT NULL");
            }
            log.info("Se creo la columna {}.{}: los usuarios que ya existian quedan activos.", table, COLUMN);
        } catch (SQLException ex) {
            // Si no se pudo, Hibernate avisara al validar el esquema. No se oculta el problema.
            log.warn("No se pudo crear la columna {}.{}: {}", TABLE, COLUMN, ex.getMessage());
        }
    }

    /** Devuelve el nombre real de la tabla, que cada motor escribe en mayusculas o minusculas. */
    private String findTable(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String name = tables.getString("TABLE_NAME");
                if (TABLE.equalsIgnoreCase(name)) {
                    return name;
                }
            }
        }
        return null;
    }

    private boolean hasActiveColumn(Connection connection, String table) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, table, "%")) {
            while (columns.next()) {
                if (COLUMN.equalsIgnoreCase(columns.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
