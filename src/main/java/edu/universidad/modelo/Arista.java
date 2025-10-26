/**
 * En esta clase se representan las aristas o rutas del grafo
 * une dos nodos y define el peso o distancia entre ellos
 */
package edu.universidad.modelo;

public class Arista {
    private String origenId;
    private String destinoId;
    private double peso;

    public Arista(){}

    public Arista(String origenId, String destinoId, double peso) {
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.peso = peso;
    }

    public String getOrigenId() {
        return origenId;
    }
    public String getDestinoId() {
        return destinoId;
    }
    public double getPeso() {
        return peso;
    }
}
