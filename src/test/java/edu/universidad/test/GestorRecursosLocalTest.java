package edu.universidad.test;

import edu.universidad.modelo.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Prueba 5: Verificar operaciones CRUD del gestor de recursos
 * Requisito: Gestión completa de recursos, zonas y rutas
 */
public class GestorRecursosLocalTest {

    private GestorRecursosLocal gestor;

    @BeforeEach
    public void setUp() {
        gestor = GestorRecursosLocal.getInstance();
        gestor.limpiarTodosDatos();
    }

    @Test
    @DisplayName("5.1 - Agregar recurso funciona correctamente")
    public void testAgregarRecurso() {
        Recurso recurso = gestor.agregarRecurso("Agua", 1000);

        assertNotNull(recurso, "Debe retornar el recurso creado");
        assertEquals("Agua", recurso.getNombre(), "Nombre correcto");
        assertEquals(1000, recurso.getDisponible(), "Cantidad inicial correcta");

        List<Recurso> recursos = gestor.obtenerRecursos();
        assertEquals(1, recursos.size(), "Debe haber 1 recurso");
    }

    @Test
    @DisplayName("5.2 - Actualizar recurso modifica datos correctamente")
    public void testActualizarRecurso() {
        Recurso recurso = gestor.agregarRecurso("Mantas", 500);
        Long id = recurso.getId();

        boolean actualizado = gestor.actualizarRecurso(id, "Mantas Térmicas", 750);

        assertTrue(actualizado, "Debe actualizar exitosamente");

        Recurso modificado = gestor.obtenerRecursos().get(0);
        assertEquals("Mantas Térmicas", modificado.getNombre(), "Nombre actualizado");
        assertEquals(750, modificado.getDisponible(), "Cantidad actualizada");
    }

    @Test
    @DisplayName("5.3 - Eliminar recurso funciona correctamente")
    public void testEliminarRecurso() {
        Recurso recurso = gestor.agregarRecurso("Medicamentos", 200);
        Long id = recurso.getId();

        boolean eliminado = gestor.eliminarRecurso(id);

        assertTrue(eliminado, "Debe eliminar exitosamente");
        assertTrue(gestor.obtenerRecursos().isEmpty(), "Lista debe estar vacía");
    }

    @Test
    @DisplayName("5.4 - Agregar y buscar zona funciona correctamente")
    public void testAgregarYBuscarZona() {
        Zona zona = gestor.agregarZona("Zona Test", "afectada", 75);

        assertNotNull(zona, "Debe crear la zona");

        Zona encontrada = gestor.buscarZonaPorNombre("Zona Test");
        assertNotNull(encontrada, "Debe encontrar la zona");
        assertEquals("afectada", encontrada.getEstado(), "Estado correcto");
        assertEquals(75, encontrada.getPrioridad(), "Prioridad correcta");
    }
}