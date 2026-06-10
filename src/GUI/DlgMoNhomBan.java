
package GUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import Entity.BanAn;
import DAO.BanAnDAO;
import DAO.HoaDonDAO;
import DAO.KhachHangDAO;

public class DlgMoNhomBan extends JDialog {

    private JFrame parent;
    private List<BanAn> selectedBanList    = new ArrayList<>();
    private List<BanAn> danhSachBanTrong  = new ArrayList<>();
    private JTextField  txtTenKhach;
    private JTextField  txtSDT;
    private JFormattedTextField txtSoKhach;  // Thay đổi từ JSpinner sang JFormattedTextField
    private JLabel      lblTongSucChua;
    private ModernButton btnOK;
    private ModernButton btnCancel;
    private JTabbedPane  tabbedPane;

    private final BanAnDAO     banAnDAO     = new BanAnDAO();
    private final HoaDonDAO    hoaDonDAO    = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    public DlgMoNhomBan(JFrame parent) {
        super(parent, "Mở nhóm bàn cho khách vãng lai", true);
        this.parent = parent;
        initUI();
        loadDanhSachBanTrong();
    }

    private void initUI() {
        setUndecorated(true);
        setSize(900, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BG_APP);
        root.setOpaque(true);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_INPUT, 1),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(new EmptyBorder(0, 4, 16, 4));

        JPanel titleLeft = new JPanel();
        titleLeft.setLayout(new BoxLayout(titleLeft, BoxLayout.Y_AXIS));
        titleLeft.setOpaque(false);
        JLabel lblTitle = new JLabel("Chọn bàn trống để mở nhóm bàn");
        lblTitle.setFont(UIConstants.FONT_BOLD_18);
        lblTitle.setForeground(UIConstants.TEXT_DARK);
        JLabel lblSub = new JLabel("Nhấn vào thẻ bàn để chọn • Có thể chọn nhiều bàn");
        lblSub.setFont(UIConstants.FONT_PLAIN_12);
        lblSub.setForeground(UIConstants.TEXT_GRAY);
        titleLeft.add(lblTitle);
        titleLeft.add(Box.createVerticalStrut(2));
        titleLeft.add(lblSub);

        // Nút Đóng (Dùng chữ X thường để không vỡ font)
        JButton btnClose = new JButton("") {
            private boolean h = false;
            {
                setFocusPainted(false); setBorderPainted(false);
                setContentAreaFilled(false); setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setFont(UIConstants.FONT_BOLD_15);
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
        btnClose.setPreferredSize(new Dimension(30, 30));
        btnClose.addActionListener(e -> dispose());

        titleBar.add(titleLeft, BorderLayout.CENTER);
        titleBar.add(btnClose,  BorderLayout.EAST);


        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.FONT_PLAIN_14);
        tabbedPane.setBackground(UIConstants.BG_APP);
        tabbedPane.setForeground(UIConstants.TEXT_DARK);


        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(UIConstants.CARD_BG);
        pnlForm.setOpaque(true);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_INPUT, 1, true),
                new EmptyBorder(24, 28, 24, 28)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 16, 10, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        java.util.function.Function<String, JLabel> makeLabel = text -> {
            JLabel l = new JLabel(text);
            l.setFont(UIConstants.FONT_BOLD_13);
            l.setForeground(UIConstants.TEXT_DARK);
            return l;
        };

