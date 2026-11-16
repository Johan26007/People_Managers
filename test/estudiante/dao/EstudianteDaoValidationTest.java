package estudiante.dao;

import java.time.LocalDate;
import estudiante.model.Estudiante;
import org.junit.Test;
import static org.junit.Assert.*;

public class EstudianteDaoValidationTest {

    @Test public void daoRechazaDatosInvalidosAntesDeConectarse() {
        EstudianteDao dao = new EstudianteDao();
        Estudiante edadInvalida = Estudiante.desdeBaseDatos(1, "C-1", "Ana", "Paz", 17,
                LocalDate.of(2001, 1, 1), "Femenino", "ana@example.com", "", null);
        assertThrows(IllegalArgumentException.class, () -> dao.guardar(edadInvalida));
    }
}
