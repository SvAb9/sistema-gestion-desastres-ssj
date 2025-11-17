package edu.universidad.repositorio;

import edu.universidad.modelo.Usuario;
import edu.universidad.util.PersistenciaJSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioRepository {

    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private static UsuarioRepository instance;
    private Map<String, Usuario> usuarios = new HashMap<>();

    private UsuarioRepository() {
        cargarUsuarios();
        // Si no hay usuarios, crear los de prueba
        if (usuarios.isEmpty()) {
            crearUsuariosPorDefecto();
        }
    }

    public static synchronized UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
        }
        return instance;
    }

    /**
     * Carga usuarios desde archivo JSON
     */
    private void cargarUsuarios() {
        System.out.println(" Cargando usuarios desde " + ARCHIVO_USUARIOS);
        List<Usuario> lista = PersistenciaJSON.cargarLista(ARCHIVO_USUARIOS, Usuario.class);

        for (Usuario u : lista) {
            usuarios.put(u.getEmail(), u);
        }

        System.out.println( usuarios.size() + " usuarios cargados");
    }
    /**
     * Guarda usuarios en archivo JSON
     */
    private void guardarUsuarios() {
        List<Usuario> lista = new ArrayList<>(usuarios.values());
        PersistenciaJSON.guardar(ARCHIVO_USUARIOS, lista);
        System.out.println(" Usuarios guardados: " + lista.size());
    }
    /**
     * Crea usuarios por defecto si no existen
     */
    private void crearUsuariosPorDefecto() {
        Usuario admin = new Usuario("Administrador", "admin@desarecu.com", "admin", Usuario.Rol.ADMINISTRADOR);
        Usuario coord = new Usuario("Coordinador General", "coord@desarecu.com", "coord123", Usuario.Rol.COORDINADOR);
        Usuario oper = new Usuario("Operador de Campo", "operador@desarecu.com", "oper123", Usuario.Rol.OPERADOR);
        Usuario viewer = new Usuario("Observador", "viewer@desarecu.com", "view123", Usuario.Rol.VISUALIZADOR);

        usuarios.put(admin.getEmail(), admin);
        usuarios.put(coord.getEmail(), coord);
        usuarios.put(oper.getEmail(), oper);
        usuarios.put(viewer.getEmail(), viewer);

        guardarUsuarios();
        System.out.println("Usuarios por defecto creados");
    }

    public Usuario autenticar(String email, String password) {
        Usuario usuario = usuarios.get(email);
        if (usuario != null && usuario.getPassword().equals(password) && usuario.isActivo()) {
            return usuario;
        }
        return null;
    }

    public boolean registrar(Usuario usuario) {
        if (usuarios.containsKey(usuario.getEmail())) {
            return false; // Ya existe
        }
        usuarios.put(usuario.getEmail(), usuario);
        guardarUsuarios(); // AGREGADO: Persistir cambios
        return true;
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios.values());
    }

    public List<Usuario> listarActivos() {
        return usuarios.values().stream()
                .filter(Usuario::isActivo)
                .collect(Collectors.toList());
    }

    public Usuario buscarPorEmail(String email) {
        return usuarios.get(email);
    }

    public boolean actualizar(Usuario usuario) {
        if (usuarios.containsKey(usuario.getEmail())) {
            usuarios.put(usuario.getEmail(), usuario);
            guardarUsuarios(); // AGREGADO: Persistir cambios
            return true;
        }
        return false;
    }

    public boolean eliminar(String email) {
        boolean eliminado = usuarios.remove(email) != null;
        if (eliminado) {
            guardarUsuarios(); //  AGREGADO: Persistir cambios
        }
        return eliminado;
    }

    public boolean desactivar(String email) {
        Usuario usuario = usuarios.get(email);
        if (usuario != null) {
            usuario.setActivo(false);
            guardarUsuarios(); //  AGREGADO: Persistir cambios
            return true;
        }
        return false;
    }
}
