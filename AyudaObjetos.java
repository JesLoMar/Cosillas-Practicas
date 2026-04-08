package org.example;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class AyudaObjetos {

    // ==========================================================
    // Genera una huella digital SHA-256 en formato hexadecimal.
    // ==========================================================
    public static String obtenerHash(String texto) {
        try {
            // Configura el algoritmo de cifrado SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Genera el hash en formato binario (bytes)
            byte[] hashCodificado = digest.digest(texto.getBytes(StandardCharsets.UTF_8));

            // Convierte cada byte a su representación hexadecimal de dos caracteres
            StringBuilder cadenaHexadecimal = new StringBuilder();
            for (byte b : hashCodificado) {
                String hex = String.format("%02x", b);
                cadenaHexadecimal.append(hex);
            }
            return cadenaHexadecimal.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            // Error crítico si el entorno no soporta el estándar SHA-256
            throw new RuntimeException("Error al inicializar el algoritmo de hash", e);
        }
    }

    // ==========================================================
    // Elimina acentos, símbolos y números, dejando solo letras y espacios.
    // ==========================================================
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        // Descompone caracteres (ej: 'ñ' -> 'n' + '~') para separar acentos
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);

        // Expresión regular para identificar y eliminar marcas diacríticas
        Pattern patronAcentos = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String textoSinAcentos = patronAcentos.matcher(textoNormalizado).replaceAll("");

        // Filtra el texto manteniendo exclusivamente letras latinas y espacios
        return textoSinAcentos.replaceAll("[^a-zA-Z\\s]", "");
    }

    // ==========================================================
    // Elimina conectores y artículos comunes en apellidos hispanos.
    // ==========================================================
    public static String limpiarParticulas(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }
        // Identifica partículas aisladas ignorando mayúsculas/minúsculas
        String regexParticulas = "(?i)\\b(de|del|la|las|el|los|y)\\b";

        // Remueve las partículas y normaliza espacios en blanco internos
        String resultado = texto.replaceAll(regexParticulas, "");
        return resultado.replaceAll("\\s+", " ").trim();
    }
}
