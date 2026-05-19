package com.example.login2;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ValidacionCamposRegistro {


    //Verifica que la lógica de validación del registro rechaza correctamente cada caso de error en el orden correcto
    //Como campos vacios, incorrectos o incompletos
    //"Clean Code" || Robert C. Martin || Capítulo 9 ||  Pruebas de validación



    @Test
    public void testValidacionCamposRegistro() {
        // Nombre vacío
        assertFalse(camposValidos("", "test@gmail.com", "123456", "123456"));

        // Correo vacío
        assertFalse(camposValidos("Juan Pérez", "", "123456", "123456"));

        // Contraseña corta
        assertFalse(camposValidos("Juan Pérez", "test@gmail.com", "123", "123"));

        // Contraseñas no coinciden
        assertFalse(camposValidos("Juan Pérez", "test@gmail.com", "123456", "654321"));

        // Todo valido
        assertTrue(camposValidos("Juan Pérez", "test@gmail.com", "123456", "123456"));
    }


    private boolean camposValidos(String nombre, String correo,
                                  String password, String confirmar) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (correo == null || correo.trim().isEmpty()) return false;
        if (password == null || password.length() < 6) return false;
        if (!password.equals(confirmar)) return false;
        return true;
    }
}