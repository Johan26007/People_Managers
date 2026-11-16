package dao;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
interface AsignacionConnectionProvider {
    Connection getConnection() throws SQLException;
}
