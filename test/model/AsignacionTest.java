package model;

import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

public class AsignacionTest {
    private Asignacion valida() {
        return new Asignacion("000168204", "604", 90, new Date(0));
    }

    @Test
    public void constructorYGettersConservanLosCuatroDatos() {
        Asignacion a = valida();
        assertEquals("000168204", a.getNoCarnet());
        assertEquals("604", a.getIdCurso());
        assertEquals(90, a.getCalificacion());
        assertEquals(new Date(0), a.getFechaAprobacion());
    }

    @Test
    public void settersGuardanValoresValidos() {
        Asignacion a = valida();
        Date fecha = new Date(1000);
        a.setNoCarnet("000000001");
        a.setIdCurso("001");
        a.setCalificacion(100);
        a.setFechaAprobacion(fecha);
        assertEquals("000000001", a.getNoCarnet());
        assertEquals("001", a.getIdCurso());
        assertEquals(100, a.getCalificacion());
        assertEquals(fecha, a.getFechaAprobacion());
    }

    @Test public void rechazaCarnetNulo() { assertInvalid(() -> new Asignacion(null, "604", 90, new Date())); }
    @Test public void rechazaCarnetVacio() { assertInvalid(() -> new Asignacion("", "604", 90, new Date())); }
    @Test public void aceptaCalificacionCero() { assertEquals(0, new Asignacion("000168204", "604", 0, new Date()).getCalificacion()); }
    @Test public void rechazaCalificacionNegativa() { assertInvalid(() -> new Asignacion("000168204", "604", -1, new Date())); }
    @Test public void aceptaCalificacionCien() { assertEquals(100, new Asignacion("000168204", "604", 100, new Date()).getCalificacion()); }
    @Test public void rechazaCalificacionSobreCien() { assertInvalid(() -> new Asignacion("000168204", "604", 101, new Date())); }
    @Test public void rechazaCarnetConLetras() { assertInvalid(() -> new Asignacion("00016A204", "604", 90, new Date())); }
    @Test public void rechazaCursoDeLongitudIncorrecta() { assertInvalid(() -> new Asignacion("000168204", "60", 90, new Date())); }

    @Test
    public void equalsComparaCarnetYCursoYHashCodeEsConsistente() {
        Asignacion a = valida();
        Asignacion b = new Asignacion("000168204", "604", 10, new Date(123));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Asignacion("000168204", "605", 90, new Date(0)));
        assertTrue(a.toString().contains("000168204"));
        assertTrue(a.toString().contains("604"));
    }

    private static void assertInvalid(Runnable action) {
        try { action.run(); fail("Se esperaba IllegalArgumentException"); }
        catch (IllegalArgumentException expected) { }
    }
}
