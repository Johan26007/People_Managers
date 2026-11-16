package estudiante.model;

import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

public class EstudianteValidationTest {

    @Test public void datosObligatoriosEnBlancoSeRechazan() {
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarRegistro(
                "C-1", "   ", "Paz", 25, LocalDate.of(2001, 1, 1), "Femenino",
                "ana@example.com", "", ""));
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarRegistro(
                "C-1", "Ana", "Paz", 25, LocalDate.of(2001, 1, 1), "Femenino",
                "   ", "", ""));
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarRegistro(
                "C-1", "Ana", "Paz", 25, null, "Femenino", "ana@example.com", "", ""));
    }

    @Test public void fechaNulaYFuturaSeRechazan() {
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarFechaNacimiento(null));
        assertThrows(IllegalArgumentException.class,
                () -> Estudiante.validarFechaNacimiento(LocalDate.now().plusDays(1)));
    }

    @Test public void correoYGeneroInvalidosSeRechazan() {
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarCorreo("sin-arroba"));
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarGenero("Seleccione su género"));
    }

    @Test public void dpiDebeTenerTreceDigitosYPuedeOmitirse() {
        Estudiante.validarDpi("");
        Estudiante.validarDpi("1234567890123");
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarDpi("123ABC7890123"));
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarDpi("123456789012"));
    }

    @Test public void limitaLongitudesSegunLaTabla() {
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarCarnet("C".repeat(51)));
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarNombre("A".repeat(101), "nombre"));
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarTelefono("1".repeat(21)));
        assertThrows(IllegalArgumentException.class, () -> Estudiante.validarCorreo("a".repeat(145) + "@x.com"));
    }

    @Test public void aceptaValoresLimite() {
        Estudiante.validarEdad(18);
        Estudiante.validarEdad(65);
        Estudiante.validarEdad(100);
        Estudiante.validarFechaNacimiento(LocalDate.now());
    }
}
