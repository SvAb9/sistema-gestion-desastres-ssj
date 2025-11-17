package edu.universidad.test;

import edu.universidad.modelo.Recurso;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba 6: Verificar lógica de uso de recursos
 * Requisito: Gestión de disponibilidad de recursos
 */
public class RecursoTest {

    private Recurso recurso;

    @BeforeEach
    public void setUp() {
        recurso = new Recurso("Agua Potable", 1000);
    }

    @Test
    @DisplayName("6.1 - Usar recursos reduce disponibles correctamente")
    public void testUsarRecursos() {
        recurso.usar(300);

        assertEquals(700, recurso.getDisponible(), "Disponibles debe ser 700");
        assertEquals(300, recurso.getUsado(), "Usados debe ser 300");
        assertEquals(1000, recurso.getTotal(), "Total debe mantenerse en 1000");
    }

    @Test
    @DisplayName("6.2 - Liberar recursos aumenta disponibles")
    public void testLiberarRecursos() {
        recurso.usar(400);
        recurso.liberar(200);

        assertEquals(800, recurso.getDisponible(), "Disponibles debe ser 800");
        assertEquals(200, recurso.getUsado(), "Usados debe ser 200");
    }

    @Test
    @DisplayName("6.3 - Verificar disponibilidad funciona correctamente")
    public void testVerificarDisponibilidad() {
        assertTrue(recurso.hayDisponible(500), "Debe haber suficientes recursos");
        assertFalse(recurso.hayDisponible(1500), "No debe haber suficientes recursos");
    }

    @Test
    @DisplayName("6.4 - No se pueden usar más recursos de los disponibles")
    public void testUsarMasDeLoDisponible() {
        recurso.usar(1200); // Intenta usar más de los disponibles

        assertEquals(1000, recurso.getDisponible(), "Disponibles no debe cambiar");
        assertEquals(0, recurso.getUsado(), "Usados debe seguir en 0");
    }
}