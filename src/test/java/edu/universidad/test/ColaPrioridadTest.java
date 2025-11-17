package edu.universidad.test;

import edu.universidad.estructura.ColaPrioridad;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba 1: Verificar funcionamiento de Cola de Prioridad
 * Requisito: Estructura de datos Cola de Prioridad con Min-Heap
 */
public class ColaPrioridadTest {

    private ColaPrioridad<String> cola;

    @BeforeEach
    public void setUp() {
        cola = new ColaPrioridad<>();
    }

    @Test
    @DisplayName("1.1 - Insertar y extraer elementos respeta prioridades")
    public void testInsertarYExtraerConPrioridades() {
        // Insertar elementos con diferentes prioridades
        cola.insertar("Baja", 5);
        cola.insertar("Alta", 1);
        cola.insertar("Media", 3);

        // Verificar que extrae en orden de prioridad (menor número = mayor prioridad)
        assertEquals("Alta", cola.extraer(), "Debe extraer el elemento de mayor prioridad (1)");
        assertEquals("Media", cola.extraer(), "Debe extraer el siguiente elemento (3)");
        assertEquals("Baja", cola.extraer(), "Debe extraer el último elemento (5)");
        assertTrue(cola.estaVacia(), "La cola debe estar vacía después de extraer todos");
    }

    @Test
    @DisplayName("1.2 - Peek retorna elemento sin extraerlo")
    public void testPeekSinExtraer() {
        cola.insertar("Elemento1", 10);
        cola.insertar("Elemento2", 5);

        String primero = cola.peek();
        assertEquals("Elemento2", primero, "Peek debe retornar el de mayor prioridad");
        assertEquals(2, cola.tamanio(), "El tamaño debe seguir siendo 2");
    }

    @Test
    @DisplayName("1.3 - Excepción al extraer de cola vacía")
    public void testExcepcionColaVacia() {
        assertThrows(IllegalStateException.class, () -> cola.extraer(),
                "Debe lanzar excepción al extraer de cola vacía");
    }

    @Test
    @DisplayName("1.4 - Limpiar cola funciona correctamente")
    public void testLimpiarCola() {
        cola.insertar("A", 1);
        cola.insertar("B", 2);
        cola.insertar("C", 3);

        cola.limpiar();
        assertTrue(cola.estaVacia(), "La cola debe estar vacía después de limpiar");
        assertEquals(0, cola.tamanio(), "El tamaño debe ser 0");
    }
}