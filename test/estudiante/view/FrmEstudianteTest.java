package estudiante.view;

import javax.swing.JTextField;
import org.junit.Test;
import static org.junit.Assert.*;

public class FrmEstudianteTest {

    @Test public void campoEdadSoloAceptaDigitosYHastaTresCaracteres() {
        JTextField campo = new JTextField();
        FrmEstudiante.instalarFiltro(campo, 3, "[0-9]*");
        campo.setText("25");
        campo.setText("2501");
        assertEquals("25", campo.getText());
        campo.setText("abc");
        assertEquals("25", campo.getText());
    }

    @Test public void campoDeTextoRespetaLaLongitudMaxima() {
        JTextField campo = new JTextField();
        FrmEstudiante.instalarFiltro(campo, 5, null);
        campo.setText("ABCDEF");
        assertEquals("", campo.getText());
        campo.setText("ABCDE");
        assertEquals("ABCDE", campo.getText());
    }

    @Test public void reemplazoDeTextoRespetaElFiltro() {
        JTextField campo = new JTextField();
        FrmEstudiante.instalarFiltro(campo, 3, "[0-9]*");
        campo.setText("123");
        campo.setText("a1");
        assertEquals("123", campo.getText());
        campo.setText("42");
        assertEquals("42", campo.getText());
    }

    @Test public void consultaCarnetAceptaSoloNumeros() {
        assertEquals("12345", FrmEstudiante.validarNumeroCarnetBusqueda(" 12345 "));
        assertThrows(IllegalArgumentException.class,
                () -> FrmEstudiante.validarNumeroCarnetBusqueda("ABC123"));
        assertThrows(IllegalArgumentException.class,
                () -> FrmEstudiante.validarNumeroCarnetBusqueda("12-34"));
    }
}
