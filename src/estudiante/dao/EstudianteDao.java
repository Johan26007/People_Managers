package estudiante.dao;

import conexion.CreateConnection;
import estudiante.model.Estudiante;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDao {

    // Usa la conexion compartida del proyecto unificado (servidor de Estudiantes).
    private Connection abrirConexion() throws SQLException {
        Connection conn = CreateConnection.getConnectionEstudiantes();
        if (conn == null) throw new SQLException("No fue posible conectarse a la base de datos.");
        return conn;
    }

    public boolean probarConexion() {
        try (Connection ignored = abrirConexion()) {
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public boolean guardar(Estudiante est) {
        validar(est);
        String sql = "INSERT INTO tb_estudiante (no_carnet_estudiante, nombre_estudiante, apellido_estudiante, edad_estudiante, fecha_nacimiento_estudiante, "
                + "genero_estudiante, correo_electronico_estudiante, telefono_estudiante, no_dpi_estudiante) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, est.getNoCarnetEstudiante());
            ps.setString(2, est.getNombreEstudiante());
            ps.setString(3, est.getApellidoEstudiante());
            ps.setInt(4, est.getEdadEstudiante());
            ps.setObject(5, est.getFechaNacimientoEstudiante());
            ps.setString(6, est.getGeneroEstudiante());
            ps.setString(7, est.getCorreoElectronicoEstudiante());
            ps.setString(8, est.getTelefonoEstudiante());
            ps.setString(9, est.getNoDpiEstudiante());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("No se pudo guardar el colaborador en la base de datos.");
        }
        return false;
    }

    public List<Estudiante> obtenerTodos() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM tb_estudiante ORDER BY id_estudiante ASC";

        try (Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Estudiante est = Estudiante.desdeBaseDatos(
                        rs.getInt("id_estudiante"),
                        rs.getString("no_carnet_estudiante"),
                        rs.getString("nombre_estudiante"),
                        rs.getString("apellido_estudiante"),
                        rs.getInt("edad_estudiante"),
                        rs.getObject("fecha_nacimiento_estudiante", LocalDate.class),
                        rs.getString("genero_estudiante"),
                        rs.getString("correo_electronico_estudiante"),
                        rs.getString("telefono_estudiante"),
                        rs.getString("no_dpi_estudiante"));

                lista.add(est);
            }
        } catch (SQLException e) {
            System.err.println("No se pudo consultar la lista de colaboradores.");
        }
        return lista;
    }

    public Estudiante obtenerPorCarnet(String carnet) {
        Estudiante est = null;
        String sql = "SELECT * FROM tb_estudiante WHERE no_carnet_estudiante = ?";

        try (Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, carnet);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    est = Estudiante.desdeBaseDatos(
                            rs.getInt("id_estudiante"),
                            rs.getString("no_carnet_estudiante"),
                            rs.getString("nombre_estudiante"),
                            rs.getString("apellido_estudiante"),
                            rs.getInt("edad_estudiante"),
                            rs.getObject("fecha_nacimiento_estudiante", LocalDate.class),
                            rs.getString("genero_estudiante"),
                            rs.getString("correo_electronico_estudiante"),
                            rs.getString("telefono_estudiante"),
                            rs.getString("no_dpi_estudiante"));
                }
            }
        } catch (SQLException e) {
            System.err.println("No se pudo consultar el colaborador.");
        }
        return est;
    }

    public boolean actualizar(Estudiante est) {
        validar(est);
        String sql = "UPDATE tb_estudiante SET no_carnet_estudiante = ?, nombre_estudiante = ?, apellido_estudiante = ?, edad_estudiante = ?, "
                + "fecha_nacimiento_estudiante = ?, genero_estudiante = ?, correo_electronico_estudiante = ?, telefono_estudiante = ?, no_dpi_estudiante = ? "
                + "WHERE id_estudiante = ?";
        try (Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, est.getNoCarnetEstudiante());
            ps.setString(2, est.getNombreEstudiante());
            ps.setString(3, est.getApellidoEstudiante());
            ps.setInt(4, est.getEdadEstudiante());
            ps.setObject(5, est.getFechaNacimientoEstudiante());
            ps.setString(6, est.getGeneroEstudiante());
            ps.setString(7, est.getCorreoElectronicoEstudiante());
            ps.setString(8, est.getTelefonoEstudiante());
            ps.setString(9, est.getNoDpiEstudiante());
            ps.setInt(10, est.getIdEstudiante());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("No se pudo actualizar el colaborador.");
        }
        return false;
    }

    public boolean eliminarPorCarnet(String carnet) {
        String sql = "DELETE FROM tb_estudiante WHERE no_carnet_estudiante = ?";

        try (Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, carnet);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si encontró el carnet y lo borró con éxito

        } catch (SQLException e) {
            System.err.println("No se pudo eliminar el colaborador.");
        }
        return false;
    }

    private void validar(Estudiante est) {
        if (est == null) throw new IllegalArgumentException("El colaborador es obligatorio.");
        Estudiante.validarRegistro(est.getNoCarnetEstudiante(),
                est.getNombreEstudiante(), est.getApellidoEstudiante(),
                est.getEdadEstudiante(), est.getFechaNacimientoEstudiante(),
                est.getGeneroEstudiante(), est.getCorreoElectronicoEstudiante(),
                est.getTelefonoEstudiante(), est.getNoDpiEstudiante());
    }
}
