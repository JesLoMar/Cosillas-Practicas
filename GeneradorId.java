package com.zaitec.valoraClick.domain.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class GeneradorId {

    // =========================================================================
    // CONSTANTES
    // =========================================================================
    private static final String PREFIJO_ADMIN = "ADM-";
    private static final String PREFIJO_PROFESOR = "PRO-";
    private static final String PREFIJO_ALUMNO = "ALU-";
    private static final String PREFIJO_CENTRO = "CEN-";
    private static final String PREFIJO_CLASE = "CLA-";
    private static final String PREFIJO_ENCUESTA = "ENC-";
    private static final String PREFIJO_INFORME = "INF-";
    private static final String PREFIJO_PREGUNTA = "PRG-";
    private static final String PREFIJO_QR_ENCUESTA = "QRE-";
    private static final String PREFIJO_QR_REGISTRO = "QRR-";
    private static final String PREFIJO_RESPUESTA = "RSP-";

    private static final String VALOR_HASH_VACIO_CENTRO = "CEN-XXX-7423";
    private static final String VALOR_NULO_CENTRO = "CEN-XXX-NULL";

    private static final int LONGITUD_HASH = 4;

    private static final Pattern PATRON_ACENTOS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final String REGEX_PARTICULAS = "(?i)\\b(de|del|la|las|el|los|y)\\b";
    private static final String SIGLAS_EDUCATIVAS = "ies|ceip|cepa|cifp|cpc|cp|colegio|instituto|universidad|uned|upv|uam|ucm";
    private static final String REGEX_SIGLAS = "(?i)\\b(" + SIGLAS_EDUCATIVAS + ")\\b";

    // Contadores para las pruebas
    private static int total = 0;
    private static int passed = 0;

    // =========================================================================
    // MÉTODO MAIN DE PRUEBAS
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   INICIANDO PRUEBAS DE GeneradorId");
        System.out.println("==========================================\n");

        // ---------------------------------------------------------------
        // PRUEBAS: Admin
        // ---------------------------------------------------------------
        System.out.println("--- PRUEBAS ADMIN ---");

        String idAdmin1 = generarIdAdministrador("María", "García López", "12345678Z");
        boolean test1 = idAdmin1.startsWith("ADM-");
        boolean test2 = idAdmin1.length() == 12;
        boolean test3 = idAdmin1.charAt(4) == 'M' || idAdmin1.charAt(4) == 'm';
        boolean test4 = idAdmin1.split("-").length == 3;
        printResult("Admin formato básico", test1 && test2 && test3 && test4);

        String idAdminNull = generarIdAdministrador(null, "García", "12345678Z");
        boolean test5 = idAdminNull.contains("X");
        printResult("Admin con nombre null", test5);

        String idAdminApellNull = generarIdAdministrador("Juan", null, "12345678Z");
        boolean test6 = idAdminApellNull.contains("XX");
        printResult("Admin con apellidos null", test6);

        String idAdmin2 = generarIdAdministrador("José", "de la Torre", "87654321A");
        boolean test7 = idAdmin2.startsWith("ADM-");
        printResult("Admin limpia partículas", test7);

        // ---------------------------------------------------------------
        // PRUEBAS: Profesor
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS PROFESOR ---");

        String idProfe1 = generarIdProfesor("Ana", "Martínez Ruiz", "11111111H");
        boolean test8 = idProfe1.startsWith("PRO-");
        boolean test9 = idProfe1.length() == 12;
        printResult("Profesor formato básico", test8 && test9);

        String idProfe2 = generarIdProfesor("Ana", "Martínez Ruiz", "11111111H");
        boolean test10 = idProfe1.equals(idProfe2);
        printResult("Profesor determinista (mismo DNI)", test10);

        String idProfe3 = generarIdProfesor("Ana", "Martínez Ruiz", "22222222J");
        boolean test11 = !idProfe1.equals(idProfe3);
        printResult("Profesor diferenciado por DNI", test11);

        // ---------------------------------------------------------------
        // PRUEBAS: Alumno
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS ALUMNO ---");

        String idAlumno1 = generarIdAlumno("CEN-ABC-1234", "codigoSecreto001");
        boolean test12 = idAlumno1.startsWith("ALU-");
        printResult("Alumno formato básico", test12);

        String idAlumno2 = generarIdAlumno("CEN-ABC-1234", "codigoSecreto001");
        boolean test13 = idAlumno1.equals(idAlumno2);
        printResult("Alumno determinista (mismo código)", test13);

        String idAlumno3 = generarIdAlumno("CEN-ABC-1234", "otroCodigo");
        boolean test14 = !idAlumno1.equals(idAlumno3);
        printResult("Alumno diferenciado por código", test14);

        String idAlumnoNull = generarIdAlumno(null, "codigo001");
        boolean test15 = idAlumnoNull.contains("XXX");
        printResult("Alumno con centroId null", test15);

        // ---------------------------------------------------------------
        // PRUEBAS: Centro
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS CENTRO ---");

        String idCentro1 = generarIdCentro("IES Francisco de Goya", "Madrid", "Madrid", "Centro");
        boolean test16 = idCentro1.startsWith("CEN-");
        boolean test17 = !idCentro1.contains("ies");
        boolean test18 = !idCentro1.contains("de");
        printResult("Centro limpia siglas y partículas", test16 && test17 && test18);

        String idCentroNull = generarIdCentro(null, "Cataluña", "Barcelona", "Gracia");
        boolean test19 = idCentroNull.equals(VALOR_NULO_CENTRO) || idCentroNull.contains("XXX");
        printResult("Centro con nombre null", test19);

        String idCentroTodoNull = generarIdCentro(null, null, null, null);
        boolean test20 = idCentroTodoNull.equals(VALOR_NULO_CENTRO);
        printResult("Centro todo null devuelve VALOR_NULO_CENTRO", test20);

        String idCentro2 = generarIdCentro("Colegio San Juan", "Andalucía", "Sevilla", "Triana");
        boolean test21 = idCentro2.startsWith("CEN-");
        printResult("Centro formato básico", test21);

        // ---------------------------------------------------------------
        // PRUEBAS: Clase
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS CLASE ---");

        String idClase1 = generarIdClase("Matemáticas", "Secundaria", "2º", "2024/2025", "A", "CEN-ABC-1234");
        boolean test22 = idClase1.startsWith("CLA-");
        printResult("Clase formato básico", test22);

        String idClase2 = generarIdClase("Matemáticas", "Secundaria", "2º", "2024/2025", "A", "CEN-ABC-1234");
        boolean test23 = idClase1.equals(idClase2);
        printResult("Clase determinista (mismos parámetros)", test23);

        String idClase3 = generarIdClase("Lengua", "Secundaria", "2º", "2024/2025", "A", "CEN-ABC-1234");
        boolean test24 = !idClase1.equals(idClase3);
        printResult("Clase diferenciada por nombre", test24);

        String idClaseNull = generarIdClase("Historia", "Bachillerato", "1º", "2024/2025", "B", null);
        boolean test25 = idClaseNull.contains("XXX");
        printResult("Clase con centroId null", test25);

        // ---------------------------------------------------------------
        // PRUEBAS: Encuesta
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS ENCUESTA ---");

        String idEncuesta1 = generarIdEncuesta("Clima Escolar", "2024/2025", 1, "PRO-AB-CD12");
        boolean test26 = idEncuesta1.startsWith("ENC-");
        printResult("Encuesta formato básico", test26);

        String idEncuesta2 = generarIdEncuesta("Clima Escolar", "2024/2025", 1, "PRO-AB-CD12");
        boolean test27 = idEncuesta1.equals(idEncuesta2);
        printResult("Encuesta determinista (mismos parámetros)", test27);

        String idEncuesta3 = generarIdEncuesta("Clima Escolar", "2024/2025", 2, "PRO-AB-CD12");
        boolean test28 = !idEncuesta1.equals(idEncuesta3);
        printResult("Encuesta diferenciada por trimestre", test28);

        // ---------------------------------------------------------------
        // PRUEBAS: Informe
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS INFORME ---");

        String idInforme1 = generarIdInforme("ENC-AB-CD12", "CEN-XYZ-5678", "PRO-WQ-9ABC");
        boolean test29 = idInforme1.startsWith("INF-");
        printResult("Informe formato básico", test29);

        String idInforme2 = generarIdInforme("ENC-AB-CD12", "CEN-XYZ-5678", "PRO-WQ-9ABC");
        boolean test30 = idInforme1.equals(idInforme2);
        printResult("Informe determinista", test30);

        // ---------------------------------------------------------------
        // PRUEBAS: Pregunta
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS PREGUNTA ---");

        String idPregunta1 = generarIdPregunta("ENC-AB-CD12", "¿Cómo te sientes hoy?", 1);
        boolean test31 = idPregunta1.startsWith("PRG-");
        printResult("Pregunta formato básico", test31);

        String idPregunta2 = generarIdPregunta("ENC-AB-CD12", "¿Cómo te sientes hoy?", 1);
        boolean test32 = idPregunta1.equals(idPregunta2);
        printResult("Pregunta determinista", test32);

        String idPregunta3 = generarIdPregunta("ENC-AB-CD12", "¿Cómo te sientes hoy?", 2);
        boolean test33 = !idPregunta1.equals(idPregunta3);
        printResult("Pregunta diferenciada por orden", test33);

// ---------------------------------------------------------------
// PRUEBAS: QR
// ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS QR ---");

// QR tipo ENCUESTA
        String idQrEncuesta = generarIdQr("ENC-AB-CD12", null);
        boolean test34 = idQrEncuesta.startsWith("QRE-");
        boolean test35 = !idQrEncuesta.contains("QRR-");
        printResult("QR tipo ENCUESTA", test34 && test35);

// QR tipo REGISTRO_CLASE
        String idQrRegistro = generarIdQr(null, "codigoClase123");
        boolean test36 = idQrRegistro.startsWith("QRR-");
        boolean test37 = !idQrRegistro.contains("QRE-");
        printResult("QR tipo REGISTRO_CLASE", test36 && test37);

// QR determinista
        String idQr1 = generarIdQr("ENC-AB-CD12", null);
        String idQr2 = generarIdQr("ENC-AB-CD12", null);
        boolean test38 = idQr1.equals(idQr2);
        printResult("QR determinista", test38);

// QR con ambos null (debería lanzar excepción)
        try {
            generarIdQr(null, null);
            System.out.println("  FAIL ✗ - QR con ambos null NO lanzó excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("  PASS ✓ - QR con ambos null lanza excepción correctamente");
        }

// ---------------------------------------------------------------
// PRUEBAS: Respuesta
// ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS RESPUESTA ---");

        String idRespuesta1 = generarIdRespuesta("ENC-AB-CD12", "PRG-CD12-A1B2", "ALU-XYZ-5678");
        boolean test51 = idRespuesta1.startsWith("RSP-");
        boolean test52 = !idRespuesta1.startsWith("ENC-"); // Ya no empieza por ENC-
        boolean test53 = idRespuesta1.split("-").length == 3; // RSP-CD12-XXXX
        printResult("Respuesta formato básico", test51 && test52 && test53);

        String idRespuesta2 = generarIdRespuesta("ENC-AB-CD12", "PRG-CD12-A1B2", "ALU-XYZ-5678");
        boolean test54 = idRespuesta1.equals(idRespuesta2);
        printResult("Respuesta determinista", test54);

        // Verificar que contiene siglas de la encuesta (completadas a 3 caracteres)
        boolean test55 = idRespuesta1.contains("ABX");
        printResult("Respuesta contiene siglas encuesta", test55);

        // Dudas: https://c.tenor.com/B0piVWUiKaUAAAAd/tenor.gif
        // ---------------------------------------------------------------
        // PRUEBAS: Métodos de hashing
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS HASHING ---");

        String hash1 = generarHash("texto de prueba");
        String hash2 = generarHash("texto de prueba");
        boolean test42 = hash1.equals(hash2);
        printResult("Hash determinista", test42);

        String hash3 = generarHash("otro texto");
        boolean test43 = !hash1.equals(hash3);
        printResult("Hash diferenciado", test43);

        boolean test44 = hash1 != null && !hash1.isEmpty() && hash1.length() == 64;
        printResult("Hash longitud correcta (SHA-256)", test44);

        // ---------------------------------------------------------------
        // PRUEBAS: Métodos de normalización
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS NORMALIZACIÓN ---");

        String normalizado1 = normalizar("Canción");
        boolean test45 = normalizado1.equals("Cancion");
        printResult("Normaliza acentos", test45);

        String normalizado2 = normalizar("¡Hola! ¿Qué tal?");
        boolean test46 = !normalizado2.contains("¡") && !normalizado2.contains("¿") && !normalizado2.contains("?");
        printResult("Normaliza caracteres especiales", test46);

        // ---------------------------------------------------------------
        // PRUEBAS: Métodos de limpieza
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS LIMPIEZA ---");

        String limpio1 = limpiarParticulas("María de la O");
        boolean test47 = !limpio1.contains(" de ") && !limpio1.contains(" la ");
        printResult("Limpia partículas", test47);

        String limpio2 = limpiarSiglasCentro("IES Colegio San Juan");
        boolean test48 = !limpio2.contains("IES");
        printResult("Limpia siglas centro", test48);

        // ---------------------------------------------------------------
        // PRUEBAS: Casos borde adicionales
        // ---------------------------------------------------------------
        System.out.println("\n--- PRUEBAS CASOS BORDE ---");

        try {
            generarIdAdministrador("", "", "");
            generarIdProfesor("", "", "");
            generarIdAlumno("", "");
            generarIdCentro("", "", "", "");
            generarIdClase("", "", "", "", "", "");
            generarIdEncuesta("", "", 0, "");
            generarIdInforme("", "", "");
            generarIdPregunta("", "", 0);
            generarIdRespuesta("", "", "");
            System.out.println("  PASS ✓ - Todos los generadores soportan strings vacíos");
        } catch (Exception e) {
            System.out.println("  FAIL ✗ - Algún generador falló con strings vacíos: " + e.getMessage());
        }

        String[] ids = {
                generarIdAdministrador("Test", "User", "00000000T"),
                generarIdProfesor("Test", "User", "00000000T"),
                generarIdAlumno("CEN-ABC-1234", "alumno@ejemplo.com"),
                generarIdCentro("Test", "Test", "Test", "Test"),
                generarIdClase("Test", "Test", "Test", "Test", "A", "CEN-ABC-1234"),
                generarIdEncuesta("Test", "2024", 1, "PRO-AB-CD12"),
                generarIdInforme("ENC-AB-CD12", "CEN-XYZ-5678", "PRO-WQ-9ABC"),
                generarIdPregunta("ENC-AB-CD12", "Test", 1),
                generarIdQr("ENC-AB-CD12", null),
                generarIdRespuesta("ENC-AB-CD12", "PRG-CD12-A1B2", "ALU-XYZ-5678")
        };

        boolean noSpaces = true;
        for (String id : ids) {
            if (id.contains(" ")) {
                noSpaces = false;
                break;
            }
        }
        printResult("Ningún ID contiene espacios", noSpaces);

        System.out.println("\n==========================================");
        System.out.println("   PRUEBAS COMPLETADAS");
        System.out.println("   Total: " + total + " | Pasadas: " + passed + " | Fallidas: " + (total - passed));
        // ---------------------------------------------------------------
// MUESTRA DE IDs GENERADOS
// ---------------------------------------------------------------
        System.out.println("\n==========================================");
        System.out.println("   MUESTRA DE IDs GENERADOS");
        System.out.println("==========================================\n");

        String centroPrueba = generarIdCentro("IES Francisco de Goya", "Madrid", "Madrid", "Centro");
        String adminPrueba = generarIdAdministrador("María", "García López", "12345678Z");
        String profePrueba = generarIdProfesor("Ana", "Martínez Ruiz", "11111111H");
        String alumnoPrueba = generarIdAlumno(centroPrueba, "alumno1@ejemplo.com");
        String clasePrueba = generarIdClase("Matemáticas", "Secundaria", "2º", "2024/2025", "A", centroPrueba);
        String encuestaPrueba = generarIdEncuesta("Clima Escolar", "2024/2025", 1, profePrueba);
        String preguntaPrueba = generarIdPregunta(encuestaPrueba, "¿Cómo te sientes hoy?", 1);
        String informePrueba = generarIdInforme(encuestaPrueba, centroPrueba, profePrueba);
        String respuestaPrueba = generarIdRespuesta(encuestaPrueba, preguntaPrueba, alumnoPrueba);
        String qrEncuestaPrueba = generarIdQr(encuestaPrueba, null);
        String qrRegistroPrueba = generarIdQr(null, "codigoClase123");

        System.out.println("  Admin:     " + adminPrueba);
        System.out.println("  Profesor:  " + profePrueba);
        System.out.println("  Alumno:    " + alumnoPrueba);
        System.out.println("  Centro:    " + centroPrueba);
        System.out.println("  Clase:     " + clasePrueba);
        System.out.println("  Encuesta:  " + encuestaPrueba);
        System.out.println("  Pregunta:  " + preguntaPrueba);
        System.out.println("  Informe:   " + informePrueba);
        System.out.println("  Respuesta: " + respuestaPrueba);
        System.out.println("  QR (Enc):  " + qrEncuestaPrueba);
        System.out.println("  QR (Reg):  " + qrRegistroPrueba);

// Mostrar partes internas de un ID como ejemplo
        System.out.println("\n  --- Desglose de Respuesta ---");
        System.out.println("  ID completo:  " + respuestaPrueba);
        String[] partesRespuesta = respuestaPrueba.split("-");
        System.out.println("  Prefijo:      " + partesRespuesta[0]);
        System.out.println("  Siglas Enc:   " + partesRespuesta[1]);
        System.out.println("  Hash:         " + partesRespuesta[2]);

        System.out.println("\n==========================================");
        System.out.println("==========================================");
    }

    private static void printResult(String testName, boolean passedTest) {
        total++;
        if (passedTest) {
            passed++;
            System.out.println("  PASS ✓ - " + testName);
        } else {
            System.out.println("  FAIL ✗ - " + testName);
        }
    }

    // =========================================================================
    // BLOQUE 1: API PÚBLICA (GENERADORES DE IDENTIFICADORES)
    // =========================================================================
    public static String generarIdAdministrador(String nombre, String apellidos, String dni) {
        String inicialNombre = obtenerInicialNombre(nombre);
        String apellidosNormalizados = normalizar(apellidos).trim().toUpperCase();
        String inicialesApellidos = obtenerInicialesApellidos(apellidosNormalizados);
        String sufijoHash = obtenerSufijoHash(generarHash(dni));
        return PREFIJO_ADMIN + inicialNombre + inicialesApellidos + "-" + sufijoHash;
    }

    public static String generarIdProfesor(String nombre, String apellidos, String dni) {
        String inicialNombre = obtenerInicialNombre(nombre);
        String apellidosNormalizados = normalizar(apellidos).trim().toUpperCase();
        String inicialesApellidos = obtenerInicialesApellidos(apellidosNormalizados);
        String sufijoHash = obtenerSufijoHash(generarHash(dni));
        return PREFIJO_PROFESOR + inicialNombre + inicialesApellidos + "-" + sufijoHash;
    }

    public static String generarIdAlumno(String centroId, String codigoAcceso) {
        String centroNombre = obtenerMitadId(centroId);
        String sufijoHash = obtenerSufijoHash(generarHash(codigoAcceso));
        return PREFIJO_ALUMNO + centroNombre + "-" + sufijoHash;
    }

    public static String generarIdCentro(String nombre, String comunidad, String ciudad, String localidad) {
        if (nombre == null || nombre.isBlank()) {
            return VALOR_NULO_CENTRO;
        }
        String nomLimpio = nombre;
        String comLimpia = comunidad == null ? "" : comunidad;
        String ciuLimpia = ciudad == null ? "" : ciudad;
        String locLimpia = localidad == null ? "" : localidad;

        String centroInfo = nomLimpio + comLimpia + ciuLimpia + locLimpia;
        String hashCentro = obtenerSufijoHash(generarHash(centroInfo));
        String nombreCentro = obtenerNombreCentro(nomLimpio);

        String idCentro = PREFIJO_CENTRO + nombreCentro + "-" + hashCentro;

        if (idCentro.equals(VALOR_HASH_VACIO_CENTRO)) {
            return VALOR_NULO_CENTRO;
        }
        return idCentro;
    }

    public static String generarIdClase(String nombre, String nivel, String grado, String curso, String letra, String centroId) {
        String infoHash = normalizar(nombre) + normalizar(nivel) + normalizar(grado) + normalizar(curso) + normalizar(letra);
        String claseId = obtenerSufijoHash(generarHash(infoHash));
        return PREFIJO_CLASE + obtenerMitadId(centroId) + "-" + claseId;
    }

    public static String generarIdEncuesta(String nombre, String cursoAcademico, Integer trimestre, String profesorId) {
        String infoHash = obtenerSufijoHash(generarHash(normalizar(nombre) + normalizar(cursoAcademico) + trimestre));
        return PREFIJO_ENCUESTA + obtenerMitadId(profesorId) + "-" + infoHash;
    }

    public static String generarIdInforme(String encuestaId, String centroId, String profeId) {
        String infoHash = obtenerSufijoHash(generarHash(obtenerMitadId(centroId) + obtenerMitadId(profeId)));
        return PREFIJO_INFORME + obtenerMitadId(encuestaId) + "-" + infoHash;
    }

    public static String generarIdPregunta(String encuestaId, String textoPregunta, Integer orden) {
        String infoHash = obtenerSufijoHash(generarHash(normalizar(textoPregunta) + orden));
        return PREFIJO_PREGUNTA + obtenerMitadId(encuestaId) + "-" + infoHash;
    }

    public static String generarIdQr(String encuestaId, String codigoAcceso) {
        if (encuestaId != null && !encuestaId.isBlank()) {
            // Es un QR de tipo ENCUESTA
            return PREFIJO_QR_ENCUESTA + obtenerMitadId(encuestaId) + "-" + obtenerSufijoHash(generarHash(encuestaId));
        } else if (codigoAcceso != null && !codigoAcceso.isBlank()) {
            // Es un QR de tipo REGISTRO_CLASE
            return PREFIJO_QR_REGISTRO + "REG-" + obtenerSufijoHash(generarHash(codigoAcceso));
        } else {
            throw new IllegalArgumentException("Para generar un ID de QR se necesita 'encuestaId' o 'codigoAcceso'.");
        }
    }

    public static String generarIdRespuesta(String encuestaId, String preguntaId, String alumnoId) {
        String infoHash = obtenerSufijoHash(generarHash(obtenerMitadId(preguntaId) + obtenerMitadId(alumnoId)));
        return PREFIJO_RESPUESTA + obtenerMitadId(encuestaId) + "-" + infoHash;
    }

    // =========================================================================
    // BLOQUE 2: LÓGICA DE EXTRACCIÓN Y FORMATEO (PRIVADOS)
    // =========================================================================
    private static String obtenerInicialNombre(String nombre) {
        if (nombre == null) return "X";
        String nombreLimpio = normalizar(nombre).trim().toUpperCase();
        if (nombreLimpio.isEmpty()) return "X";
        return nombreLimpio.substring(0, 1);
    }

    private static String obtenerInicialesApellidos(String apellidos) {
        if (apellidos == null || apellidos.isBlank()) return "XX";
        String apellidosProcesados = limpiarParticulas(apellidos);
        if (apellidosProcesados.isEmpty()) apellidosProcesados = apellidos;
        String[] partes = apellidosProcesados.split("\\s+");
        if (partes.length >= 2) {
            return partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1);
        } else {
            String soloUnApellido = partes[0];
            return soloUnApellido.length() >= 2 ? soloUnApellido.substring(0, 2) : soloUnApellido.substring(0, 1) + "X";
        }
    }

    private static String obtenerNombreCentro(String nombre) {
        if (nombre == null || nombre.isBlank()) return "XXX";
        String procesado = normalizar(nombre).toUpperCase();
        procesado = limpiarSiglasCentro(procesado);
        procesado = limpiarParticulas(procesado);
        String[] partes = procesado.split("\\s+");
        if (partes.length >= 3) {
            return partes[0].substring(0, 1) + partes[1].substring(0, 1) + partes[2].substring(0, 1);
        } else if (partes.length == 2) {
            return partes[0].substring(0, 2) + partes[1].substring(0, 1);
        } else if (partes.length == 1 && !partes[0].isEmpty()) {
            String sola = partes[0];
            return sola.length() >= 3 ? sola.substring(0, 3) : (sola + "XXX").substring(0, 3);
        }
        return "XXX";
    }

    private static String obtenerSufijoHash(String hash) {
        if (hash == null || hash.length() < LONGITUD_HASH) return "XXXXX";
        return hash.substring(0, LONGITUD_HASH);
    }

    private static String obtenerMitadId(String idCompleta) {
        if (idCompleta == null || idCompleta.isBlank()) return "XXX";
        String[] mitadIdArray = idCompleta.split("-");
        if (mitadIdArray.length >= 2) {
            String mitad = mitadIdArray[1];
            while (mitad.length() < 3) {
                mitad += "X";
            }
            return mitad.substring(0, 3);
        }
        return "XXX"; // Que no XD
    }

    // =========================================================================
    // BLOQUE 3: MOTOR CRIPTOGRÁFICO Y LIMPIEZA DE TEXTO
    // =========================================================================
    public static String generarHash(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashCodificado = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder cadenaHexadecimal = new StringBuilder();
            for (byte b : hashCodificado) {
                String hex = String.format("%02x", b);
                cadenaHexadecimal.append(hex);
            }
            return cadenaHexadecimal.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al inicializar el algoritmo de hash", e);
        }
    }

    private static String normalizar(String texto) {
        if (texto == null) return "";
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        String textoSinAcentos = PATRON_ACENTOS.matcher(textoNormalizado).replaceAll("");
        return textoSinAcentos.replaceAll("[^a-zA-Z\\s]", "");
    }

    private static String limpiarParticulas(String texto) {
        if (texto == null || texto.isBlank()) return texto;
        String resultado = texto.replaceAll(REGEX_PARTICULAS, "");
        return resultado.replaceAll("\\s+", " ").trim();
    }

    private static String limpiarSiglasCentro(String texto) {
        if (texto == null || texto.isBlank()) return texto;
        String resultado = texto.replaceAll(REGEX_SIGLAS, "");
        return resultado.replaceAll("\\s+", " ").trim();
    }
}
