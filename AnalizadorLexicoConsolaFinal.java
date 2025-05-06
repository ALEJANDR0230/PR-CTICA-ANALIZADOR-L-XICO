/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sincomponentes;

/**
 *
 * @author rober
 */


import java.util.Scanner;

public class AnalizadorLexicoConsolaFinal {

    // Palabras reservadas y patrones
    public static final String[][] PALABRAS_RESERVADAS = {
        // Palabras clave de programación
        {"if", "CONTROL"},
        {"else", "CONTROL"},
        {"while", "CONTROL"},
        {"for", "CONTROL"},
        {"function", "DECLARACION"},
        {"return", "CONTROL"},
        {"class", "DECLARACION"},
        {"var", "DECLARACION"},
        {"let", "DECLARACION"},
        {"const", "DECLARACION"},
        {"true", "BOOLEANO"},
        {"false", "BOOLEANO"},
        {"null", "ESPECIAL"},
        
        // Instrucciones técnicas
        {"Apagar", "INSTRUCCION"},
        {"Encender", "INSTRUCCION"},
        {"Limpiar", "INSTRUCCION"},
        {"Verificar", "INSTRUCCION"},
        {"Reemplazar", "INSTRUCCION"},
        {"Calibrar", "INSTRUCCION"},
        {"Diagnosticar", "INSTRUCCION"},
        {"Iniciar", "INSTRUCCION"},
        {"Detener", "INSTRUCCION"},
        {"Configurar", "INSTRUCCION"}
    };

    // Clase Token para representar cada elemento léxico
    public static class Token {
        public final String tipo;
        public final String valor;
        public final String detalle;
        public final int posicion;
        public final int linea;
        public final int columna;
        
        public Token(String tipo, String valor, String detalle, int posicion, int linea, int columna) {
            this.tipo = tipo;
            this.valor = valor;
            this.detalle = detalle;
            this.posicion = posicion;
            this.linea = linea;
            this.columna = columna;
        }
    }

    // Clase para el análisis léxico
    public static class AnalizadorLexico {
        private final String entrada;
        private int posicion;
        private int linea = 1;
        private int columna = 1;
        
        public AnalizadorLexico(String entrada) {
            this.entrada = entrada;
            this.posicion = 0;
        }
        
        public Token[] analizar() throws Exception {
            Token[] tokensArray = new Token[0];
            Token token;
            
            while ((token = siguienteToken()) != null) {
                Token[] nuevoArray = new Token[tokensArray.length + 1];
                System.arraycopy(tokensArray, 0, nuevoArray, 0, tokensArray.length);
                nuevoArray[tokensArray.length] = token;
                tokensArray = nuevoArray;
                
                // Manejo especial para asignaciones
                if (token.tipo.equals("IDENTIFICADOR") && 
                    posicion < entrada.length() && entrada.charAt(posicion) == '=') {
                    
                    Token tokenAsignacion = new Token("OPERADOR", "=", "asignacion", posicion, linea, columna);
                    
                    Token[] nuevoArrayConAsignacion = new Token[tokensArray.length + 1];
                    System.arraycopy(tokensArray, 0, nuevoArrayConAsignacion, 0, tokensArray.length);
                    nuevoArrayConAsignacion[tokensArray.length] = tokenAsignacion;
                    tokensArray = nuevoArrayConAsignacion;
                    
                    posicion++;
                    columna++;
                    
                    Token tokenValor = siguienteToken();
                    if (tokenValor != null) {
                        Token[] nuevoArrayConValor = new Token[tokensArray.length + 1];
                        System.arraycopy(tokensArray, 0, nuevoArrayConValor, 0, tokensArray.length);
                        nuevoArrayConValor[tokensArray.length] = tokenValor;
                        tokensArray = nuevoArrayConValor;
                    }
                }
            }
            
            return tokensArray;
        }
        
