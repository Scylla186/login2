package com.example.login2;

// ══════════════════════════════════════════════════════════════════════
// PRUEBA 1 – Partición de equivalencia (Sommerville §8.1.2)
// Partición VÁLIDA del patrón QR: formato correcto
// ══════════════════════════════════════════════════════════════════════

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.regex.Pattern;

public class QrPatternParticionValidaTest {

    // Se extrae el mismo patrón que usa ScanFragment internamente
    private static final Pattern QR_PATTERN =
            Pattern.compile("^OH-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}$");

    @Test
    public void testCodigoQrDentroDeParticionValidaEsAceptado() {
        // Punto medio de la partición válida: código bien formado
        String codigoValido = "OH-A1B2-C3D4-E5F6-G7H8";
        assertTrue(
                "Un código con formato OH-XXXX-XXXX-XXXX-XXXX debe ser aceptado",
                QR_PATTERN.matcher(codigoValido).matches()
        );
    }

    @Test
    public void testCodigoQrConLetrasMinusculasEnParticionValidaEsAceptado() {
        // El patrón acepta [A-Za-z0-9], las minúsculas son parte de la misma partición válida
        String codigoConMinusculas = "OH-abcd-ef01-gh23-ij45";
        assertTrue(
                "Letras minúsculas también pertenecen a la partición válida del patrón",
                QR_PATTERN.matcher(codigoConMinusculas).matches()
        );
    }

    @Test
    public void testCodigoQrFueraDeParticionInvalidaEsRechazado() {
        // Partición INVÁLIDA: sin prefijo OH
        String codigoSinPrefijo = "XX-A1B2-C3D4-E5F6-G7H8";
        assertFalse(
                "Un código sin prefijo OH debe pertenecer a la partición inválida y rechazarse",
                QR_PATTERN.matcher(codigoSinPrefijo).matches()
        );
    }
}
