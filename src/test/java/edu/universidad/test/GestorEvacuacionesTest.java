package edu.universidad.test;

import edu.universidad.modelo.*;
import edu.universidad.servicio.GestorEvacuaciones;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

/**
 * Prueba 4: Verificar gestión de evacuaciones con prioridades
 * Requisito: Sistema de evacuaciones usando cola de prioridad
 */
public class GestorEvacuacionesTest {

    private GestorEvacuaciones gestor;
    private Zona zonaAlta;
    private Zona zonaBaja;
    private EquipoRescate equipo;

    @BeforeEach
    public void setUp() {
        gestor = new GestorEvacuaciones();

        zonaAlta = new Zona("Zona Crítica", "afectada", 90);
        zonaAlta.setId(1L);

        zonaBaja = new Zona("Zona Segura", "normal", 30);
        zonaBaja.setId(2L);

        equipo = new EquipoRescate("Equipo Alpha", "Comandante");
        equipo.setId(1L);
    }

    @Test
    @DisplayName("4.1 - Programar evacuación crea registro correctamente")
    public void testProgramarEvacuacion() {
        GestorEvacuaciones.Evacuacion evacuacion = gestor.programarEvacuacion(zonaAlta, 500);

        assertNotNull(evacuacion, "Debe crear la evacuación");
        assertEquals("Zona Crítica", evacuacion.getZonaNombre(), "Nombre de zona correcto");
        assertEquals(500, evacuacion.getPersonasAEvacuar(), "Cantidad de personas correcta");
        assertEquals("pendiente", evacuacion.getEstado(), "Estado inicial debe ser pendiente");
    }

    @Test
    @DisplayName("4.2 - Evacuaciones se ordenan por prioridad")
    public void testOrdenPorPrioridad() {
        gestor.programarEvacuacion(zonaBaja, 100);  // Prioridad 30
        gestor.programarEvacuacion(zonaAlta, 200);  // Prioridad 90

        GestorEvacuaciones.Evacuacion siguiente = gestor.verSiguienteEvacuacion();

        assertNotNull(siguiente, "Debe haber evacuación pendiente");
        assertEquals("Zona Crítica", siguiente.getZonaNombre(),
                "Zona de mayor prioridad debe ser la siguiente");
    }

    @Test
    @DisplayName("4.3 - Iniciar evacuación cambia estado correctamente")
    public void testIniciarEvacuacion() {
        gestor.programarEvacuacion(zonaAlta, 300);

        GestorEvacuaciones.Evacuacion iniciada = gestor.iniciarSiguienteEvacuacion(equipo);

        assertNotNull(iniciada, "Debe iniciar la evacuación");
        assertEquals("en_proceso", iniciada.getEstado(), "Estado debe cambiar a en_proceso");
        assertFalse(equipo.isDisponible(), "Equipo debe marcarse como no disponible");
    }

}