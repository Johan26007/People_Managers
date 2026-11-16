package estudiante.controller;

import estudiante.dao.EstudianteDao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import estudiante.model.Estudiante;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class EstudianteControllerTest {

    private FakeDao dao;
    private EstudianteController controlador;

    @Before public void preparar() {
        dao = new FakeDao();
        controlador = new EstudianteController(dao);
    }

    @Test public void validacionFallidaNoInvocaAlDao() {
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarEstudiante(
                "C-1", "Ana2", "Paz", 25, LocalDate.of(2001, 1, 1),
                "Femenino", "ana@example.com", "", ""));
        assertEquals(0, dao.guardarLlamadas);
    }

    @Test public void datosObligatoriosIncompletosNoSeInsertan() {
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarEstudiante(
                "C-1", "", "Paz", 25, LocalDate.of(2001, 1, 1),
                "Femenino", "ana@example.com", "", ""));
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarEstudiante(
                "C-1", "Ana", "Paz", 25, null,
                "Femenino", "ana@example.com", "", ""));
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarEstudiante(
                "C-1", "Ana", "Paz", 25, LocalDate.of(2001, 1, 1),
                "Seleccione su género", "ana@example.com", "", ""));
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarEstudiante(
                "C-1", "Ana", "Paz", 25, LocalDate.of(2001, 1, 1),
                "Femenino", "   ", "", ""));
        assertEquals(0, dao.guardarLlamadas);
    }

    @Test public void guardarValidoDelegaEnDao() {
        assertTrue(controlador.agregarEstudiante("C-1", "Ana", "Paz", 25,
                LocalDate.of(2001, 1, 1), "Femenino", "ana@example.com", "", ""));
        assertEquals(1, dao.guardarLlamadas);
        assertEquals("C-1", dao.ultimoGuardado.getNoCarnetEstudiante());
    }

    @Test public void buscarVacioNoInvocaAlDao() {
        assertNull(controlador.obtenerEstudiantePorCarnet("   "));
        assertEquals(0, dao.consultasCarnet);
    }

    @Test public void buscarValidoDelegaEnDaoYRetornaResultado() {
        dao.encontrado = persona("C-1");
        assertSame(dao.encontrado, controlador.obtenerEstudiantePorCarnet(" C-1 "));
        assertEquals(1, dao.consultasCarnet);
    }

    @Test public void actualizarValidoDelegaEnDao() {
        assertTrue(controlador.actualizarEstudiante(7, "C-1", "Ana", "Paz", 25,
                LocalDate.of(2001, 1, 1), "Femenino", "ana@example.com", "", ""));
        assertEquals(1, dao.actualizarLlamadas);
        assertEquals(7, dao.ultimoActualizado.getIdEstudiante());
    }

    @Test public void borrarVacioNoInvocaAlDao() {
        assertFalse(controlador.eliminarEstudiantePorCarnet(" "));
        assertEquals(0, dao.eliminarLlamadas);
    }

    @Test public void borrarValidoDelegaEnDao() {
        assertTrue(controlador.eliminarEstudiantePorCarnet(" C-1 "));
        assertEquals(1, dao.eliminarLlamadas);
        assertEquals("C-1", dao.ultimoEliminado);
    }

    @Test public void listaYConexionSeDeleganEnDao() {
        assertSame(dao.registros, controlador.obtenerEstudiantes());
        assertTrue(controlador.probarConexion());
        assertEquals(1, dao.listaLlamadas);
        assertEquals(1, dao.conexionLlamadas);
    }

    private Estudiante persona(String carnet) {
        return new Estudiante(7, carnet, "Ana", "Paz", 25,
                LocalDate.of(2001, 1, 1), "Femenino", "ana@example.com", "", null);
    }

    private static class FakeDao extends EstudianteDao {
        int guardarLlamadas, actualizarLlamadas, eliminarLlamadas;
        int consultasCarnet, listaLlamadas, conexionLlamadas;
        String ultimoEliminado;
        Estudiante encontrado, ultimoGuardado, ultimoActualizado;
        List<Estudiante> registros = new ArrayList<>();

        @Override public boolean probarConexion() { conexionLlamadas++; return true; }
        @Override public boolean guardar(Estudiante estudiante) { guardarLlamadas++; ultimoGuardado = estudiante; return true; }
        @Override public boolean actualizar(Estudiante estudiante) { actualizarLlamadas++; ultimoActualizado = estudiante; return true; }
        @Override public boolean eliminarPorCarnet(String carnet) { eliminarLlamadas++; ultimoEliminado = carnet; return true; }
        @Override public Estudiante obtenerPorCarnet(String carnet) { consultasCarnet++; return encontrado; }
        @Override public List<Estudiante> obtenerTodos() { listaLlamadas++; return registros; }
    }
}