        // Row 1: Tên khách + Số điện thoại
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlForm.add(makeLabel.apply("Tên khách:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtTenKhach = buildTextField("Nhập tên khách hàng");
        txtTenKhach.setPreferredSize(new Dimension(200, 36));
        pnlForm.add(txtTenKhach, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlForm.add(makeLabel.apply("Số điện thoại:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtSDT = buildTextField("0xxxxxxxxx");
        txtSDT.setPreferredSize(new Dimension(200, 36));
        txtSDT.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { autoFillTenKhach(); }
            @Override public void removeUpdate(DocumentEvent e)  { autoFillTenKhach(); }
            @Override public void changedUpdate(DocumentEvent e) { autoFillTenKhach(); }
        });
        pnlForm.add(txtSDT, gbc);

        // Row 2: Số khách + Tổng sức chứa
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlForm.add(makeLabel.apply("Số khách:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        // Tạo JFormattedTextField cho số khách (không có nút tăng/giảm)
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(nf);
        formatter.setMinimum(1);
        formatter.setMaximum(100);
        formatter.setAllowsInvalid(false);
        txtSoKhach = new JFormattedTextField(formatter);
        txtSoKhach.setValue(1);
        txtSoKhach.setFont(UIConstants.FONT_PLAIN_14);
        txtSoKhach.setPreferredSize(new Dimension(200, 36));
        txtSoKhach.setHorizontalAlignment(JFormattedTextField.LEFT);
        txtSoKhach.setBackground(Color.WHITE);
        txtSoKhach.setForeground(UIConstants.TEXT_DARK);
        txtSoKhach.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_INPUT, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        txtSoKhach.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                txtSoKhach.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDER_FOCUS, 1, true),
                        new EmptyBorder(0, 10, 0, 10)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                txtSoKhach.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDER_INPUT, 1, true),
                        new EmptyBorder(0, 10, 0, 10)));
            }
        });
        pnlForm.add(txtSoKhach, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlForm.add(makeLabel.apply("Tổng sức chứa:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        JPanel pnlCapacity = new JPanel(new BorderLayout());
        pnlCapacity.setBackground(UIConstants.CARD_BG);
        pnlCapacity.setOpaque(false);
        lblTongSucChua = new JLabel("0 người");
        lblTongSucChua.setFont(UIConstants.FONT_BOLD_14);
        lblTongSucChua.setForeground(UIConstants.PRIMARY);
        pnlCapacity.add(lblTongSucChua, BorderLayout.WEST);
        pnlForm.add(pnlCapacity, gbc);

        // Row 3: Buttons (spanning all columns)
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlButtons.setOpaque(false);

        btnCancel = new ModernButton("Hủy", ModernButton.Style.OUTLINE);
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> dispose());

        btnOK = new ModernButton("Mở bàn", ModernButton.Style.PRIMARY);
        btnOK.setPreferredSize(new Dimension(130, 38));
        btnOK.addActionListener(e -> xuLyMoNhomBan());

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnOK);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(16, 16, 0, 16);
        pnlForm.add(pnlButtons, gbc);

        root.add(titleBar,   BorderLayout.NORTH);
        root.add(tabbedPane, BorderLayout.CENTER);
        root.add(pnlForm,    BorderLayout.SOUTH);

        setContentPane(root);
    }



    private void loadDanhSachBanTrong() {
        danhSachBanTrong.clear();
        List<BanAn> allBan = banAnDAO.getAllBanAn();

        if (tabbedPane.getTabCount() == 0) {
            List<String> floors = getDistinctFloors(allBan);
            for (String floor : floors) {
                JPanel pnlFloor = new JPanel(new GridLayout(0, 5, 16, 16));  // Thay 4 cột thành 5, tăng spacing
                pnlFloor.setBackground(UIConstants.BG_APP);
                pnlFloor.setBorder(new EmptyBorder(16, 16, 16, 16));  // Tăng padding

                JPanel wrapTop = new JPanel(new BorderLayout());
                wrapTop.setBackground(UIConstants.BG_APP);
                wrapTop.add(pnlFloor, BorderLayout.NORTH);

                JScrollPane scroll = new JScrollPane(wrapTop);
                scroll.setBorder(null);
                scroll.setOpaque(true);
                scroll.getViewport().setOpaque(true);
                scroll.getVerticalScrollBar().setUnitIncrement(16);
                scroll.getViewport().setBackground(UIConstants.BG_APP);

                // Đã xóa Icon Emoji để tránh lỗi ô vuông
                tabbedPane.addTab(floor, scroll);
            }
            tabbedPane.addChangeListener(e -> loadDanhSachBanTrong());

        }

        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) {
            selectedIndex = 0;
            tabbedPane.setSelectedIndex(0);
        }
        String selectedTab = tabbedPane.getTitleAt(selectedIndex);

        selectedBanList.clear();

        for (BanAn ban : allBan) {
            if (ban.getViTri() != null && ban.getViTri().equalsIgnoreCase(selectedTab) &&
                    ban.getTrangThai() != null && ban.getTrangThai().equalsIgnoreCase("Trống")) {
                danhSachBanTrong.add(ban);
            }
        }

        JScrollPane scroll = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
        JPanel wrapTop = (JPanel) scroll.getViewport().getView();
        JPanel pnlFloor = (JPanel) wrapTop.getComponent(0);
        pnlFloor.removeAll();

        for (BanAn ban : danhSachBanTrong) {
            pnlFloor.add(createTableCard(ban));
        }

        pnlFloor.revalidate();
        pnlFloor.repaint();
        capNhatTongSucChua();
    }

    private JPanel createTableCard(BanAn ban) {
        RoundedPanel card = new RoundedPanel(10, false, UIConstants.CARD_BG);
        card.setLayout(new BorderLayout(0, 8));
        card.setPreferredSize(new Dimension(110, 100));  // Tăng kích thước card
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_INPUT, 2));

        JLabel lblName = new JLabel(ban.getTenBan(), SwingConstants.CENTER);
        lblName.setFont(UIConstants.FONT_BOLD_14);
        lblName.setForeground(UIConstants.TEXT_DARK);
        lblName.setBorder(new EmptyBorder(8, 4, 4, 4));

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(0, 4, 8, 4));

        // Đã xóa Icon Emoji
        JLabel lblCap = new JLabel("SL: " + ban.getSucChua());
        lblCap.setFont(UIConstants.FONT_PLAIN_13);
        lblCap.setForeground(UIConstants.TEXT_GRAY);
        pnlBottom.add(lblCap);

        card.add(lblName, BorderLayout.CENTER);
        card.add(pnlBottom, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedBanList.contains(ban)) {
                    selectedBanList.remove(ban);
                    card.setBackground(UIConstants.CARD_BG);
                    card.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_INPUT, 2));
                    lblName.setForeground(UIConstants.TEXT_DARK);
                    lblCap.setForeground(UIConstants.TEXT_GRAY);
                } else {
                    selectedBanList.add(ban);
                    card.setBackground(UIConstants.PRIMARY_TINT);
                    card.setBorder(BorderFactory.createLineBorder(UIConstants.PRIMARY, 2));
                    lblName.setForeground(UIConstants.PRIMARY);
                    lblCap.setForeground(UIConstants.PRIMARY);
                }
                capNhatTongSucChua();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selectedBanList.contains(ban)) {
                    card.setBorder(BorderFactory.createLineBorder(UIConstants.TEXT_GRAY, 2));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selectedBanList.contains(ban)) {
                    card.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_INPUT, 2));
                }
            }
        });
        return card;
    }

    private List<String> getDistinctFloors(List<BanAn> allBan) {
        List<String> floors = new ArrayList<>();
        for (BanAn ban : allBan) {
            String viTri = ban.getViTri();
            if (viTri != null && !floors.contains(viTri)) {
                floors.add(viTri);
            }
        }
        return floors;
    }

    private void capNhatTongSucChua() {
        int tong = 0;
        for (BanAn ban : selectedBanList) {
            tong += ban.getSucChua();
        }
        lblTongSucChua.setText(tong + " người");
    }

    private void autoFillTenKhach() {
        String sdt = txtSDT.getText().trim();
        if (!sdt.isEmpty()) {
            String ten = khachHangDAO.timTenKhachTheoSDT(sdt);
            if (ten != null) {
                txtTenKhach.setText(ten);
            }
        }
    }

    private void xuLyMoNhomBan() {
        List<String> selectedMaBan = new ArrayList<>();
        for (BanAn ban : selectedBanList) {
            selectedMaBan.add(ban.getMaBan());
        }

        if (selectedMaBan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một bàn!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tenKhach = txtTenKhach.getText().trim();
        String sdt      = txtSDT.getText().trim();
        int soKhach;
        try {
            txtSoKhach.commitEdit();
            soKhach = ((Number) txtSoKhach.getValue()).intValue();
        } catch (java.text.ParseException e) {
            soKhach = 1;
        }

        if (tenKhach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtTenKhach.requestFocus();
            return;
        }

        int tongSucChua = 0;
        for (BanAn ban : selectedBanList) {
            tongSucChua += ban.getSucChua();
        }

        if (soKhach > tongSucChua) {
            if (JOptionPane.showConfirmDialog(this,
                    "Số khách (" + soKhach + ") vượt quá tổng sức chứa (" + tongSucChua + ")!\nVẫn tiếp tục?",
                    "Cảnh báo", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String maNV = "NV005";
        if (Entity.LuuLog.nhanVienDangNhap != null) {
            maNV = Entity.LuuLog.nhanVienDangNhap.getMaNV();
        }

        boolean hdOk = hoaDonDAO.taoHoaDonNhomBan(selectedMaBan, maNV, tenKhach, sdt, soKhach);

        if (hdOk) {
            boolean banOk = true;
            for (String maBan : selectedMaBan) {
                if (!banAnDAO.capNhatTrangThai(maBan, "Có khách")) {
                    banOk = false;
                }
            }
            if (banOk) {
                JOptionPane.showMessageDialog(this, "Mở nhóm bàn thành công cho khách " + tenKhach + "!");
                if (parent instanceof FrmLeTan) {
                    ((FrmLeTan) parent).refreshSoDoBan();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI Helper — modern text field
    // ─────────────────────────────────────────────────────────────────────────

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
                BorderFactory.createLineBorder(UIConstants.BORDER_INPUT, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        field.setPreferredSize(new Dimension(200, 36));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDER_FOCUS, 1, true),
                        new EmptyBorder(0, 10, 0, 10)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDER_INPUT, 1, true),
                        new EmptyBorder(0, 10, 0, 10)));
                field.repaint();
            }
        });
        return field;
    }
}