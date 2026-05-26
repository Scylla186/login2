cat > /mnt/user-data/outputs/README.md << 'EOF'
# OpticHelp QR Scanner 

Aplicación Android para escanear códigos QR de productos y consultar su información nutricional en tiempo real, con autenticación de usuarios mediante Firebase.
Con un enfoque en personas invidentes.

---

## Funcionalidades

- **Autenticación** — Registro e inicio de sesión con Firebase Auth
- **Escáner QR** — Lectura de códigos con CameraX + ML Kit
- **Consulta de productos** — Información nutricional, alérgenos y etiqueta vegana desde Firestore
- **Sesión persistente** — Redirección automática si el usuario ya tiene sesión activa
- **Multiidioma** — Soporte para español e inglés según la configuración del dispositivo
- **Accesibilidad** — Anuncios para lectores de pantalla en pantallas clave

---

## Arquitectura

```
com.example.login2
├── Login.java               # Pantalla de inicio de sesión
├── Register.java            # Pantalla de registro
├── ScanActivity.java        # Contenedor del ViewPager (escáner + info)
├── ScanFragment.java        # Lógica de cámara y validación QR
├── ProductInfoFragment.java # Muestra la información del producto escaneado
├── ProductData.java         # Singleton — datos del producto activo
├── SesionUsuario.java       # Singleton — datos de la sesión activa
└── IdiomaUtils.java         # Gestión de idioma (es / en)
```

### Flujo principal

```
Login → [autenticación Firebase] → ScanActivity
                                        ├── ScanFragment  →  valida QR
                                        │                →  consulta Firestore
                                        │                →  llena ProductData
                                        └── ProductInfoFragment  →  lee ProductData → muestra info
```

---

## Tecnologías

| Componente | Librería / Servicio |
|---|---|
| Autenticación | Firebase Auth |
| Base de datos | Firebase Firestore |
| Cámara | CameraX |
| Lectura QR | ML Kit Barcode Scanning |
| UI | ViewPager2, Material Components |
| Lenguaje | Java |
| Min SDK | API 21 (Android 5.0) |

---

## Configuración

### Prerrequisitos

- Android Studio Hedgehog o superior
- JDK 11
- Cuenta en [Firebase Console](https://console.firebase.google.com)

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/login2.git
   cd login2
   ```

2. **Conectar Firebase**
   - Crear un proyecto en Firebase Console
   - Agregar una app Android con el package `com.example.login2`
   - Descargar `google-services.json` y colocarlo en `/app/`

3. **Habilitar servicios en Firebase**
   - Authentication → Email/Password
   - Firestore Database

4. **Estructura de Firestore requerida**

   ```
   usuarios/{uid}
     nombre, usuario, correo, fecha_registro, activo

   codigos_qr/{docId}
     codigo_qr, activo

   productos/{docId}
     id_qr, nombre, descripcion, detalles, es_vegano, alergenos

   escaneos/{docId}
     uid_usuario, codigo_qr, fecha_escaneo
   ```

5. **Ejecutar** — Abrir en Android Studio y correr en emulador o dispositivo físico.

---

## Pruebas unitarias

Las pruebas se ubican en `src/test/` y se ejecutan con:

```bash
./gradlew test
```

| Clase de prueba | Qué verifica |
|---|---|
| `regexqrCheck` | Formato de la expresión regular QR |
| `QrPatternParticionValidaTest` | Partición de equivalencia del patrón QR |
| `QrPatternLineamientoMensajeErrorTest` | Entradas que fuerzan mensajes de error |
| `ProductDatacheck` | Patrón Singleton de `ProductData` |
| `ProductDataSecuenciaUnElementoTest` | Secuencia de un solo alérgeno / lista vacía |
| `ProductDataMockDependenciaTest` | Aislamiento de Firestore con objeto mock |
| `SesionUsuarioTransicionEstadoTest` | Transiciones de estado de `SesionUsuario` |
| `ValidacionCamposRegistro` | Validación de campos del formulario de registro |

---

## Formato de código QR

Los códigos escaneados deben seguir el patrón:

```
OH-XXXX-XXXX-XXXX-XXXX
```

Donde cada bloque `XXXX` contiene exactamente 4 caracteres alfanuméricos `[A-Za-z0-9]`.

Ejemplos válidos: `OH-Ab12-Cd34-Ef56-Gh78`, `OH-AAAA-BBBB-CCCC-DDDD`

---

## Licencia

Este proyecto fue desarrollado con fines académicos.
EOF


## Enlace de descarga del APK de ANDROID

https://drive.google.com/file/d/15rTMkZfbrtGrJfJDl_3Tx5drENCySnkh/view?usp=sharing 
