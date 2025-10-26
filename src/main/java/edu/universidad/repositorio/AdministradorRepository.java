/**
 * En esta clase se manejaran los usuarios y las constraseñas del sistema
 * vamos a validar el inicio de sesion con el patrón singleton
 */
package edu.universidad.repositorio;

import java.util.HashMap;
import java.util.Map;

public class AdministradorRepository {
    private static AdministradorRepository instance;
    private Map<String, String> credenciales = new HashMap<>();

    private AdministradorRepository() {
        credenciales.put("admin", "admin");
    }

    public static synchronized AdministradorRepository getInstance() {
        if (instance == null) instance = new AdministradorRepository();
        return instance;
    }

    public boolean autenticar(String usuario, String contrasenia) {
        return contrasenia!=null && contrasenia.equals(credenciales.get(usuario));
    }
}
