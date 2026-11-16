package estudiante.view;

import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import estudiante.model.Estudiante;
import estudiante.controller.EstudianteController;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Dimension;
import java.net.URL;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Objects;

public class FrmEstudiante extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmEstudiante.class.getName());
    private static final Color FONDO = new Color(0xF5F7FA);
    private static final Color AZUL_PRINCIPAL = new Color(0x0B2D5B);
    private static final Color AZUL_SECUNDARIO = new Color(0x1F4E79);
    private static final Color TURQUESA = new Color(0x5AC8C1);
    private static final Color AZUL_CLARO = new Color(0xE6F4F8);
    private static final Color GRIS_DESHABILITADO = new Color(0xD1D9E6);
    private static final Color BLANCO = Color.WHITE;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private final EstudianteController controlador;
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Carnet", "Nombre", "Apellido", "Edad", "Fecha Nac.", "Género", "Correo", "Teléfono", "DPI"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private Estudiante seleccionado;

    /**
     * Creates new form FrmEstudiante
     */
    public FrmEstudiante() {
        initComponents();
        controlador = new EstudianteController(new estudiante.dao.EstudianteDao());
        jTable1.setModel(modeloTabla);
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) cargarSeleccionDeTabla();
        });
        instalarRestriccionesDeEntrada();
        jPanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 12));
        jPanel3.add(BotonLimpiar);
        aplicarEstiloPeopleManagers();
        BotonActualizar.setEnabled(false);
        BotonEliminar.setEnabled(false);
        BotonGuardar.setEnabled(true);
        // La tabla permanece visible desde el inicio, pero el modelo comienza vacío.
        jScrollPane1.setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent event) {
                jTextField1.requestFocusInWindow();
            }
        });
        pack();
        setLocationRelativeTo(null);
    }

    private void aplicarEstiloPeopleManagers() {
        getContentPane().setBackground(FONDO);
        jPanel1.setBackground(AZUL_CLARO);
        jPanel2.setOpaque(false);
        jPanel3.setOpaque(false);

        jLabel1.setText("GESTIÓN DE COLABORADORES");
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jLabel1.setForeground(AZUL_PRINCIPAL);
        jLabel1.setBackground(FONDO);
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setVerticalAlignment(SwingConstants.CENTER);
        jLabel1.setIconTextGap(16);
        jLabel1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, TURQUESA),
                BorderFactory.createEmptyBorder(0, 4, 1, 4)));

        URL logo = getClass().getResource("/estudiante/view/resources/people-managers-logo.png");
        if (logo != null) {
        Image imagen = new ImageIcon(logo).getImage().getScaledInstance(58, 52, Image.SCALE_SMOOTH);
            ImageIcon icono = new ImageIcon(imagen);
            jLabel1.setIcon(icono);
            setIconImage(imagen);
        }

        estilizarEtiqueta(jLabel2);
        estilizarEtiqueta(jLabel3);
        estilizarEtiqueta(jLabel4);
        estilizarEtiqueta(jLabel5);
        estilizarEtiqueta(jLabel6);
        estilizarEtiqueta(jLabel7);
        estilizarEtiqueta(jLabel8);
        estilizarEtiqueta(jLabel9);
        estilizarEtiqueta(jLabel10);
        jLabel4.setFont(new Font("Segoe UI", Font.BOLD, 11));

        for (javax.swing.JTextField campo : new javax.swing.JTextField[]{
            jTextField1, jTextField2, jTextField3, jTextField4,
            jTextField5, jTextField6, jTextField7, jTextField8}) {
            campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            campo.setForeground(AZUL_PRINCIPAL);
            campo.setBackground(BLANCO);
            campo.setCaretColor(AZUL_SECUNDARIO);
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AZUL_SECUNDARIO, 1),
                    BorderFactory.createEmptyBorder(5, 7, 5, 7)));
            campo.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override public void focusGained(java.awt.event.FocusEvent event) {
                    campo.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(TURQUESA, 2),
                            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
                }
                @Override public void focusLost(java.awt.event.FocusEvent event) {
                    campo.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(AZUL_SECUNDARIO, 1),
                            BorderFactory.createEmptyBorder(5, 7, 5, 7)));
                }
            });
        }
        jTextField5.setToolTipText("Formato de fecha: dd/MM/aaaa");

        jComboBox1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jComboBox1.setForeground(AZUL_PRINCIPAL);
        jComboBox1.setBackground(BLANCO);
        jComboBox1.setBorder(BorderFactory.createLineBorder(AZUL_SECUNDARIO, 1));

        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jTable1.setForeground(AZUL_PRINCIPAL);
        jTable1.setBackground(BLANCO);
        jTable1.setSelectionBackground(TURQUESA);
        jTable1.setSelectionForeground(AZUL_PRINCIPAL);
        jTable1.setGridColor(GRIS_DESHABILITADO);
        jTable1.setRowHeight(26);
        int[] anchosColumnas = {82, 90, 90, 52, 95, 80, 165, 95, 105};
        for (int columna = 0; columna < anchosColumnas.length; columna++) {
            jTable1.getColumnModel().getColumn(columna).setPreferredWidth(anchosColumnas[columna]);
        }
        JTableHeader encabezado = jTable1.getTableHeader();
        encabezado.setBackground(AZUL_PRINCIPAL);
        encabezado.setForeground(BLANCO);
        encabezado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        encabezado.setPreferredSize(new Dimension(encabezado.getPreferredSize().width, 32));
        encabezado.setReorderingAllowed(false);
        encabezado.setOpaque(true);
        encabezado.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable tabla,
                    Object valor, boolean seleccionado, boolean tieneFoco, int fila, int columna) {
                JLabel celda = (JLabel) super.getTableCellRendererComponent(
                        tabla, valor, seleccionado, tieneFoco, fila, columna);
                celda.setOpaque(true);
                celda.setBackground(AZUL_PRINCIPAL);
                celda.setForeground(BLANCO);
                celda.setFont(new Font("Segoe UI", Font.BOLD, 12));
                celda.setHorizontalAlignment(SwingConstants.CENTER);
                celda.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, AZUL_SECUNDARIO),
                        BorderFactory.createEmptyBorder(4, 3, 4, 3)));
                return celda;
            }
        });
        jScrollPane1.setBorder(BorderFactory.createLineBorder(AZUL_SECUNDARIO, 1));
        jScrollPane1.getViewport().setBackground(AZUL_CLARO);

        for (JButton boton : new JButton[]{BotonConsultarCarnet, BotonConsultarTabla,
            BotonGuardar, BotonActualizar, BotonEliminar, BotonLimpiar}) {
            estilizarBoton(boton);
        }
    }

    private void estilizarEtiqueta(JLabel etiqueta) {
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        etiqueta.setForeground(AZUL_PRINCIPAL);
    }

    private void estilizarBoton(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(BLANCO);
        boton.setBackground(AZUL_PRINCIPAL);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL_SECUNDARIO, 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        boton.setMargin(new Insets(6, 10, 6, 10));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addPropertyChangeListener("enabled", event -> {
            boton.setBackground(boton.isEnabled() ? AZUL_PRINCIPAL : GRIS_DESHABILITADO);
            boton.setForeground(boton.isEnabled() ? BLANCO : AZUL_SECUNDARIO);
        });
        boton.setBackground(boton.isEnabled() ? AZUL_PRINCIPAL : GRIS_DESHABILITADO);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        BotonConsultarCarnet = new javax.swing.JButton();
        BotonConsultarTabla = new javax.swing.JButton();
        BotonGuardar = new javax.swing.JButton();
        BotonActualizar = new javax.swing.JButton();
        BotonEliminar = new javax.swing.JButton();
        BotonLimpiar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestion Colaboradores - PEOPLE MANAGERS");

        jLabel1.setBackground(new java.awt.Color(102, 153, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("GESTION COLABORADORES:");

        jLabel2.setText("No. Colaborador:");

        jLabel3.setText("Nombre(s):");

        jLabel4.setText("Apellido(s):");

        jLabel5.setText("Edad:");

        jLabel6.setText("Fecha de Nacimiento:");

        jLabel7.setText("Género:");

        jLabel8.setText("Correo Electronico:");

        jLabel9.setText("Teléfono:");

        jLabel10.setText("No. DPI:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione su género", "Masculino", "Femenino" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField5)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField6)
                            .addComponent(jTextField7)
                            .addComponent(jTextField8))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        BotonConsultarCarnet.setText("Consultar Carnet");
        BotonConsultarCarnet.addActionListener(this::BotonConsultarCarnetActionPerformed);

        BotonConsultarTabla.setText("Consultar Tabla");
        BotonConsultarTabla.addActionListener(this::BotonConsultarTablaActionPerformed);

        BotonGuardar.setText("Guardar Datos");
        BotonGuardar.addActionListener(this::BotonGuardarActionPerformed);

        BotonActualizar.setText("Actualizar Datos");
        BotonActualizar.addActionListener(this::BotonActualizarActionPerformed);

        BotonEliminar.setText("Eliminar Datos");
        BotonEliminar.addActionListener(this::BotonEliminarActionPerformed);

        BotonLimpiar.setText("Limpiar / Nuevo");
        BotonLimpiar.addActionListener(this::BotonLimpiarActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(BotonConsultarCarnet)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonConsultarTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonLimpiar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BotonEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BotonActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BotonLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BotonConsultarCarnet, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BotonConsultarTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BotonGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No.Colaborador", "Nombre(s)", "Apellido(s)", "Edad", "Fecha de Nacimiento", "Género", "Correo Electronico", "Teléfono", "No. DPI"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(508, 508, 508)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 789, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void BotonConsultarCarnetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonConsultarCarnetActionPerformed
        String entrada = JOptionPane.showInputDialog(this, "Ingrese el número de colaborador a consultar:");
        if (entrada == null) return;
        if (entrada.isBlank()) {
            mostrarAdvertencia("Ingrese un número de colaborador para buscar.");
            return;
        }
        try {
            String carnet = validarNumeroCarnetBusqueda(entrada);
            modeloTabla.setRowCount(0);
            mostrarTabla(true);
            if (!controlador.probarConexion()) {
                mostrarError("No hay conexión con la base de datos. Verifique el servidor e inténtelo de nuevo.");
                return;
            }
            Estudiante estudiante = controlador.obtenerEstudiantePorCarnet(carnet);
            if (estudiante == null) {
                mostrarAdvertencia("Colaborador no encontrado.");
                return;
            }
            agregarFila(estudiante);
            cargarDatosEnFormulario(estudiante);
            jTable1.setRowSelectionInterval(0, 0);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }//GEN-LAST:event_BotonConsultarCarnetActionPerformed

    private void BotonConsultarTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonConsultarTablaActionPerformed
        if (!controlador.probarConexion()) {
            mostrarError("No hay conexión con la base de datos. Verifique el servidor e inténtelo de nuevo.");
            return;
        }
        cargarEstudiantes();
        mostrarTabla(true);
        limpiarCamposFormulario();
        if (modeloTabla.getRowCount() == 0) mostrarInformacion("No hay colaboradores registrados.");
    }//GEN-LAST:event_BotonConsultarTablaActionPerformed

    private void BotonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarActionPerformed
        try {
            Estudiante nuevo = leerFormulario();
            if (!controlador.probarConexion()) {
                mostrarError("No hay conexión con la base de datos. Verifique el servidor e inténtelo de nuevo.");
                return;
            }
            if (controlador.obtenerEstudiantePorCarnet(nuevo.getNoCarnetEstudiante()) != null) {
                mostrarAdvertencia("El colaborador ya existe.");
                return;
            }
            boolean agregado = controlador.agregarEstudiante(nuevo.getNoCarnetEstudiante(),
                    nuevo.getNombreEstudiante(), nuevo.getApellidoEstudiante(),
                    nuevo.getEdadEstudiante(), nuevo.getFechaNacimientoEstudiante(),
                    nuevo.getGeneroEstudiante(), nuevo.getCorreoElectronicoEstudiante(),
                    nuevo.getTelefonoEstudiante(), nuevo.getNoDpiEstudiante());
            if (!agregado) {
                mostrarError("No se pudo guardar el colaborador. Verifique que el DPI no esté registrado.");
                return;
            }
            cargarEstudiantes();
            mostrarTabla(true);
            limpiarCamposFormulario();
            mostrarInformacion("Colaborador guardado correctamente.");
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha debe tener el formato dd/MM/aaaa.");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }//GEN-LAST:event_BotonGuardarActionPerformed

    private void BotonActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonActualizarActionPerformed
        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un colaborador.");
            return;
        }
        try {
            Estudiante nuevo = leerFormulario();
            if (mismosDatos(seleccionado, nuevo)) {
                mostrarInformacion("No hay cambios para guardar.");
                return;
            }
            if (!controlador.probarConexion()) {
                mostrarError("No hay conexión con la base de datos. Verifique el servidor e inténtelo de nuevo.");
                return;
            }
            boolean actualizado = controlador.actualizarEstudiante(seleccionado.getIdEstudiante(),
                    seleccionado.getNoCarnetEstudiante(), nuevo.getNombreEstudiante(),
                    nuevo.getApellidoEstudiante(), nuevo.getEdadEstudiante(),
                    nuevo.getFechaNacimientoEstudiante(), nuevo.getGeneroEstudiante(),
                    nuevo.getCorreoElectronicoEstudiante(), nuevo.getTelefonoEstudiante(),
                    nuevo.getNoDpiEstudiante());
            if (!actualizado) {
                mostrarError("No se pudo actualizar el colaborador.");
                return;
            }
            cargarEstudiantes();
            mostrarTabla(true);
            limpiarCamposFormulario();
            mostrarInformacion("Colaborador actualizado correctamente.");
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha debe tener el formato dd/MM/aaaa.");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }//GEN-LAST:event_BotonActualizarActionPerformed

    private void BotonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEliminarActionPerformed
        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un colaborador.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Confirma que desea eliminar al colaborador " + seleccionado.getNombreEstudiante() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) return;

        if (!controlador.probarConexion()) {
            mostrarError("No hay conexión con la base de datos. Verifique el servidor e inténtelo de nuevo.");
            return;
        }
        if (!controlador.eliminarEstudiantePorCarnet(seleccionado.getNoCarnetEstudiante())) {
            mostrarError("No se pudo eliminar el colaborador.");
            return;
        }
        cargarEstudiantes();
        mostrarTabla(true);
        limpiarCamposFormulario();
        mostrarInformacion("Colaborador eliminado correctamente.");
    }//GEN-LAST:event_BotonEliminarActionPerformed

    private void BotonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonLimpiarActionPerformed
        limpiarCamposFormulario();
    }//GEN-LAST:event_BotonLimpiarActionPerformed

    private void cargarSeleccionDeTabla() {
        int fila = jTable1.getSelectedRow();
        if (fila < 0) return;
        int filaModelo = jTable1.convertRowIndexToModel(fila);
        String carnet = String.valueOf(modeloTabla.getValueAt(filaModelo, 0));
        Estudiante est = controlador.obtenerEstudiantePorCarnet(carnet);
        if (est != null) cargarDatosEnFormulario(est);
    }

    private void cargarDatosEnFormulario(Estudiante est) {
        seleccionado = est;
        jTextField1.setText(est.getNoCarnetEstudiante());
        jTextField1.setEnabled(false);
        jTextField2.setText(est.getNombreEstudiante());
        jTextField3.setText(est.getApellidoEstudiante());
        jTextField4.setText(String.valueOf(est.getEdadEstudiante()));
        jTextField5.setText(est.getFechaNacimientoEstudiante() == null ? ""
                : est.getFechaNacimientoEstudiante().format(FORMATO_FECHA));
        jComboBox1.setSelectedItem(est.getGeneroEstudiante());
        jTextField6.setText(est.getCorreoElectronicoEstudiante());
        jTextField7.setText(est.getTelefonoEstudiante() == null ? "" : est.getTelefonoEstudiante());
        jTextField8.setText(est.getNoDpiEstudiante() == null ? "" : est.getNoDpiEstudiante());
        BotonGuardar.setEnabled(false);
        BotonActualizar.setEnabled(true);
        BotonEliminar.setEnabled(true);
    }

    private void seleccionarEnTabla(Estudiante est) {
        for (int row = 0; row < modeloTabla.getRowCount(); row++) {
            if (Objects.equals(modeloTabla.getValueAt(row, 0), est.getNoCarnetEstudiante())) {
                int viewRow = jTable1.convertRowIndexToView(row);
                jTable1.setRowSelectionInterval(viewRow, viewRow);
                return;
            }
        }
    }

    private void limpiarCamposFormulario() {
        jTable1.clearSelection();
        seleccionado = null;
        jTextField1.setEnabled(true);
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jComboBox1.setSelectedIndex(0);
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField8.setText("");
        BotonGuardar.setEnabled(true);
        BotonActualizar.setEnabled(false);
        BotonEliminar.setEnabled(false);
        jTextField1.requestFocusInWindow();
    }

    private Estudiante leerFormulario() {
        String edadTexto = jTextField4.getText().trim();
        if (edadTexto.isEmpty()) throw new IllegalArgumentException("La edad es obligatoria.");
        if (!edadTexto.matches("[0-9]{1,3}")) {
            throw new IllegalArgumentException("La edad debe contener solo números.");
        }
        String fechaTexto = jTextField5.getText().trim();
        if (fechaTexto.isEmpty()) throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        return new Estudiante(jTextField1.getText(), jTextField2.getText(),
                jTextField3.getText(), Integer.parseInt(edadTexto), LocalDate.parse(fechaTexto, FORMATO_FECHA),
                String.valueOf(jComboBox1.getSelectedItem()), jTextField6.getText(),
                jTextField7.getText(), jTextField8.getText());
    }

    private boolean mismosDatos(Estudiante actual, Estudiante nuevo) {
        return Objects.equals(actual.getNombreEstudiante(), nuevo.getNombreEstudiante())
                && Objects.equals(actual.getApellidoEstudiante(), nuevo.getApellidoEstudiante())
                && actual.getEdadEstudiante() == nuevo.getEdadEstudiante()
                && Objects.equals(actual.getFechaNacimientoEstudiante(), nuevo.getFechaNacimientoEstudiante())
                && Objects.equals(actual.getGeneroEstudiante(), nuevo.getGeneroEstudiante())
                && Objects.equals(actual.getCorreoElectronicoEstudiante(), nuevo.getCorreoElectronicoEstudiante())
                && Objects.equals(normalizarOpcional(actual.getTelefonoEstudiante()), normalizarOpcional(nuevo.getTelefonoEstudiante()))
                && Objects.equals(normalizarOpcional(actual.getNoDpiEstudiante()), normalizarOpcional(nuevo.getNoDpiEstudiante()));
    }

    private String normalizarOpcional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void cargarEstudiantes() {
        modeloTabla.setRowCount(0);
        List<Estudiante> estudiantes = controlador.obtenerEstudiantes();
        for (Estudiante est : estudiantes) {
            agregarFila(est);
        }
    }

    private void agregarFila(Estudiante est) {
        modeloTabla.addRow(new Object[]{est.getNoCarnetEstudiante(), est.getNombreEstudiante(),
            est.getApellidoEstudiante(), est.getEdadEstudiante(), est.getFechaNacimientoEstudiante() == null
                    ? "" : est.getFechaNacimientoEstudiante().format(FORMATO_FECHA),
            est.getGeneroEstudiante(), est.getCorreoElectronicoEstudiante(),
            est.getTelefonoEstudiante(), est.getNoDpiEstudiante()});
    }

    private void mostrarTabla(boolean visible) {
        // Conserva la distribución original; las consultas solo cambian sus filas.
        jScrollPane1.setVisible(true);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarInformacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void instalarRestriccionesDeEntrada() {
        instalarFiltro(jTextField1, 50, null);
        instalarFiltro(jTextField2, 100, null);
        instalarFiltro(jTextField3, 100, null);
        instalarFiltro(jTextField4, 3, "[0-9]*");
        instalarFiltro(jTextField5, 10, "[0-9/]*");
        instalarFiltro(jTextField6, 150, null);
        instalarFiltro(jTextField7, 20, "[0-9+() .-]*");
        instalarFiltro(jTextField8, 13, "[0-9]*");
    }

    static void instalarFiltro(javax.swing.JTextField campo, int maximo, String caracteresPermitidos) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            private boolean permite(String texto) {
                return texto.length() <= maximo
                        && (caracteresPermitidos == null || texto.matches(caracteresPermitidos));
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs)
                    throws BadLocationException {
                if (texto == null) return;
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, offset) + texto + actual.substring(offset);
                if (permite(nuevo)) super.insertString(fb, offset, texto, attrs);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs)
                    throws BadLocationException {
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String reemplazo = texto == null ? "" : texto;
                String nuevo = actual.substring(0, offset) + reemplazo + actual.substring(offset + length);
                if (permite(nuevo)) super.replace(fb, offset, length, texto, attrs);
            }
        });
    }

    static String validarNumeroCarnetBusqueda(String entrada) {
        Estudiante.validarCarnet(entrada);
        String carnet = entrada.trim();
        if (!carnet.matches("[0-9]+")) {
            throw new IllegalArgumentException("Formato de carnet inválido: ingrese solo números.");
        }
        return carnet;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmEstudiante().setVisible(true));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonActualizar;
    private javax.swing.JButton BotonConsultarCarnet;
    private javax.swing.JButton BotonConsultarTabla;
    private javax.swing.JButton BotonEliminar;
    private javax.swing.JButton BotonGuardar;
    private javax.swing.JButton BotonLimpiar;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
