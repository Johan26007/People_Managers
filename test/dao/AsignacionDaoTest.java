package dao;

import java.sql.*;
import model.Asignacion;
import org.junit.*;
import static org.junit.Assert.*;

public class AsignacionDaoTest {
    private FakeJdbc jdbc;

    @Before
    public void resetFakeDatabase() {
        jdbc = new FakeJdbc();
    }

    private AsignacionDao dao(boolean empty) {
        jdbc.emptyResult = empty;
        return new AsignacionDao(jdbc::getConnection);
    }

    @Test
    public void conexionSimuladaDisponible() throws Exception {
        try (Connection connection = jdbc.getConnection()) {
            assertNotNull(connection);
            assertTrue(connection.isValid(1));
        }
    }

    @Test
    public void fallaDeConexionSePropagaComoSQLExceptionSinAccesoReal() {
        jdbc.failConnection = true;
        try {
            dao(false).obtenerTodos();
            fail("Se esperaba SQLException");
        } catch (SQLException e) {
            assertEquals("Fallo de conexión simulado", e.getMessage());
        }
    }

    @Test
    public void crearConsultarActualizarYEliminarUsanLasSentenciasPreparadas() throws Exception {
        AsignacionDao dao = dao(false);
        Asignacion a = new Asignacion("000168204", "604", 90, java.sql.Date.valueOf("2026-11-12"));
        assertTrue(dao.guardar(a));
        assertTrue(jdbc.lastSql.startsWith("INSERT"));
        assertEquals("000168204", jdbc.lastParameters.get(1));
        assertEquals("604", jdbc.lastParameters.get(2));
        assertEquals(90, jdbc.lastParameters.get(3));

        assertEquals(1, dao.buscarPorCarnet("000168204").size());
        assertTrue(jdbc.lastSql.contains("WHERE \"No_Carnet\" = ?"));

        assertTrue(dao.actualizar(a, "000168204", "604"));
        assertTrue(jdbc.lastSql.startsWith("UPDATE"));
        assertTrue(dao.eliminar("000168204"));
        assertTrue(jdbc.lastSql.startsWith("DELETE"));
    }

    @Test
    public void consultaSinFilasDevuelveListaVacia() throws Exception {
        AsignacionDao dao = dao(true);
        assertTrue(dao.obtenerTodos().isEmpty());
        assertTrue(dao.buscarPorCarnet("000168204").isEmpty());
    }

    @Test
    public void textoDeInyeccionSeEnviaComoParametroYNoSeConcatena() throws Exception {
        String intento = "000' OR '1'='1";
        dao(true).buscarPorCarnet(intento);
        assertTrue(jdbc.lastSql.contains("= ?"));
        assertFalse(jdbc.lastSql.contains(intento));
        assertEquals(intento, jdbc.lastParameters.get(1));
    }
}
