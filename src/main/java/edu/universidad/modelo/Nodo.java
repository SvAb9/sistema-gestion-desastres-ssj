/**
 * En esta clase vamos a representar los nodos o zonas del grafo
 * se van a guardar sus identificadores y los vecinos con sus respectivos pesos
 * en esta clase se agregan y consultan las conexiones entre zonas
 */

package edu.universidad.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public class Nodo {
    private String id;
    private Map<String,Double> vecinos = new LinkedHashMap<>();
    public Nodo() {}
    public Nodo(String id) { this.id = id; }
    public String getId() { return id; }
    public Map<String,Double> getVecinos() { return vecinos; }
    public void addVecino(String id, double peso) { vecinos.put(id,peso); }
}
