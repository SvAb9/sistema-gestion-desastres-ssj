package edu.universidad.test;

import edu.universidad.modelo.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Prueba 3: Verificar algoritmo de Dijkstra en Grafo
 * Requisito: Cálculo de rutas óptimas con algoritmo de Dijkstra
 */
public class GrafoTest {

    private Grafo grafo;

    @BeforeEach
    public void setUp() {
        grafo = new Grafo();
        grafo.agregarNodo(new Nodo("A"));
        grafo.agregarNodo(new Nodo("B"));
        grafo.agregarNodo(new Nodo("C"));
        grafo.agregarNodo(new Nodo("D"));

        grafo.agregarArista("A", "B", 5.0);
        grafo.agregarArista("B", "C", 3.0);
        grafo.agregarArista("A", "C", 10.0);
        grafo.agregarArista("C", "D", 2.0);
    }

    @Test
    @DisplayName("3.1 - Dijkstra encuentra ruta más corta")
    public void testDijkstraRutaCorta() {
        List<Nodo> ruta = grafo.dijkstra("A", "C");

        assertNotNull(ruta, "Debe encontrar una ruta");
        assertEquals(3, ruta.size(), "La ruta debe tener 3 nodos");
        assertEquals("A", ruta.get(0).getId(), "Debe comenzar en A");
        assertEquals("B", ruta.get(1).getId(), "Debe pasar por B");
        assertEquals("C", ruta.get(2).getId(), "Debe terminar en C");
    }

    @Test
    @DisplayName("3.2 - Dijkstra retorna vacío si no hay ruta")
    public void testDijkstraSinRuta() {
        grafo.agregarNodo(new Nodo("Z")); // Nodo aislado

        List<Nodo> ruta = grafo.dijkstra("A", "Z");
        assertTrue(ruta.isEmpty(), "Debe retornar lista vacía si no hay ruta");
    }

    @Test
    @DisplayName("3.3 - BFS encuentra camino correctamente")
    public void testBFS() {
        List<Nodo> ruta = grafo.bfs("A", "D");

        assertNotNull(ruta, "BFS debe encontrar una ruta");
        assertFalse(ruta.isEmpty(), "La ruta no debe estar vacía");
        assertEquals("A", ruta.get(0).getId(), "Debe comenzar en A");
        assertEquals("D", ruta.get(ruta.size() - 1).getId(), "Debe terminar en D");
    }

    @Test
    @DisplayName("3.4 - Grafo verifica conectividad correctamente")
    public void testGrafoConectado() {
        assertTrue(grafo.esConectado(), "El grafo debe estar conectado");

        Grafo grafoDesconectado = new Grafo();
        grafoDesconectado.agregarNodo(new Nodo("X"));
        grafoDesconectado.agregarNodo(new Nodo("Y"));
        // No hay aristas

        assertFalse(grafoDesconectado.esConectado(), "Grafo sin aristas no está conectado");
    }
}
