package view;

import controller.AsignacionController;
import controller.AsignacionView;
import dao.AsignacionDao;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Asignacion;
import java.util.List;

//IMPORTACIONES PDF
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfContentByte;
import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.Chunk;
import java.text.ParseException;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;

public class FrmAsignacion extends javax.swing.JFrame implements AsignacionView {
    //CREAR VARIABLES GLOBALES
    //INSTANCIAMOS EL CONTROLADOR
    private final AsignacionController controlador;
    //INICIALIZAR EL MODELO DE LA TABLA DEFINIENDO CABECERAS
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
    new Object[]{"No Carnet", "Id Curso", "Calificacion", "Fecha Aprobacion"},0);
    private String noCarnetOriginal, idCursoOriginal;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmAsignacion.class.getName());

    /**
     * Creates new form FrmEstudiante
     */
    public FrmAsignacion() {
        initComponents();
        // Configura para que solo se cierre esta ventana y no afecte el menú principal
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        controlador = new AsignacionController(new AsignacionDao(), this);
        //ASIGNA EL MODELO CON LAS CABECERAS PREDEFINIDAS
        jtAsignacion.setModel(modeloTabla);
        
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnActualizar.setText("Actualizar");
        btnActualizar.setToolTipText("Seleccione una asignación de la tabla para habilitar la actualización.");
        lblIndicacion.setVisible(true);

        jtAsignacion.getSelectionModel().addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) {
            return;
        }

        int filaVista = jtAsignacion.getSelectedRow();
            if (filaVista < 0) {
                noCarnetOriginal = null;
                idCursoOriginal = null;
                txtNoCarnet.setEditable(true);
                btnGuardar.setEnabled(true);
                btnActualizar.setEnabled(false);
                btnEliminar.setEnabled(false);
                lblIndicacion.setVisible(true);
                return;
            }

            int fila = jtAsignacion.convertRowIndexToModel(filaVista);

            noCarnetOriginal = String.valueOf(modeloTabla.getValueAt(fila, 0));
            idCursoOriginal = String.valueOf(modeloTabla.getValueAt(fila, 1));

            txtNoCarnet.setText(noCarnetOriginal);
            txtIdCurso.setText(idCursoOriginal);
            txtCalificacion.setText(String.valueOf(modeloTabla.getValueAt(fila, 2)));
            txtFechaAprobacion.setText(String.valueOf(modeloTabla.getValueAt(fila, 3)));
            txtNoCarnet.setEditable(false);

            btnGuardar.setEnabled(false);
            btnActualizar.setEnabled(true);
            btnEliminar.setEnabled(true);
            btnActualizar.setText("Actualizar");
            lblIndicacion.setVisible(false);
    });
        configuarCampoNumerico(txtNoCarnet, 9);
        configuarCampoNumerico(txtIdCurso, 3);
        configuarCampoNumerico(txtCalificacion, 3);
        aplicarEstiloPeopleManagers();
    }
    public void cargarAsignaciones (){
        try {
            List<Asignacion> asignaciones = controlador.obtenerTodos();
            // Solo vacía la tabla cuando la consulta terminó correctamente.
            modeloTabla.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (Asignacion asig : asignaciones) {
                modeloTabla.addRow(new Object[]{
                    asig.getNoCarnet(),
                    asig.getIdCurso(),
                    asig.getCalificacion(),
                    asig.getFechaAprobacion() != null ? sdf.format(asig.getFechaAprobacion()) : ""
                });
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    //LIMPA EL CONTENIDO DE LAS CAJAS DE TEXTO
    public void limpiar() {
        txtNoCarnet.setText("");
        txtIdCurso.setText("");
        txtCalificacion.setText("");
        txtFechaAprobacion.setText("");
        noCarnetOriginal = null;
        idCursoOriginal = null;
        txtNoCarnet.setEditable(true);
        jtAsignacion.clearSelection();
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        lblIndicacion.setVisible(true);
        txtNoCarnet.requestFocusInWindow();
    }
    private void configuarCampoNumerico (javax.swing.JTextField campo, int longitudMaxima){
        AbstractDocument documento = (AbstractDocument) campo.getDocument();
        documento.setDocumentFilter(new DocumentFilter() {

            private boolean esEntradaValida(FilterBypass fb, int offset, int longitudAReemplazar, String textoNuevo) throws BadLocationException {
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String insercion = textoNuevo == null ? "" : textoNuevo;
                String resultado = actual.substring(0, offset)
                        + insercion
                        + actual.substring(offset + longitudAReemplazar);
                return resultado.length() <= longitudMaxima && resultado.matches("[0-9]*");
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet atributos) throws BadLocationException {
                replace(fb, offset, 0, texto, atributos);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet atributos) throws BadLocationException {
                if (esEntradaValida(fb, offset, longitud, texto)) {
                    fb.replace(offset, longitud, texto, atributos);
                }
            }
        });
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtIdCurso = new javax.swing.JTextField();
        txtCalificacion = new javax.swing.JTextField();
        txtFechaAprobacion = new javax.swing.JTextField();
        txtNoCarnet = new javax.swing.JTextField();
        btnConsultar = new javax.swing.JButton();
        btnConsultarCarnet = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtAsignacion = new javax.swing.JTable();
        btnGenerarCertificacion = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnGenerarDiploma = new javax.swing.JButton();
        lblIndicacion = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Certificación y Calificaciones - PEOPLE MANAGERS");
        setBackground(new java.awt.Color(230, 244, 248));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(11, 45, 91));
        jLabel1.setText("No.Carnet");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(11, 45, 91));
        jLabel2.setText("Fecha de Aprobación");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(11, 45, 91));
        jLabel3.setText("Id Curso");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(11, 45, 91));
        jLabel4.setText("Calificación");

        txtIdCurso.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        txtCalificacion.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        txtFechaAprobacion.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        txtNoCarnet.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        btnConsultar.setBackground(new java.awt.Color(11, 45, 91));
        btnConsultar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnConsultar.setForeground(new java.awt.Color(255, 255, 255));
        btnConsultar.setText("Consultar");
        btnConsultar.addActionListener(this::btnConsultar);

        btnConsultarCarnet.setBackground(new java.awt.Color(11, 45, 91));
        btnConsultarCarnet.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnConsultarCarnet.setForeground(new java.awt.Color(255, 255, 255));
        btnConsultarCarnet.setText("Consultar Carnet");
        btnConsultarCarnet.addActionListener(this::btnConsultarNoCarnet);

        btnGuardar.setBackground(new java.awt.Color(11, 45, 91));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardar);

        btnActualizar.setBackground(new java.awt.Color(209, 217, 230));
        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(102, 102, 102));
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(this::btnActualizar);

        btnEliminar.setBackground(new java.awt.Color(209, 217, 230));
        btnEliminar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(102, 102, 102));
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminar);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        jtAsignacion.setGridColor(new java.awt.Color(209, 217, 230));
        jtAsignacion.setRowHeight(26);
        jtAsignacion.setShowGrid(true);
        jtAsignacion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "No Carnet", "Id Curso", "Calificacion", "Fecha Aprobacion"
            }
        ));
        jScrollPane1.setViewportView(jtAsignacion);

        btnGenerarCertificacion.setBackground(new java.awt.Color(31, 78, 121));
        btnGenerarCertificacion.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnGenerarCertificacion.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerarCertificacion.setText("Generar Certificación");
        btnGenerarCertificacion.addActionListener(this::btnGenerarCertificacionGenerarCertificacion);

        btnLimpiar.setBackground(new java.awt.Color(31, 78, 121));
        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiar);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(11, 45, 91));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("GESTIÓN DE CERTIFICACIÓN");
        jLabel5.setOpaque(true);

        btnGenerarDiploma.setBackground(new java.awt.Color(0, 102, 102));
        btnGenerarDiploma.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnGenerarDiploma.setForeground(new java.awt.Color(11, 45, 91));
        btnGenerarDiploma.setText("Generar Diploma");
        btnGenerarDiploma.addActionListener(this::btnGenerarDiploma);

        lblIndicacion.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblIndicacion.setForeground(new java.awt.Color(90, 90, 90));
        lblIndicacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIndicacion.setText("Seleccione una fila para actualizar o eliminar.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIdCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCalificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFechaAprobacion, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNoCarnet, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(144, 144, 144))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGenerarDiploma, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(btnGenerarCertificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(lblIndicacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(btnConsultarCarnet, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(31, 31, 31))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNoCarnet, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtIdCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCalificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtFechaAprobacion, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConsultarCarnet, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(lblIndicacion)
                .addGap(13, 13, 13)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGenerarCertificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerarDiploma, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //CONSULTAR
    private void btnConsultar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultar
        cargarAsignaciones();
    }//GEN-LAST:event_btnConsultar
    //CONSULTARO O BUSCAR POR CARNET
    private void btnConsultarNoCarnet(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarNoCarnet
        //SOLICITA AL USUARIO EL ID A CONSULTAR
        String noCarnet = JOptionPane.showInputDialog("Ingresar el Carnet a buscar");

        //SI PRESIONA CANCELAR O CIERRA EL DIÁLOGO NO BUSCAMOS
        if (noCarnet == null) {
            return;
        }
        noCarnet = noCarnet.trim();
        //SI ACEPTA EL DIÁLOGO CON ESPACIOS VACÍOS SE LE AVISA
        if (noCarnet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un carnet para buscar", "Carnet Vacío", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (noCarnet != null && !noCarnet.trim().isEmpty()) {
            try {
                //SOLICITA EL OBJETO ASIGNACIÓN AL CONTROLADOR USANOD EL CARNET INGRESADO
                List<Asignacion> listaAsignaciones = controlador.buscarPorCarnet(noCarnet.trim());
                modeloTabla.setRowCount(0);

                if (listaAsignaciones != null && !listaAsignaciones.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    for (Asignacion asig : listaAsignaciones) {
                        modeloTabla.addRow(new Object[]{
                            asig.getNoCarnet(),
                            asig.getIdCurso(),
                            asig.getCalificacion(),
                            asig.getFechaAprobacion() != null ? sdf.format(asig.getFechaAprobacion()) : ""
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Asignación Inexistente");
                    limpiar();
                }

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Carnet Invalido", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error al consultar", "Error de Consulta", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnConsultarNoCarnet
    //GUARDAR
    private void btnGuardar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardar
        //EVITAR CAMPOS VACÍOS
        if (txtNoCarnet.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el carnet.", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNoCarnet.requestFocusInWindow();
            return;
        } else if (txtIdCurso.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el curso.", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
            txtIdCurso.requestFocusInWindow();
            return;
        } else if (txtCalificacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar la calificación.", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
            txtCalificacion.requestFocusInWindow();
            return;
        } else if (txtFechaAprobacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar la fecha de aprobación.", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
            txtFechaAprobacion.requestFocusInWindow();
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            Date fechaAprobacion = sdf.parse(txtFechaAprobacion.getText().trim());
            if (fechaAprobacion.after(new Date())) {
                throw new IllegalArgumentException("La fecha de aprobación no puede ser futura");
            }

            //ENVIA LOS DATOS AL CONTROLADOR
            boolean guardado = controlador.guardarAsignacion(
                    txtNoCarnet.getText().trim(),
                    txtIdCurso.getText().trim(),
                    Integer.parseInt(txtCalificacion.getText().trim()),
                    fechaAprobacion);

            if (guardado) {
                JOptionPane.showMessageDialog(this, "Asignación insertada correctamente");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar la asignación", "Error Al Guardar", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La calificación debe ser un número entero", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
            txtCalificacion.requestFocusInWindow(); //PONER EL CURSOR SOBRE ESE CAMPO
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Datos Inválidos", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe ser válida y tener formato dd-MM-yyyy", "Fecha Inválida", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de base de Datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la asignación revise los datos", "Error Al Guardar", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardar
    //ACTUALIZAR
    private void btnActualizar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizar
        if (jtAsignacion.getSelectedRow() < 0
                || noCarnetOriginal == null
                || idCursoOriginal == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una asignación de la tabla antes de actualizar.",
                    "Seleccione una asignación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {


            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            Date fechaAprobacion = sdf.parse(txtFechaAprobacion.getText().trim());
            if (fechaAprobacion.after(new Date())) {
                throw new IllegalArgumentException("La fecha de aprobación no puede ser futura");
            }

            boolean actualizado = controlador.actualizarAsignacion(
                    noCarnetOriginal,
                    idCursoOriginal,
                    txtIdCurso.getText().trim(),
                    Integer.parseInt(txtCalificacion.getText().trim()),
                    fechaAprobacion
            );

            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Actualizado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró esa asignación.", "Asignación inexistente", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La calificación debe ser un número entero.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
            txtCalificacion.requestFocusInWindow();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "La fecha debe ser válida y tener el formato dd-MM-yyyy.", "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            txtFechaAprobacion.requestFocusInWindow();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Datos inválidos", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo realizar la actualización.", "Error al actualizar", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnActualizar
    //ELIMINAR
    private void btnEliminar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar
        String noCarnet = JOptionPane.showInputDialog("Ingrese el No.Carnet a Eliminar");
        if (noCarnet == null) {
            return;
        }
        noCarnet = noCarnet.trim();
        if (noCarnet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el carnet a eliminar", "Carnet Requerido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, "Está seguro de eliminar las asignaciones del carnet" + noCarnet + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean eliminado = controlador.eliminarAsignacion(noCarnet);
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Eliminado Exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "Asignación Inexistente", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEliminar

    private void btnGenerarCertificacionGenerarCertificacion(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarCertificacionGenerarCertificacion
        String noCarnet = JOptionPane.showInputDialog(this, "Ingrese el No Cartnet para generar la certificación");
        if (noCarnet != null && !noCarnet.trim().isEmpty()) {
            try {
                //CONSULTAR LAS ASIGNACIONES MEDIANTE EL CONTROLADOR
                List<Asignacion> listaAsignaciones = controlador.buscarPorCarnet(noCarnet.trim());
                
                if (listaAsignaciones == null || listaAsignaciones.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No se encontraron registros para el carnet");
                    return;
                }
                estudiante.dao.EstudianteDao estudianteDao = new estudiante.dao.EstudianteDao();
                estudiante.model.Estudiante est = estudianteDao.obtenerPorCarnet(noCarnet.trim());
                
                String nombreCompleto = "Estudiante No Registrado";
                String carnetMostrado = noCarnet.trim();
                
                if (est != null) {
                    nombreCompleto = est.getNombreEstudiante() + " " + est.getApellidoEstudiante();
                    carnetMostrado = est.getNoCarnetEstudiante();
                }
                
                cursos.dao.CursoDao cursoDao = new cursos.dao.CursoDao();
                
                //CONFIGURAR LA RUTA Y CREAR EL DOCUMENTO PDF
                String rutaArchivo = "Certificacion_" + noCarnet.trim() + ".pdf";
                
                Document documento = new Document(PageSize.LETTER);
                PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));
                documento.open();

                //FUENTES
                Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
                Font fuenteTitulo2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
                Font fuenteSub = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
                Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
                Font fuenteTabla = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

                //FUENTES PARA TABLA
                Font fuenteTabla1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(90, 90, 90));
                Font fuenteTabla2 = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
                
                //ENCABEZADO SE HACE UNA TABLA PARA SU POSICIÓN
                PdfPTable encabezado = new PdfPTable(2);
                encabezado.setWidthPercentage(100);
                encabezado.setWidths(new float[]{1.5f, 8.5f});
                
                //LOGOTIPO
                PdfPCell celdaLogo = new PdfPCell();
                celdaLogo.setBorder(PdfPCell.NO_BORDER);
                try {
                    Image logo = Image.getInstance("logo.png");
                    logo.scaleToFit(100, 100); //Ancho y alto máximo
                    celdaLogo.addElement(logo);
                } catch (Exception e){
                    System.out.println("No se pudo cargar la imagen");
                }
                celdaLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                encabezado.addCell(celdaLogo);
                
                PdfPCell celdaTitulo = new PdfPCell();
                celdaTitulo.setBorder(PdfPCell.NO_BORDER);
                celdaTitulo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                
                //ENCABEZADO
                Paragraph titulo = new Paragraph("CERTIFICACIÓN DE CURSOS", fuenteTitulo);
                titulo.setAlignment(Element.ALIGN_RIGHT);
                Paragraph subtitulo = new Paragraph("Sistema Empresarial", fuenteSub);
                subtitulo.setAlignment(Element.ALIGN_RIGHT);
                Paragraph subtitulo2 = new Paragraph("Empresa People Managers", fuenteSub);
                subtitulo2.setAlignment(Element.ALIGN_RIGHT);
                Paragraph subtitulo3 = new Paragraph("FACULTAD DE INGENIERÍA", fuenteTitulo2);
                subtitulo3.setAlignment(Element.ALIGN_RIGHT);
                Paragraph subtitulo4 = new Paragraph("Carrera De Ingeniería En Sistemas", fuenteSub);
                subtitulo4.setAlignment(Element.ALIGN_RIGHT);
                
                celdaTitulo.addElement(titulo);
                celdaTitulo.addElement(subtitulo);
                celdaTitulo.addElement(subtitulo2);
                celdaTitulo.addElement(subtitulo3);
                celdaTitulo.addElement(subtitulo4);
                encabezado.addCell(celdaTitulo);
                
                documento.add(encabezado);
                documento.add(new Paragraph(" "));
                
                //SEPARADOR
                LineSeparator linea = new LineSeparator();
                linea.setLineColor(new java.awt.Color(80, 80, 80));
                linea.setPercentage(100); //OCUPA TODO EL ANCHO
                documento.add(new Chunk(linea));
                documento.add(new Paragraph(" "));

                //INFORMACIÓN DEL ESTUDIANTE
                SimpleDateFormat sdfFormato = new SimpleDateFormat("dd/MM/yyyy");
                String fechaActual = sdfFormato.format(new Date());
                
                PdfPTable tablaInfo = new PdfPTable(2);
                tablaInfo.setWidthPercentage(100);
                tablaInfo.setWidths(new float[]{50f, 50f});
                
                //COLORES DE FONDO
                Color fondo = new Color(245, 245, 245);
                
                //FILA 1 TÍTULOS
                PdfPCell c1 = new PdfPCell(new Paragraph("Nombre del Colaborador:", fuenteTabla1));
                c1.setBackgroundColor(fondo);
                c1.setPadding(6f);
                tablaInfo.addCell(c1);
                
                PdfPCell c2 = new PdfPCell(new Paragraph("Fecha de Emisión:", fuenteTabla1));
                c2.setBackgroundColor(fondo);
                c2.setPadding(6f);
                tablaInfo.addCell(c2);
                
                //FILA 2 VALORES
                PdfPCell c3 = new PdfPCell(new Paragraph(nombreCompleto, fuenteTabla2));
                c3.setBackgroundColor(Color.WHITE);
                c3.setPaddingBottom(8f);
                c3.setPaddingLeft(6f);
                tablaInfo.addCell(c3);
                
                PdfPCell c4 = new PdfPCell(new Paragraph(fechaActual, fuenteTabla2));
                c4.setBackgroundColor(Color.white);
                c4.setPaddingBottom(8f);
                c4.setPaddingLeft(6f);
                tablaInfo.addCell(c4);
                
                //FILA 3 TÍTULOS
                PdfPCell c5 = new PdfPCell(new Paragraph("No. De Colaborador", fuenteTabla1));
                c5.setBackgroundColor(fondo);
                c5.setPadding(6f);
                tablaInfo.addCell(c5);
                
                PdfPCell c6 = new PdfPCell(new Paragraph("Ciclo", fuenteTabla1));
                c6.setBackgroundColor(fondo);
                c6.setPadding(6f);
                tablaInfo.addCell(c6);
                
                //FILA 4 VALORES
                PdfPCell c7 = new PdfPCell(new Paragraph(carnetMostrado, fuenteTabla2));
                c7.setBackgroundColor(Color.WHITE);
                c7.setPaddingBottom(8f);
                c7.setPaddingLeft(6f);
                tablaInfo.addCell(c7);
                
                PdfPCell c8 = new PdfPCell(new Paragraph("Semestre II", fuenteTabla2));
                c8.setBackgroundColor(Color.WHITE);
                c8.setPaddingBottom(8f);
                c8.setPaddingLeft(6f);
                tablaInfo.addCell(c8);

                documento.add(tablaInfo);
                documento.add(new Paragraph(" "));
                
                //TABLA DE CURSOS CON CONEXIÓN
                PdfPTable tabla = new PdfPTable(4);
                tabla.setWidthPercentage(100);
                tabla.setWidths(new float[]{2f, 3f, 2f, 3f});
                
                String[] cabeceras = {"ID Curso", "Nombre del Curso", "Calificación", "Fecha Aprobación"};
                for (String cabecera : cabeceras) {
                    PdfPCell celda = new PdfPCell(new Paragraph(cabecera, fuenteTabla));
                    celda.setBackgroundColor(new Color(0, 139, 139));
                    celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celda.setPadding(6);
                    tabla.addCell(celda);
                }
                
                SimpleDateFormat sdfAsig = new SimpleDateFormat("dd-MM-yyyy");
                for (Asignacion asig : listaAsignaciones) {
                    String nombreCurso = "Curso Desconocido";
                    try{
                        int idCursoInt = Integer.parseInt(asig.getIdCurso());
                        cursos.model.Curso cursoObj = cursoDao.obtenerId(idCursoInt);
                        if (cursoObj != null) {
                            nombreCurso = cursoObj.getNombreCurso();
                        }
                    }catch (NumberFormatException e){
                        nombreCurso = asig.getIdCurso();
                    }
                    tabla.addCell(new PdfPCell(new Paragraph(asig.getIdCurso(), fuenteNormal)));
                    tabla.addCell(new PdfPCell(new Paragraph(nombreCurso, fuenteNormal)));
                    tabla.addCell(new PdfPCell(new Paragraph(String.valueOf(asig.getCalificacion()), fuenteNormal)));
                    
                    String fechaAprob = asig.getFechaAprobacion() != null ? sdfAsig.format(asig.getFechaAprobacion()) : "";
                    tabla.addCell(new PdfPCell(new Paragraph(fechaAprob, fuenteNormal)));
                }
                documento.add(tabla);

                //FIRMA
                documento.add(new Paragraph(" "));
                Paragraph firma = new Paragraph("_______________________________________\nRegistro Académico", fuenteNormal);
                firma.setAlignment(Element.ALIGN_RIGHT);
                documento.add(firma);
                documento.close();
                JOptionPane.showMessageDialog(this, "Certificado Generado Con Éxito");
                try {
                    File archivoPdf = new File(rutaArchivo);
                    if (archivoPdf.exists()) {
                        Desktop.getDesktop().open(archivoPdf);
                    }
                } catch (Exception e) {
                }
            }catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al generar el PDF", "Error Al Generar PDF", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
        }
}
    }//GEN-LAST:event_btnGenerarCertificacionGenerarCertificacion

    private void btnLimpiar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiar
        limpiar();
    }//GEN-LAST:event_btnLimpiar

    private void btnGenerarDiploma(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarDiploma
        String noCarnet = txtNoCarnet.getText().trim();
        if (noCarnet.isEmpty()) {
            noCarnet = JOptionPane.showInputDialog(this, "Ingrese el No. Carnet para generar el diploma:");
        }
        if (noCarnet == null || noCarnet.trim().isEmpty()) {
            return;
        }
        noCarnet = noCarnet.trim();

        try {
            List<Asignacion> listaAsignaciones = controlador.buscarPorCarnet(noCarnet);
            if (listaAsignaciones == null || listaAsignaciones.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron asignaciones registradas para el carnet: " + noCarnet, "Sin Registros", JOptionPane.WARNING_MESSAGE);
                return;
            }

            estudiante.dao.EstudianteDao estudianteDao = new estudiante.dao.EstudianteDao();
            estudiante.model.Estudiante est = estudianteDao.obtenerPorCarnet(noCarnet);

            String nombreCompleto = "Colaborador No Registrado";
            String carnetMostrado = noCarnet;
            if (est != null) {
                nombreCompleto = est.getNombreEstudiante() + " " + est.getApellidoEstudiante();
                carnetMostrado = est.getNoCarnetEstudiante();
            }

            String rutaArchivo = "Diploma_" + noCarnet + ".pdf";

            // GENERACIÓN GRÁFICA EN ALTA RESOLUCIÓN DEL DIPLOMA OFICIAL (2200 x 1550 px)
            int W = 2200;
            int H = 1550;
            BufferedImage imgDiploma = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = imgDiploma.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            Color azulPrincipal = new Color(0x0B, 0x2D, 0x5B);
            Color azulSecundario = new Color(0x1F, 0x4E, 0x79);
            Color turquesa = new Color(0x5A, 0xC8, 0xC1);

            // 1. Fondo blanco
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, W, H);

            // 2. Marco exterior Azul Principal
            g2.setColor(azulPrincipal);
            g2.setStroke(new BasicStroke(10f));
            g2.drawRect(50, 50, W - 100, H - 100);

            // 3. Marco interior Turquesa
            g2.setColor(turquesa);
            g2.setStroke(new BasicStroke(4f));
            g2.drawRect(65, 65, W - 130, H - 130);

            // 4. Esquinas decorativas
            g2.setColor(azulPrincipal);
            g2.setStroke(new BasicStroke(8f));
            int cSize = 65;
            g2.drawLine(60, 60, 60 + cSize, 60);
            g2.drawLine(60, 60, 60, 60 + cSize);
            g2.drawLine(W - 60, 60, W - 60 - cSize, 60);
            g2.drawLine(W - 60, 60, W - 60, 60 + cSize);
            g2.drawLine(60, H - 60, 60 + cSize, H - 60);
            g2.drawLine(60, H - 60, 60, H - 60 - cSize);
            g2.drawLine(W - 60, H - 60, W - 60 - cSize, H - 60);
            g2.drawLine(W - 60, H - 60, W - 60, H - 60 - cSize);

            // 5. Logotipo People Managers
            try {
                File logoFile = new File("logo.png");
                if (logoFile.exists()) {
                    BufferedImage logoImg = ImageIO.read(logoFile);
                    g2.drawImage(logoImg, 140, 110, 130, 110, null);
                }
            } catch (Exception ignored) {}

            // 6. Encabezado institucional
            g2.setColor(azulPrincipal);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
            FontMetrics fmUni = g2.getFontMetrics();
            g2.drawString("UNIVERSIDAD PEOPLE MANAGERS", (W - fmUni.stringWidth("UNIVERSIDAD PEOPLE MANAGERS")) / 2, 145);

            g2.setColor(azulSecundario);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 19));
            FontMetrics fmFac = g2.getFontMetrics();
            g2.drawString("FACULTAD DE INGENIERÍA • SISTEMA ACADÉMICO", (W - fmFac.stringWidth("FACULTAD DE INGENIERÍA • SISTEMA ACADÉMICO")) / 2, 185);

            // 7. Título Principal
            g2.setColor(azulPrincipal);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 50));
            FontMetrics fmTit = g2.getFontMetrics();
            g2.drawString("DIPLOMA DE RECONOCIMIENTO", (W - fmTit.stringWidth("DIPLOMA DE RECONOCIMIENTO")) / 2, 320);

            // Línea de acento turquesa bajo el título
            g2.setColor(turquesa);
            g2.setStroke(new BasicStroke(5f));
            g2.drawLine(W / 2 - 200, 355, W / 2 + 200, 355);

            // 8. Texto introductorio
            g2.setColor(new Color(100, 100, 100));
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 24));
            FontMetrics fmIntro = g2.getFontMetrics();
            g2.drawString("Se otorga el presente reconocimiento con distinción a:", (W - fmIntro.stringWidth("Se otorga el presente reconocimiento con distinción a:")) / 2, 430);

            // 9. Nombre del Colaborador
            g2.setColor(azulPrincipal);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 54));
            FontMetrics fmNom = g2.getFontMetrics();
            g2.drawString(nombreCompleto, (W - fmNom.stringWidth(nombreCompleto)) / 2, 520);

            // 10. Carnet
            g2.setColor(azulSecundario);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
            String textoCarnet = "No. Colaborador / Carnet: " + carnetMostrado;
            FontMetrics fmCar = g2.getFontMetrics();
            g2.drawString(textoCarnet, (W - fmCar.stringWidth(textoCarnet)) / 2, 575);

            // 11. Cuerpo de Acreditación
            g2.setColor(new Color(50, 50, 50));
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 22));
            String linea1 = "Por haber completado satisfactoriamente los cursos del programa académico correspondiente al ciclo 2026,";
            String linea2 = "acreditando encontrarse al día en sus requisitos de capacitación y formación continua.";
            FontMetrics fmCuerpo = g2.getFontMetrics();
            g2.drawString(linea1, (W - fmCuerpo.stringWidth(linea1)) / 2, 670);
            g2.drawString(linea2, (W - fmCuerpo.stringWidth(linea2)) / 2, 710);

            // 12. Sello Medallón Institucional (Costado Izquierdo)
            int sealX = 350;
            int sealY = 1000;
            int sealR = 140;
            GradientPaint gp = new GradientPaint(sealX - sealR, sealY - sealR, turquesa, sealX + sealR, sealY + sealR, azulSecundario);
            g2.setPaint(gp);
            g2.fillOval(sealX - sealR, sealY - sealR, sealR * 2, sealR * 2);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(4f));
            g2.drawOval(sealX - sealR + 10, sealY - sealR + 10, (sealR - 10) * 2, (sealR - 10) * 2);

            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
            FontMetrics fmS1 = g2.getFontMetrics();
            g2.drawString("CURSOS AL DÍA", sealX - (fmS1.stringWidth("CURSOS AL DÍA") / 2), sealY - 30);

            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 36));
            FontMetrics fmS2 = g2.getFontMetrics();
            g2.drawString("2026", sealX - (fmS2.stringWidth("2026") / 2), sealY + 12);

            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
            FontMetrics fmS3 = g2.getFontMetrics();
            g2.drawString("EXCELENCIA", sealX - (fmS3.stringWidth("EXCELENCIA") / 2), sealY + 45);

            // Fecha bajo el sello
            g2.setColor(azulSecundario);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
            FontMetrics fmF1 = g2.getFontMetrics();
            g2.drawString("Guatemala, Noviembre de 2026", sealX - (fmF1.stringWidth("Guatemala, Noviembre de 2026") / 2), sealY + sealR + 40);

            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
            FontMetrics fmF2 = g2.getFontMetrics();
            g2.drawString("Registro Académico • People Managers", sealX - (fmF2.stringWidth("Registro Académico • People Managers") / 2), sealY + sealR + 70);

            // 13. Firma Única (Costado Derecho)
            int sigCenterX = W - 450;
            int sigLineY = 1150;
            try {
                File firmaFile = new File("Firma.jpg");
                if (firmaFile.exists()) {
                    BufferedImage firmaImg = ImageIO.read(firmaFile);
                    int sigW = 260;
                    int sigH = (int) (firmaImg.getHeight() * ((double) sigW / firmaImg.getWidth()));
                    g2.drawImage(firmaImg, sigCenterX - sigW / 2, sigLineY - sigH + 15, sigW, sigH, null);
                }
            } catch (Exception ignored) {}

            // Línea de firma
            g2.setColor(azulPrincipal);
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(sigCenterX - 180, sigLineY, sigCenterX + 180, sigLineY);

            // Textos bajo la firma única
            g2.setColor(azulPrincipal);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
            FontMetrics fmSig1 = g2.getFontMetrics();
            g2.drawString("Firma Autorizada", sigCenterX - (fmSig1.stringWidth("Firma Autorizada") / 2), sigLineY + 35);

            g2.setColor(azulSecundario);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
            FontMetrics fmSig2 = g2.getFontMetrics();
            g2.drawString("People Managers", sigCenterX - (fmSig2.stringWidth("People Managers") / 2), sigLineY + 65);

            g2.dispose();

            // 14. Escribir a PDF de alta resolución apaisado
            Document documento = new Document(PageSize.LETTER.rotate(), 0, 0, 0, 0);
            PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));
            documento.open();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imgDiploma, "PNG", baos);
            Image pdfImg = Image.getInstance(baos.toByteArray());
            pdfImg.scaleToFit(PageSize.LETTER.rotate().getWidth(), PageSize.LETTER.rotate().getHeight());
            pdfImg.setAlignment(Element.ALIGN_CENTER);
            documento.add(pdfImg);
            documento.close();

            JOptionPane.showMessageDialog(this, "Diploma Generado Con Éxito", "Diploma", JOptionPane.INFORMATION_MESSAGE);

            try {
                File archivoPdf = new File(rutaArchivo);
                if (archivoPdf.exists()) {
                    Desktop.getDesktop().open(archivoPdf);
                }
            } catch (Exception ignored) {}

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar el diploma: " + e.getMessage(), "Error al generar Diploma", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnGenerarDiploma

    private void aplicarEstiloPeopleManagers() {
        Color azulPrincipal = new Color(0x0B, 0x2D, 0x5B);
        Color azulSecundario = new Color(0x1F, 0x4E, 0x79);
        Color turquesa = new Color(0x5A, 0xC8, 0xC1);
        Color azulClaro = new Color(0xE6, 0xF4, 0xF8);
        Color grisMedio = new Color(0xD1, 0xD9, 0xE6);

        // Fondo de la ventana
        getContentPane().setBackground(azulClaro);

        // Banner superior
        try {
            File fLogo = new File("logo.png");
            if (fLogo.exists()) {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon("logo.png");
                java.awt.Image imgScaled = icon.getImage().getScaledInstance(45, 40, java.awt.Image.SCALE_SMOOTH);
                jLabel5.setIcon(new javax.swing.ImageIcon(imgScaled));
                jLabel5.setIconTextGap(15);
            }
        } catch (Exception ignored) {
        }

        // Etiquetas
        jLabel1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        jLabel1.setForeground(azulPrincipal);
        jLabel2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        jLabel2.setForeground(azulPrincipal);
        jLabel3.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        jLabel3.setForeground(azulPrincipal);
        jLabel4.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        jLabel4.setForeground(azulPrincipal);

        // Cajas de texto
        javax.swing.JTextField[] campos = {txtNoCarnet, txtIdCurso, txtCalificacion, txtFechaAprobacion};
        for (javax.swing.JTextField txt : campos) {
            txt.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
            txt.setBackground(Color.WHITE);
            txt.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(azulSecundario, 1),
                    new javax.swing.border.EmptyBorder(4, 8, 4, 8)
            ));
        }

        // Botones de acción principales
        btnConsultar.setBackground(azulPrincipal);
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnConsultar.setFocusPainted(false);
        btnConsultar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnConsultarCarnet.setBackground(azulPrincipal);
        btnConsultarCarnet.setForeground(Color.WHITE);
        btnConsultarCarnet.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnConsultarCarnet.setText("Consultar Carnet");
        btnConsultarCarnet.setFocusPainted(false);
        btnConsultarCarnet.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnGuardar.setBackground(azulPrincipal);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnLimpiar.setBackground(azulSecundario);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnActualizar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnActualizar.setFocusPainted(false);
        btnEliminar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnEliminar.setFocusPainted(false);

        // Tabla personalizada
        jtAsignacion.setRowHeight(26);
        jtAsignacion.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        jtAsignacion.setShowGrid(true);
        jtAsignacion.setGridColor(grisMedio);

        // Encabezado con estilo azul secundario garantizado
        jtAsignacion.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                javax.swing.JLabel l = new javax.swing.JLabel(value != null ? value.toString() : "");
                l.setBackground(azulSecundario);
                l.setForeground(Color.WHITE);
                l.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                l.setOpaque(true);
                l.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 1, azulPrincipal),
                        javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6)
                ));
                return l;
            }
        });

        // Viewport y scrollpane en blanco
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        jScrollPane1.setBackground(Color.WHITE);
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(azulSecundario, 1));

        // Filas alternas cebra
        jtAsignacion.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : azulClaro);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(195, 230, 245));
                    c.setForeground(azulPrincipal);
                }
                return c;
            }
        });

        // Botones de emisión de documentos
        btnGenerarCertificacion.setText("Generar Certificación");
        btnGenerarCertificacion.setBackground(azulSecundario);
        btnGenerarCertificacion.setForeground(Color.WHITE);
        btnGenerarCertificacion.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnGenerarCertificacion.setFocusPainted(false);
        btnGenerarCertificacion.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(azulPrincipal, 1, true),
                new javax.swing.border.EmptyBorder(8, 16, 8, 16)
        ));
        btnGenerarCertificacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnGenerarDiploma.setText("Generar Diploma");
        btnGenerarDiploma.setBackground(azulSecundario);
        btnGenerarDiploma.setForeground(Color.WHITE);
        btnGenerarDiploma.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnGenerarDiploma.setFocusPainted(false);
        btnGenerarDiploma.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(azulPrincipal, 2, true),
                new javax.swing.border.EmptyBorder(8, 18, 8, 18)
        ));
        btnGenerarDiploma.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmAsignacion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnConsultarCarnet;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGenerarCertificacion;
    private javax.swing.JButton btnGenerarDiploma;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblIndicacion;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtAsignacion;
    private javax.swing.JTextField txtCalificacion;
    private javax.swing.JTextField txtFechaAprobacion;
    private javax.swing.JTextField txtIdCurso;
    private javax.swing.JTextField txtNoCarnet;
    // End of variables declaration//GEN-END:variables
}
