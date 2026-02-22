# API de Pedidos - Documentación para Android Studio

## Configuración Base

### Base URL
```
http://10.0.2.2:8080/api
```
> Nota: `10.0.2.2` es la IP del localhost desde el emulador de Android

### Dependencias (build.gradle)
```gradle
dependencies {
    // Retrofit para consumir API REST
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // OkHttp para interceptores
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // SQLite (ya incluido en Android)
    
    // Permisos de cámara y GPS
    implementation 'androidx.camera:camera-camera2:1.3.0'
    implementation 'androidx.camera:camera-lifecycle:1.3.0'
    implementation 'androidx.camera:camera-view:1.3.0'
    
    // Lector de QR
    implementation 'com.google.mlkit:barcode-scanning:17.2.0'
    
    // Google Play Services para GPS
    implementation 'com.google.android.gms:play-services-location:21.0.1'
}
```

### Permisos (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## Endpoints de la API

### 1. Autenticación

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "cliente1",
  "password": "password"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "cliente1",
  "role": "CLIENTE"
}
```

#### Registro
```http
POST /auth/register
Content-Type: application/json

{
  "username": "nuevo_usuario",
  "password": "123456",
  "email": "usuario@example.com",
  "role": "CLIENTE"
}
```

**Roles válidos:** `CLIENTE`, `VENDEDOR`

---

### 2. Productos

#### Listar Productos (público)
```http
GET /productos
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Laptop HP",
    "precio": 899.99,
    "stock": 15,
    "descripcion": "Laptop HP 15.6 pulgadas"
  }
]
```

#### Crear Producto (solo VENDEDOR)
```http
POST /productos
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Mouse Logitech",
  "precio": 25.50,
  "stock": 50,
  "descripcion": "Mouse inalámbrico"
}
```

---

### 3. Pedidos

#### Crear Pedido (CLIENTE)
```http
POST /orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "direccion": "Av. Central y Loja, Casa #123",
  "detallePedido": "10 cajas de producto A, 5 unidades de producto B",
  "tipoPago": "efectivo",
  "fotoBase64": "/9j/4AAQSkZJRgABAQEAYABgAAD...",
  "latitud": -0.1807,
  "longitud": -78.4678,
  "productos": [
    {
      "productoId": 1,
      "cantidad": 10
    },
    {
      "productoId": 2,
      "cantidad": 5
    }
  ]
}
```

**Campos obligatorios:**
- `direccion`: Dirección de entrega
  - `detallePedido`: Descripción del pedido
  - `tipoPago`: `"efectivo"` o `"transferencia"`
  - `latitud`: GPS desde donde se hace el pedido (-90 a 90)
  - `longitud`: GPS desde donde se hace el pedido (-180 a 180)
  //Nota, para la latitud longitud es necesario usar el gps del dispositivo.
  **Campos opcionales:**
  - `fotoBase64`: Foto en base64 (puede ser null)
  - `productos`: Lista de productos (opcional)

**Respuesta:**
```json
{
  "id": 1,
  "nombreCliente": "cliente1",
  "direccion": "Av. Central y Loja, Casa #123",
  "detallePedido": "10 cajas de producto A",
  "tipoPago": "efectivo",
  "fotoUrl": "/fotos/abc123.jpg",
  "latitud": -0.1807,
  "longitud": -78.4678,
  "fecha": "2024-02-20T22:30:00",
  "estado": "PENDIENTE",
  "total": 8999.90
}
```

#### Listar Pedidos
```http
GET /orders
Authorization: Bearer {token}
```

- **CLIENTE**: Ve solo sus pedidos
  - **VENDEDOR**: Ve todos los pedidos

#### Ver Detalle de Pedido
```http
GET /orders/{id}
Authorization: Bearer {token}
```

#### Actualizar Estado (solo VENDEDOR)
```http
PATCH /orders/{id}/estado
Authorization: Bearer {token}
Content-Type: application/json

