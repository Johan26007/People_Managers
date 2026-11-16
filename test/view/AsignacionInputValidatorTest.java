package view;

import java.util.Date;
import model.Asignacion;
import org.junit.Test;
import static org.junit.Assert.*;

/** Casos de validación del formulario comprobados contra las reglas del modelo. */
public class AsignacionInputValidatorTest {
    @Test
    public void carnetValidoDeNueveDigitosSeAcepta() {
        Asignacion a = new Asignacion("000168204", "604", 90, new Date(0));
        assertEquals("000168204", a.getNoCarnet());
    }

    @Test
    public void carnetVacioONuloSeRechaza() {
        assertInvalid(() -> new Asignacion("", "604", 90, new Date()));
        assertInvalid(() -> new Asignacion(null, "604", 90, new Date()));
    }

    @Test
    public void carnetConLetrasSimbolosOLongitudIncorrectaSeRechaza() {
        assertInvalid(() -> new Asignacion("00016A204", "604", 90, new Date()));
        assertInvalid(() -> new Asignacion("000@68204", "604", 90, new Date()));
        assertInvalid(() -> new Asignacion("00016820", "604", 90, new Date()));
        assertInvalid(() -> new Asignacion("0001682040", "604", 90, new Date()));
    }

    @Test
    public void cursoDebeTenerExactamenteTresDigitos() {
        assertEquals("006", new Asignacion("000168204", "006", 90, new Date()).getIdCurso());
        assertInvalid(() -> new Asignacion("000168204", "60A", 90, new Date()));
        assertInvalid(() -> new Asignacion("000168204", "60", 90, new Date()));
    }

    @Test
    public void calificacionesEnLosLimitesSonAceptadas() {
        assertEquals(0, new Asignacion("000168204", "604", 0, new Date()).getCalificacion());
        assertEquals(100, new Asignacion("000168204", "604", 100, new Date()).getCalificacion());
    }

    @Test
    public void calificacionesFueraDelRangoSeRechazan() {
        assertInvalid(() -> new Asignacion("000168204", "604", -1, new Date()));
        assertInvalid(() -> new Asignacion("000168204", "604", 101, new Date()));
    }

    private static void assertInvalid(Runnable action) {
        try { action.run(); fail("Se esperaba IllegalArgumentException"); }
        catch (IllegalArgumentException expected) { }
    }
}