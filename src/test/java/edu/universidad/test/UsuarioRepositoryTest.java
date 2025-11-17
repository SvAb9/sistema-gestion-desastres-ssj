package edu.universidad.test;

import edu.universidad.modelo.Usuario;
import edu.universidad.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Prueba 7: Verificar sistema de autenticación y permisos
 * Requisito: Control de acceso por roles
 */
public class UsuarioRepositoryTest {

    private UsuarioRepository repository;

    @BeforeEach
    public void setUp() {
        repository = UsuarioRepository.getInstance();
    }

    @Test
    @DisplayName("7.1 - Autenticación con credenciales correctas")
    public void testAutenticacionExitosa() {
        Usuario admin = repository.autenticar("admin@desarecu.com", "admin");

        assertNotNull(admin, "Debe autenticar correctamente");
        assertEquals("Administrador", admin.getNombre(), "Nombre correcto");
        assertEquals(Usuario.Rol.ADMINISTRADOR, admin.getRol(), "Rol correcto");
    }

    @Test
    @DisplayName("7.2 - Autenticación falla con credenciales incorrectas")
    public void testAutenticacionFallida() {
        Usuario usuario = repository.autenticar("admin@desarecu.com", "wrongpassword");

        assertNull(usuario, "No debe autenticar con contraseña incorrecta");
    }

    @Test
    @DisplayName("7.3 - Registrar nuevo usuario funciona correctamente")
    public void testRegistrarUsuario() {
        Usuario nuevo = new Usuario("Test User", "test@test.com", "test123", Usuario.Rol.OPERADOR);

        boolean registrado = repository.registrar(nuevo);

        assertTrue(registrado, "Debe registrar exitosamente");

        Usuario encontrado = repository.buscarPorEmail("test@test.com");
        assertNotNull(encontrado, "Debe encontrar el usuario registrado");
        assertEquals("Test User", encontrado.getNombre(), "Nombre correcto");
    }

    @Test
    @DisplayName("7.4 - Permisos por rol funcionan correctamente")
    public void testPermisosRoles() {
        Usuario admin = new Usuario("Admin", "admin@test.com", "pass", Usuario.Rol.ADMINISTRADOR);
        Usuario viewer = new Usuario("Viewer", "viewer@test.com", "pass", Usuario.Rol.VISUALIZADOR);

        assertTrue(admin.puedeEditar(), "Administrador debe poder editar");
        assertTrue(admin.puedeCoordinarOperaciones(), "Administrador debe poder coordinar");
        assertFalse(admin.soloLectura(), "Administrador no es solo lectura");

        assertFalse(viewer.puedeEditar(), "Visualizador no debe poder editar");
        assertFalse(viewer.puedeCoordinarOperaciones(), "Visualizador no debe poder coordinar");
        assertTrue(viewer.soloLectura(), "Visualizador es solo lectura");
    }
}