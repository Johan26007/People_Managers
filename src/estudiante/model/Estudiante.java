package estudiante.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Datos de una persona gestionada por la pantalla de colaboradores.
 * Los constructores y setters validan valores nuevos. La fábrica desde BD
 * conserva registros históricos para que una regla nueva no impida leerlos.
 */
public class Estudiante {

    private static final Pattern NOMBRE_VALIDO = Pattern.compile("[\\p{L}]+(?:[ '\u2019-][\\p{L}]+)*");
    private static final Pattern CORREO_VALIDO = Pattern.compile(
            "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$");
    private static final Pattern TELEFONO_VALIDO = Pattern.compile("[0-9+() .-]*");
    private static final Pattern DPI_VALIDO = Pattern.compile("[0-9]{13}");

    private int idEstudiante;
    private String noCarnetEstudiante;
    private String nombreEstudiante;
    private String apellidoEstudiante;
    private int edadEstudiante;
    private LocalDate fechaNacimientoEstudiante;
    private String generoEstudiante;
    private String correoElectronicoEstudiante;
    private String telefonoEstudiante;
    private String noDpiEstudiante;

    public Estudiante() {
    }

    public Estudiante(int idEstudiante, String noCarnetEstudiante, String nombreEstudiante,
            String apellidoEstudiante, int edadEstudiante, LocalDate fechaNacimientoEstudiante,
            String generoEstudiante, String correoElectronicoEstudiante,
            String telefonoEstudiante, String noDpiEstudiante) {
        this(noCarnetEstudiante, nombreEstudiante, apellidoEstudiante, edadEstudiante,
                fechaNacimientoEstudiante, generoEstudiante, correoElectronicoEstudiante,
                telefonoEstudiante, noDpiEstudiante);
        this.idEstudiante = idEstudiante;
    }

    public Estudiante(String noCarnetEstudiante, String nombreEstudiante,
            String apellidoEstudiante, int edadEstudiante, LocalDate fechaNacimientoEstudiante,
            String generoEstudiante, String correoElectronicoEstudiante,
            String telefonoEstudiante, String noDpiEstudiante) {
        setNoCarnetEstudiante(noCarnetEstudiante);
        setNombreEstudiante(nombreEstudiante);
        setApellidoEstudiante(apellidoEstudiante);
        setEdadEstudiante(edadEstudiante);
        setFechaNacimientoEstudiante(fechaNacimientoEstudiante);
        setGeneroEstudiante(generoEstudiante);
        setCorreoElectronicoEstudiante(correoElectronicoEstudiante);
        setTelefonoEstudiante(telefonoEstudiante);
        setNoDpiEstudiante(noDpiEstudiante);
    }