        private Token siguienteToken() throws Exception {
            if (posicion >= entrada.length()) return null;
            
            saltarEspacios();
            if (posicion >= entrada.length()) return null;
            
            char actual = entrada.charAt(posicion);
            
            // Identificadores y palabras reservadas
            if (esLetra(actual) || actual == '_') {
                return procesarIdentificador();
            }
            
            // Números (incluyendo valores con unidades)
            if (esDigito(actual)) {
                return procesarNumero();
            }
            
            // Cadenas
            if (actual == '"' || actual == '\'') {
                return procesarCadena();
            }
            
            // Comentarios
            if (actual == '/' && posicion + 1 < entrada.length()) {
                char siguiente = entrada.charAt(posicion + 1);
                if (siguiente == '/' || siguiente == '*') {
                    return procesarComentario();
                }
            }
            
            // Operadores
            if (esOperador(actual)) {
                return procesarOperador();
            }
            
            // Puntuación
            if (esPuntuacion(actual)) {
                return procesarPuntuacion();
            }
            
            // Códigos de pieza (ej: #A3-45)
            if (actual == '#') {
                return procesarCodigoPieza();
            }
            
            // Carácter desconocido
            if (!esCaracterValido(actual)) {
                int posError = posicion;
                int colError = columna;
                int linError = linea;
                String simbolo = String.valueOf(actual);
                posicion++;
                columna++;
                return new Token("ERROR", simbolo, "desconocido", posError, linError, colError);
            }
            
            // Carácter desconocido no capturado por las reglas anteriores
            posicion++;
            columna++;
            throw new Exception("Símbolo no reconocido: '" + actual + "' en línea " + linea + ", columna " + columna);
        }
        
        private boolean esLetra(char c) {
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        }
        
        private boolean esDigito(char c) {
            return c >= '0' && c <= '9';
        }
        
        private boolean esCaracterValido(char c) {
            return esLetra(c) || esDigito(c) || 
                   esEspacioBlanco(c) ||
                   esOperador(c) ||
                   esPuntuacion(c) ||
                   c == '_' || 
                   c == '#' ||
                   c == '"' ||
                   c == '\'' ||
                   c == '/' ||
                   c == '\\' ||
                   c == '.';
        }
        
        private boolean esEspacioBlanco(char c) {
            return c == ' ' || c == '\t' || c == '\n' || c == '\r';
        }
            
        private Token procesarIdentificador() {
            int inicio = posicion;
            int lineaInicio = linea;
            int columnaInicio = columna;
            
            while (posicion < entrada.length()) {
                char c = entrada.charAt(posicion);
                if (!esLetra(c) && !esDigito(c) && c != '_') break;
                posicion++;
                columna++;
            }
            
            String valor = entrada.substring(inicio, posicion);
            String tipo = "IDENTIFICADOR";
            String detalle = "variable";
            
            // Buscar en palabras reservadas
            for (String[] palabra : PALABRAS_RESERVADAS) {
                if (palabra[0].equals(valor)) {
                    tipo = palabra[1];
                    detalle = tipo.toLowerCase();
                    break;
                }
            }
            
            return new Token(tipo, valor, detalle, inicio, lineaInicio, columnaInicio);
        }
        
        private Token procesarNumero() {
            int inicio = posicion;
            int lineaInicio = linea;
            int columnaInicio = columna;
            boolean tieneDecimal = false;
            boolean tieneUnidad = false;
            
            while (posicion < entrada.length()) {
                char c = entrada.charAt(posicion);
                
                if (esDigito(c)) {
                    posicion++;
                    columna++;
                } 
                else if (c == '.' && !tieneDecimal) {
                    tieneDecimal = true;
                    posicion++;
                    columna++;
                } 
                else if (esLetra(c)) {
                    tieneUnidad = true;
                    posicion++;
                    columna++;
                    break;
                } 
                else {
                    break;
                }
            }
            
            String valor = entrada.substring(inicio, posicion);
            String detalle = tieneUnidad ? "medicion" : tieneDecimal ? "decimal" : "entero";
            
            return new Token("NUMERO", valor, detalle, inicio, lineaInicio, columnaInicio);
        }
        
        private Token procesarCadena() throws Exception {
            int inicio = posicion;
            int lineaInicio = linea;
            int columnaInicio = columna;
            char comilla = entrada.charAt(posicion);
            posicion++;
            columna++;
            boolean escapado = false;
            
            while (posicion < entrada.length()) {
                char c = entrada.charAt(posicion);
                
                if (escapado) {
                    escapado = false;
                } 
                else if (c == '\\') {
                    escapado = true;
                } 
                else if (c == comilla) {
                    posicion++;
                    columna++;
                    break;
                }
                
                posicion++;
                columna++;
            }
            
            if (posicion > entrada.length()) {
                throw new Exception("Cadena no terminada en línea " + lineaInicio + ", columna " + columnaInicio);
            }
            
            String valor = entrada.substring(inicio, posicion);
            return new Token("CADENA", valor, "literal", inicio, lineaInicio, columnaInicio);
        }
        
