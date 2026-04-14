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

  private static final String VALOR_HASH_VACIO_CENTRO = "CEN-XXX-7423";
  private static final String VALOR_NULO_CENTRO = "CEN-XXX-NULL";

  private static final int LONGITUD_HASH = 4;

  private static final Pattern PATRON_ACENTOS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
  private static final String REGEX_PARTICULAS = "(?i)\\b(de|del|la|las|el|los|y)\\b";
  private static final String SIGLAS_EDUCATIVAS = "ies|ceip|cepa|cifp|cpc|cp|colegio|instituto|universidad|uned|upv|uam|ucm";
  private static final String REGEX_SIGLAS = "(?i)\\b(" + SIGLAS_EDUCATIVAS + ")\\b";

  /* =========================================================================
   * BLOQUE 1: API PÚBLICA (GENERADORES DE IDENTIFICADORES)
   * =========================================================================
   */

  public static String generarIdAdministrador(String nombre, String apellidos, String hashDni) {
    String inicialNombre = obtenerInicialNombre(nombre);
    String apellidosNormalizados = normalizar(apellidos).trim().toUpperCase();
    String inicialesApellidos = obtenerInicialesApellidos(apellidosNormalizados);
    String sufijoHash = obtenerSufijoHash(hashDni);

    return PREFIJO_ADMIN + inicialNombre + inicialesApellidos + "-" + sufijoHash;
  }

  public static String generarIdProfesor(String nombre, String apellidos, String hashDni) {
    String inicialNombre = obtenerInicialNombre(nombre);
    String apellidosNormalizados = normalizar(apellidos).trim().toUpperCase();
    String inicialesApellidos = obtenerInicialesApellidos(apellidosNormalizados);
    String sufijoHash = obtenerSufijoHash(hashDni);

    return PREFIJO_PROFESOR + inicialNombre + inicialesApellidos + "-" + sufijoHash;
  }

  public static String generarIdAlumno(String centroId, String hashNia) {
    String centroNombre = obtenerMitadId(centroId);
    String sufijoHash = obtenerSufijoHash(hashNia);

    return PREFIJO_ALUMNO + centroNombre + "-" + sufijoHash;
  }

  public static String generarIdCentro(String nombre, String comunidad, String ciudad, String localidad) {
    String nomLimpio = nombre == null ? "" : nombre;
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

  public static String generarIdClase(String nombre, String centroId) {
    String claseId = obtenerSufijoHash(generarHash(normalizar(nombre)));
    return PREFIJO_CLASE + obtenerMitadId(centroId) + "-" + claseId;
  }

  public static String generarIdEncuesta(String nombre, int trimestre, String curso, String profesorId) {
    String infoHash = obtenerSufijoHash(
            generarHash(normalizar(nombre) + normalizar(curso) + trimestre));
    return PREFIJO_ENCUESTA + obtenerMitadId(profesorId) + "-" + infoHash;
  }

  public static String generarIdInforme(String encuestaId, String centroId, String profeId) {
    String infoHash = obtenerSufijoHash(
            generarHash(obtenerMitadId(centroId) + obtenerMitadId(profeId)));
    return PREFIJO_INFORME + obtenerMitadId(encuestaId) + "-" + infoHash;
  }

  /* =========================================================================
   * BLOQUE 2: LÓGICA DE EXTRACCIÓN Y FORMATEO (PRIVADOS)
   * =========================================================================
   */

  private static String obtenerInicialNombre(String nombre) {
    if (nombre == null) {
      return "X";
    }
    String nombreLimpio = normalizar(nombre).trim().toUpperCase();
    if (nombreLimpio.isEmpty()) {
      return "X";
    }
    return nombreLimpio.substring(0, 1);
  }

  private static String obtenerInicialesApellidos(String apellidos) {
    if (apellidos == null || apellidos.isBlank()) {
      return "XX";
    }

    String apellidosProcesados = limpiarParticulas(apellidos);
    if (apellidosProcesados.isEmpty()) {
      apellidosProcesados = apellidos;
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

  private static String obtenerNombreCentro(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      return "XXX";
    }

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
    if (hash == null || hash.length() < LONGITUD_HASH) {
      return "XXXXX";
    }
    return hash.substring(0, LONGITUD_HASH);
  }

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
    if (texto == null) {
      return "";
    }

    String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
    String textoSinAcentos = PATRON_ACENTOS.matcher(textoNormalizado).replaceAll("");

    return textoSinAcentos.replaceAll("[^a-zA-Z\\s]", "");
  }

  private static String limpiarParticulas(String texto) {
    if (texto == null || texto.isBlank()) {
      return texto;
    }

    String resultado = texto.replaceAll(REGEX_PARTICULAS, "");
    return resultado.replaceAll("\\s+", " ").trim();
  }

  private static String limpiarSiglasCentro(String texto) {
    if (texto == null || texto.isBlank()) {
      return texto;
    }

    String resultado = texto.replaceAll(REGEX_SIGLAS, "");
    return resultado.replaceAll("\\s+", " ").trim();
  }
}
