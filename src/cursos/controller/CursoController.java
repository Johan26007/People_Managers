package cursos.controller;

import cursos.dao.CursoDao;
import cursos.model.Curso;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CursoController {
    private final CursoDao dao = new CursoDao();
    private String mensajeError = "";

    public String getMensajeError() {
        return mensajeError.isEmpty() ? "No se pudo completar la operacion" : mensajeError;
    }

    public Curso obtenerId(String id) {
        limpiarMensaje();
        try {
            String normalizado = Curso.normalizarId(id);
            Curso.validarId(normalizado);
            return dao.obtenerId(normalizado);
        } catch (IllegalArgumentException ex) {
            mensajeError = ex.getMessage();
        } catch (SQLException ex) {
            mensajeError = "No se pudo consultar la base de datos";
            registrar("consultar", ex);
        }
        return null;
    }

    /** Firma conservada para FrmAsignacion. */
    public Curso obtenerId(int id) { return obtenerId(String.format("%03d", id)); }

    public List<Curso> obtenerCursos() {
        limpiarMensaje();
        try {
            return dao.obtenerTodos();
        } catch (SQLException ex) {
            mensajeError = "No se pudieron cargar los cursos";
            registrar("listar", ex);
            return new ArrayList<>();
        }
    }

    public boolean agregarCurso(String id, String nombre, int creditos, String codigo, String anioPensum) {
        limpiarMensaje();
        try {
            Curso curso = new Curso(Curso.normalizarId(id), nombre, creditos, codigo, anioPensum);
            curso.validar();
            if (dao.obtenerId(curso.getIdCurso()) != null) {
                mensajeError = "Ya existe un curso con ese ID";
                return false;
            }
            if (!dao.guardar(curso)) {
                mensajeError = "No se pudo guardar el curso";
                return false;
            }
            return true;
        } catch (IllegalArgumentException ex) {
            mensajeError = ex.getMessage();
        } catch (SQLException ex) {
            mensajeError = "No se pudo guardar el curso en la base de datos";
            registrar("guardar", ex);
        }
        return false;
    }

    public boolean agregarCurso(int id, String nombre, int creditos, String codigo, String anioPensum) {
        return agregarCurso(String.format("%03d", id), nombre, creditos, codigo, anioPensum);
    }

    public boolean actualizarCurso(String id, String nombre, int creditos, String codigo, String anioPensum) {
        limpiarMensaje();
        try {
            Curso curso = new Curso(Curso.normalizarId(id), nombre, creditos, codigo, anioPensum);
            curso.validar();
            if (!dao.actualizar(curso)) {
                mensajeError = "El curso no existe";
                return false;
            }
            return true;
        } catch (IllegalArgumentException ex) {
            mensajeError = ex.getMessage();
        } catch (SQLException ex) {
            mensajeError = "No se pudo actualizar el curso en la base de datos";
            registrar("actualizar", ex);
        }
        return false;
    }

    public boolean actualizarCurso(int id, String nombre, int creditos, String codigo, String anioPensum) {
        return actualizarCurso(String.format("%03d", id), nombre, creditos, codigo, anioPensum);
    }

    public boolean eliminarCurso(String id) {
        limpiarMensaje();
        try {
            String normalizado = Curso.normalizarId(id);
            Curso.validarId(normalizado);
            if (!dao.eliminar(normalizado)) {
                mensajeError = "El curso no existe";
                return false;
            }
            return true;
        } catch (IllegalArgumentException ex) {
            mensajeError = ex.getMessage();
        } catch (SQLException ex) {
            mensajeError = "No se pudo eliminar el curso de la base de datos";
            registrar("eliminar", ex);
        }
        return false;
    }

    public boolean eliminarCurso(int id) { return eliminarCurso(String.format("%03d", id)); }

    private void limpiarMensaje() { mensajeError = ""; }

    private void registrar(String operacion, SQLException ex) {
        java.util.logging.Logger.getLogger(CursoController.class.getName())
                .log(java.util.logging.Level.SEVERE, "Fallo al " + operacion + " cursos", ex);
    }
}