    /** Construye un registro leído de la BD sin descartar datos históricos. */
    public static Estudiante desdeBaseDatos(int idEstudiante, String carnet, String nombre,
            String apellido, int edad, LocalDate fechaNacimiento, String genero,
            String correo, String telefono, String dpi) {
        Estudiante estudiante = new Estudiante();
        estudiante.idEstudiante = idEstudiante;
        estudiante.noCarnetEstudiante = carnet;
        estudiante.nombreEstudiante = nombre;
        estudiante.apellidoEstudiante = apellido;
        estudiante.edadEstudiante = edad;
        estudiante.fechaNacimientoEstudiante = fechaNacimiento;
        estudiante.generoEstudiante = genero;
        estudiante.correoElectronicoEstudiante = correo;
        estudiante.telefonoEstudiante = telefono;
        estudiante.noDpiEstudiante = dpi;
        return estudiante;
    }

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    public String getNoCarnetEstudiante() { return noCarnetEstudiante; }
    public void setNoCarnetEstudiante(String value) {
        validarCarnet(value);
        this.noCarnetEstudiante = value.trim();
    }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String value) {
        validarNombre(value, "nombre");
        this.nombreEstudiante = value.trim();
    }

    public String getApellidoEstudiante() { return apellidoEstudiante; }
    public void setApellidoEstudiante(String value) {
        validarNombre(value, "apellido");
        this.apellidoEstudiante = value.trim();
    }

    public int getEdadEstudiante() { return edadEstudiante; }
    public void setEdadEstudiante(int value) {
        validarEdad(value);
        this.edadEstudiante = value;
    }

    public LocalDate getFechaNacimientoEstudiante() { return fechaNacimientoEstudiante; }
    public void setFechaNacimientoEstudiante(LocalDate value) {
        validarFechaNacimiento(value);
        this.fechaNacimientoEstudiante = value;
    }

    public String getGeneroEstudiante() { return generoEstudiante; }
    public void setGeneroEstudiante(String value) {
        validarGenero(value);
        this.generoEstudiante = value.trim();
    }

    public String getCorreoElectronicoEstudiante() { return correoElectronicoEstudiante; }
    public void setCorreoElectronicoEstudiante(String value) {
        validarCorreo(value);
        this.correoElectronicoEstudiante = value.trim();
    }

    public String getTelefonoEstudiante() { return telefonoEstudiante; }
    public void setTelefonoEstudiante(String value) {
        validarTelefono(value);
        this.telefonoEstudiante = value == null ? null : value.trim();
    }

    public String getNoDpiEstudiante() { return noDpiEstudiante; }
    public void setNoDpiEstudiante(String value) {
        validarDpi(value);
        this.noDpiEstudiante = value == null ? null : value.trim();
    }

    @Override
    public String toString() {
        return "Estudiante{id=" + idEstudiante + ", carnet='" + noCarnetEstudiante
                + "', nombre='" + nombreEstudiante + " " + apellidoEstudiante + "'}";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Estudiante estudiante)) return false;
        if (idEstudiante > 0 || estudiante.idEstudiante > 0)
            return idEstudiante > 0 && idEstudiante == estudiante.idEstudiante;
        return noCarnetEstudiante != null
                && noCarnetEstudiante.equals(estudiante.noCarnetEstudiante);
    }

    @Override
    public int hashCode() {
        return idEstudiante > 0 ? Integer.hashCode(idEstudiante)
                : Objects.hashCode(noCarnetEstudiante);
    }

    public static void validarRegistro(String carnet, String nombre, String apellido,
            int edad, LocalDate fechaNacimiento, String genero, String correo,
            String telefono, String dpi) {
        validarCarnet(carnet);
        validarNombre(nombre, "nombre");
        validarNombre(apellido, "apellido");
        validarEdad(edad);
        validarFechaNacimiento(fechaNacimiento);
        validarGenero(genero);
        validarCorreo(correo);
        validarTelefono(telefono);
        validarDpi(dpi);
    }

    public static void validarCarnet(String value) {
        requerido(value, "El número de colaborador es obligatorio.");
        if (value.trim().length() > 50) {
            throw new IllegalArgumentException("El número de colaborador no puede exceder 50 caracteres.");
        }
    }

    public static void validarNombre(String value, String campo) {
        requerido(value, "El " + campo + " es obligatorio.");
        String texto = value.trim();
        if (texto.length() > 100) {
            throw new IllegalArgumentException("El " + campo + " no puede exceder 100 caracteres.");
        }
        if (!NOMBRE_VALIDO.matcher(texto).matches()) {
            throw new IllegalArgumentException("El " + campo + " solo puede contener letras, espacios, apóstrofes o guiones.");
        }
    }

    public static void validarEdad(int value) {
        if (value < 18 || value > 100) {
            throw new IllegalArgumentException("La edad debe estar entre 18 y 100 años.");
        }
    }

    public static void validarFechaNacimiento(LocalDate value) {
        if (value == null) throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
    }

    public static void validarGenero(String value) {
        requerido(value, "Seleccione un género.");
        if (!"Masculino".equalsIgnoreCase(value.trim())
                && !"Femenino".equalsIgnoreCase(value.trim())) {
            throw new IllegalArgumentException("Seleccione un género válido.");
        }
    }

    public static void validarCorreo(String value) {
        requerido(value, "El correo electrónico es obligatorio.");
        String texto = value.trim();
        if (texto.length() > 150 || !CORREO_VALIDO.matcher(texto).matches()) {
            throw new IllegalArgumentException("Ingrese un correo electrónico válido (máximo 150 caracteres).");
        }
    }

    public static void validarTelefono(String value) {
        if (value == null || value.isBlank()) return;
        String texto = value.trim();
        if (texto.length() > 20 || !TELEFONO_VALIDO.matcher(texto).matches()) {
            throw new IllegalArgumentException("El teléfono admite números y los símbolos + ( ) - .; máximo 20 caracteres.");
        }
    }

    public static void validarDpi(String value) {
        if (value == null || value.isBlank()) return;
        if (!DPI_VALIDO.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("El DPI debe contener exactamente 13 dígitos.");
        }
    }

    private static void requerido(String value, String mensaje) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(mensaje);
    }
}
