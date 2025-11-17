package edu.universidad.vista;

import edu.universidad.modelo.*;
import edu.universidad.servicio.GestorEvacuaciones;
import edu.universidad.vista.componentes.*;
import edu.universidad.vista.paneles.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Ventana Principal - Sistema de Gestión de Desastres
 * Clase simplificada que delega a componentes especializados
 */
public class VentanaPrincipal extends JFrame {

    // ATRIBUTOS PRINCIPALES

    private Grafo grafo;
    private MapPanel mapPanel;
    private JPanel centerContainer;
    private String currentView = "inicio";
    private Usuario usuarioActual;
    private GestorRecursosLocal gestor;
    private GestorEvacuaciones gestorEvacuaciones;

    // Componentes especializados
    private HeaderComponent headerComponent;
    private NavigationComponent navigationComponent;

    // Paneles de vistas
    private PanelInicio panelInicio;
    private PanelAdministracion panelAdministracion;
    private PanelRutas panelRutas;
    private PanelEstadisticas panelEstadisticas;
    private PanelDistribucion panelDistribucion;
    private PanelMapa panelMapa;

    // CONSTRUCTOR

    public VentanaPrincipal(Grafo grafo, Usuario usuario) {
        super("DesaRecu - Sistema de Gestión de Desastres");
        this.grafo = grafo;
        this.usuarioActual = usuario;
        this.gestor = GestorRecursosLocal.getInstance();
        this.gestorEvacuaciones = new GestorEvacuaciones();

        sincronizarGrafoConRutas();
        initComponentes();
        initUI();
    }

    // INICIALIZACIÓN DE COMPONENTES

    private void initComponentes() {
        // Crear componentes reutilizables
        headerComponent = new HeaderComponent(usuarioActual, this);
        navigationComponent = new NavigationComponent(this::navegarA);

        // Crear paneles de vistas
        panelInicio = new PanelInicio(gestor, gestorEvacuaciones);
        panelAdministracion = new PanelAdministracion(gestor, usuarioActual, this::sincronizarGrafoConRutas);
        panelRutas = new PanelRutas(gestor, gestorEvacuaciones, usuarioActual, this::sincronizarGrafoConRutas);
        panelEstadisticas = new PanelEstadisticas(gestor, gestorEvacuaciones);
        panelDistribucion = new PanelDistribucion(gestor);
        panelMapa = new PanelMapa(grafo);

        // Guardar referencia al mapPanel para sincronización
        this.mapPanel = panelMapa.getMapPanel();
    }

    // SINCRONIZACIÓN DEL GRAFO

    private void sincronizarGrafoConRutas() {
        System.out.println("Sincronizando grafo con rutas...");

        grafo = new Grafo();

        for (Zona zona : gestor.obtenerZonas()) {
            if (grafo.getNodo(zona.getNombre()) == null) {
                grafo.agregarNodo(new Nodo(zona.getNombre()));
            }
        }

        for (Ruta ruta : gestor.obtenerRutas()) {
            if (grafo.getNodo(ruta.getOrigenZona()) == null) {
                grafo.agregarNodo(new Nodo(ruta.getOrigenZona()));
            }
            if (grafo.getNodo(ruta.getDestinoZona()) == null) {
                grafo.agregarNodo(new Nodo(ruta.getDestinoZona()));
            }

            grafo.agregarArista(ruta.getOrigenZona(), ruta.getDestinoZona(), ruta.getPeso());
        }

        if (mapPanel != null) {
            mapPanel.actualizarGrafo(grafo);
        }

        System.out.println("Grafo sincronizado: " + grafo.getNodos().size() + " nodos, " +
                grafo.getAristas().size() + " aristas");
    }

    // INICIALIZACIÓN DE LA UI

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(EstilosUI.COLOR_BACKGROUND);

        // Agregar header
        add(headerComponent.crear(navigationComponent), BorderLayout.NORTH);

        // Contenedor central
        centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(EstilosUI.COLOR_BACKGROUND);
        centerContainer.setBorder(new EmptyBorder(20, 40, 20, 40));
        add(centerContainer, BorderLayout.CENTER);

        // Mostrar vista inicial
        navegarA("inicio");

        setVisible(true);
    }

    // NAVEGACIÓN ENTRE VISTAS
    private void navegarA(String seccion) {
        currentView = seccion;
        centerContainer.removeAll();

        switch (seccion) {
            case "inicio":
                centerContainer.add(panelInicio.crear());
                break;
            case "admin":
                centerContainer.add(panelAdministracion.crear());
                break;
            case "rutas":
                centerContainer.add(panelRutas.crear());
                break;
            case "estadisticas":
                centerContainer.add(panelEstadisticas.crear());
                break;
            case "distribucion":
                centerContainer.add(panelDistribucion.crear());
                break;
            case "mapa":
                centerContainer.add(panelMapa.crear());
                break;
        }

        navigationComponent.actualizarEstado(currentView);
        centerContainer.revalidate();
        centerContainer.repaint();
    }

    // MÉTODO PÚBLICO PARA CERRAR SESIÓN

    public void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginView();
            dispose();
        }
    }

    // GETTERS NECESARIOS

    public String getCurrentView() {
        return currentView;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}