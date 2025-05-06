/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Bernardo;

/**
 *
 * @author ALEJANDRO
 */
/**
 *
 * @author ALEJANDRO
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

public class AnalizadorLexicoGUI extends JFrame {
    
    // Componentes de la GUI
    private JTextPane editorPane;
    private JTable tokenTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollEditor, scrollTable;
    private JButton btnAnalizar, btnLimpiar, btnGuardar, btnAbrir;
    private JComboBox<String> ejemplosCombo;
    private JSplitPane splitPane;
    private JLabel statusLabel;
    private JMenuBar menuBar;
    
    // Colores para resaltado de sintaxis
    private final Color COLOR_PALABRA_CLAVE = new Color(0, 0, 150);
    private final Color COLOR_IDENTIFICADOR = new Color(0, 0, 0);
    private final Color COLOR_NUMERO = new Color(0, 128, 0);
    private final Color COLOR_CADENA = new Color(128, 0, 0);
    private final Color COLOR_COMENTARIO = new Color(128, 128, 128);
    private final Color COLOR_OPERADOR = new Color(128, 0, 128);
    private final Color COLOR_PUNTUACION = new Color(0, 100, 100);
    
    // Estilos para resaltado de sintaxis
    private StyledDocument document;
    private Style estiloDefault, estiloPalabraClave, estiloIdentificador;
    private Style estiloNumero, estiloCadena, estiloComentario, estiloOperador, estiloPuntuacion;
    
    // Mapa de palabras reservadas
    public static final Map<String, String> PALABRAS_RESERVADAS = new HashMap<>();
    static {
        PALABRAS_RESERVADAS.put("if", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("else", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("while", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("for", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("do", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("switch", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("case", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("default", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("break", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("continue", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("return", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("function", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("var", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("let", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("const", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("class", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("public", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("private", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("protected", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("static", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("void", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("int", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("float", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("double", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("boolean", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("string", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("true", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("false", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("null", "PALABRA_CLAVE");
        PALABRAS_RESERVADAS.put("undefined", "PALABRA_CLAVE");
    }
    
    // Ejemplos de código para la combobox
    private final String[] EJEMPLOS_CODIGO = {
        "Seleccionar ejemplo...",
        "function calcularFactorial(n) {\n    if (n <= 1) return 1;\n    return n * calcularFactorial(n-1);\n}",
        "for (let i = 0; i < 10; i++) {\n    console.log(\"El valor es: \" + i);\n}",
        "class Persona {\n    constructor(nombre, edad) {\n        this.nombre = nombre;\n        this.edad = edad;\n    }\n    \n    saludar() {\n        return \"Hola, mi nombre es \" + this.nombre;\n    }\n}"
    };
    
    public AnalizadorLexicoGUI() {
        super("Analizador Léxico y Sintáctico");
        initComponents();
        crearEstilos();
        configMenuBar();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con botones y combo
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Botones con iconos
        btnAnalizar = new JButton("Analizar");
        btnLimpiar = new JButton("Limpiar");
        btnGuardar = new JButton("Guardar");
        btnAbrir = new JButton("Abrir");
        
        // ComboBox para ejemplos
        ejemplosCombo = new JComboBox<>(EJEMPLOS_CODIGO);
        ejemplosCombo.setPreferredSize(new Dimension(200, 25));
        
        // Añadir componentes al panel de botones
        buttonPanel.add(btnAnalizar);
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnAbrir);
        
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        comboPanel.add(new JLabel("Ejemplos: "));
        comboPanel.add(ejemplosCombo);
        
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(comboPanel, BorderLayout.EAST);
        
        // Editor de texto con números de línea
        editorPane = new JTextPane();
        document = editorPane.getStyledDocument();
        editorPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        TextLineNumber textLineNumber = new TextLineNumber(editorPane);
        scrollEditor = new JScrollPane(editorPane);
        scrollEditor.setRowHeaderView(textLineNumber);
        scrollEditor.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Tabla de tokens con nuevas columnas
        String[] columnNames = {"N°", "Tipo", "Valor", "Parámetro", "Descripción"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tokenTable = new JTable(tableModel);
        tokenTable.getTableHeader().setReorderingAllowed(false);
        tokenTable.setFillsViewportHeight(true);
        tokenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        scrollTable = new JScrollPane(tokenTable);
        scrollTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // SplitPane para dividir editor y tabla
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollEditor, scrollTable);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerLocation(450);
        
        // Barra de estado
        statusLabel = new JLabel("Listo");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Añadir componentes al panel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Añadir panel principal al frame
        add(mainPanel);
        
        // Configurar event listeners
        configurarEventos();
    }
    
    private void configMenuBar() {
        menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        JMenuItem itemAbrir = new JMenuItem("Abrir");
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        JMenuItem itemGuardarComo = new JMenuItem("Guardar como...");
        JMenuItem itemSalir = new JMenuItem("Salir");
        
        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        
        // Menú Editar
        JMenu menuEditar = new JMenu("Editar");
        JMenuItem itemCortar = new JMenuItem("Cortar");
        JMenuItem itemCopiar = new JMenuItem("Copiar");
        JMenuItem itemPegar = new JMenuItem("Pegar");
        JMenuItem itemSeleccionarTodo = new JMenuItem("Seleccionar todo");
        
        menuEditar.add(itemCortar);
        menuEditar.add(itemCopiar);
        menuEditar.add(itemPegar);
        menuEditar.addSeparator();
        menuEditar.add(itemSeleccionarTodo);
        
        // Menú Análisis
        JMenu menuAnalisis = new JMenu("Análisis");
        JMenuItem itemAnalizar = new JMenuItem("Analizar código");
        JMenuItem itemEstadisticas = new JMenuItem("Estadísticas");
        JMenuItem itemExportar = new JMenuItem("Exportar resultados");
        
        menuAnalisis.add(itemAnalizar);
        menuAnalisis.add(itemEstadisticas);
        menuAnalisis.add(itemExportar);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemDocumentacion = new JMenuItem("Documentación");
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");
        
        menuAyuda.add(itemDocumentacion);
        menuAyuda.add(itemAcercaDe);
        
        // Añadir menús a la barra
        menuBar.add(menuArchivo);
        menuBar.add(menuEditar);
        menuBar.add(menuAnalisis);
        menuBar.add(menuAyuda);
        
        // Configurar eventos del menú
        itemNuevo.addActionListener(e -> limpiarEditor());
        itemAbrir.addActionListener(e -> abrirArchivo());
        itemGuardar.addActionListener(e -> guardarArchivo(false));
        itemGuardarComo.addActionListener(e -> guardarArchivo(true));
        itemSalir.addActionListener(e -> System.exit(0));
        
        itemCortar.addActionListener(e -> editorPane.cut());
        itemCopiar.addActionListener(e -> editorPane.copy());
        itemPegar.addActionListener(e -> editorPane.paste());
        itemSeleccionarTodo.addActionListener(e -> editorPane.selectAll());
        
        itemAnalizar.addActionListener(e -> analizarCodigo());
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());
        
        setJMenuBar(menuBar);
    }
    
    private void crearEstilos() {
        // Estilo por defecto
        estiloDefault = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        // Estilo para palabras clave
        estiloPalabraClave = document.addStyle("palabraClave", estiloDefault);
        StyleConstants.setForeground(estiloPalabraClave, COLOR_PALABRA_CLAVE);
        StyleConstants.setBold(estiloPalabraClave, true);
        
        // Estilo para identificadores
        estiloIdentificador = document.addStyle("identificador", estiloDefault);
        StyleConstants.setForeground(estiloIdentificador, COLOR_IDENTIFICADOR);
        
        // Estilo para números
        estiloNumero = document.addStyle("numero", estiloDefault);
        StyleConstants.setForeground(estiloNumero, COLOR_NUMERO);
        
        // Estilo para cadenas
        estiloCadena = document.addStyle("cadena", estiloDefault);
        StyleConstants.setForeground(estiloCadena, COLOR_CADENA);
        
        // Estilo para comentarios
        estiloComentario = document.addStyle("comentario", estiloDefault);
        StyleConstants.setForeground(estiloComentario, COLOR_COMENTARIO);
        StyleConstants.setItalic(estiloComentario, true);
        
        // Estilo para operadores
        estiloOperador = document.addStyle("operador", estiloDefault);
        StyleConstants.setForeground(estiloOperador, COLOR_OPERADOR);
        
        // Estilo para puntuación
        estiloPuntuacion = document.addStyle("puntuacion", estiloDefault);
        StyleConstants.setForeground(estiloPuntuacion, COLOR_PUNTUACION);
    }
    
    private void configurarEventos() {
        // Eventos de botones
        btnAnalizar.addActionListener(e -> analizarCodigo());
        btnLimpiar.addActionListener(e -> limpiarEditor());
        btnGuardar.addActionListener(e -> guardarArchivo(false));
        btnAbrir.addActionListener(e -> abrirArchivo());
        
        // Evento de combobox
        ejemplosCombo.addActionListener(e -> {
            int seleccionado = ejemplosCombo.getSelectedIndex();
            if (seleccionado > 0) {
                editorPane.setText(EJEMPLOS_CODIGO[seleccionado]);
                resaltarSintaxis(editorPane.getText());
                ejemplosCombo.setSelectedIndex(0);
            }
        });
        
        // Evento de teclado para detectar cambios
        editorPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> resaltarSintaxis(editorPane.getText()));
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> resaltarSintaxis(editorPane.getText()));
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                // No se usa para plain text components
            }
        });
    }
    
    private void analizarCodigo() {
        String codigo = editorPane.getText();
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                                         "El editor está vacío. Ingrese código para analizar.", 
                                         "Editor vacío", 
                                         JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Limpiar tabla
        tableModel.setRowCount(0);
        
        // Analizador léxico
        AnalizadorLexico analizador = new AnalizadorLexico(codigo);
        Token token;
        int contador = 1;
        
        try {
            while ((token = analizador.siguienteToken()) != null) {
                tableModel.addRow(new Object[]{
                    contador++,
                    token.tipo,
                    token.valor,
                    token.parametro,
                    obtenerDescripcion(token)
                });
            }
            
            statusLabel.setText("Análisis léxico completado. " + (contador - 1) + " tokens encontrados.");
            
            // Realizar análisis sintáctico
            analizarSintaxis(codigo);
            
            // Ajustar anchos de columnas
            ajustarAnchoColumnas();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                                         "Error durante el análisis: " + ex.getMessage(), 
                                         "Error", 
                                         JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Error durante el análisis");
        }
    }
    
private String obtenerDescripcion(Token token) {
    if (token == null) return "Token nulo";
    
    // Descripciones concisas que complementan (no repiten) el parámetro
    switch (token.tipo) {
        case "PALABRA_CLAVE":
            return "Elemento reservado del lenguaje";
            
        case "IDENTIFICADOR":
            if (token.parametro != null && token.parametro.startsWith("Valor=")) {
                return "Asignación de variable";
            }
            return "Referencia a " + (token.parametro != null ? 
                  token.parametro.toLowerCase() : "elemento");
            
        case "NUMERO":
            return "Valor numérico";
            
        case "CADENA":
            return "Literal de texto";
            
        case "OPERADOR":
            switch(token.parametro) {
                case "asignacion": return "Asigna un valor";
                case "comparacion": return "Compara valores";
                case "aritmetico": return "Operación matemática";
                case "logico": return "Operación lógica";
                default: return "Operación";
            }
            
        case "PUNTUACION":
            return "Delimitador de estructura";
            
        case "COMENTARIO":
            return "Anotación no ejecutable";
            
        case "ASIGNACION_COMPLETA":
            return "Asignación de valor";
            
        default:
            return "Elemento del código";
    }
}

    private void analizarSintaxis(String codigo) {
        Stack<Character> pila = new Stack<>();
        Stack<Integer> lineas = new Stack<>();
        int lineaActual = 1;
        int columnaActual = 1;
        boolean error = false;

        for (char c : codigo.toCharArray()) {
            if (c == '\n') {
                lineaActual++;
                columnaActual = 1;
            } else {
                columnaActual++;
            }

            // Verificar apertura de símbolos
            if (c == '(' || c == '{' || c == '[') {
                pila.push(c);
                lineas.push(lineaActual);
            } 
            // Verificar cierre de símbolos
            else if (c == ')' || c == '}' || c == ']') {
                if (pila.isEmpty()) {
                    statusLabel.setText("Error sintáctico: Símbolo '"+c+"' sin abrir (Línea "+lineaActual+")");
                    return;
                }
                
                char top = pila.pop();
                int lineaError = lineas.pop();
                
                if ((c == ')' && top != '(') || 
                    (c == '}' && top != '{') || 
                    (c == ']' && top != '[')) {
                    statusLabel.setText("Error sintáctico: Símbolo '"+c+"' no coincide con '"+top+"' (Línea "+lineaError+")");
                    return;
                }
            }
        }

        if (!pila.isEmpty()) {
            statusLabel.setText("Error sintáctico: Símbolo '"+pila.peek()+"' sin cerrar (Línea "+lineas.peek()+")");
        } else {
            statusLabel.setText("Análisis completado. Sintaxis correcta.");
        }
    }
    
    private void ajustarAnchoColumnas() {
        tokenTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // N°
        tokenTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Tipo
        tokenTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Valor
        tokenTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Parámetro
        tokenTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Descripción
    }
    
    private void resaltarSintaxis(String texto) {
    try {
        // Resetear todo el estilo
        document.setCharacterAttributes(0, document.getLength(), estiloDefault, true);
        
        // Necesitamos rastrear la posición manualmente
        int posicionActual = 0;
        
        // Análisis léxico para resaltado
        AnalizadorLexico analizador = new AnalizadorLexico(texto);
        Token token;
        
        while ((token = analizador.siguienteToken()) != null) {
            Style estilo = estiloDefault;
            
            // Encontrar la posición del token en el texto
            int inicio = texto.indexOf(token.valor, posicionActual);
            if (inicio >= 0) {
                posicionActual = inicio + token.valor.length();
                
                switch (token.tipo) {
                    case "PALABRA_CLAVE":
                        estilo = estiloPalabraClave;
                        break;
                    case "IDENTIFICADOR":
                        estilo = estiloIdentificador;
                        break;
                    case "NUMERO":
                        estilo = estiloNumero;
                        break;
                    case "CADENA":
                        estilo = estiloCadena;
                        break;
                    case "COMENTARIO":
                        estilo = estiloComentario;
                        break;
                    case "OPERADOR":
                        estilo = estiloOperador;
                        break;
                    case "PUNTUACION":
                        estilo = estiloPuntuacion;
                        break;
                    default:
                        estilo = estiloDefault;
                }
                
                // Aplicar el estilo al token encontrado
                document.setCharacterAttributes(inicio, token.valor.length(), estilo, true);
            }
        }
    } catch (Exception e) {
        // Ignorar errores durante el resaltado
        System.err.println("Error en resaltado: " + e.getMessage());
    }
}
    
    private void limpiarEditor() {
        editorPane.setText("");
        tableModel.setRowCount(0);
        statusLabel.setText("Listo");
    }
    
    private void guardarArchivo(boolean guardarComo) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Código");
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                writer.write(editorPane.getText());
                statusLabel.setText("Archivo guardado en: " + fileToSave.getAbsolutePath());
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, 
                                             "Error al guardar archivo: " + e.getMessage(), 
                                             "Error", 
                                             JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Error al guardar archivo");
            }
        }
    }
    
    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir Código");
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToOpen))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                
                editorPane.setText(content.toString());
                resaltarSintaxis(editorPane.getText());
                statusLabel.setText("Archivo abierto: " + fileToOpen.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                                             "Error al abrir archivo: " + e.getMessage(), 
                                             "Error", 
                                             JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Error al abrir archivo");
            }
        }
    }
    
    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this, 
                                     "Analizador Léxico y Sintáctico\nProyecto de Compiladores\nVersión 2.0", 
                                     "Acerca de", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new AnalizadorLexicoGUI().setVisible(true);
        });
    }
    
    /**
     * Clase para los números de línea en el editor
     */
    class TextLineNumber extends JComponent {
        private static final long serialVersionUID = 1L;
        private final static int HEIGHT = Integer.MAX_VALUE - 1000000;
        
        private JTextComponent component;
        private int updateFont;
        private int borderGap;
        private Color currentLineForeground;
        private float digitAlignment;
        private int minimumDisplayDigits;
        
        private int lastDigits;
        private int lastHeight;
        private int lastLine;
        
        private HashMap<String, FontMetrics> fonts;
        
        public TextLineNumber(JTextComponent component) {
            this(component, 3);
        }
        
        public TextLineNumber(JTextComponent component, int minimumDisplayDigits) {
            this.component = component;
            this.updateFont = 0;
            this.borderGap = 5;
            this.currentLineForeground = Color.RED;
            this.digitAlignment = 1.0f;
            this.minimumDisplayDigits = minimumDisplayDigits;
            
            setFont(component.getFont());
            setBorderGap(5);
            setCurrentLineForeground(Color.BLUE);
            setDigitAlignment(1.0f);
            setMinimumDisplayDigits(3);
            
            component.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    documentChanged();
                }
                
                @Override
                public void removeUpdate(DocumentEvent e) {
                    documentChanged();
                }
                
                @Override
                public void changedUpdate(DocumentEvent e) {
                    documentChanged();
                }
                
                private void documentChanged() {
                    repaint();
                }
            });
            
            component.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    revalidate();
                    repaint();
                }
            });
            
            fonts = new HashMap<>();
        }
        
        public void setUpdateFont(int updateFont) {
            this.updateFont = updateFont;
        }
        
        public void setBorderGap(int borderGap) {
            this.borderGap = borderGap;
            lastDigits = 0;
            setPreferredWidth();
        }
        
        public void setCurrentLineForeground(Color currentLineForeground) {
            this.currentLineForeground = currentLineForeground;
        }
        
        public void setDigitAlignment(float digitAlignment) {
            this.digitAlignment = digitAlignment > 1.0f ? 1.0f : digitAlignment < 0.0f ? 0.0f : digitAlignment;
        }
        
        public void setMinimumDisplayDigits(int minimumDisplayDigits) {
            this.minimumDisplayDigits = minimumDisplayDigits;
            lastDigits = 0;
            setPreferredWidth();
        }
        
        private void setPreferredWidth() {
            Element root = component.getDocument().getDefaultRootElement();
            int lines = root.getElementCount();
            int digits = Math.max(String.valueOf(lines).length(), minimumDisplayDigits);
            
            if (lastDigits != digits) {
                lastDigits = digits;
                FontMetrics fontMetrics = getFontMetrics(getFont());
                int width = fontMetrics.charWidth('0') * digits;
                Insets insets = getInsets();
                int preferredWidth = insets.left + insets.right + width;
                
                Dimension d = getPreferredSize();
                d.setSize(preferredWidth, HEIGHT);
                setPreferredSize(d);
                setSize(d);
            }
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            FontMetrics fontMetrics = component.getFontMetrics(component.getFont());
            Insets insets = getInsets();
            int availableWidth = getSize().width - insets.left - insets.right;
            
            Rectangle clip = g.getClipBounds();
            int rowStartOffset = component.viewToModel2D(new Point(0, clip.y));
            int endOffset = component.viewToModel2D(new Point(0, clip.y + clip.height));
            
            while (rowStartOffset <= endOffset) {
                try {
                    if (isCurrentLine(rowStartOffset))
                        g.setColor(currentLineForeground);
                    else
                        g.setColor(getForeground());
                    
                    String lineNumber = getTextLineNumber(rowStartOffset);
                    int stringWidth = fontMetrics.stringWidth(lineNumber);
                    int x = getOffsetX(availableWidth, stringWidth) + insets.left;
                    int y = getOffsetY(rowStartOffset, fontMetrics);
                    g.drawString(lineNumber, x, y);
                    
                    rowStartOffset = Utilities.getRowEnd(component, rowStartOffset) + 1;
                } catch (Exception e) {
                    break;
                }
            }
        }
        
        private boolean isCurrentLine(int rowStartOffset) {
            int caretPosition = component.getCaretPosition();
            Element root = component.getDocument().getDefaultRootElement();
            
            return root.getElementIndex(rowStartOffset) == root.getElementIndex(caretPosition);
        }
        
        private String getTextLineNumber(int rowStartOffset) {
            Element root = component.getDocument().getDefaultRootElement();
            int index = root.getElementIndex(rowStartOffset);
            return String.valueOf(index + 1);
        }
        
        private int getOffsetX(int availableWidth, int stringWidth) {
            return (int)((availableWidth - stringWidth) * digitAlignment);
        }
        
        private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics) {
            Rectangle r = null;
            
            try {
                r = component.modelToView2D(rowStartOffset).getBounds();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            
            int lineHeight = fontMetrics.getHeight();
            int y = r.y + r.height;
            int descent = 0;
            
            if (r.height == lineHeight) {
                descent = fontMetrics.getDescent();
            } else {
                if (fonts.get(r.height) == null) {
                    Font font = new Font(component.getFont().getName(), Font.PLAIN, r.height);
                    fonts.put(String.valueOf(r.height), getFontMetrics(font));
                }
                
                descent = fonts.get(String.valueOf(r.height)).getDescent();
            }
            
            return y - descent;
        }
    }
}

