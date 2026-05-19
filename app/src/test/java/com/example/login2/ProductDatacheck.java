package com.example.login2;

import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ProductDatacheck {

    // Verifica que el patrón Singleton (Osea garantizar que una clase solo tenga
    // una unica instancia) funciona correctamente
    // que ScanFragment y ProductInfoFragment comparten
    // exactamente la misma instancia de datos. Si esto fallara
    // el fragmento de información mostraría datos vacíos aunque
    // el scanner haya detectado un producto válido.

    //"Effective Unit Testing" || Lasse Koskela || Capítulo 4 || pruebas de patrones de diseño.

    @Test
    public void testSingletonProductData() {
        // Verificar que siempre es la misma instancia
        ProductData instancia1 = ProductData.getInstance();
        ProductData instancia2 = ProductData.getInstance();
        assertSame(instancia1, instancia2);

        // Verificar setProducto guarda correctamente
        instancia1.setProducto(
                "OH-Ab12-Cd34-Ef56-Gh78",
                "Papas Margarita Pollo 30g",
                "Snack de papa",
                "160 kcal, no apto veganos",
                false,
                "gluten, lacteos"
        );

        assertEquals("Papas Margarita Pollo 30g", instancia2.getNombre());
        assertEquals("OH-Ab12-Cd34-Ef56-Gh78", instancia2.getQrId());
        assertTrue(instancia2.tieneProducto());
        assertFalse(instancia2.isEsVegano());

        // Verificar limpiar() resetea todo
        instancia1.limpiar();
        assertEquals("", instancia2.getNombre());
        assertFalse(instancia2.tieneProducto());
    }
}