package edu.universidad.test;

import edu.universidad.estructura.ArbolDistribucion;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Prueba 2: Verificar distribución de recursos con Árbol
 * Requisito: Estructura jerárquica para distribución de recursos
 */
public class ArbolDistribucionTest {

    private ArbolDistribucion<String> arbol;

    @BeforeEach
    public void setUp() {
        arbol = new ArbolDistribucion<>("Centro", "centro");
        arbol.agregarNodo("centro", "Zona Norte", "norte");
        arbol.agregarNodo("centro", "Zona Sur", "sur");
    }

    @Test
    @DisplayName("2.1 - Distribución equitativa divide recursos correctamente")
    public void testDistribucionEquitativa() {
        arbol.distribuirRecursos(100);

        ArbolDistribucion.NodoArbol<String> norte = arbol.buscarNodo("norte");
        ArbolDistribucion.NodoArbol<String> sur = arbol.buscarNodo("sur");

        assertEquals(50, norte.getCantidadAsignada(), "Zona Norte debe recibir 50");
        assertEquals(50, sur.getCantidadAsignada(), "Zona Sur debe recibir 50");
    }

    @Test
    @DisplayName("2.2 - Distribución con prioridades asigna más recursos a mayor prioridad")
    public void testDistribucionConPrioridades() {
        Map<String, Integer> prioridades = new HashMap<>();
        prioridades.put("norte", 3); // Mayor prioridad
        prioridades.put("sur", 1);   // Menor prioridad

        arbol.distribuirConPrioridades(100, prioridades);

        ArbolDistribucion.NodoArbol<String> norte = arbol.buscarNodo("norte");
        ArbolDistribucion.NodoArbol<String> sur = arbol.buscarNodo("sur");

        assertTrue(norte.getCantidadAsignada() > sur.getCantidadAsignada(),
                "Zona Norte (prioridad 3) debe recibir más que Sur (prioridad 1)");
    }

    @Test
    @DisplayName("2.3 - Calcular total en hojas suma correctamente")
    public void testCalcularTotalHojas() {
        arbol.distribuirRecursos(100);

        int total = arbol.calcularTotalHojas();
        assertEquals(100, total, "El total en hojas debe ser 100");
    }

    @Test
    @DisplayName("2.4 - Agregar nodo crea estructura correcta")
    public void testAgregarNodo() {
        boolean agregado = arbol.agregarNodo("norte", "Sector A", "norte_a");

        assertTrue(agregado, "Debe agregar el nodo exitosamente");
        assertNotNull(arbol.buscarNodo("norte_a"), "El nodo debe existir en el árbol");
    }
}