/**
 * Clase para realizar el análisis léxico
 */
class AnalizadorLexico {
    private String input;
    private int posicion;
    
    public AnalizadorLexico(String input) {
        this.input = input;
        this.posicion = 0;
    }
    
public Token siguienteToken() {
    if (posicion >= input.length()) {
        return null;
    }

    // Ignorar espacios en blanco
    while (posicion < input.length() && Character.isWhitespace(input.charAt(posicion))) {
        posicion++;
    }

    if (posicion >= input.length()) {
        return null;
    }

    char c = input.charAt(posicion);
    if (Character.isLetter(c) || c == '_') {
        StringBuilder sb = new StringBuilder();
        
        // Capturar el identificador
        do {
            sb.append(c);
            posicion++;
            if (posicion >= input.length()) break;
            c = input.charAt(posicion);
        } while (Character.isLetterOrDigit(c) || c == '_');

        String lexema = sb.toString();

        // Verificar si es palabra reservada
        if (AnalizadorLexicoGUI.PALABRAS_RESERVADAS.containsKey(lexema)) {
            return new Token("PALABRA_CLAVE", lexema, "reservada");
        }

        // Verificar si sigue un = (sin espacios intermedios)
        if (posicion < input.length() && input.charAt(posicion) == '=') {
            // Guardar posición actual para el operador =
            int posOperador = posicion;
            posicion++; // Avanzar sobre el =
            
            // Obtener el token del valor (recursivamente)
            Token valorToken = siguienteToken();
            
            // Retroceder para que el próximo llamado obtenga el =
            posicion = posOperador;
            return new Token("IDENTIFICADOR", lexema, "variable");
        }

        return new Token("IDENTIFICADOR", lexema, "variable");
    }

    // Detectar operador =
    if (c == '=') {
        posicion++;
        // Verificar si es == (operador de comparación)
        if (posicion < input.length() && input.charAt(posicion) == '=') {
            posicion++;
            return new Token("OPERADOR", "==", "comparacion");
        }
        return new Token("OPERADOR", "=", "asignacion");
    }



    // Número
    if (Character.isDigit(c)) {
        StringBuilder sb = new StringBuilder();
        boolean hasDecimalPoint = false;

        do {
            sb.append(c);
            posicion++;

            if (posicion >= input.length()) {
                break;
            }

            c = input.charAt(posicion);

            // Punto decimal
            if (c == '.' && !hasDecimalPoint) {
                if (posicion + 1 < input.length() && Character.isDigit(input.charAt(posicion + 1))) {
                    sb.append(c);
                    posicion++;
                    hasDecimalPoint = true;

                    if (posicion >= input.length()) {
                        break;
                    }

                    c = input.charAt(posicion);
                }
            }
        } while (Character.isDigit(c));

        String tipoNum = hasDecimalPoint ? "decimal" : "entero";
        return new Token("NUMERO", sb.toString(), tipoNum);
    }

    // Operador de asignación (manejo explícito)
    if (c == '=') {
        posicion++;
        return new Token("OPERADOR", "=", "asignacion");
    }
    // Cadena de texto
    if (c == '"' || c == '\'') {
        char quoteChar = c;
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        posicion++;
        
        boolean escaped = false;
        
        while (posicion < input.length()) {
            c = input.charAt(posicion);
            
            if (escaped) {
                sb.append(c);
                escaped = false;
            } else if (c == '\\') {
                sb.append(c);
                escaped = true;
            } else if (c == quoteChar) {
                sb.append(c);
                posicion++;
                break;
            } else {
                sb.append(c);
            }
            
            posicion++;
        }
        
        return new Token("CADENA", sb.toString(), "texto");
    }
    
    // Comentarios de una línea
    if (c == '/' && posicion + 1 < input.length() && input.charAt(posicion + 1) == '/') {
        StringBuilder sb = new StringBuilder();
        
        // Consumir ambos caracteres '/'
        sb.append("//");
        posicion += 2;
        
        while (posicion < input.length() && input.charAt(posicion) != '\n') {
            sb.append(input.charAt(posicion));
            posicion++;
        }
        
        return new Token("COMENTARIO", sb.toString(), "linea");
    }
    
    // Comentarios multilínea
    if (c == '/' && posicion + 1 < input.length() && input.charAt(posicion + 1) == '*') {
        StringBuilder sb = new StringBuilder();
        
        // Consumir '/*'
        sb.append("/*");
        posicion += 2;
        
        while (posicion + 1 < input.length() && 
              !(input.charAt(posicion) == '*' && input.charAt(posicion + 1) == '/')) {
            
            c = input.charAt(posicion);
            sb.append(c);
            posicion++;
        }
        
        if (posicion + 1 < input.length()) {
            // Consumir '*/'
            sb.append("*/");
            posicion += 2;
        }
        
        return new Token("COMENTARIO", sb.toString(), "bloque");
    }
    
    // Operadores
    if ("+-*/%=<>!&|^~?:".indexOf(c) != -1) {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        posicion++;
        
        String tipoOperador = "general";
        
        // Detectar operadores de 2 caracteres
        if (posicion < input.length()) {
            char nextChar = input.charAt(posicion);
            String operador = String.valueOf(c) + nextChar;
            
            if (operador.equals("++") || operador.equals("--")) {
                tipoOperador = "incremento";
                sb.append(nextChar);
                posicion++;
            } 
            else if (operador.equals("==") || operador.equals("!=") || operador.equals("<=") || 
                     operador.equals(">=") || operador.equals("<") || operador.equals(">")) {
                tipoOperador = "comparacion";
                sb.append(nextChar);
                posicion++;
            } 
            else if (operador.equals("&&") || operador.equals("||")) {
                tipoOperador = "logico";
                sb.append(nextChar);
                posicion++;
            }
            else if (operador.equals("+=") || operador.equals("-=") || operador.equals("*=") || 
                     operador.equals("/=") || operador.equals("%=") || operador.equals("=")) {
                tipoOperador = "asignacion";
                sb.append(nextChar);
                posicion++;
            }
            else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                tipoOperador = "aritmetico";
            }
        }
        
        return new Token("OPERADOR", sb.toString(), tipoOperador);
    }
    
    // Puntuación
    if ("(){}[];,.".indexOf(c) != -1) {
        posicion++;
        String ambito = (c == '{' || c == '}') ? "bloque" : "general";
        return new Token("PUNTUACION", Character.toString(c), "separador");
    }
    
    // Si llegamos aquí, es un token desconocido
    posicion++;
    return new Token("DESCONOCIDO", Character.toString(c), "desconocido");
}
}
/**
 * Clase para representar un token
 */
class Token {
    public String tipo;
    public String valor;
    public String parametro;
    
    public Token(String tipo, String valor, String parametro) {
        this.tipo = tipo;
        this.valor = valor;
        this.parametro = parametro;
    }
    
    // Método adicional para crear tokens de asignación
    public static Token[] crearAsignacion(String nombreVar, String valor, String tipoValor) {
        return new Token[] {
            new Token("IDENTIFICADOR", nombreVar, "variable"),
            new Token("OPERADOR", "=", "asignacion"),
            new Token(tipoValor, valor, "valor")
        };
    }
    
    @Override
    public String toString() {
        return "Token [tipo=" + tipo + ", valor=" + valor + ", parametro=" + parametro + "]";
    }
}
               