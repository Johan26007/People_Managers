package estudiante.controller;

import estudiante.dao.EstudianteDao;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import estudiante.model.Estudiante;

/** Controlador de las operaciones CRUD del módulo de colaboradores. */
public class EstudianteController {

    private final EstudianteDao estudianteDao;

    public EstudianteController() {
        this(new EstudianteDao());
    }

    /** Permite probar las operaciones del controlador con un DAO simulado. */
    public EstudianteController(EstudianteDao estudianteDao) {
        this.estudianteDao = Objects.requireNonNull(estudianteDao, "El DAO es obligatorio.");
    }

    public List<Estudiante> obtenerEstudiantes() {
        return estudianteDao.obtenerTodos();
    }

    public Estudiante obtenerEstudiantePorCarnet(String carnet) {
        if (carnet == null || carnet.isBlank()) return null;
        return estudianteDao.obtenerPorCarnet(carnet.trim());
    }

    public boolean probarConexion() {
        return estudianteDao.probarConexion();
    }

    public boolean agregarEstudiante(String carnet, String nombre, String apellido, int edad,
            LocalDate fechaNacimiento, String genero, String correo, String telefono, String dpi) {
        Estudiante.validarRegistro(carnet, nombre, apellido, edad, fechaNacimiento,
                genero, correo, telefono, dpi);
        Estudiante estudiante = new Estudiante(carnet, nombre, apellido, edad,
                fechaNacimiento, genero, correo, telefono, dpi);
        return estudianteDao.guardar(estudiante);
    }

    public boolean actualizarEstudiante(int id, String carnet, String nombre, String apellido,
            int edad, LocalDate fechaNacimiento, String genero, String correo,
            String telefono, String dpi) {
        Estudiante.validarRegistro(carnet, nombre, apellido, edad, fechaNacimiento,
                genero, correo, telefono, dpi);
        Estudiante estudiante = new Estudiante(id, carnet, nombre, apellido, edad,
                fechaNacimiento, genero, correo, telefono, dpi);
        return estudianteDao.actualizar(estudiante);
    }

    public boolean eliminarEstudiantePorCarnet(String carnet) {
        if (carnet == null || carnet.isBlank()) return false;
        return estudianteDao.eliminarPorCarnet(carnet.trim());
    }
}
