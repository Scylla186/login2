package com.example.login2;
// ══════════════════════════════════════════════════════════════════════
// PRUEBA 4 – Lineamiento: secuencia de un solo elemento (Sommerville §8.1.2)
// Lineamiento: "Probar software con secuencias que tengan sólo un valor único"
// ══════════════════════════════════════════════════════════════════════

import org.junit.Test;
import static org.junit.Assert.*;

public class ProductDataSecuenciaUnElementoTest {

    @Test
    public void testProductoConSoloUnAlergeno() {
        // Lineamiento Sommerville: probar con secuencia de UN solo valor
        // El campo alergenos normalmente se espera como lista; probamos el caso mínimo
        ProductData data = ProductData.getInstance();
        data.limpiar();

        data.setProducto(
                "OH-1111-2222-3333-4444",
                "Leche",
                "Producto lácteo",
                "Calcio, Vitamina D",
                false,
                "Lactosa"   // ← secuencia de UN solo alérgeno (valor único)
        );

        // El sistema no debe fallar ni omitir el dato al ser sólo un elemento
        assertEquals("Con un solo alérgeno, debe almacenarse sin modificación",
                "Lactosa", data.getAlergenos());
        assertTrue("Debe marcar tieneProducto incluso con un solo alérgeno",
                data.tieneProducto());
    }

    @Test
    public void testProductoSinAlergenos() {
        // Caso extremo del lineamiento: secuencia vacía (cero elementos)
        ProductData data = ProductData.getInstance();
        data.limpiar();

        data.setProducto(
                "OH-AAAA-BBBB-CCCC-DDDD",
                "Agua Mineral",
                "Sin aditivos",
                "Natural",
                true,
                ""   // ← secuencia vacía
        );

        assertEquals("Alérgenos vacíos deben almacenarse como cadena vacía, no null",
                "", data.getAlergenos());
        assertTrue(data.tieneProducto());
    }
}