{
  "estado": "PAGADO"
}
```

**Estados válidos:** `PENDIENTE`, `PAGADO`, `ENVIADO`, `ENTREGADO`

---

## Estructura SQLite Local

### Tabla: pedidos_local
```sql
CREATE TABLE pedidos_local (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    direccion TEXT NOT NULL,
    detalle_pedido TEXT NOT NULL,
    tipo_pago TEXT NOT NULL,
    foto_path TEXT,
    latitud REAL NOT NULL,
    longitud REAL NOT NULL,
    fecha TEXT NOT NULL,
    estado TEXT NOT NULL,
    sincronizado INTEGER DEFAULT 0,
    error_sync TEXT
);
```

**Estados de sincronización:**
- `sincronizado = 0`: Pendiente de sincronización
  - `sincronizado = 1`: Sincronizado correctamente
  - `sincronizado = -1`: Error al sincronizar

### Tabla: productos_pedido
```sql
CREATE TABLE productos_pedido (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pedido_local_id INTEGER,
    producto_id INTEGER,
    cantidad INTEGER,
    FOREIGN KEY (pedido_local_id) REFERENCES pedidos_local(id)
);
```

---

## Flujo de la Aplicación

### 1. Login
1. Usuario ingresa credenciales
   2. Llamar a `POST /auth/login`
   3. Guardar token en `SharedPreferences`
   4. Guardar role en `SharedPreferences`
   5. Redirigir según role:
      - CLIENTE → Pantalla de crear pedido
      - VENDEDOR → Pantalla de gestión de pedidos

### 2. Crear Pedido (CLIENTE)

#### Pantalla: Nuevo Pedido
**Campos del formulario:**
- EditText: Dirección (autocompletable con QR)
  - EditText: Detalle del pedido
  - RadioButton: Tipo de pago (efectivo/transferencia)
  - Button: Tomar foto
  - Button: Leer QR del cliente
  - Button: Seleccionar productos
  - Button: Guardar pedido

**Flujo:**
1. Usuario llena el formulario
   2. **Botón "Leer QR":**
      - Abrir cámara
      - Escanear QR con formato: `CLIENTE=Juan Perez|TEL=0999999999|DIR=Av. Central y Loja`
      - Parsear el texto del QR
      - Autocompletar campo "Dirección"
   3. **Botón "Tomar foto":**
      - Abrir cámara
      - Capturar foto
      - Convertir a base64
   4. **GPS automático:**
      - Obtener latitud y longitud actual
   5. **Guardar:**
      - Insertar en SQLite local con `sincronizado = 0`
      - Mostrar mensaje "Pedido guardado localmente"

### 3. Sincronización

#### Pantalla: Lista de Pedidos
- RecyclerView con pedidos locales
  - Mostrar estado: Pendiente / Sincronizado / Error
  - Botón "Sincronizar"

**Flujo de sincronización:**
1. Usuario presiona "Sincronizar"
   2. Obtener pedidos con `sincronizado = 0`
   3. Para cada pedido:
      - Llamar a `POST /orders` con el token
      - Si respuesta OK:
        - Actualizar `sincronizado = 1`
      - Si error:
        - Actualizar `sincronizado = -1`
        - Guardar mensaje de error en `error_sync`
   4. Mostrar resultado al usuario

### 4. Gestión de Pedidos (VENDEDOR)

#### Pantalla: Lista de Todos los Pedidos
1. Llamar a `GET /orders`
   2. Mostrar en RecyclerView
   3. Al hacer clic en un pedido:
      - Ver detalle
      - Botón "Cambiar Estado"
      - Spinner con estados: PENDIENTE, PAGADO, ENVIADO, ENTREGADO
      - Llamar a `PATCH /orders/{id}/estado`

---

## Ejemplo de Código Retrofit

### ApiService.java
```java
public interface ApiService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);
    
    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
    
    @GET("productos")
    Call<List<Producto>> getProductos();
    
    @POST("orders")
    Call<PedidoResponse> crearPedido(
        @Header("Authorization") String token,
        @Body PedidoRequest request
    );
    
    @GET("orders")
    Call<List<PedidoResponse>> getPedidos(
        @Header("Authorization") String token
    );
    
    @PATCH("orders/{id}/estado")
    Call<PedidoResponse> actualizarEstado(
        @Header("Authorization") String token,
        @Path("id") Long id,
        @Body EstadoRequest request
    );
}
```

### RetrofitClient.java
```java
public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private static Retrofit retrofit;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }
}
```

---

## Formato del QR

El QR debe contener texto en este formato:
```
CLIENTE=Juan Perez|TEL=0999999999|DIR=Av. Central y Loja
```

**Parseo en Android:**
```java
String qrText = "CLIENTE=Juan Perez|TEL=0999999999|DIR=Av. Central y Loja";
String[] parts = qrText.split("\\|");

