# Generador de IDs Automáticos - ValoraClick

## 📌 ¿Qué es esto?

Es una clase `GeneradorId` que genera **todos los IDs de la base de datos de forma automática, única y semántica**.

En lugar de usar el típico ID autoincremental `00001`, `00002` (que obliga a consultar la BD constantemente), se crean IDs **inteligentes** con prefijos identificativos, iniciales legibles y un hash criptográfico que garantiza unicidad.

### Formato de los IDs:

| Entidad | Formato | Ejemplo |
|---------|---------|---------|
| **Admin** | `ADM-` + Inicial Nombre + Iniciales Apellidos + `-` + Hash(DNI) | `ADM-MGL-A1B2` |
| **Profesor** | `PRO-` + Inicial Nombre + Iniciales Apellidos + `-` + Hash(DNI) | `PRO-AMR-C3D4` |
| **Alumno** | `ALU-` + Siglas Centro + `-` + Hash(CódigoAcceso) | `ALU-ABC-E5F6` |
| **Centro** | `CEN-` + 3 Letras Nombre + `-` + Hash(Ubicación) | `CEN-FGO-7G8H` |
| **Clase** | `CLA-` + Siglas Centro + `-` + Hash(Clase) | `CLA-ABC-I9J0` |
| **Encuesta** | `ENC-` + Siglas Profesor + `-` + Hash(Encuesta) | `ENC-CD12-K1L2` |
| **Informe** | `INF-` + Siglas Encuesta + `-` + Hash(Centro+Profe) | `INF-CD12-M3N4` |
| **Pregunta** | `PRG-` + Siglas Encuesta + `-` + Hash(Texto+Orden) | `PRG-CD12-O5P6` |
| **QR** | `QR-` + Siglas Encuesta/REG + `-` + Hash | `QR-CD12-Q7R8` / `QR-REG-S9T0` |
| **Respuesta** | EncuestaId + `-RSP-` + Hash(Alumno) | `ENC-AB-CD12-RSP-U1V2` |

---

## 🚀 ¿Cómo se usa?

La clase es **totalmente estática**, no hay que instanciar nada. Solo llamas al método y le pasas los datos:

```java
// 1. PERSONAS
String idAdmin  = GeneradorId.generarIdAdministrador("Óliver", "García López", "12345678Z");
String idProfe  = GeneradorId.generarIdProfesor("Ana", "Martínez Ruiz", "87654321X");
String idAlumno = GeneradorId.generarIdAlumno(idCentro, "codigoAccesoSecreto");

// 2. CENTROS
String idCentro = GeneradorId.generarIdCentro("IES Francisco de Goya", "Madrid", "Madrid", "Centro");

// 3. ENTIDADES DEPENDIENTES (heredan siglas del padre)
String idClase     = GeneradorId.generarIdClase("Matemáticas", "Secundaria", "2º", "2024/2025", "A", idCentro);
String idEncuesta  = GeneradorId.generarIdEncuesta("Clima Escolar", "2024/2025", 1, idProfe);
String idInforme   = GeneradorId.generarIdInforme(idEncuesta, idCentro, idProfe);
String idPregunta  = GeneradorId.generarIdPregunta(idEncuesta, "¿Cómo te sientes?", 1);
String idRespuesta = GeneradorId.generarIdRespuesta(idEncuesta, idAlumno);

// 4. QR (puede ser de tipo ENCUESTA o REGISTRO_CLASE)
String idQrEncuesta = GeneradorId.generarIdQr(idEncuesta, null);       // Tipo ENCUESTA
String idQrRegistro = GeneradorId.generarIdQr(null, "codigoClase123"); // Tipo REGISTRO_CLASE
```
## 🛡️ ¿Cómo funciona internamente?

### 1. Limpieza del texto

Antes de generar cualquier ID, el sistema **normaliza** el texto:

- Elimina acentos (`Canción` → `Cancion`)
- Elimina caracteres especiales (`¡Hola!` → `Hola`)
- Elimina partículas (`de`, `la`, `del`, `el`, `los`, `y`)
- Elimina siglas educativas (`IES`, `CEIP`, `CEPA`, `Colegio`, `Instituto`, `Universidad`, etc.)

### 2. Generación del hash

Se usa **SHA-256** sobre los campos más estables de cada entidad:

| Entidad | Campos usados para el hash |
|---------|---------------------------|
| Admin | DNI |
| Profesor | DNI |
| Alumno | Código de acceso |
| Centro | Nombre + Comunidad + Ciudad + Localidad |
| Clase | Nombre + Nivel + Grado + Curso + Letra |
| Encuesta | Nombre + Curso académico + Trimestre |
| Informe | Centro + Profesor |
| Pregunta | Texto + Orden |
| QR | EncuestaId o CódigoAcceso |
| Respuesta | AlumnoId |

Del hash SHA-256 (64 caracteres hexadecimales) se toman solo los **4 primeros caracteres** para mantener los IDs compactos.

### 3. Manejo de casos nulos

Si un campo obligatorio es `null` o vacío, el sistema usa valores por defecto:

| Valor por defecto | Significado |
|-------------------|-------------|
| `X` | Inicial de nombre no disponible |
| `XX` | Iniciales de apellidos no disponibles |
| `XXX` | Siglas de entidad no disponibles |
| `XXXXX` | Sufijo hash no disponible |

---

## 🔒 ¿Pueden repetirse los IDs?

La probabilidad es **extremadamente baja**. Para que dos IDs colisionen tendrían que coincidir:

1. Las iniciales (para personas) o siglas (para entidades)
2. Los **mismos 4 caracteres hexadecimales** del hash SHA-256 (1 entre 65.536)

Si el sistema crece mucho, se puede aumentar la variable `LONGITUD_HASH` (actualmente en `4`) para reducir aún más la probabilidad de colisión.

---

## ✅ Pruebas incluidas

La clase incluye un **método `main` con 40 pruebas automáticas** que verifican:

- ✅ Formato correcto de cada tipo de ID
- ✅ Determinismo (mismos inputs → mismo ID)
- ✅ Diferenciación (distintos inputs → distinto ID)
- ✅ Manejo de `null` y strings vacíos
- ✅ Normalización de acentos y caracteres especiales
- ✅ Limpieza de partículas y siglas educativas
- ✅ IDs sin espacios en blanco
- ✅ Longitud correcta del hash SHA-256
