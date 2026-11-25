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
import java.util.List;

/**
 * Crea las columnas nuevas que a la base de datos todavia le faltan.
 *
 * <p>Produccion arranca con ddl-auto=validate: si a una tabla le falta una columna de la entidad,
 * Hibernate detiene el arranque y Render marca el despliegue como fallido. Este paso corre antes
 * de esa validacion, asi que nadie tiene que acordarse de correr las migraciones a mano.</p>
 *
 * <p>Cada columna nueva se registra en {@link #COLUMNS}, junto a su migracion en backend/db/migrations.</p>
 */
public class MissingColumnsInitializer implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(MissingColumnsInitializer.class);

    /**
     * @param definition tipo y restricciones, como en ALTER TABLE ... ADD COLUMN
     * @param backfill   UPDATE opcional para las filas que ya existian (puede ser null)
     */
    record MissingColumn(String table, String column, String definition, String backfill) {
    }

    static final List<MissingColumn> COLUMNS = List.of(
            // HU8.2 (V003): tipo de evento. Los eventos que ya existian quedan como clase.
            new MissingColumn("activities", "type", "VARCHAR(20)",
                    "UPDATE activities SET type = 'CLASE' WHERE type IS NULL"),
            // HU9.1 (V004): un usuario inactivo no puede iniciar sesion.
            new MissingColumn("users", "active", "BOOLEAN DEFAULT TRUE NOT NULL", null)
    );

    private final DataSource dataSource;

    public MissingColumnsInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() {
        try (Connection connection = dataSource.getConnection()) {
            for (MissingColumn column : COLUMNS) {
                addIfMissing(connection, column);
            }
        } catch (SQLException ex) {
            // Si no se pudo, Hibernate avisara al validar el esquema. No se oculta el problema.
            log.warn("No se pudo revisar el esquema: {}", ex.getMessage());
        }
    }

    private void addIfMissing(Connection connection, MissingColumn column) {
        try {
            String table = findTable(connection, column.table());
            // En las pruebas Hibernate crea el esquema despues: no hay nada que parchar todavia.
            if (table == null || hasColumn(connection, table, column.column())) {
                return;
            }
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column.column()
                        + " " + column.definition());
                if (column.backfill() != null) {
                    statement.executeUpdate(column.backfill());
                }
            }
            log.info("Se creo la columna {}.{}", table, column.column());
        } catch (SQLException ex) {
            log.warn("No se pudo crear la columna {}.{}: {}", column.table(), column.column(), ex.getMessage());
        }
    }

    /**
     * Nombre real de la tabla en el esquema de la conexion. Se limita a ese esquema porque en
     * Supabase tambien existe auth.users, que no es la tabla de la aplicacion.
     */
    private String findTable(Connection connection, String wanted) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, connection.getSchema(), "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String name = tables.getString("TABLE_NAME");
                if (wanted.equalsIgnoreCase(name)) {
                    return name;
                }
            }
        }
        return null;
    }

    private boolean hasColumn(Connection connection, String table, String column) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, connection.getSchema(), table, "%")) {
            while (columns.next()) {
                if (column.equalsIgnoreCase(columns.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