        private Token procesarComentario() {
            int inicio = posicion;
            int lineaInicio = linea;
            int columnaInicio = columna;
            char segundo = entrada.charAt(posicion + 1);
            
            if (segundo == '/') {
                // Comentario de línea
                posicion += 2;
                columna += 2;
                
                while (posicion < entrada.length() && entrada.charAt(posicion) != '\n') {
                    posicion++;
                    columna++;
                }
            } 
            else {
                // Comentario de bloque
                posicion += 2;
                columna += 2;
                
                while (posicion + 1 < entrada.length()) {
                    if (entrada.charAt(posicion) == '*' && entrada.charAt(posicion + 1) == '/') {
                        posicion += 2;
                        columna += 2;
                        break;
                    }
                    
                    if (entrada.charAt(posicion) == '\n') {
                        linea++;
                        columna = 1;
                    } 
                    else {
                        columna++;
                    }
                    
                    posicion++;
                }
            }
            
            String valor = entrada.substring(inicio, posicion);
            return new Token("COMENTARIO", valor, "comentario", inicio, lineaInicio, columnaInicio);
        }
        
        private Token procesarOperador() {
            int inicio = posicion;
            int lineaInicio = linea;
            int columnaInicio = columna;
            char primero = entrada.charAt(posicion);
            
            if (posicion + 1 < entrada.length()) {
                char segundo = entrada.charAt(posicion + 1);
                String combinado = String.valueOf(primero) + segundo;
                
                if (esOperadorCombinado(combinado)) {
                    posicion += 2;
                    columna += 2;
                    String detalle = obtenerDetalleOperador(combinado);
                    return new Token("OPERADOR", combinado, detalle, inicio, lineaInicio, columnaInicio);
                }
            }
            
            posicion++;
            columna++;
            String detalle = obtenerDetalleOperador(String.valueOf(primero));
            return new Token("OPERADOR", String.valueOf(primero), detalle, inicio, lineaInicio, columnaInicio);
        }
        
        private Token procesarPuntuacion() {
            int inicio = posicion;
            int lineaInicio = linea;
            int columnaInicio = columna;
            char c = entrada.charAt(posicion);
            posicion++;
            columna++;
            return new Token("PUNTUACION", String.valueOf(c), "delimitador", inicio, lineaInicio, columnaInicio);
        }
        
        private Token procesarCodigoPieza() {
            int inicio = posicion;
            int lineaInicio = linea;
            int columnaInicio = columna;
            posicion++;
            columna++;
            
            while (posicion < entrada.length()) {
                char c = entrada.charAt(posicion);
                if (!esLetra(c) && !esDigito(c) && c != '-' && c != '_') break;
                posicion++;
                columna++;
            }
            
            String valor = entrada.substring(inicio, posicion);
            return new Token("CODIGO_PIEZA", valor, "pieza", inicio, lineaInicio, columnaInicio);
        }
        
        private void saltarEspacios() {
            while (posicion < entrada.length()) {
                char c = entrada.charAt(posicion);
                
                if (c == '\n') {
                    linea++;
                    columna = 1;
                    posicion++;
                } 
                else if (esEspacioBlanco(c)) {
                    columna++;
                    posicion++;
                } 
                else {
                    break;
                }
            }
        }
        
        private boolean esOperador(char c) {
            return "+-*/%=<>!&|^~?:.".indexOf(c) != -1;
        }
        
        private boolean esOperadorCombinado(String op) {
            return op.equals("==") || op.equals("!=") || op.equals("<=") || 
                   op.equals(">=") || op.equals("++") || op.equals("--") ||
                   op.equals("+=") || op.equals("-=") || op.equals("*=") || 
                   op.equals("/=") || op.equals("%=") || op.equals("&&") || 
                   op.equals("||");
        }
        
