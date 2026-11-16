package cursos.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class PanelFondo extends JPanel {

    public PanelFondo() {
        setOpaque(true);
        setBackground(Estilo.FONDO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Estilo.FONDO);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
