/**
 * En esta clase se maneja toda la estructura del grafo
 * aqui se van a agregar nodos, aristas y se calculan rutas con el algoritmo dijkstra
 */
package edu.universidad.modelo;

import java.util.*;

public class Grafo {

    private Map<String,Nodo> nodos= new LinkedHashMap<>();
    private List<Arista> aristas=new ArrayList<>();

    public void agregarNodo (Nodo n){
        nodos.put(n.getId(), n);
    }
    public void agregarArista( String origen, String destino, double peso) {
        Nodo org= nodos.get(origen), dest= nodos.get(destino);   // ambos son de tipo nodo
        if (org == null || dest == null) return;
        aristas.add(new Arista (origen, destino, peso));
        org.addVecino(destino, peso);
    }

    public Collection<Nodo> getNodos(String id){
        return nodos.values();
    }

    public List <Arista> getAristas () {
        return aristas;
    }

    public Nodo getNodo (String id){
        return nodos.get(id);
    }

    public List <Nodo> dijkstra (String origenId, String destinoId){   // Encuentra el camino más corto entre dos nodos
        if(!nodos.containsKey(origenId) || !nodos.containsKey(destinoId)) return Collections.emptyList();
        // si alguno de los dos nodos no existe, retorna una lista vacia
        Map<String, Double> distancia= new HashMap<>();
        Map<String, String> previo= new HashMap<>();
        for (String id: nodos.keySet()){
            distancia.put(id, Double.POSITIVE_INFINITY);  // las distancias se inicializan como infinitas
            previo.put(id, null); // los nodos previos se inicializan en nulos
        }
        distancia.put(origenId, 0.0);
        PriorityQueue<String> masCorta= new PriorityQueue<>(Comparator.comparingDouble(distancia:: get));
        masCorta.add(origenId);
        //la cola procesa primero el nodo con la distancia más corta

        while(!masCorta.isEmpty()){
            String nodo= masCorta.poll();  // extrae el nodo con menor distancia
            if(nodo.equals(destinoId)) break;  // si llegamos al destino se termina el bucle

            Nodo n= nodos.get(nodo);
            for(Map. Entry<String, Double> e : n.getVecinos().entrySet()){  //revisa a todos los vecinos
                String id = e.getKey();  // id del vecino
                double peso= e.getValue();  // peso de la arista
                double alt= distancia.get(nodo) + peso;  //distancia alternativa

                if(alt<distancia.get(id)){  // si el camino es mas corto
                    distancia.put(id, alt);  // actualizamos la distancia
                    previo.put(id, nodo);  // actualizamos el nodo anterior
                    masCorta.remove(id);   // borramos el nodo anterior
                    masCorta.add(id);  //agregamos el nuevo nodo encontrado
                }
            }
        }

        LinkedList<Nodo>  camino= new LinkedList<>();
        String d= destinoId;
        if (previo.get(d) == null && !d.equals(origenId)){
            if(d.equals(origenId))
                camino.add(nodos.get(d));
                return camino;
        // verifica si hay un camino valido
        }

        while (d != null){
            camino.addFirst(nodos.get(d));
            d = previo.get(d);
        // construye el camino encontrados desde el destino hasta el origen
        }
        return camino;
    }
}