        private String obtenerDetalleOperador(String op) {
            if (op.equals("=") || op.equals(".")) return "asignacion";
            if (op.equals("==") || op.equals("!=") || op.equals("<") || 
                op.equals(">") || op.equals("<=") || op.equals(">=")) return "comparacion";
            if (op.equals("+") || op.equals("-") || op.equals("*") || 
                op.equals("/") || op.equals("%")) return "aritmetico";
            if (op.equals("++") || op.equals("--")) return "incremento";
            if (op.equals("+=") || op.equals("-=") || op.equals("*=") || 
                op.equals("/=") || op.equals("%=")) return "compuesto";
            if (op.equals("&&") || op.equals("||")) return "logico";
            return "operador";
        }
        
        private boolean esPuntuacion(char c) {
            return "(){}[];,".indexOf(c) != -1;
        }
    }

    // Métodos para mostrar resultados en consola
    private static void mostrarTablaTokens(Token[] tokens) {
        System.out.println("+-------+-----------------------+-------------------+---------+---------+---------------------+");
        System.out.println("| Línea | Tipo de Token         | Valor             | Detalle | Columna | Descripción         |");
        System.out.println("+-------+-----------------------+-------------------+---------+---------+---------------------+");
        
        for (Token token : tokens) {
            String descripcion = obtenerDescripcionToken(token);
            System.out.printf("| %-5d | %-21s | %-17s | %-7s | %-7d | %-19s |%n",
                token.linea,
                token.tipo,
                acortarValor(token.valor, 17),
                token.detalle,
                token.columna,
                acortarValor(descripcion, 19));
        }
        
        System.out.println("+-------+-----------------------+-------------------+---------+---------+---------------------+");
    }
    
    private static String acortarValor(String valor, int maxLength) {
        if (valor.length() > maxLength) {
            return valor.substring(0, maxLength - 3) + "...";
        }
        return valor;
    }
    
    private static String obtenerDescripcionToken(Token token) {
        if (token.tipo.equals("CONTROL")) return "Palabra de control";
        if (token.tipo.equals("DECLARACION")) return "Declaración";
        if (token.tipo.equals("INSTRUCCION")) return "Instrucción técnica";
        if (token.tipo.equals("BOOLEANO")) return "Valor booleano";
        if (token.tipo.equals("ESPECIAL")) return "Valor especial";
        if (token.tipo.equals("IDENTIFICADOR")) return "Identificador";
        
        if (token.tipo.equals("NUMERO")) {
            if (token.detalle.equals("entero")) return "Número entero";
            if (token.detalle.equals("decimal")) return "Número decimal";
            return "Valor numérico";
        }
        
        if (token.tipo.equals("OPERADOR")) {
            if (token.detalle.equals("asignacion")) return "Operador asignación";
            if (token.detalle.equals("comparacion")) return "Operador comparación";
            return "Operador";
        }
        
        if (token.tipo.equals("CADENA")) return "Cadena de texto";
        if (token.tipo.equals("COMENTARIO")) return "Comentario";
        if (token.tipo.equals("PUNTUACION")) return "Puntuación";
        if (token.tipo.equals("CODIGO_PIEZA")) return "Código de pieza";
        if (token.tipo.equals("ERROR")) return "Error léxico";
        
        return "Elemento código";
    }
    
    private static void mostrarEstadisticas(Token[] tokens) {
        String[] tipos = new String[tokens.length];
        int[] conteos = new int[tokens.length];
        int totalTipos = 0;
        
        for (Token token : tokens) {
            boolean encontrado = false;
            for (int i = 0; i < totalTipos; i++) {
                if (tipos[i].equals(token.tipo)) {
                    conteos[i]++;
                    encontrado = true;
                    break;
                }
            }
            
            if (!encontrado) {
                tipos[totalTipos] = token.tipo;
                conteos[totalTipos] = 1;
                totalTipos++;
            }
        }
        
        // Ordenar tipos por conteo (descendente)
        for (int i = 0; i < totalTipos - 1; i++) {
            for (int j = i + 1; j < totalTipos; j++) {
                if (conteos[i] < conteos[j]) {
                    // Intercambiar conteos
                    int tempConteo = conteos[i];
                    conteos[i] = conteos[j];
                    conteos[j] = tempConteo;
                    
                    // Intercambiar tipos
                    String tempTipo = tipos[i];
                    tipos[i] = tipos[j];
                    tipos[j] = tempTipo;
                }
            }
        }
        
        System.out.println("\nESTADÍSTICAS:");
        System.out.println("+-----------------------+-------+");
        System.out.println("| Tipo de Token         | Count |");
        System.out.println("+-----------------------+-------+");
        
        for (int i = 0; i < totalTipos; i++) {
            System.out.printf("| %-21s | %-5d |%n", tipos[i], conteos[i]);
        }
        
        System.out.println("+-----------------------+-------+");
        System.out.printf("Total tokens: %d%n", tokens.length);
    }
    
