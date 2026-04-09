package org.example;

public class Pruebas {
    public static void main(String[] args) {

        System.out.println("=========================================");
        System.out.println("  TESTS DE ADMINISTRADOR / PROFESOR / ALUMNO");
        System.out.println("=========================================");

        // --- CASOS DE PRUEBA EXTREMOS ---
        // 1. Caso de éxito estándar
        String ex1 = CrearId.administrador("Óliver", "García", CrearId.obtenerHash("12345678Z"));
        // 2. Apellidos con partículas (más de 2 palabras)
        String ex2 = CrearId.administrador("Ana", "De la Fuente", CrearId.obtenerHash("11111111A"));
        // 3. Caracteres que NO son letras (Símbolos y números)
        String ex3 = CrearId.administrador("J05é", "M@rtín", CrearId.obtenerHash("22222222B"));
        // 4. Strings que se quedan VACÍOS tras limpiar
        String ex4 = CrearId.administrador("-", "777", CrearId.obtenerHash("33333333C"));
        // 5. Apellidos de una sola letra (Nombres orientales o errores)
        String ex5 = CrearId.alumno("CEN-CER-A1B2", CrearId.obtenerHash("44444444D"));
        // 6. Espacios múltiples y tabulaciones
        String ex6 = CrearId.alumno("CEN-CER-A1B2", CrearId.obtenerHash("55555555E"));
        // 7. Valores Nulos (Null Safety)
        String ex7 = CrearId.profesor(null, null, CrearId.obtenerHash("00000000X"));
        // 8. Apellido compuesto y doble.
        String ex8 = CrearId.profesor("Ana", "Flores del Campo", CrearId.obtenerHash("5523455E"));

        // --- IMPRESIÓN DE RESULTADOS ADMINISTRADOR ---
        System.out.println("1. Estándar: " + ex1);
        System.out.println("2. Partículas: " + ex2);
        System.out.println("3. Símbolos: " + ex3);
        System.out.println("4. Vacíos: " + ex4);
        System.out.println("5. Apellido 1 letra: " + ex5);
        System.out.println("6. Espacios extra: " + ex6);
        System.out.println("7. Nulos: " + ex7);
        System.out.println("8. Apellido compuesto: " + ex8);


        System.out.println("=========================================");
        System.out.println("  TESTS EXTREMOS DE CENTROS EDUCATIVOS");
        System.out.println("=========================================");

        // 1. Siglas educativas al inicio + números
        String cen1 = CrearId.centro("I.E.S. Nº 12", "Madrid", "Madrid", "Retiro");
        // 2. Idiomas cooficiales y apóstrofes
        String cen2 = CrearId.centro("L'Escola d'Arts", "Cataluña", "Barcelona", "Sants");
        // 3. Sigla "fantasma" que coincide con una partícula (CP)
        String cen3 = CrearId.centro("C.P. Cervantes", "Andalucía", "Sevilla", "Nervión");
        // 4. Centros homónimos en diferentes ubicaciones
        String cen4a = CrearId.centro("Colegio San José", "Madrid", "Madrid", "Centro");
        String cen4b = CrearId.centro("Colegio San José", "Andalucía", "Málaga", "Centro");
        // 5. Centro nulo (Null Safety total)
        String cen5 = CrearId.centro(null, null, null, null);

        System.out.println("1. Números y Siglas (IES 12): " + cen1);
        System.out.println("2. Apóstrofes (L'Escola): " + cen2);
        System.out.println("3. Sigla fantasma (CP Cervantes): " + cen3);
        System.out.println("4A. Homónimo (San José - Madrid): " + cen4a);
        System.out.println("4B. Homónimo (San José - Málaga): " + cen4b);
        System.out.println("5. Todo Nulos: " + cen5);


        System.out.println("=========================================");
        System.out.println("  TESTS EXTREMOS ENTIDADES RELACIONALES");
        System.out.println("=========================================");

        // Variables base para testear herencia de IDs
        String idCentroTest = "CEN-CER-A1B2";
        String idProfeTest = "PRO-MGA-99XX";

        // 1. Encuesta estándar (La que tenías)
        String rel1 = CrearId.encuesta("FP DAW 1 A", 1, "2025-2026", "PRO-LMP-0002");
        // 2. Clase de 1 letra/número
        String rel2 = CrearId.clase("1ºA", idCentroTest);
        // 3. Encuesta con símbolos extraños
        String rel3 = CrearId.encuesta("Evaluación #1!", 1, "2º Bachillerato", idProfeTest);
        // 4. Informe a partir de IDs generados (Test de cadena)
        String rel4 = CrearId.informe(rel3, rel2, idCentroTest, idProfeTest);
        // 5. Dependencia mal formateada (Falta de guiones)
        String rel5 = CrearId.clase("Matemáticas", "ID_INVENTADO_SIN_GUIONES");
        // 6. Cadena de Nulos
        String rel6 = CrearId.informe(null, null, null, null);

        System.out.println("1. Encuesta Estándar: " + rel1);
        System.out.println("2. Clase Corta (1ºA): " + rel2);
        System.out.println("3. Encuesta con Símbolos: " + rel3);
        System.out.println("4. Informe en Cadena: " + rel4);
        System.out.println("5. Fallo de Dependencia (Sin guiones): " + rel5);
        System.out.println("6. Informe Nulo: " + rel6);
        System.out.println("=========================================");
    }
}
