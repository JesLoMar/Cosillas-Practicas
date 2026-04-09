# IDs Automáticos (Cosillas Prácticas)
## Saluditos
Buenas, os dejo esto que llevo toda la tarde haciendo.
(He estado tan en ello que no me he dado cuenta de que llevo desde las 2:30 y son ya las 8:30 —y lo que me queda porque voy a seguir xD).

Update: Lo que empezó como un generador de Administradores se me fue de las manos y ahora es un sistema completo y blindado para todo el proyecto (Profesores, Alumnos, Centros, Clases, Encuestas e Informes).

## ¿Qué es esto?
Es una clase (CrearId.java) que he montado para generar todas las IDs de la base de datos de forma automática, única y segura.

En lugar de usar el típico ID autoincremental 00001, 00002 (que nos obligaría a hacer llamadas a la BD constantemente para ver por qué número vamos), he creado IDs "inteligentes" y semánticos.

Tienen esta pinta:

Personas: ADM-OGA-31C3E (Prefijo - Iniciales - Hash del DNI)

Centros: CEN-CER-D478D (CEN - 3 Letras del nombre - Hash de su ubicación)

Entidades Relacionales: CLA-CER-A1B2 (Clase - Hereda las letras del Centro al que pertenece - Hash propio).

## ¿Cómo se usa? (Para copiar y pegar)
La clase es totalmente estática, así que no hay que instanciar nada. Solo llamáis al método que necesitéis y le pasáis los Strings. Él se encarga de limpiar nulos, quitar acentos, borrar partículas ("de", "la", "el") y hasta comerse siglas educativas ("IES", "CEIP") para que el ID quede limpio.

// 1. Personas (Admins, Profesores, Alumnos)

String idAdmin = CrearId.administrador("Óliver", "García", "12345678Z"); 

String idProfe = CrearId.profesor("Ana", "De la Fuente", "87654321X");

String idAlumno = CrearId.alumno("Paco", "Pérez", "NIA12345");

// 2. Centros (Le pasas la info y él hace el hash combinado)

String idCentro = CrearId.centro("I.E.S. San José", "Andalucía", "Sevilla", "Dos Hermanas");

// 3. Entidades relacionales (¡Piden el ID del padre para heredar sus siglas!)

String idClase = CrearId.clase("1º DAW", idCentro);

String idEncuesta = CrearId.encuesta("Evaluación 1", 1, "2025-2026", idProfe);

String idInforme = CrearId.informe(idEncuesta, idClase, idCentro, idProfe);

## ¿Por qué lo he hecho así?
Como aviso digo que con esto quería evitar todas las llamadas posibles a base de datos para no saturar. Para generar el identificador final único, lo que he hecho ha sido usar los primeros caracteres de un Hash SHA-256 (del DNI para personas, o de la combinación de datos para el resto).

Pero... ¿pueden repetirse?
Malo será que coincidan exactamente las 3 letras de las iniciales y además colisionen los caracteres del hash. Aún así, si el sistema crece mucho, se podría reforzar cambiando dinámicamente el valor de la variable longitudHash (ahora mismo está en 4) dentro de la función obtenerSufijoHash.

## ¿Cómo lo has hecho?

Soy consciente de que podría haberlo hecho todo en 1 hora en vez de echarle 6 (el primer día, han sido 8 en total), pero me he empeñado en hacerlo sin usar la IA al principio para pensar un rato. He echado un rato agradable la verdad; no quizá muy productivo, pero obligarse a pensar es bueno.
Para lo que sí he usado la IA (Gemini) es, al haber terminado la lógica propia y funcional:

- Para una optimización del código y aplicar Clean Code manteniendo mi lógica.

- Para refrescar qué hacían algunos métodos (como los .split("\\s+")).

- Para encontrar casos extremos donde el código podría "explotar" y blindarlo contra nulos.

- Para ayudarme a ayudaros comentando el código y un resúmen para este readme.
