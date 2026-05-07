package com.example.login2;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.regex.Pattern;

public class regexqrCheck {

    @Test
    public void testValidacionFormatoQR() {
        Pattern QR_PATTERN = Pattern.compile(
                "^OH-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}$"
        );

        // Casos válidos
        assertTrue(QR_PATTERN.matcher("OH-Ab12-Cd34-Ef56-Gh78").matches());
        assertTrue(QR_PATTERN.matcher("OH-AAAA-BBBB-CCCC-DDDD").matches());
        assertTrue(QR_PATTERN.matcher("OH-1234-5678-9012-3456").matches());

        // Casos inválidos
        assertFalse(QR_PATTERN.matcher("QR-Ab12-Cd34-Ef56-Gh78").matches());
        assertFalse(QR_PATTERN.matcher("OH-Ab12-Cd34-Ef56").matches());
        assertFalse(QR_PATTERN.matcher("OH-Ab12-Cd34-Ef56-Gh7").matches());
        assertFalse(QR_PATTERN.matcher("").matches());
        assertFalse(QR_PATTERN.matcher("OH-Ab12-Cd34-Ef56-Gh78-Extra").matches());
    }
}