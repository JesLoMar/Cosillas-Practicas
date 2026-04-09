package org.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class CrearId {

    /* =========================================================================
     * BLOQUE 1: API PÚBLICA (GENERADORES DE IDENTIFICADORES)
     * =========================================================================
     */

    // ==========================================================
    // Genera el ID para un Administrador (Formato: ADM-III-HHHH)
    // ==========================================================
    public static String administrador(String nombre, String apellidos, String hashDni) {
        String inicialNombre = obtenerInicialNombre(nombre);
        String apellidosNormalizados = normalizar(apellidos).trim().toUpperCase();
        String inicialesApellidos = obtenerInicialesApellidos(apellidosNormalizados);
        String sufijoHash = obtenerSufijoHash(hashDni);

        return "ADM-" + inicialNombre + inicialesApellidos + "-" + sufijoHash;
    }

    // ==========================================================
    // Genera el ID para un Profesor (Formato: PRO-III-HHHH)
    // ==========================================================
    public static String profesor(String nombre, String apellidos, String hashDni) {
        String inicialNombre = obtenerInicialNombre(nombre);
        String apellidosNormalizados = normalizar(apellidos).trim().toUpperCase();
        String inicialesApellidos = obtenerInicialesApellidos(apellidosNormalizados);
        String sufijoHash = obtenerSufijoHash(hashDni);

        return "PRO-" + inicialNombre + inicialesApellidos + "-" + sufijoHash;
    }

    // ==========================================================
    // Genera el ID para un Alumno (Formato: ALU-III-HHHH)
    // ==========================================================
    public static String alumno(String nombre, String apellidos, String hashNia) {
        String inicialNombre = obtenerInicialNombre(nombre);
        String apellidosNormalizados = normalizar(apellidos).trim().toUpperCase();
        String inicialesApellidos = obtenerInicialesApellidos(apellidosNormalizados);
        String sufijoHash = obtenerSufijoHash(hashNia);

        return "ALU-" + inicialNombre + inicialesApellidos + "-" + sufijoHash;
    }

    // ==========================================================
    // Genera el ID para un Centro (Formato: CEN-III-HHHH)
    // ==========================================================
    public static String centro(String nombre, String comunidad, String ciudad, String localidad) {
        // saneamiento de nulos a vacíos para concatenación segura
        String nomLimpio = nombre == null ? "" : nombre;
        String comLimpia = comunidad == null ? "" : comunidad;
        String ciuLimpia = ciudad == null ? "" : ciudad;
        String locLimpia = localidad == null ? "" : localidad;

        String centroInfo = nomLimpio + comLimpia + ciuLimpia + locLimpia;
        String hashCentro = obtenerSufijoHash(obtenerHash(centroInfo));
        String nombreCentro = obtenerNombreCentro(nomLimpio);

        String idCentro = "CEN-" + nombreCentro + "-" + hashCentro;

        // Control: Si fue nulo/vacio, E3B0 es el inicio del hash de "" en SHA-256
        if (idCentro.equals("CEN-XXX-E3B0")) {
            return "CEN-XXX-NULL";
        }

        return idCentro;
    }

    // ==========================================================
    // Genera el ID relacional para una Clase (Hereda del Centro)
    // ==========================================================
    public static String clase(String nombre, String centroId) {
        String claseId = obtenerSufijoHash(obtenerHash(normalizar(nombre)));
        return "CLA-" + obtenerMitadId(centroId) + "-" + claseId;
    }

    // ==========================================================
    // Genera el ID relacional para una Encuesta (Hereda del Profesor)
    // ==========================================================
    public static String encuesta(String nombre, int trimestre, String curso, String profesorId) {
        String infoHash = obtenerSufijoHash(obtenerHash(normalizar(nombre) + normalizar(curso) + trimestre));
        return "ENC-" + obtenerMitadId(profesorId) + "-" + infoHash;
    }

    // ==========================================================
    // Genera el ID relacional para un Informe (Hereda de la Encuesta)
    // ==========================================================
    public static String informe(String encuestaId, String claseId, String centroId, String profeId) {
        String infoHash = obtenerSufijoHash(obtenerHash(encuestaId + claseId + centroId + profeId));
        return "INF-" + obtenerMitadId(encuestaId) + "-" + infoHash;
    }


    /* =========================================================================
     * BLOQUE 2: LÓGICA DE EXTRACCIÓN Y FORMATEO (PRIVADOS)
     * =========================================================================
     */

    // ==========================================================
    // Extrae la primera letra del nombre tras normalizarlo.
    // ==========================================================
    private static String obtenerInicialNombre(String nombre) {
        if (nombre == null) return "X";
        String nombreLimpio = normalizar(nombre).trim().toUpperCase();
        if (nombreLimpio.isEmpty()) return "X";
        return nombreLimpio.substring(0, 1);
    }

    // ==========================================================
    // Procesa apellidos para obtener dos iniciales representativas.
    // ==========================================================
    private static String obtenerInicialesApellidos(String apellidos) {
        if (apellidos == null || apellidos.isBlank()) return "XX";

        String apellidosProcesados = limpiarParticulas(apellidos);
        if (apellidosProcesados.isEmpty()) {
            apellidosProcesados = apellidos; // Fallback si el borrado fue total
        }

        String[] partes = apellidosProcesados.split("\\s+");

        if (partes.length >= 2) {
            return partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1);
        } else {
            String soloUnApellido = partes[0];
            return soloUnApellido.length() >= 2
                    ? soloUnApellido.substring(0, 2)
                    : soloUnApellido.substring(0, 1) + "X";
        }
    }

    // ==========================================================
    // Extrae 3 letras representativas del nombre del centro educativo.
    // ==========================================================
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

    // ==========================================================
    // Trunca un hash completo para generar el sufijo del identificador.
    // ==========================================================
    private static String obtenerSufijoHash(String hash) {
        int longitudHash = 4;
        if (hash == null || hash.length() < longitudHash) {
            return "XXXXX";
        }
        return hash.substring(0, longitudHash);
    }

    // ==========================================================
    // Extrae la parte central de un ID padre para mantener la herencia.
    // ==========================================================
    private static String obtenerMitadId(String idCompleta) {
        if (idCompleta == null || idCompleta.isBlank()) {
            return "XXX";
        }
        String[] mitadIdArray = idCompleta.split("-");
        if (mitadIdArray.length >= 2) {
            return mitadIdArray[1];
        }
        return "XXX";
    }


    /* =========================================================================
     * BLOQUE 3: MOTOR CRIPTOGRÁFICO Y LIMPIEZA DE TEXTO
     * =========================================================================
     */

    // ==========================================================
    // Genera una huella digital SHA-256 en formato hexadecimal.
    // ==========================================================
    public static String obtenerHash(String texto) {
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

    // ==========================================================
    // Elimina acentos, símbolos y números, dejando solo letras y espacios.
    // ==========================================================
    private static String normalizar(String texto) {
        if (texto == null) return "";

        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        Pattern patronAcentos = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String textoSinAcentos = patronAcentos.matcher(textoNormalizado).replaceAll("");

        return textoSinAcentos.replaceAll("[^a-zA-Z\\s]", "");
    }

    // ==========================================================
    // Elimina conectores y artículos comunes en apellidos hispanos.
    // ==========================================================
    private static String limpiarParticulas(String texto) {
        if (texto == null || texto.isBlank()) return texto;

        String regexParticulas = "(?i)\\b(de|del|la|las|el|los|y)\\b";
        String resultado = texto.replaceAll(regexParticulas, "");

        return resultado.replaceAll("\\s+", " ").trim();
    }

    // ==========================================================
    // Elimina conectores y siglas educativas comunes (IES, CEIP, etc.).
    // ==========================================================
    private static String limpiarSiglasCentro(String texto) {
        if (texto == null || texto.isBlank()) return texto;

        String siglasEducativas = "ies|ceip|cepa|cifp|cpc|cp|colegio|instituto|universidad|uned|upv|uam|ucm";
        String regexCompleta = "(?i)\\b(" + siglasEducativas + ")\\b";
        String resultado = texto.replaceAll(regexCompleta, "");

        return resultado.replaceAll("\\s+", " ").trim();
    }
}
