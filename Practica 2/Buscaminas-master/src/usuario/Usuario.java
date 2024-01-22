package usuario;

import java.io.Serializable;

public class Usuario implements Serializable{
    String nombre;
    int puntuacion;
    int partidas;
    
    public Usuario(String nombre, int puntuacion, int partidas){
        this.nombre = nombre;
        this.puntuacion = puntuacion;
        this.partidas = partidas;                
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public int getPartidas() {
        return partidas;
    }

    public void setPartidas(int partidas) {
        this.partidas = partidas;
    }                 
}
