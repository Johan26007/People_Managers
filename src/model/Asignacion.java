package model;

import java.util.Date;
import java.util.Objects;

public class Asignacion {
    private String noCarnet;
    private String idCurso;
    private int calificacion;
    private Date fechaAprobacion;

    public Asignacion (){
        
    }
    public Asignacion(String noCarnet, String idCurso, int calificacion, Date fechaAprobacion) {
        this.noCarnet = validarNoCarnet(noCarnet);
        this.idCurso = validarIdCurso(idCurso);
        this.calificacion = validadCalificacion(calificacion);
        this.fechaAprobacion = fechaAprobacion;
    }
    
    public String getNoCarnet() {
        return noCarnet;
    }

    public void setNoCarnet(String noCarnet) {
        this.noCarnet = validarNoCarnet(noCarnet);
    }

    public String getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(String idCurso) {
        this.idCurso = validarIdCurso(idCurso);
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = validadCalificacion(calificacion);
    }

    public Date getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(Date fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }
    
    private static String validarNoCarnet (String noCarnet){
        if (noCarnet == null || !noCarnet.matches("000[0-9]{6}")) {
            throw new IllegalArgumentException("El carnet debe tener 9 dígitos y comnezar con 000");
        }
        return noCarnet;
    }
    
    private static int validadCalificacion (int calificacion){
        if (calificacion < 0 || calificacion > 100) {
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 100");
        }
        return calificacion;
    }
    
   private static String validarIdCurso (String idCurso){
       if (idCurso == null || !idCurso.matches("[0-9]{3}")) {
           throw new IllegalArgumentException("El curso debe contener 3 dígitos");
       }
       return idCurso;
   }
   
    @Override
    public String toString() {
        return "Asignacion{"
                + "noCarnet='" + noCarnet + '\''
                + ", idCurso='" + idCurso + '\''
                + ", calificacion=" + calificacion
                + ", fechaAprobacion=" + fechaAprobacion
                + '}';
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Asignacion)) {
            return false;
        }

        Asignacion otra = (Asignacion) objeto;
        return Objects.equals(noCarnet, otra.noCarnet)
                && Objects.equals(idCurso, otra.idCurso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noCarnet, idCurso);
    }
}
