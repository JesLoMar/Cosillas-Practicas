# Cosillas Prácticas

## Saluditos
Buenas, os dejo esto que llevo toda la tarde haciendo.  
*(He estado tan en ello que no me he dado cuenta que llevo desde las 2:30 y son ya las 8:30 —y lo que me queda porque voy a seguir xD)*

---

## ¿Qué es esto?
Son unos métodos que he hecho para generar todas las **IDs de forma automática**. Actualmente solo está el de generar administradores pero, a ver si para esta noche hago algunos más o lo termino todo. La cuestión es que con este ejemplo sirve para ver la idea.

---

## ¿Cómo funciona?
Pues la verdad que es bastante "simple", mirad en los propios archivos que le he dicho a [Gemini](https://gemini.google.com/) que lo comente **bonito y breve**. 

¡Ah!, acabo de acordarme, como aviso digo que con esto, quería evitar todas las llamadas posibles a base de datos para no saturar y para generar el id numerico final, lo que nosotros poníamos como 00001, 00002 y tal, lo que he hecho ha sido en el administrador usar los 5 primeros caracteres del hash del DNI.
### ¿Porqué?

Porque malo será que coincidan las 3 letras y los 5 caracteres de los hashes, aún así, se podría reforzar haciendo alguna que otra validación en BD y cambiando dinámicamente un una variable llamada: *longitudHash* de la función: *obtenerSufijoHash*.

---

## ¿Cómo lo has hecho?
Soy consciente de que podría haberlo hecho todo en 1 hora en vez de hacer solo 1 parte en 6 horas, pero me he empeñado en hacerlo **sin usar la IA al principio** para pensar un rato. He echado un rato agradable la verdad; no quizá muy productivo, pero obligarse a pensar es bueno.

Para lo que sí he usado la IA es al haber terminado la lógica propia y funcional:
* He pedido una **optimización del código** manteniendo la lógica.
* La he usado para **refrescar qué hacían algunos métodos**.
* Para preguntar si hay algún método que haga **X cosa** (no nos vamos a engañar).

---

## ¿Qué pruebas ha pasado esta m*****?
He tratado de formatear diferentes posibles entradas de datos. Estos son los resultados (también están en el código):

### CASOS DE PRUEBA EXTREMOS

1. **Caso de éxito estándar** → `SUPERADO`
```java
String ex1 = crearIdAdministrador("Óliver", "García", AyudaObjetos.obtenerHash("12345678Z"));
// Es un usuario relativamente normal y fácil. La normalidad.
```

2. **Apellidos con partículas (+2 palabras)** → `SUPERADO`
```java
String ex2 = crearIdAdministrador("Ana", "De la Fuente", AyudaObjetos.obtenerHash("11111111A"));
// Reto: ¿Cómo maneja el split tres palabras?
```

3. **Caracteres que NO son letras (Símbolos/Números)** → `SUPERADO`
```java
String ex3 = crearIdAdministrador("J05é", "M@rtín", AyudaObjetos.obtenerHash("22222222B"));
// Reto: ¿Limpia el '0', '5', '@' y deja 'JOSE' y 'MARTIN'?
```

4. **Strings que quedan VACÍOS tras limpiar** → `SUPERADO`
```java
String ex4 = crearIdAdministrador("-", "777", AyudaObjetos.obtenerHash("33333333C"));
// Reto: Aquí es donde suelen saltar los StringIndexOutOfBoundsException.
```

5. **Apellidos de una sola letra** → `SUPERADO`
```java
String ex5 = crearIdAdministrador("Li", "Y", AyudaObjetos.obtenerHash("44444444D"));
// Reto: ¿Pone la 'X' de relleno correctamente?
```

6. **Espacios múltiples y tabulaciones** → `SUPERADO`
```java
String ex6 = crearIdAdministrador(" Clara ", " Chia Chusma ", AyudaObjetos.obtenerHash("55555555E"));
// Reto: ¿El trim() y el split() ignoran los espacios extra?
```

7. **Valores Nulos (Null Safety)** → `SUPERADO`
```java
String ex7 = crearIdAdministrador(null, null, AyudaObjetos.obtenerHash("00000000X"));
// Ojo: Si tu metodo no gestiona nulls, esto romperá el programa.
```

8. **Apellido compuesto y doble** → `SUPERADO`
```java
String ex8 = crearIdAdministrador("Ana", "De la Fuente Romero", AyudaObjetos.obtenerHash("5523455E"));
```
