package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import DAO.NhanVienDAO;
import Entity.NhanVien;
import connectDatabase.ConnectDB;
import Entity.LuuLog;
import Model.NhanVienModel;
import DAO.NhatKyDangNhapDAO;

public class FrmDangNhap extends JFrame {

    private PlaceholderTextField txtUsername;
    private PlaceholderPasswordField txtPassword;
    private JButton btnDangNhap;

    private static final String BG_IMAGE_PATH = "src/image/unnamed.jpg";

    public FrmDangNhap() {
        initUI();
    }

    private void initUI() {
        setTitle("Hệ Thống Quản Lý Nhà Hàng Ngói Đỏ");
        setSize(1500, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(createLeftPanel());
        mainPanel.add(createRightPanel());
        setContentPane(mainPanel);
    }

    private JPanel createLeftPanel() {
        BackgroundImagePanel leftPanel = new BackgroundImagePanel(BG_IMAGE_PATH);
        leftPanel.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(0, 35, 35, 35));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Nhà Hàng Ngói Đỏ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Hương vị truyền thống, không gian hiện đại");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        lblSub.setForeground(Color.WHITE);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(lblTitle);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblSub);

        leftPanel.add(infoPanel, BorderLayout.SOUTH);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(245, 245, 245));

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(20, 80, 20, 80));

        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(createLogoPanel());
        centerPanel.add(Box.createVerticalStrut(8));

        JLabel lblSystem = new JLabel("Hệ Thống Quản Lý Nhà Hàng Ngói Đỏ");
        lblSystem.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSystem.setForeground(Color.BLACK);
        lblSystem.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblSub.setForeground(new Color(90, 90, 90));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(lblSystem);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(lblSub);
        centerPanel.add(Box.createVerticalStrut(40));


        JPanel pnlUserLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlUserLabel.setOpaque(false);
        pnlUserLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlUserLabel.add(lblUser);

        JPanel userField = createInputContainer("👤", txtUsername = new PlaceholderTextField("Nhập tên đăng nhập"));


        JPanel pnlPassLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlPassLabel.setOpaque(false);
        pnlPassLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlPassLabel.add(lblPass);

        JPanel passField = createPasswordContainer("🔒", txtPassword = new PlaceholderPasswordField("Nhập mật khẩu"));


        btnDangNhap = new DangNhapRoundedButton("Đăng nhập", 25);
        btnDangNhap.setBackground(Color.RED);
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorderPainted(false);
        btnDangNhap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btnDangNhap.setPreferredSize(new Dimension(0, 60));
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDangNhap.addActionListener(e -> xuLyDangNhap());

        getRootPane().setDefaultButton(btnDangNhap);

        centerPanel.add(pnlUserLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(userField);
        centerPanel.add(Box.createVerticalStrut(24));
        centerPanel.add(pnlPassLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passField);
        centerPanel.add(Box.createVerticalStrut(28));
        centerPanel.add(btnDangNhap);

        rightPanel.add(centerPanel, BorderLayout.CENTER);
        return rightPanel;
    }

    private JPanel createLogoPanel() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);

        JPanel logoCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.RED);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        logoCircle.setPreferredSize(new Dimension(100, 100));
        logoCircle.setMaximumSize(new Dimension(100, 100));
        logoCircle.setOpaque(false);
        logoCircle.setLayout(new GridBagLayout());

        JLabel lblLantern = new JLabel("🏮");
        lblLantern.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        logoCircle.add(lblLantern);

        wrapper.add(logoCircle);
        return wrapper;
    }

    private JPanel createInputContainer(String iconText, JTextField textField) {
        RoundedPanel panel = new RoundedPanel(18);
        panel.setLayout(new BorderLayout(12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 14, 10, 14));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        panel.setPreferredSize(new Dimension(0, 62));

        JLabel icon = new JLabel(iconText);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setForeground(new Color(140, 140, 140));

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        textField.setBorder(null);
        textField.setOpaque(false);
        textField.setForeground(Color.BLACK);
        textField.setCaretColor(Color.BLACK);

        panel.add(icon, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPasswordContainer(String iconText, PlaceholderPasswordField passwordField) {
        DangNhapRoundedPanel panel = new DangNhapRoundedPanel(18);
        panel.setLayout(new BorderLayout(12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 14, 10, 14));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        panel.setPreferredSize(new Dimension(0, 62));

        JLabel icon = new JLabel(iconText);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setForeground(new Color(140, 140, 140));

        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passwordField.setBorder(null);
        passwordField.setOpaque(false);
        passwordField.setForeground(Color.BLACK);
        passwordField.setCaretColor(Color.BLACK);

        JToggleButton btnEye = new JToggleButton("👁");
        btnEye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btnEye.setFocusPainted(false);
        btnEye.setBorder(null);
        btnEye.setContentAreaFilled(false);
        btnEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEye.addActionListener(e -> {
            if (btnEye.isSelected())
                passwordField.setEchoChar((char) 0);
            else
                passwordField.restoreEchoChar();
        });

        panel.add(icon, BorderLayout.WEST);
        panel.add(passwordField, BorderLayout.CENTER);
        panel.add(btnEye, BorderLayout.EAST);
        return panel;
    }

    private void xuLyDangNhap() {
        String username = txtUsername.getRealText().trim();
        String password = new String(txtPassword.getRealPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập.");
            txtUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu.");
            txtPassword.requestFocus();
            return;
        }

        btnDangNhap.setEnabled(false);
        btnDangNhap.setText("Đang đăng nhập");

        SwingWorker<NhanVien, Void> worker = new SwingWorker<>() {
            @Override
            protected NhanVien doInBackground() {
                return new NhanVienDAO().dangNhap(username, password);
            }

            @Override
            protected void done() {
                try {
                    NhanVien nhanVien = get();

                    if (nhanVien == null) {
                        JOptionPane.showMessageDialog(FrmDangNhap.this, "Sai tên đăng nhập hoặc mật khẩu.",
                                "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
                        btnDangNhap.setEnabled(true);
                        btnDangNhap.setText("Đăng nhập");
                        return;
                    }

                    String vaiTro = nhanVien.getVaiTro();
                    if (vaiTro == null || vaiTro.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(FrmDangNhap.this, "Tài khoản chưa được gán vai trò.");
                        btnDangNhap.setEnabled(true);
                        btnDangNhap.setText("Đăng nhập");
                        return;
                    }
                    Entity.LuuLog.nhanVienDangNhap = new Model.NhanVienModel(
                            nhanVien.getMaNV(),
                            nhanVien.getHoTenNV(),
                            nhanVien.getVaiTro());
                    Entity.LuuLog.nhanVienDangNhap.taiKhoan = nhanVien.getTenDangNhap();
                    vaiTro = vaiTro.trim();
                    NhatKyDangNhapDAO logDAO = new NhatKyDangNhapDAO();
                    logDAO.ghiNhanDangNhap(nhanVien.getMaNV(), nhanVien.getHoTenNV(), vaiTro);
                    dispose();

                    switch (vaiTro) {
                        case "Admin":
                        case "Quản lý":
                            new FrmDashBoard(nhanVien).setVisible(true);
                            break;
                        case "Thu ngân":
                            new FrmThuNgan(nhanVien).setVisible(true);
                            break;
                        case "Lễ Tân":

                            new FrmLeTan().setVisible(true);
                            break;
                        case "Phục vụ":
                            new FrmPhucVu(nhanVien).setVisible(true);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Vai trò \"" + vaiTro + "\" chưa có giao diện riêng.",
                                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            break;
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(FrmDangNhap.this, "Lỗi kết nối database:\n" + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    btnDangNhap.setEnabled(true);
                    btnDangNhap.setText("Đăng nhập");
                }
            }
        };

        worker.execute();
    }


    public static void main(String[] args) {

        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new FrmDangNhap().setVisible(true));
    }
}



class BackgroundImagePanel extends JPanel {
    private Image backgroundImage;

    public BackgroundImagePanel(String imagePath) {
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            if (icon.getIconWidth() > 0)
                backgroundImage = icon.getImage();
        } catch (Exception e) {
            backgroundImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(35, 35, 35), 0, getHeight(), new Color(15, 15, 15));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.setColor(new Color(0, 0, 0, 65));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}

class DangNhapRoundedPanel extends JPanel {
    private final int radius;

    public DangNhapRoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(new Color(210, 210, 210));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class DangNhapRoundedButton extends JButton {
    private final int radius;

    public DangNhapRoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getModel().isPressed())
            g2.setColor(getBackground().darker());
        else if (getModel().isRollover())
            g2.setColor(getBackground().brighter());
        else
            g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}

class PlaceholderTextField extends JTextField {
    private final String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    public String getRealText() {
        return getText();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(160, 160, 160));
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 2, y);
            g2.dispose();
        }
    }
}

class PlaceholderPasswordField extends JPasswordField {
    private final String placeholder;
    private char defaultEchoChar;

    public PlaceholderPasswordField(String placeholder) {
        this.placeholder = placeholder;
        this.defaultEchoChar = getEchoChar();
        setBorder(new EmptyBorder(0, 0, 0, 0));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                restoreEchoChar();
            }
        });
    }

    public char[] getRealPassword() {
        return getPassword();
    }

    public void restoreEchoChar() {
        setEchoChar(defaultEchoChar);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getPassword().length == 0 && !isFocusOwner()) {
            setEchoChar((char) 0);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(160, 160, 160));
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 2, y);
            g2.dispose();
        } else if (!((JToggleButton) ((Container) getParent()).getComponent(2)).isSelected()) {
            restoreEchoChar();
        }
    }
}
