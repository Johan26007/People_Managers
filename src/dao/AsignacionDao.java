package dao;

import conexion.CreateConnection;
import model.Asignacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AsignacionDao {
    //DEVOLVER TODOS LOS REGISTROS
    private final AsignacionConnectionProvider connectionProvider;

    public AsignacionDao() {
        this(CreateConnection::getConnectionAsignaciones);
    }

    AsignacionDao(AsignacionConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "connectionProvider");
    }

    public List<Asignacion> obtenerTodos () throws SQLException {
        List<Asignacion> lista = new ArrayList<>();
        //INDICAMOS EL ESQUEMA EN QUE ESTÁ ADEMÁS DE INDICARLE EL NOMBRE ESPECÍFICO
        String sql = "SELECT * FROM public.\"Asignación\"";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {                
                Asignacion asig = new Asignacion(
                rs.getString("No_Carnet"),
                rs.getString("Id_Curso"),
                rs.getInt("Calificación"),
                rs.getDate("Fecha_Aprobación")
                );
                lista.add(asig);
            }
        }
        return lista;
    }
    
    //FILTRAR POR ID
    public List<Asignacion> buscarPorCarnet (String carnet) throws SQLException {
        List<Asignacion> listaAsignaciones = new ArrayList<>();
        String sql = "SELECT * FROM public.\"Asignación\" WHERE \"No_Carnet\" = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, carnet);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Asignacion asig = new Asignacion(
                            rs.getString("No_Carnet"),
                            rs.getString("Id_Curso"),
                            rs.getInt("Calificación"),
                            rs.getDate("Fecha_Aprobación")
                    );
                    listaAsignaciones.add(asig);
                }
            }
        }
        return listaAsignaciones;
    }
    
    //GUARDAR
    public boolean guardar (Asignacion asig) throws SQLException{
        String sqlBuscar = "SELECT 1 FROM public.\"Asignación\" WHERE \"No_Carnet\" = ? AND \"Id_Curso\" = ?";
        String sqlInsertar = "INSERT INTO public.\"Asignación\" (\"No_Carnet\", \"Id_Curso\", \"Calificación\", \"Fecha_Aprobación\") VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connectionProvider.getConnection()){
            try (PreparedStatement psBuscar = conn.prepareStatement(sqlBuscar)){
                psBuscar.setString(1, asig.getNoCarnet());
                psBuscar.setString(2, asig.getIdCurso());
                
                try (ResultSet rs = psBuscar.executeQuery()){
                    if (rs.next()) {
                        throw  new IllegalArgumentException("Ya existe una asignación para ese carnet y curso");
                    }
                }
            }
            try (PreparedStatement psInsertar = conn.prepareStatement(sqlInsertar)){
                psInsertar.setString(1, asig.getNoCarnet());
                psInsertar.setString(2, asig.getIdCurso());
                psInsertar.setInt(3, asig.getCalificacion());
                psInsertar.setDate(4, new java.sql.Date(asig.getFechaAprobacion().getTime()));
                return psInsertar.executeUpdate() > 0;
            }
        } 
    }
    //ACTUALIZAR
    public boolean actualizar(Asignacion asig, String noCarnetOriginal, String idCursoOriginal) throws SQLException {
        //CAMBIAR
        String sql = "UPDATE public.\"Asignación\" SET \"Id_Curso\" = ?, \"Calificación\" = ?, \"Fecha_Aprobación\" = ? WHERE \"No_Carnet\" = ? AND \"Id_Curso\" = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
      
            ps.setString(1, asig.getIdCurso()); // Curso nuevo
            ps.setInt(2, asig.getCalificacion());
            ps.setDate(3, new java.sql.Date(asig.getFechaAprobacion().getTime()));
            ps.setString(4, noCarnetOriginal);
            ps.setString(5, idCursoOriginal);

            return ps.executeUpdate() > 0;
        }
    }
    
    //ELIMINAR
    public boolean eliminar(String noCarnet) throws SQLException {
        String sql = "DELETE FROM public.\"Asignación\" WHERE \"No_Carnet\" = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noCarnet);
            
            return ps.executeUpdate() > 0;
        }
    }
}
