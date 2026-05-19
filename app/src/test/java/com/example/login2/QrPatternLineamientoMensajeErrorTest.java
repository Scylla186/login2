package com.example.login2;
// ══════════════════════════════════════════════════════════════════════
// PRUEBA 2 – Lineamientos de prueba: forzar mensaje de error (Sommerville §8.1.2)
// Lineamiento: "Elegir entradas que fuercen al sistema a generar
//               todos los mensajes de error"
// ══════════════════════════════════════════════════════════════════════

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.regex.Pattern;

public class QrPatternLineamientoMensajeErrorTest {

    private static final Pattern QR_PATTERN =
            Pattern.compile("^OH-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}$");

    @Test
    public void testCadenaVaciaFuerzaRechazoDeMensajeError() {
        // Lineamiento: entrada que obliga al sistema a reportar error
        assertFalse("Una cadena vacía debe ser rechazada por el validador",
                QR_PATTERN.matcher("").matches());
    }

    @Test
    public void testCodigoConEspaciosInternosFuerzaMensajeError() {
        // Espacio en medio: entrada diseñada para provocar rechazo explícito
        String codigoConEspacio = "OH-A1 B-C3D4-E5F6-G7H8";
        assertFalse("Un código con espacios internos debe ser rechazado",
                QR_PATTERN.matcher(codigoConEspacio).matches());
    }

    @Test
    public void testCodigoConCaracteresEspecialesFuerzaMensajeError() {
        // Caracteres fuera del alfabeto [A-Za-z0-9] diseñados para forzar error
        String codigoConGuionBajo = "OH-A1B_-C3D4-E5F6-G7H8";
        assertFalse("Un código con guion bajo debe ser rechazado (fuera del charset permitido)",
                QR_PATTERN.matcher(codigoConGuionBajo).matches());
    }
}