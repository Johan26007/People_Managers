package cursos.dao;

import conexion.CreateConnection;
import cursos.model.Curso;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a tabla cursos mediante la conexion compartida del proyecto. */
public class CursoDao {
    private static final String COLUMNAS = "id_curso, nombre_curso, credito_curso, codigo_curso, \"año_pensum\"";

    public List<Curso> obtenerTodos() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM cursos ORDER BY id_curso ASC";
        List<Curso> cursos = new ArrayList<>();
        try (Connection conn = CreateConnection.getConnectionCursos();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cursos.add(mapear(rs));
        }
        return cursos;
    }

    public Curso obtenerId(String id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM cursos WHERE id_curso = ?";
        try (Connection conn = CreateConnection.getConnectionCursos();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setId(ps, 1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** API conservada porque FrmAsignacion consulta el curso con un int. */
    public Curso obtenerId(int id) {
        try {
            return obtenerId(String.format("%03d", id));
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(CursoDao.class.getName()).log(java.util.logging.Level.SEVERE, "No se pudo consultar el curso", ex);
            return null;
        }
    }

    public boolean guardar(Curso curso) throws SQLException {
        String sql = "INSERT INTO cursos (id_curso, nombre_curso, credito_curso, codigo_curso, \"año_pensum\") VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = CreateConnection.getConnectionCursos();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setId(ps, 1, curso.getIdCurso());
            ps.setString(2, curso.getNombreCurso());
            ps.setInt(3, curso.getCreditoCurso());
            ps.setString(4, curso.getCodigoCurso());
            ps.setDate(5, Date.valueOf(curso.getAnioPensum()));
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Curso curso) throws SQLException {
        String sql = "UPDATE cursos SET nombre_curso = ?, credito_curso = ?, codigo_curso = ?, \"año_pensum\" = ? WHERE id_curso = ?";
        try (Connection conn = CreateConnection.getConnectionCursos();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, curso.getNombreCurso());
            ps.setInt(2, curso.getCreditoCurso());
            ps.setString(3, curso.getCodigoCurso());
            ps.setDate(4, Date.valueOf(curso.getAnioPensum()));
            setId(ps, 5, curso.getIdCurso());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(String id) throws SQLException {
        String sql = "DELETE FROM cursos WHERE id_curso = ?";
        try (Connection conn = CreateConnection.getConnectionCursos();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setId(ps, 1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** API conservada para llamadas antiguas con ID entero. */
    public boolean eliminar(int id) {
        try {
            return eliminar(String.format("%03d", id));
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(CursoDao.class.getName()).log(java.util.logging.Level.SEVERE, "No se pudo eliminar el curso", ex);
            return false;
        }
    }

    /**
     * En la BD id_curso es varchar(3) con CHECK '^[0-9]{3}$', por eso se envia como texto ("096").
     * Enviarlo como entero (setInt) hace fallar los WHERE: "operador no existe: character varying = integer".
     */
    private static void setId(PreparedStatement ps, int indice, String id) throws SQLException {
        String normalizado = Curso.normalizarId(id);
        Curso.validarId(normalizado);
        ps.setString(indice, normalizado);
    }

    private static Curso mapear(ResultSet rs) throws SQLException {
        String id = Curso.normalizarId(rs.getString("id_curso"));
        Date fecha = rs.getDate("año_pensum");
        return new Curso(id, rs.getString("nombre_curso"), rs.getInt("credito_curso"),
                rs.getString("codigo_curso"), fecha == null ? "" : fecha.toLocalDate().toString());
    }
}
