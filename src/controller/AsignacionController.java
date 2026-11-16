
package controller;

import dao.AsignacionDao;
import java.sql.SQLException;
import java.util.Date;
import model.Asignacion;
import java.util.List;
import java.util.Objects;

public class AsignacionController {
    
    private final AsignacionDao dao;
    private final AsignacionView vista;

    public AsignacionController() {
        this(new AsignacionDao(), null);
    }

    // Permite sustituir el DAO por un doble de prueba cuando sea necesario.
    public AsignacionController(AsignacionDao dao) {
        this(dao, null);
    }

    public AsignacionController(AsignacionDao dao, AsignacionView vista) {
        this.dao = Objects.requireNonNull(dao, "dao");
        this.vista = vista;
    }

    private void actualizarVistaTrasOperacion() {
        if (vista != null) {
            vista.limpiar();
            vista.cargarAsignaciones();
        }
    }
    
    //BUSCAR POR CARNET
    public List<Asignacion> buscarPorCarnet (String noCarnet){
        if (noCarnet == null || !noCarnet.trim().matches("000[0-9]{6}")) {
            throw  new IllegalArgumentException("El carnet debe tener 9 dígitos y comenzar con 000");
        }
        try {
            return dao.buscarPorCarnet(noCarnet.trim());
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar la asignación por un problema con la base de datos", e);
        }
    }
    //REGRESAR TODOS LOS REGISTROS
    public List<Asignacion> obtenerTodos (){
        try {
            return dao.obtenerTodos();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar las asignaciones por un problema con la base de datos", e);
        }
    }
    //AGREGAR ASIGNACIÓN
    public boolean guardarAsignacion (String noCarnet, String idCurso, int calificacion, Date fechaAprobacion){
        Asignacion  asig = new Asignacion(noCarnet, idCurso, calificacion, fechaAprobacion);
        try {
            boolean guardado = dao.guardar(asig);
            if (guardado) {
                actualizarVistaTrasOperacion();
            }
            return guardado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar la asignación por un problema con la base de datos", e);
        }
        
    }
    //ACTUALIZAR ASIGNACIÓN
    public boolean actualizarAsignacion(String noCarnetOriginal, String idCursoOriginal, String idCursoNuevo, int calificacion, Date fechaAprobacion) {
        Asignacion asig = new Asignacion(noCarnetOriginal, idCursoNuevo, calificacion, fechaAprobacion);
        try {
            boolean actualizado = dao.actualizar(asig, noCarnetOriginal, idCursoOriginal);
            if (actualizado) {
                actualizarVistaTrasOperacion();
            }
            return actualizado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar la asignación por un problema con la base de datos", e);
        }
    }
    //ELIMINAR ASIGNACIÓN
    public boolean eliminarAsignacion (String noCarnet){
        try {
            boolean eliminado = dao.eliminar(noCarnet);
            if (eliminado) {
                actualizarVistaTrasOperacion();
            }
            return eliminado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar la asignación por un problema con la base de datos", e);
        }
    }
}