String nombre = parts[0].split("=")[1];  // "Juan Perez"
String telefono = parts[1].split("=")[1]; // "0999999999"
String direccion = parts[2].split("=")[1]; // "Av. Central y Loja"

// Autocompletar campo dirección
editTextDireccion.setText(direccion);
```

---

## Conversión de Imagen a Base64

```java
// Convertir Bitmap a Base64
public String bitmapToBase64(Bitmap bitmap) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
    byte[] byteArray = byteArrayOutputStream.toByteArray();
    return Base64.encodeToString(byteArray, Base64.NO_WRAP);
}
```

---

## Obtener GPS

```java
// Obtener ubicación actual
FusedLocationProviderClient fusedLocationClient = 
    LocationServices.getFusedLocationProviderClient(this);

fusedLocationClient.getLastLocation()
    .addOnSuccessListener(this, location -> {
        if (location != null) {
            double latitud = location.getLatitude();
            double longitud = location.getLongitude();
        }
    });
```

---

## Manejo de Errores

### Errores de validación (400)
```json
{
  "direccion": "La dirección es obligatoria",
  "tipoPago": "El tipo de pago debe ser efectivo o transferencia"
}
```

### Errores generales
```json
{
  "error": "Usuario no encontrado"
}
```

### Sin conexión
- Guardar en SQLite local
  - Mostrar mensaje: "Sin conexión. El pedido se sincronizará cuando haya internet"

---

## Credenciales de Prueba

### Cliente
- Username: `cliente1`
  - Password: `password`
  - Role: `CLIENTE`

### Vendedor
- Username: `vendedor1`
  - Password: `password`
  - Role: `VENDEDOR`

---

## Checklist de Funcionalidades

### Obligatorias
- [ ] Login con JWT
  - [ ] Registro de usuarios
  - [ ] Formulario de pedido con todos los campos
  - [ ] Captura de foto con cámara
  - [ ] Obtención de GPS automático
  - [ ] Lector de QR para autocompletar dirección
  - [ ] Almacenamiento en SQLite local
  - [ ] Lista de pedidos con estados
  - [ ] Sincronización manual con API
  - [ ] Funciona sin conexión (offline)
  - [ ] Vendedor puede cambiar estados

### Opcionales
- [ ] Registro de ruta recorrida
  - [ ] Tiempo de recorrido
  - [ ] Notificaciones de sincronización
  - [ ] Caché de productos

---

## Notas Importantes

1. **Token JWT**: Incluir en header `Authorization: Bearer {token}` en todas las peticiones protegidas
   2. **Logout**: Solo eliminar token de SharedPreferences (no hay endpoint de logout)
   3. **Foto**: Es opcional, puede ser null
   4. **Productos**: Es opcional en el pedido
   5. **GPS**: Latitud y longitud son obligatorios
   6. **Dirección**: Es obligatoria (puede venir del QR o ingresarse manualmente)
   7. **Estados**: Solo VENDEDOR puede cambiar estados de pedidos
