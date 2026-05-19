package com.example.login2;
// ══════════════════════════════════════════════════════════════════════
// PRUEBA 5 – Objeto mock para aislar dependencia (Sommerville §8.1.1)
// "Los objetos mock simulan su funcionalidad [...] puede entrar rápidamente
//  a ellos sin las sobrecargas de llamar a una base de datos"
// ══════════════════════════════════════════════════════════════════════

import org.junit.Test;
import static org.junit.Assert.*;

public class ProductDataMockDependenciaTest {

    /**
     * Mock que simula lo que haría ScanFragment tras una respuesta exitosa
     * de Firestore: poblar ProductData sin necesidad de red ni Firebase.
     * Sommerville §8.1.1: "objetos mock con la misma interfaz como los usados
     * por objetos externos que simulan su funcionalidad".
     */
    private void simularRespuestaFirestoreExitosa(String codigoQr,
                                                  String nombre,
                                                  String descripcion,
                                                  String detalles,
                                                  boolean esVegano,
                                                  String alergenos) {
        // En producción, esta lógica la dispara el callback de Firestore.
        // Aquí la invocamos directamente para aislar ProductData de la red.
        ProductData.getInstance().setProducto(
                codigoQr, nombre, descripcion, detalles, esVegano, alergenos
        );
    }

    @Test
    public void testProductDataSePopulaCorrectamenteTrasRespuestaMock() {
        ProductData data = ProductData.getInstance();
        data.limpiar();

        // Simulamos la respuesta que Firestore daría para un producto vegano
        simularRespuestaExitosaFirestore(
                "OH-MOCK-TEST-1234-5678",
                "Avena Orgánica",
                "Cereal integral sin gluten",
                "Alto en fibra, sin azúcar añadida",
                true,
                "Ninguno"
        );

        // Verificamos que ProductData refleja los datos del mock
        assertTrue("El mock debe marcar el producto como disponible",
                data.tieneProducto());
        assertEquals("El nombre debe ser el que retornó el mock",
                "Avena Orgánica", data.getNombre());
        assertTrue("El flag vegano debe reflejar el valor del mock",
                data.isEsVegano());
        assertEquals("El QR ID debe coincidir con el código escaneado",
                "OH-MOCK-TEST-1234-5678", data.getQrId());
    }

    // Alias para claridad del método (corrige el typo del método de arriba)
    private void simularRespuestaExitosaFirestore(String qr, String nombre,
                                                  String desc, String detalles, boolean veg, String aler) {
        ProductData.getInstance().setProducto(qr, nombre, desc, detalles, veg, aler);
    }
}
