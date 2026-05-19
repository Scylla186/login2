package com.example.login2;
// ══════════════════════════════════════════════════════════════════════
// PRUEBA 3 – Transición de estado (Sommerville §8.1.1)
// Probar la secuencia: Activo → Cerrado → Activo
// ══════════════════════════════════════════════════════════════════════

import org.junit.Test;
import static org.junit.Assert.*;

public class SesionUsuarioTransicionEstadoTest {

    @Test
    public void testSecuenciaIniciarCerrarIniciarTransicionaCorrectamente() {
        SesionUsuario sesion = SesionUsuario.getInstance();

        // Estado inicial: sin sesión
        sesion.cerrarSesion();
        assertFalse("Estado inicial debe ser inactivo", sesion.isActivo());

        // Transición 1: inactivo → activo
        sesion.iniciarSesion("uid-001", "Ana Torres", "ana@mail.com");
        assertTrue("Tras iniciarSesion el estado debe ser activo", sesion.isActivo());
        assertEquals("El uid debe corresponder al usuario que inició sesión",
                "uid-001", sesion.getUid());

        // Transición 2: activo → inactivo
        sesion.cerrarSesion();
        assertFalse("Tras cerrarSesion el estado debe volver a inactivo", sesion.isActivo());
        assertEquals("El uid debe quedar vacío tras cerrar sesión",
                "", sesion.getUid());

        // Transición 3: inactivo → activo con nuevo usuario
        sesion.iniciarSesion("uid-002", "Luis Mora", "luis@mail.com");
        assertTrue("Debe poder volver a iniciar sesión con otro usuario", sesion.isActivo());
        assertEquals("El uid debe ser el del nuevo usuario",
                "uid-002", sesion.getUid());
    }
}