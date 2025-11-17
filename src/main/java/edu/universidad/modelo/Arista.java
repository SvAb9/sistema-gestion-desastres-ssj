package edu.universidad.modelo;

public class Arista {
    private String origenId;
    private String destinoId;
    private double peso;
    public Arista() {}
    public Arista(String o, String d, double p) { this.origenId=o; this.destinoId=d; this.peso=p;}
    public String getOrigen(){return origenId;}
    public String getDestino(){return destinoId;}
    public double getPeso(){return peso;}
}
