package org.example;

public class Main {
    public static void main(String[] args) {
// --- CASOS DE PRUEBA EXTREMOS ---
// 1. Caso de éxito estándar -> SUPERADO
        String ex1 = crearIdAdministrador("Óliver", "García", AyudaObjetos.obtenerHash("12345678Z"));
// 2. Apellidos con partículas (más de 2 palabras) -> SUPERADO
        String ex2 = crearIdAdministrador("Ana", "De la Fuente", AyudaObjetos.obtenerHash("11111111A"));
// Reto: ¿Cómo maneja el split tres palabras?
// 3. Caracteres que NO son letras (Símbolos y números)-> SUPERADO
        String ex3 = crearIdAdministrador("J05é", "M@rtín", AyudaObjetos.obtenerHash("22222222B"));
// Reto: ¿Limpia el '0', '5', '@' y deja 'JOSE' y 'MARTIN'?
// 4. Strings que se quedan VACÍOS tras limpiar -> SUPERADO
        String ex4 = crearIdAdministrador("-", "777", AyudaObjetos.obtenerHash("33333333C"));
// Reto: Aquí es donde suelen saltar los StringIndexOutOfBoundsException.
// 5. Apellidos de una sola letra (Nombres orientales o errores) -> SUPERADO
        String ex5 = crearIdAdministrador("Li", "Y", AyudaObjetos.obtenerHash("44444444D"));
// Reto: ¿Pone la 'X' de relleno correctamente?
// 6. Espacios múltiples y tabulaciones -> SUPERADO
        String ex6 = crearIdAdministrador(" Clara  ", "  Chia   Chusma  ", AyudaObjetos.obtenerHash("55555555E"));
// Reto: ¿El trim() y el split() ignoran los espacios extra?
// 7. Valores Nulos (Null Safety) -> SUPERADO
// Ojo: Si tu metodo no gestiona nulls, esto romperá el programa.
        String ex7 = crearIdAdministrador(null, null, AyudaObjetos.obtenerHash("00000000X"));
// 8. Apellido compuesto y doble. -> SUPERADO
        String ex8 = crearIdAdministrador("Ana", "De la Fuente Romero", AyudaObjetos.obtenerHash("5523455E"));
// --- IMPRESIÓN DE RESULTADOS ---
        System.out.println("1. Estándar: " + ex1);
        System.out.println("2. Partículas: " + ex2);
        System.out.println("3. Símbolos: " + ex3);
        System.out.println("4. Vacíos: " + ex4);
        System.out.println("5. Apellido 1 letra: " + ex5);
        System.out.println("6. Espacios extra: " + ex6);
        System.out.println("7. Nulos: " + ex7);
        System.out.println("8. Apellido compuesto + apellido: " + ex8);
    }

    // ==========================================================
    // Extrae la primera letra del nombre tras normalizarlo.
    // ==========================================================
    private static String obtenerInicialNombre(String nombre) {
        if (nombre == null) {
            return "X";
        }

        // Limpia el nombre y lo prepara para la extracción
        String nombreLimpio = AyudaObjetos.normalizar(nombre).trim().toUpperCase();

        // Valida si el nombre quedó vacío tras la normalización
        if (nombreLimpio.isEmpty()) {
            return "X";
        }

        return nombreLimpio.substring(0, 1);
    }

    // ==========================================================
    // Procesa apellidos para obtener dos iniciales representativas.
    // ==========================================================
    private static String obtenerInicialesApellidos(String apellidos) {
        if (apellidos == null || apellidos.isBlank()) {
            return "XX";
        }

        // Remueve artículos/conectores y gestiona excepciones de borrado total
        String apellidosProcesados = AyudaObjetos.limpiarParticulas(apellidos);
        if (apellidosProcesados.isEmpty()) {
            apellidosProcesados = apellidos;
        }

        // Divide el texto en palabras ignorando espacios múltiples
        String[] partes = apellidosProcesados.split("\\s+");

        if (partes.length >= 2) {
            // Retorna inicial del primer y último apellido (caso múltiple)
            return partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1);
        } else {
            // Retorna las dos primeras letras (caso de un solo apellido)
            String soloUnApellido = partes[0];
            return soloUnApellido.length() >= 2
                    ? soloUnApellido.substring(0, 2)
                    : soloUnApellido.substring(0, 1) + "X";
        }
    }

    // ==========================================================
    // Trunca el hash del DNI para generar el sufijo del identificador.
    // ==========================================================
    private static String obtenerSufijoHash(String hashDni) {
        int longitudHash = 5;

        // Control de seguridad por si el hash es nulo o inesperadamente corto
        if (hashDni == null || hashDni.length() < longitudHash) {
            return "XXXXX";
        }

        return hashDni.substring(0, longitudHash);
    }

    // ==========================================================
    // Orquestador principal que construye el ID final del administrador.
    // ==========================================================
    public static String crearIdAdministrador(String nombre, String apellidos, String hashDni) {
        // Obtiene la inicial del nombre de forma segura
        String inicialNombre = obtenerInicialNombre(nombre);

        // Normaliza y extrae las iniciales de los apellidos
        String apellidosNormalizados = AyudaObjetos.normalizar(apellidos).trim().toUpperCase();
        String inicialesApellidos = obtenerInicialesApellidos(apellidosNormalizados);

        // Obtiene el fragmento del hash criptográfico
        String sufijoHash = obtenerSufijoHash(hashDni);

        // Compone el ID siguiendo el estándar ADM-III-HHHHH
        return "ADM-" + inicialNombre + inicialesApellidos + "-" + sufijoHash;
    }
}
