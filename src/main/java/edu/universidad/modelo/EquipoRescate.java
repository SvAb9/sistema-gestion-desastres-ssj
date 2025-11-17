package edu.universidad.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "equipos_rescate")
public class EquipoRescate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String responsable;
    private boolean disponible;
    private int miembros; // ✅ CAMPO AGREGADO

    public EquipoRescate() {}

    public EquipoRescate(String nombre, String responsable){
        this.nombre = nombre;
        this.responsable = responsable;
        this.disponible = true;
        this.miembros = 0; // ✅ INICIALIZACIÓN
    }

    // Getters y Setters
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String n){
        this.nombre = n;
    }

    public String getResponsable(){
        return responsable;
    }

    public void setResponsable(String r){
        this.responsable = r;
    }

    public boolean isDisponible(){
        return disponible;
    }

    public void setDisponible(boolean d){
        this.disponible = d;
    }

    // ✅ MÉTODOS AGREGADOS
    public int getMiembros() {
        return miembros;
    }

    public void setMiembros(int miembros) {
        this.miembros = miembros;
    }

    @Override
    public String toString() {
        return "EquipoRescate{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", responsable='" + responsable + '\'' +
                ", disponible=" + disponible +
                ", miembros=" + miembros +
                '}';
    }
}