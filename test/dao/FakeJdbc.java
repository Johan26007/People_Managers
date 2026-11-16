package dao;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/** Proveedor JDBC falso en memoria; no modifica DriverManager ni consulta la BD real. */
final class FakeJdbc {
    String lastSql;
    Map<Integer, Object> lastParameters;
    boolean emptyResult;
    boolean failConnection;

    FakeJdbc() { }

    Connection getConnection() throws SQLException {
        if (failConnection) {
            throw new SQLException("Fallo de conexión simulado");
        }
        return connection(emptyResult);
    }

    private Connection connection(boolean empty) {
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[]{Connection.class}, (proxy, method, args) -> {
            if (method.getName().equals("prepareStatement")) {
                lastSql = (String) args[0];
                lastParameters = new HashMap<>();
                return statement(empty);
            }
            if (method.getName().equals("isClosed")) return false;
            if (method.getName().equals("isValid")) return true;
            return defaultValue(method.getReturnType());
        });
    }

    private PreparedStatement statement(boolean empty) {
        return (PreparedStatement) Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(), new Class[]{PreparedStatement.class}, (proxy, method, args) -> {
            String name = method.getName();
            if (name.startsWith("set") && args != null && args.length >= 2 && args[0] instanceof Integer) {
                lastParameters.put((Integer) args[0], args[1]);
                return null;
            }
            if (name.equals("executeQuery")) return resultSet(empty || lastSql.startsWith("SELECT 1"));
            if (name.equals("executeUpdate")) return 1;
            return defaultValue(method.getReturnType());
        });
    }

    private ResultSet resultSet(boolean empty) {
        return (ResultSet) Proxy.newProxyInstance(ResultSet.class.getClassLoader(), new Class[]{ResultSet.class}, new InvocationHandler() {
            private boolean positioned;
            @Override public Object invoke(Object proxy, Method method, Object[] args) {
                String name = method.getName();
                if (name.equals("next")) {
                    if (empty || positioned) return false;
                    positioned = true;
                    return true;
                }
                if (name.equals("getString")) return "No_Carnet".equals(String.valueOf(args[0])) ? "000168204" : "604";
                if (name.equals("getInt")) return 90;
                if (name.equals("getDate")) return java.sql.Date.valueOf("2026-11-12");
                return defaultValue(method.getReturnType());
            }
        });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive() || type == void.class) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0d;
        return null;
    }
}
