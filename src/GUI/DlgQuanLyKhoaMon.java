package GUI;

import DAO.MonAnDAO;
import DAO.NotificationDAO;
import Entity.DanhMuc;
import Entity.LuuLog;
import Entity.MonAn;
import Service.NotificationService;
import connectDatabase.ConnectDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DlgQuanLyKhoaMon extends JDialog {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    private JPanel   panelMenu;
    private JPanel   panelCategory;
    private String   currentCategory = "Tất cả";
    private JTextField txtSearch;
    private JLabel   lblTongMon;
    private JLabel   lblDaKhoa;

    // Màu sắc
    private static final Color BG_MAIN      = new Color(248, 248, 248);
    private static final Color CARD_BG      = Color.WHITE;
    private static final Color BORDER_CLR   = new Color(230, 230, 230);
    private static final Color RED_MAIN     = new Color(220, 38, 38);
    private static final Color TEXT_DARK    = new Color(30, 30, 30);
    private static final Color TEXT_GRAY    = new Color(110, 110, 110);
    private static final Color GREEN_PRICE  = new Color(22, 163, 74);
    private static final Color GREEN_BTN    = new Color(22, 163, 74);
    private static final Color LOCKED_BG    = new Color(254, 226, 226);
    private static final Color LOCKED_BORDER= new Color(239, 68, 68);

    public DlgQuanLyKhoaMon(Window owner) {
        super(owner, "Quản lý khóa món ăn", ModalityType.APPLICATION_MODAL);
        setSize(1100, 750);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadData();
    }


    public DlgQuanLyKhoaMon() {
        this(null);
    }



    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_MAIN);
        root.setBorder(new EmptyBorder(15, 20, 15, 20));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createContent(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setBackground(BG_MAIN);
        headerWrap.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);

        // Tiêu đề trái
        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setBackground(BG_MAIN);

        JLabel lblTitle = new JLabel("QUẢN LÝ KHÓA MÓN ĂN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Nhấn vào món để khóa hoặc mở khóa");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftContent.add(lblTitle);
        leftContent.add(Box.createVerticalStrut(4));
        leftContent.add(lblSub);

        // Thống kê phải
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setOpaque(false);

        lblTongMon = new JLabel("Tổng: 0 món");
        lblTongMon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTongMon.setForeground(TEXT_GRAY);

        lblDaKhoa = new JLabel("Đã khóa: 0");
        lblDaKhoa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDaKhoa.setForeground(RED_MAIN);

        statsPanel.add(lblTongMon);
        statsPanel.add(lblDaKhoa);

        // Search
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(220, 32));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                new EmptyBorder(0, 10, 0, 10)));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm món ăn");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { loadData(); }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(txtSearch);

        JPanel topRight = new JPanel(new BorderLayout(10, 5));
        topRight.setOpaque(false);
        topRight.add(statsPanel,  BorderLayout.NORTH);
        topRight.add(searchPanel, BorderLayout.CENTER);

        northPanel.add(leftContent, BorderLayout.WEST);
        northPanel.add(topRight,    BorderLayout.EAST);

        // Category buttons
        panelCategory = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelCategory.setBackground(BG_MAIN);

        String[] categories = {"Tất cả", "Món khai vị", "Món chính", "Đồ uống", "Tráng miệng"};
        // Nạp thêm danh mục từ DB
        List<String> dbCategories = getTenDanhMucTuDB();
        List<String> allCats = new ArrayList<>();
        allCats.add("Tất cả");
        for (String cat : dbCategories) {
            if (!allCats.contains(cat)) allCats.add(cat);
        }

        for (String cat : categories) {
            if (!allCats.contains(cat)) allCats.add(cat);
        }

        for (String cat : allCats) {
            panelCategory.add(createCategoryButton(cat));
        }

        headerWrap.add(northPanel,    BorderLayout.NORTH);
        headerWrap.add(panelCategory, BorderLayout.SOUTH);
        return headerWrap;
    }

    private List<String> getTenDanhMucTuDB() {
        List<String> list = new ArrayList<>();
        try {
            ConnectDB.getInstance().connect();
            Connection con = ConnectDB.getInstance().getConnection();
            if (con == null) return list;
            PreparedStatement ps = con.prepareStatement(
                    "SELECT tenDM FROM DanhMucMonAn ORDER BY maDM");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("tenDM"));
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("Lỗi getTenDanhMucTuDB: " + e.getMessage());
        }
        return list;
    }

    private JButton createCategoryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        updateCategoryButtonStyle(btn, text.equals(currentCategory));
        btn.addActionListener(e -> {
            currentCategory = text;
            refreshCategoryButtons();
            loadData();
        });
        return btn;
    }

    private void refreshCategoryButtons() {
        for (Component c : panelCategory.getComponents()) {
            if (c instanceof JButton btn) {
                updateCategoryButtonStyle(btn, btn.getText().equals(currentCategory));
            }
        }
    }

    private void updateCategoryButtonStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(RED_MAIN);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(RED_MAIN),
                    new EmptyBorder(8, 16, 8, 16)));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(TEXT_DARK);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR),
                    new EmptyBorder(8, 16, 8, 16)));
        }
    }

    private JScrollPane createContent() {
        panelMenu = new JPanel(new GridLayout(0, 4, 16, 16));
        panelMenu.setBackground(BG_MAIN);

        JScrollPane scrollPane = new JScrollPane(panelMenu);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_MAIN);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }



    private void loadData() {
        List<MonAn> allMon = getTatCaMonAn();
        String tuKhoa = txtSearch.getText().trim().toLowerCase();

        if (!tuKhoa.isEmpty()) {
            allMon = allMon.stream()
                    .filter(m -> m.getTenMon().toLowerCase().contains(tuKhoa)
                            || m.getMaMonAn().toLowerCase().contains(tuKhoa))
                    .collect(Collectors.toList());
        }

        if (!currentCategory.equalsIgnoreCase("Tất cả")) {
            final String cat = currentCategory;
            allMon = allMon.stream()
                    .filter(m -> m.getDanhMuc() != null
                            && cat.equalsIgnoreCase(m.getDanhMuc().getTenDM()))
                    .collect(Collectors.toList());
        }

        int tong    = allMon.size();
        int daKhoa  = (int) allMon.stream().filter(m -> !m.isTinhTrang()).count();
        lblTongMon.setText("Tổng: " + tong + " món");
        lblDaKhoa.setText("Đã khóa: " + daKhoa);

        panelMenu.removeAll();
        if (allMon.isEmpty()) {
            panelMenu.setLayout(new BorderLayout());
            JLabel emptyLabel = new JLabel("Không tìm thấy món ăn nào!", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(TEXT_GRAY);
            panelMenu.add(emptyLabel, BorderLayout.CENTER);
        } else {
            panelMenu.setLayout(new GridLayout(0, 4, 16, 16));
            for (MonAn mon : allMon) {
                panelMenu.add(createItemCard(mon));
            }
        }
        panelMenu.revalidate();
        panelMenu.repaint();
    }

    /**
     * Lấy TẤT CẢ món ăn (kể cả bị khóa) từ DB.
     */
    private List<MonAn> getTatCaMonAn() {
        List<MonAn> list = new ArrayList<>();
        String sql = "SELECT m.maMonAn, m.maDM, d.tenDM, m.tenMonAn, m.donVi, m.soLuongTon, "
                   + "m.giaBan, m.moTa, m.ghiChu, m.anhMon, m.tinhTrang "
                   + "FROM MonAn m "
                   + "LEFT JOIN DanhMucMonAn d ON m.maDM = d.maDM "
                   + "ORDER BY m.maMonAn";
        try {
            ConnectDB.getInstance().connect();
            Connection con = ConnectDB.getInstance().getConnection();
            if (con == null) {
                System.err.println("Lỗi getTatCaMonAn: Connection null!");
                return list;
            }
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MonAn mon = new MonAn();
                mon.setMaMonAn(rs.getString("maMonAn"));
                mon.setTenMon(rs.getString("tenMonAn"));
                mon.setDonVi(rs.getString("donVi"));
                mon.setSoLuong(rs.getInt("soLuongTon"));
                mon.setGiaMon(rs.getDouble("giaBan"));
                mon.setMoTa(rs.getString("moTa"));
                mon.setGhiChu(rs.getString("ghiChu"));
                mon.setAnhMon(rs.getString("anhMon"));
                mon.setTinhTrang(rs.getBoolean("tinhTrang"));
                String maDM  = rs.getString("maDM");
                String tenDM = rs.getString("tenDM");
                if (maDM != null) {
                    DanhMuc dm = new DanhMuc();
                    dm.setMaDM(maDM);
                    dm.setTenDM(tenDM);
                    mon.setDanhMuc(dm);
                }
                list.add(mon);
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("Lỗi getTatCaMonAn: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }



    private JPanel createItemCard(MonAn mon) {
        boolean isLocked = !mon.isTinhTrang(); // tinhTrang = false → bị khóa

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(isLocked ? LOCKED_BG : CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isLocked ? LOCKED_BORDER : BORDER_CLR, 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        card.setPreferredSize(new Dimension(200, 290));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon khóa ở header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        headerPanel.setOpaque(false);
        if (isLocked) {
            JLabel lockIcon = new JLabel("\uD83D\uDD12"); // 🔒
            lockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            headerPanel.add(lockIcon);
        }

        // Ảnh
        JLabel lblImg = new JLabel();
        lblImg.setPreferredSize(new Dimension(180, 120));
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        hienThiAnh(lblImg, mon.getAnhMon());

        // Thông tin
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(isLocked ? LOCKED_BG : CARD_BG);
        info.setBorder(new EmptyBorder(8, 2, 2, 2));

        String danhMuc = (mon.getDanhMuc() != null) ? mon.getDanhMuc().getTenDM() : "";
        JLabel lblLoai = new JLabel(danhMuc);
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLoai.setForeground(isLocked ? LOCKED_BORDER : RED_MAIN);
        lblLoai.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTen = new JLabel("<html><div style='width:170px'>" + mon.getTenMon() + "</div></html>");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTen.setForeground(TEXT_DARK);
        lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGia = new JLabel(String.format("%,.0f đ", mon.getGiaMon()));
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblGia.setForeground(isLocked ? TEXT_GRAY : GREEN_PRICE);
        lblGia.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStatus = new JLabel(isLocked ? "Đã khóa" : "Đang phục vụ");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setForeground(isLocked ? LOCKED_BORDER : GREEN_BTN);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(lblLoai);
        info.add(Box.createVerticalStrut(4));
        info.add(lblTen);
        info.add(Box.createVerticalStrut(4));
        info.add(lblGia);
        info.add(Box.createVerticalStrut(4));
        info.add(lblStatus);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(lblImg,      BorderLayout.CENTER);
        card.add(info,        BorderLayout.SOUTH);

        // Hover & click
        MouseAdapter clickHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { xuLyKhoaMon(mon, !isLocked); }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(isLocked ? LOCKED_BORDER : RED_MAIN, 2, true),
                        new EmptyBorder(10, 10, 10, 10)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(isLocked ? LOCKED_BORDER : BORDER_CLR, 1, true),
                        new EmptyBorder(10, 10, 10, 10)));
            }
        };

        card.addMouseListener(clickHandler);
        lblImg.addMouseListener(clickHandler);
        info.addMouseListener(clickHandler);
        lblTen.addMouseListener(clickHandler);

        return card;
    }



    private void hienThiAnh(JLabel label, String path) {
        label.setIcon(null);
        label.setText("🍽");
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        label.setForeground(TEXT_GRAY);

        if (path == null || path.trim().isEmpty()) return;

        try {
            ImageIcon icon;
            File file = new File(path);
            if (!file.exists()) {
                file = new File("src/image/" + path);
            }
            if (file.exists()) {
                icon = new ImageIcon(file.getAbsolutePath());
            } else {
                java.net.URL url = getClass().getResource("/images/monan/" + path);
                if (url == null) return;
                icon = new ImageIcon(url);
            }
            Image img = icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
            label.setText("");
            label.setIcon(new ImageIcon(img));
        } catch (Exception ignored) {}
    }

    // ─── Xử lý khóa/mở khóa ─────────────────────────────────────────────────

    private void xuLyKhoaMon(MonAn mon, boolean khoa) {
        String maNV = "";
        if (LuuLog.nhanVienDangNhap != null) {
            maNV = LuuLog.nhanVienDangNhap.getMaNV();
        }

        String xacNhan = khoa
                ? "Bạn có chắc chắn muốn KHÓA món \"" + mon.getTenMon()
                  + "\" ?\nMón này sẽ không thể gọi thêm."
                : "Bạn có chắc chắn muốn MỞ KHÓA món \"" + mon.getTenMon() + "\" ?";

        int r = JOptionPane.showConfirmDialog(this, xacNhan, "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        final String maNVFinal = maNV;
        boolean ok = notificationDAO.khoaMon(mon.getMaMonAn(), khoa, maNVFinal);
        if (ok) {
            String loai    = khoa ? "KHOA_MON" : "MO_KHOA_MON";
            String tieuDe  = khoa ? "Món tạm ngừng" : "Món phục vụ trở lại";
            String noiDung = khoa
                    ? "Món \"" + mon.getTenMon() + "\" đã bị tạm ngừng."
                    : "Món \"" + mon.getTenMon() + "\" đã phục vụ trở lại.";
            Entity.ThongBao tb = new Entity.ThongBao(tieuDe, noiDung, loai, maNVFinal);
            java.util.List<Entity.ThongBao> list = new java.util.ArrayList<>();
            list.add(tb);
            SwingUtilities.invokeLater(() -> {
                NotificationService.getInstance().danhDauDaDoc(-1);
            });

            loadData();
            String icon = khoa ? "🔒" : "✅";
            JOptionPane.showMessageDialog(this,
                    icon + " " + (khoa ? "Đã khóa" : "Đã mở khóa")
                    + " món \"" + mon.getTenMon() + "\"!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi cập nhật trạng thái món!\n"
                    + "(Kiểm tra bảng Notifications đã được tạo chưa)",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
