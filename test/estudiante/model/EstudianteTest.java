package estudiante.model;

import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

public class EstudianteTest {

    private Estudiante valido() {
        return new Estudiante(7, "C-001", "José", "Muñoz", 30,
                LocalDate.of(1996, 2, 3), "Masculino", "jose@example.com", "5555-1234", "1234567890123");
    }

    @Test public void constructorYGettersConDatosValidos() {
        Estudiante estudiante = valido();
        assertEquals("C-001", estudiante.getNoCarnetEstudiante());
        assertEquals("José", estudiante.getNombreEstudiante());
        assertEquals("Muñoz", estudiante.getApellidoEstudiante());
        assertEquals(30, estudiante.getEdadEstudiante());
        assertEquals(LocalDate.of(1996, 2, 3), estudiante.getFechaNacimientoEstudiante());
        assertEquals("Masculino", estudiante.getGeneroEstudiante());
        assertEquals("jose@example.com", estudiante.getCorreoElectronicoEstudiante());
    }

    @Test public void settersActualizanAtributos() {
        Estudiante estudiante = valido();
        estudiante.setNombreEstudiante("Ana");
        estudiante.setEdadEstudiante(65);
        estudiante.setCorreoElectronicoEstudiante("ana@example.com");
        assertEquals("Ana", estudiante.getNombreEstudiante());
        assertEquals(65, estudiante.getEdadEstudiante());
        assertEquals("ana@example.com", estudiante.getCorreoElectronicoEstudiante());
    }

    @Test public void rechazaNombreNuloVacioOConNumeros() {
        Estudiante estudiante = valido();
        assertThrows(IllegalArgumentException.class, () -> estudiante.setNombreEstudiante(null));
        assertThrows(IllegalArgumentException.class, () -> estudiante.setNombreEstudiante("   "));
        assertThrows(IllegalArgumentException.class, () -> estudiante.setNombreEstudiante("Ana2"));
        assertThrows(IllegalArgumentException.class, () -> estudiante.setNombreEstudiante("Ana@"));
    }

    @Test public void aceptaNombresAcentuadosYCompuestos() {
        Estudiante estudiante = valido();
        estudiante.setNombreEstudiante("María José");
        estudiante.setApellidoEstudiante("D'Arte-López");
        assertEquals("María José", estudiante.getNombreEstudiante());
        assertEquals("D'Arte-López", estudiante.getApellidoEstudiante());
    }

    @Test public void rechazaEdadFueraDe18A100YAceptaLimites() {
        Estudiante estudiante = valido();
        assertThrows(IllegalArgumentException.class, () -> estudiante.setEdadEstudiante(-1));
        assertThrows(IllegalArgumentException.class, () -> estudiante.setEdadEstudiante(17));
        assertThrows(IllegalArgumentException.class, () -> estudiante.setEdadEstudiante(101));
        estudiante.setEdadEstudiante(18);
        assertEquals(18, estudiante.getEdadEstudiante());
        estudiante.setEdadEstudiante(100);
        assertEquals(100, estudiante.getEdadEstudiante());
    }

    @Test public void rechazaFechaFutura() {
        Estudiante estudiante = valido();
        assertThrows(IllegalArgumentException.class,
                () -> estudiante.setFechaNacimientoEstudiante(LocalDate.now().plusDays(1)));
    }

    @Test public void rechazaCorreoInvalido() {
        Estudiante estudiante = valido();
        assertThrows(IllegalArgumentException.class,
                () -> estudiante.setCorreoElectronicoEstudiante("correo-sin-arroba"));
        assertThrows(IllegalArgumentException.class,
                () -> estudiante.setCorreoElectronicoEstudiante("ana@dominio"));
    }

    @Test public void rechazaDpiNoNumericoOCantidadIncorrecta() {
        Estudiante estudiante = valido();
        assertThrows(IllegalArgumentException.class, () -> estudiante.setNoDpiEstudiante("123ABC7890123"));
        assertThrows(IllegalArgumentException.class, () -> estudiante.setNoDpiEstudiante("123456789012"));
    }

    @Test public void verificaToStringYEqualsPorIdentificador() {
        Estudiante primero = valido();
        Estudiante mismoId = new Estudiante(7, "OTRO", "Nombre", "Apellido", 30,
                LocalDate.of(1996, 2, 3), "Masculino", "otro@example.com", "", null);
        assertTrue(primero.toString().contains("C-001"));
        assertEquals(primero, mismoId);
        assertEquals(primero.hashCode(), mismoId.hashCode());
    }

    @Test public void elCarnetSinIdComparaPorCarnet() {
        Estudiante primero = new Estudiante("C-009", "Eva", "Paz", 25,
                LocalDate.of(2001, 1, 1), "Femenino", "eva@example.com", "", null);
        Estudiante segundo = new Estudiante("C-009", "Eva", "Paz", 25,
                LocalDate.of(2001, 1, 1), "Femenino", "eva@example.com", "", null);
        assertEquals(primero, segundo);
        assertEquals(primero.hashCode(), segundo.hashCode());
    }
}