    // Método para leer entrada del usuario
    private static String leerEntradaUsuario() {
        StringBuilder input = new StringBuilder();
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            String linea = scanner.nextLine();
            if (linea.equalsIgnoreCase("fin")) {
                break;
            }
            input.append(linea).append("\n");
        }
        
        return input.toString();
    }
    
    
    // Método para leer una línea de la consola
    private static String leerLineaConsola() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    
    // Método principal
    public static void main(String[] args) {
        boolean ejecutando = true;
        
        System.out.println("ANALIZADOR LÉXICO AVANZADO - VERSIÓN CONSOLA");
        System.out.println("============================================");
        
        while (ejecutando) {
            System.out.println("\nOpciones:");
            System.out.println("1. Ingresar código manualmente");
            System.out.println("2. Usar ejemplo predefinido");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            
            String opcionStr = leerLineaConsola();
            int opcion;
            try {
                opcion = Integer.parseInt(opcionStr);
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida. Intente de nuevo.");
                continue;
            }
            
            String codigo = "";
            
            switch (opcion) {
                case 1:
                    System.out.println("\nIngrese el código a analizar (escriba 'fin' en una nueva línea para terminar):");
                    codigo = leerEntradaUsuario();
                    
                    // Analizar el código inmediatamente
                    try {
                        AnalizadorLexico analizador = new AnalizadorLexico(codigo);
                        Token[] tokens = analizador.analizar();
                        
                        System.out.println("\nRESULTADOS DEL ANÁLISIS LÉXICO:");
                        mostrarTablaTokens(tokens);
                        mostrarEstadisticas(tokens);
                      
                        
                    } catch (Exception e) {
                        System.out.println("\nError durante el análisis: " + e.getMessage());
                    }
                    break;
                    
                case 2:
                    System.out.println("\nEjemplos disponibles:");
                    System.out.println("1. Ejemplo de programación");
                    System.out.println("2. Ejemplo técnico");
                    System.out.println("3. Ejemplo de asignaciones");
                    System.out.println("4. Ejemplo de sistema de control");
                    System.out.print("Seleccione un ejemplo: ");
                    
                    String ejemploStr = leerLineaConsola();
                    int ejemplo;
                    try {
                        ejemplo = Integer.parseInt(ejemploStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Opción no válida.");
                        continue;
                    }
                    
                    switch (ejemplo) {
                        case 1:
                            codigo = "// Ejemplo de programación\nfunction sumar(a, b) {\n  return a + b;\n}";
                            break;
                        case 2:
                            codigo = "// Ejemplo técnico\nCalibrar motor izquierdo\nVerificar voltaje > 12V\nReemplazar pieza #AX-345";
                            break;
                        case 3:
                            codigo = "// Asignaciones\nvelocidad = 1500\nposicion = 23.5\nLED1 = Encender";
                            break;
                        case 4:
                            codigo = "// Sistema de control\nif (temperatura > 30) {\n  Encender ventilador\n  Ajustar velocidad(2000)\n}";
                            break;
                        default:
                            System.out.println("Opción no válida.");
                            continue;
                    }
                    
                    // Analizar el ejemplo seleccionado
                    try {
                        AnalizadorLexico analizador = new AnalizadorLexico(codigo);
                        Token[] tokens = analizador.analizar();
                        
                        System.out.println("\nRESULTADOS DEL ANÁLISIS LÉXICO:");
                        mostrarTablaTokens(tokens);
                        mostrarEstadisticas(tokens);
                        
                        
                    } catch (Exception e) {
                        System.out.println("\nError durante el análisis: " + e.getMessage());
                    }
                    break;
                    
                case 3:
                    System.out.println("Saliendo del programa...");
                    ejecutando = false;
                    break;
                    
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
                    break;
            }
        }
    }
}