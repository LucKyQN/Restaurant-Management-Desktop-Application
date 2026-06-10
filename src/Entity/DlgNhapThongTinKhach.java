package Entity;

import DAO.KhachHangDAO;
import GUI.ModernButton;
import GUI.UIConstants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;


public class DlgNhapThongTinKhach extends JDialog {

    private JTextField txtTen, txtSDT;
    private JSpinner   spnSoNguoi;
    private JButton    btnXacNhan;
    private boolean    isSuccess = false;

    private final KhachHangDAO khachHangDAO    = new KhachHangDAO();
    private boolean            tenDuocTuDongDien = false;

    public DlgNhapThongTinKhach(JFrame parent) {
        super(parent, "Thông tin khách", true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // transparent window bg

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow layers
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 6, 20, 20);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 18, 18);

                // Card body
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(32, 36, 32, 36));


        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(new EmptyBorder(0, 0, 24, 0));

        JPanel titleLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        titleLeft.setOpaque(false);
        JLabel lblTitle = new JLabel("Nhập thông tin khách");
        lblTitle.setFont(UIConstants.FONT_BOLD_16);
        lblTitle.setForeground(UIConstants.TEXT_DARK);
        JLabel lblSubtitle = new JLabel("Điền thông tin để mở bàn phục vụ");
        lblSubtitle.setFont(UIConstants.FONT_PLAIN_12);
        lblSubtitle.setForeground(UIConstants.TEXT_GRAY);
        titleLeft.add(lblTitle);
        titleLeft.add(lblSubtitle);

        JButton btnClose = new JButton("X") {
            private boolean h = false;
            {
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setForeground(UIConstants.TEXT_GRAY);
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { h = true;  setForeground(UIConstants.PRIMARY); repaint(); }
                    @Override public void mouseExited (MouseEvent e) { h = false; setForeground(UIConstants.TEXT_GRAY); repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                if (h) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(UIConstants.PRIMARY_TINT);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btnClose.setPreferredSize(new Dimension(28, 28));
        btnClose.addActionListener(e -> dispose());

        titleBar.add(titleLeft, BorderLayout.CENTER);
        titleBar.add(btnClose,  BorderLayout.EAST);


        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Name field
        form.add(buildFieldLabel("Tên khách"));
        form.add(Box.createVerticalStrut(6));
        txtTen = buildTextField("Nhập tên khách");
        form.add(txtTen);

        form.add(Box.createVerticalStrut(16));

        // Phone field
        form.add(buildFieldLabel("Số điện thoại"));
        form.add(Box.createVerticalStrut(6));
        txtSDT = buildTextField("0xxxxxxxxx");
        form.add(txtSDT);

        form.add(Box.createVerticalStrut(16));

        // Guest count
        form.add(buildFieldLabel("Số người"));
        form.add(Box.createVerticalStrut(6));
        JFormattedTextField txtSoNguoi = buildNumberField("1", 1, 20);
        spnSoNguoi = new JSpinner() {
            @Override public Object getValue() { 
                try { return Integer.parseInt(txtSoNguoi.getText()); } 
                catch (Exception e) { return 1; } 
            }
            @Override public void setValue(Object value) { 
                txtSoNguoi.setText(value.toString()); 
            }
        };
        form.add(txtSoNguoi);

        form.add(Box.createVerticalStrut(28));

        // Confirm button
        btnXacNhan = new ModernButton("Xác nhận mở bàn", ModernButton.Style.PRIMARY);
        btnXacNhan.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        btnXacNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnXacNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(btnXacNhan);


        root.add(titleBar, BorderLayout.NORTH);
        root.add(form,     BorderLayout.CENTER);

        setContentPane(root);
        setSize(420, 370);
        setLocationRelativeTo(parent);

        txtSDT.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { tuDongDienTenTheoSDT(); }
            @Override public void removeUpdate(DocumentEvent e)  { tuDongDienTenTheoSDT(); }
            @Override public void changedUpdate(DocumentEvent e) { tuDongDienTenTheoSDT(); }
        });

        btnXacNhan.addActionListener(e -> {
            if (txtTen.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách!");
                return;
            }
            isSuccess = true;
            dispose();
        });
    }



    private JLabel buildFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_BOLD_13);
        lbl.setForeground(UIConstants.TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField buildTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(UIConstants.TEXT_PLACEHOLDER);
                    g2.setFont(UIConstants.FONT_ITALIC_13);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, 10, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g2.dispose();
                }
            }
        };
        field.setFont(UIConstants.FONT_PLAIN_14);
        field.setForeground(UIConstants.TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIConstants.BORDER_INPUT, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Focus border highlight
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UIConstants.BORDER_FOCUS, 1, true),
                        new EmptyBorder(0, 10, 0, 10)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UIConstants.BORDER_INPUT, 1, true),
                        new EmptyBorder(0, 10, 0, 10)));
                field.repaint(); // repaint placeholder
            }
        });
        return field;
    }

    private JFormattedTextField buildNumberField(String value, int min, int max) {
        JFormattedTextField field = new JFormattedTextField() {
            @Override
            public void setValue(Object value) {
                if (value instanceof Number n) {
                    int num = n.intValue();
                    num = Math.max(min, Math.min(max, num));
                    super.setValue(num);
                } else {
                    super.setValue(value);
                }
            }
        };
        try {
            java.text.NumberFormat fmt = java.text.NumberFormat.getInstance();
            fmt.setGroupingUsed(false);
            field.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.NumberFormatter(fmt)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        field.setText(value);
        field.setFont(UIConstants.FONT_PLAIN_14);
        field.setForeground(UIConstants.TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIConstants.BORDER_INPUT, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setHorizontalAlignment(JTextField.CENTER);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UIConstants.BORDER_FOCUS, 1, true),
                        new EmptyBorder(0, 10, 0, 10)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UIConstants.BORDER_INPUT, 1, true),
                        new EmptyBorder(0, 10, 0, 10)));
                try {
                    int num = Integer.parseInt(field.getText().replaceAll("[^0-9]", ""));
                    num = Math.max(min, Math.min(max, num));
                    field.setText(String.valueOf(num));
                } catch (Exception ex) {
                    field.setText(String.valueOf(min));
                }
            }
        });
        return field;
    }



    private void tuDongDienTenTheoSDT() {
        try {
            String sdt = txtSDT.getText().trim().replaceAll("[^0-9]", "");
            if (sdt.length() < 10) {
                if (tenDuocTuDongDien) {
                    txtTen.setText("");
                    tenDuocTuDongDien = false;
                }
                return;
            }
            String tenKhach = khachHangDAO.timTenKhachTheoSDT(sdt);
            if (tenKhach != null && !tenKhach.trim().isEmpty()) {
                txtTen.setText(tenKhach);
                tenDuocTuDongDien = true;
            } else {
                if (tenDuocTuDongDien) {
                    txtTen.setText("");
                    tenDuocTuDongDien = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String  getTen()       { return txtTen.getText().trim(); }
    public String  getSDT()       { return txtSDT.getText().trim(); }
    public int     getSoNguoi()   { return (int) spnSoNguoi.getValue(); }
    public boolean isConfirmed()  { return isSuccess; }
}
