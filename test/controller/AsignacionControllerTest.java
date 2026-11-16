package controller;

import dao.AsignacionDao;
import java.sql.SQLException;
import java.util.*;
import model.Asignacion;
import org.junit.Test;
import static org.junit.Assert.*;

public class AsignacionControllerTest {
    private static final Date FECHA = new Date(0);

    private static class VistaDoble implements AsignacionView {
        final List<String> eventos = new ArrayList<>();

        @Override public void limpiar() { eventos.add("limpiar"); }
        @Override public void cargarAsignaciones() { eventos.add("refrescar"); }
    }

    private static class DaoDoble extends AsignacionDao {
        int llamadas;
        boolean falla;
        List<Asignacion> resultado = new ArrayList<>();
        Asignacion guardada, actualizada;
        String carnetBuscado, carnetOriginal, cursoOriginal, carnetEliminado;

        private void registrar() throws SQLException {
            llamadas++;
            if (falla) throw new SQLException("Fallo SQL simulado");
        }
        @Override public List<Asignacion> obtenerTodos() throws SQLException { registrar(); return resultado; }
        @Override public List<Asignacion> buscarPorCarnet(String carnet) throws SQLException { registrar(); carnetBuscado = carnet; return resultado; }
        @Override public boolean guardar(Asignacion a) throws SQLException { registrar(); guardada = a; return true; }
        @Override public boolean actualizar(Asignacion a, String carnet, String curso) throws SQLException {
            registrar(); actualizada = a; carnetOriginal = carnet; cursoOriginal = curso; return true;
        }
        @Override public boolean eliminar(String carnet) throws SQLException { registrar(); carnetEliminado = carnet; return true; }
    }

    @Test
    public void delegaCadaOperacionEnElMetodoDAOCorrespondiente() {
        DaoDoble dao = new DaoDoble();
        AsignacionController c = new AsignacionController(dao);
        c.buscarPorCarnet("000168204");
        assertEquals("000168204", dao.carnetBuscado);
        assertSame(dao.resultado, c.obtenerTodos());
        assertTrue(c.guardarAsignacion("000168204", "604", 90, FECHA));
        assertEquals("000168204", dao.guardada.getNoCarnet());
        assertTrue(c.actualizarAsignacion("000168204", "604", "605", 80, FECHA));
        assertEquals("000168204", dao.carnetOriginal);
        assertEquals("604", dao.cursoOriginal);
        assertEquals("605", dao.actualizada.getIdCurso());
        assertTrue(c.eliminarAsignacion("000168204"));
        assertEquals("000168204", dao.carnetEliminado);
        assertEquals(5, dao.llamadas);
    }

    @Test
    public void operacionesCrudExitosasOrdenanLimpiarYRefrescarLaVista() {
        DaoDoble dao = new DaoDoble();
        VistaDoble vista = new VistaDoble();
        AsignacionController c = new AsignacionController(dao, vista);

        assertTrue(c.guardarAsignacion("000168204", "604", 90, FECHA));
        assertTrue(c.actualizarAsignacion("000168204", "604", "605", 80, FECHA));
        assertTrue(c.eliminarAsignacion("000168204"));

        assertEquals(Arrays.asList(
                "limpiar", "refrescar",
                "limpiar", "refrescar",
                "limpiar", "refrescar"), vista.eventos);
    }

    @Test
    public void datosInvalidosSeRechazanAntesDeInvocarAlDao() {
        DaoDoble dao = new DaoDoble();
        AsignacionController c = new AsignacionController(dao);
        assertIllegalArgument(() -> c.buscarPorCarnet("LETRAS"));
        assertIllegalArgument(() -> c.guardarAsignacion("malo", "604", 90, FECHA));
        assertEquals(0, dao.llamadas);
    }

    @Test
    public void consultaSinCoincidenciasDevuelveListaVaciaParaQueLaVistaLaInforme() {
        DaoDoble dao = new DaoDoble();
        assertTrue(new AsignacionController(dao).buscarPorCarnet("000168204").isEmpty());
    }

    @Test
    public void excepcionesSqlDelDaoSeEnvuelvenYConservanLaCausa() {
        DaoDoble dao = new DaoDoble();
        dao.falla = true;
        AsignacionController c = new AsignacionController(dao);
        assertDatabaseError(() -> c.obtenerTodos());
        assertDatabaseError(() -> c.buscarPorCarnet("000168204"));
        assertDatabaseError(() -> c.guardarAsignacion("000168204", "604", 90, FECHA));
        assertDatabaseError(() -> c.actualizarAsignacion("000168204", "604", "605", 90, FECHA));
        assertDatabaseError(() -> c.eliminarAsignacion("000168204"));
    }

    private static void assertDatabaseError(Runnable operation) {
        try { operation.run(); fail("Se esperaba IllegalStateException"); }
        catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("base de datos"));
            assertTrue(e.getCause() instanceof SQLException);
        }
    }

    private static void assertIllegalArgument(Runnable operation) {
        try { operation.run(); fail("Se esperaba IllegalArgumentException"); }
        catch (IllegalArgumentException expected) { }
    }
}
