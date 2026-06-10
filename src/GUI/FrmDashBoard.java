package GUI;

import DAO.NhatKyDangNhapDAO;
import Entity.NhanVien;
import DAO.HoaDonDAO;

import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrmDashBoard extends JFrame {
    private NhanVien nhanVien;
    private String tenNhanVien;

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BG_MAIN = new Color(248, 248, 248);
    private static final Color SIDEBAR_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(30, 30, 30);
    private static final Color TEXT_GRAY = new Color(110, 110, 110);
    private static final Color BORDER_CLR = new Color(230, 230, 230);

    private JLabel lbTitle;
    private JLabel lbSub;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPopupMenu currentPopup;
    private JPanel dashboardPanel;
    private FrmQLNhanVien pnlNhanVien;
    private FrmQLMonAn pnlMonAn;
    private FrmQLBanAn pnlBanAn;
    private FrmQLKhuyenMai pnlKhuyenMai;
    private FrmQLBangGia pnlBangGia;
    private FrmBaoCaoDoanhThu pnlBaoCao;
    private FrmQLSoDoBan pnlSoDoBan;
    private FrmCaiDat pnlCaiDat;
    private FrmQLKhachHang pnlKhachHang;
    private FrmThungRac pnlThungRac;
    private final Map<String, JPanel> menuMap = new LinkedHashMap<>();
    private String activeMenu = "Dashboard";
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final NhatKyDangNhapDAO nhatKyDangNhapDAO = new NhatKyDangNhapDAO();

    public FrmDashBoard(NhanVien nhanVien) {
        this.nhanVien = nhanVien;

        if (nhanVien == null || (!"Quản lý".equalsIgnoreCase(nhanVien.getVaiTro()) && !"Admin".equalsIgnoreCase(nhanVien.getVaiTro()))) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền truy cập màn hình quản lý.");
            dispose();
            return;
        }

        this.tenNhanVien = nhanVien.getHoTenNV();
        initUI();

        Service.NotificationService.getInstance().startPolling(3000);
        showPage("DASHBOARD", "Dashboard", "Tổng quan hoạt động nhà hàng - Xin chào " + tenNhanVien, "Dashboard");
    }

    private void initUI() {
        setTitle("Hệ Thống Quản Lý - Nhà Hàng Ngói Đỏ");
        setSize(1440, 860);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_MAIN);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createMainArea(), BorderLayout.CENTER);

        setContentPane(root);

    }

    //  SIDEBAR
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14));
        logoPanel.setBackground(SIDEBAR_BG);

        JPanel redCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(RED_MAIN);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        redCircle.setPreferredSize(new Dimension(40, 40));
        redCircle.setOpaque(false);
        redCircle.setLayout(new GridBagLayout());

        JLabel lbEmoji = new JLabel("🏮");
        lbEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        redCircle.add(lbEmoji);

        JPanel nameBox = new JPanel();
        nameBox.setOpaque(false);
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));

        JLabel lbName = new JLabel("Ngói Đỏ");
        lbName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbName.setForeground(TEXT_DARK);

        JLabel lbRole = new JLabel("Admin Panel");
        lbRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbRole.setForeground(TEXT_GRAY);

        nameBox.add(lbName);
        nameBox.add(lbRole);

        logoPanel.add(redCircle);
        logoPanel.add(nameBox);

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(SIDEBAR_BG);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        //MENU CHA
        JPanel btnDashboard = createMenuButton("🏠", "Dashboard");
        JPanel btnVanHanh = createMenuButton("🧩", "Vận hành");
        JPanel btnNhanSuGroup = createMenuButton("👥", "Nhân sự");
        JPanel btnHeThong = createMenuButton("⚙", "Hệ thống");

        //  MENU MAP: chỉ highlight MENU CHA
        menuMap.clear();
        menuMap.put("Dashboard", btnDashboard);
        menuMap.put("Vận hành", btnVanHanh);
        menuMap.put("Nhân sự", btnNhanSuGroup);
        menuMap.put("Hệ thống", btnHeThong);

        addMenuAction(btnDashboard, "Dashboard");

        String vaiTro = nhanVien != null ? nhanVien.getVaiTro() : "";

        java.util.List<String[]> vanHanhItems = new java.util.ArrayList<>();
        vanHanhItems.add(new String[]{"Quản Lý Sơ Đồ Bàn", "SODOBAN", "Quản Lý Sơ Đồ Bàn", "Theo dõi bàn trống, bàn có khách và bàn đã đặt", "Vận hành"});
        vanHanhItems.add(new String[]{"Quản lý Bàn", "BANAN", "Quản lý Bàn", "Thêm, sửa, xóa và quản lý danh sách bàn ăn", "Vận hành"});
        vanHanhItems.add(new String[]{"Quản lý Thực đơn", "MONAN", "Quản lý Thực đơn", "Thêm, sửa, xóa và quản lý danh sách món ăn", "Vận hành"});
        
        if ("Quản lý".equalsIgnoreCase(vaiTro) || "Admin".equalsIgnoreCase(vaiTro)) {
            vanHanhItems.add(new String[]{"Quản lý Bảng Giá", "BANGGIA", "Quản lý Bảng Giá", "Thiết lập giá theo thời điểm", "Vận hành"});
        }
        
        vanHanhItems.add(new String[]{"Quản lý Khuyến mãi", "KHUYENMAI", "Quản lý Khuyến mãi", "Tạo, cập nhật và quản lý các chương trình khuyến mãi", "Vận hành"});
        vanHanhItems.add(new String[]{"Khóa Món", "KHOAMON", "Khóa Món Ăn", "Khóa và mở khóa món ăn trong thực đơn", "Vận hành"});

        JPopupMenu popupVanHanh = createSubMenuPopup(vanHanhItems.toArray(new String[0][]));

        // SUBMENU NHÂN SỰ
        JPopupMenu popupNhanSu = null;

        if ("Quản lý".equalsIgnoreCase(vaiTro) || "Admin".equalsIgnoreCase(vaiTro)) {
            popupNhanSu = createSubMenuPopup(new String[][]{
                    {"Quản lý Nhân sự", "NHANVIEN", "Quản lý Nhân sự", "Thêm, sửa, xóa và quản lý danh sách nhân viên", "Nhân sự"},
                    {"Quản lý Khách hàng", "KHACHHANG", "Quản lý Khách hàng", "Xem thông tin và điểm tích lũy của khách hàng", "Nhân sự"}
            });
            btnNhanSuGroup.setVisible(true);
        } else {
            btnNhanSuGroup.setVisible(false);
        }

        //SUBMENU HỆ THỐNG
        JPopupMenu popupHeThong;
        if ("Admin".equalsIgnoreCase(vaiTro)) {
            popupHeThong = createSubMenuPopup(new String[][]{
                    {"Báo cáo Doanh thu", "BAOCAO", "Báo cáo Doanh thu", "Theo dõi doanh thu và hiệu suất kinh doanh", "Hệ thống"},
                    {"Cài đặt", "CAIDAT", "Cài đặt", "Quản lý tài khoản và cấu hình hệ thống", "Hệ thống"},
                    {"Thùng rác dữ liệu", "THUNGRAC", "Thùng rác dữ liệu", "Quản lý dữ liệu đã bị xóa", "Hệ thống"}
            });
        } else {
            popupHeThong = createSubMenuPopup(new String[][]{
                    {"Báo cáo Doanh thu", "BAOCAO", "Báo cáo Doanh thu", "Theo dõi doanh thu và hiệu suất kinh doanh", "Hệ thống"},
                    {"Cài đặt", "CAIDAT", "Cài đặt", "Quản lý tài khoản và cấu hình hệ thống", "Hệ thống"}
            });
        }

        addClickPopup(btnVanHanh, popupVanHanh);
        if (popupNhanSu != null) {
            addClickPopup(btnNhanSuGroup, popupNhanSu);
        }
        addClickPopup(btnHeThong, popupHeThong);

        menuPanel.add(btnDashboard);
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(btnVanHanh);
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(btnNhanSuGroup);
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(btnHeThong);

        updateMenuState();

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        bottomPanel.setBackground(SIDEBAR_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JLabel lbLogout = new JLabel("Đăng xuất");
        lbLogout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbLogout.setForeground(TEXT_GRAY);
        lbLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(FrmDashBoard.this, "Bạn có chắc muốn đăng xuất?",
                        "Đăng xuất", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new FrmDangNhap().setVisible(true);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lbLogout.setForeground(RED_MAIN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lbLogout.setForeground(TEXT_GRAY);
            }
        });
        bottomPanel.add(lbLogout);

        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPopupMenu createSubMenuPopup(String[][] items) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        popup.setBackground(Color.WHITE);

        for (String[] item : items) {
            String menuText = item[0];
            String cardName = item[1];
            String title = item[2];
            String subTitle = item[3];
            String groupName = item[4];

            JMenuItem mi = new JMenuItem(menuText);
            mi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            mi.setPreferredSize(new Dimension(220, 42));
            mi.setBackground(Color.WHITE);
            mi.setForeground(TEXT_DARK);
            mi.setOpaque(true);
            mi.setFocusPainted(false);

            mi.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mi.setBackground(new Color(254, 242, 242));
                    mi.setForeground(RED_MAIN);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mi.setBackground(Color.WHITE);
                    mi.setForeground(TEXT_DARK);
                }
            });

            mi.addActionListener(e -> {
                showPage(cardName, title, subTitle, groupName);
                popup.setVisible(false);
                currentPopup = null;
            });

            popup.add(mi);
        }

        return popup;
    }

    private void addClickPopup(JPanel parentBtn, JPopupMenu popup) {
        MouseAdapter clickHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentPopup != null && currentPopup.isVisible()) {
                    if (currentPopup == popup) {
                        currentPopup.setVisible(false);
                        currentPopup = null;
                        return;
                    }
                    currentPopup.setVisible(false);
                }

                popup.show(parentBtn, parentBtn.getWidth() - 2, 0);
                currentPopup = popup;
            }
        };

        parentBtn.addMouseListener(clickHandler);

        JLabel lbIcon = (JLabel) parentBtn.getClientProperty("iconLabel");
        JLabel lbText = (JLabel) parentBtn.getClientProperty("textLabel");

        if (lbIcon != null)
            lbIcon.addMouseListener(clickHandler);
        if (lbText != null)
            lbText.addMouseListener(clickHandler);
    }

    private JPanel createMenuButton(String icon, String label) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(SIDEBAR_BG);

        JLabel lbIcon = new JLabel(icon);
        lbIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        lbIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lbLabel = new JLabel(label);
        lbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbLabel.setForeground(TEXT_DARK);
        lbLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.add(lbIcon);
        btn.add(lbLabel);

        btn.putClientProperty("menuLabel", label);
        btn.putClientProperty("iconLabel", lbIcon);
        btn.putClientProperty("textLabel", lbLabel);

        MouseAdapter hoverEffect = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String thisLabel = (String) btn.getClientProperty("menuLabel");
                if (!thisLabel.equals(activeMenu)) {
                    btn.setBackground(new Color(250, 250, 250));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                String thisLabel = (String) btn.getClientProperty("menuLabel");
                if (!thisLabel.equals(activeMenu)) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        };

        btn.addMouseListener(hoverEffect);
        lbIcon.addMouseListener(hoverEffect);
        lbLabel.addMouseListener(hoverEffect);

        return btn;
    }

    private void addMenuAction(JPanel btn, String menuName) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (menuName) {
                    case "Dashboard":
                        showPage("DASHBOARD", "Dashboard", "Tổng quan hoạt động nhà hàng - Xin chào " + tenNhanVien,
                                "Dashboard");
                        break;
                    case "Quản lý Bàn":
                        showPage("BANAN", "Quản lý Bàn", "Thêm, sửa, xóa và quản lý danh sách bàn ăn", "Quản lý Bàn");
                        break;
                    case "Quản lý Thực đơn":
                        showPage("MONAN", "Quản lý Thực đơn", "Thêm, sửa, xóa và quản lý danh sách món ăn",
                                "Vận hành");
                        break;
                    case "Quản lý Bảng Giá":
                        showPage("BANGGIA", "Quản lý Bảng Giá", "Thiết lập giá theo thời điểm", "Vận hành");
                        break;
                    case "Quản lý Nhân sự":
                        showPage("NHANVIEN", "Quản lý Nhân sự", "Thêm, sửa, xóa và quản lý danh sách nhân viên",
                                "Quản lý Nhân sự");
                        break;
                    case "Quản lý Khách hàng":
                        showPage("KHACHHANG", "Quản lý Khách hàng", "Xem thông tin và điểm tích lũy của khách hàng", "Quản lý Khách hàng");
                        break;
                    case "Quản lý Khuyến mãi":
                        showPage("KHUYENMAI", "Quản lý Khuyến mãi", "Tạo, cập nhật và quản lý các chương trình khuyến mãi",
                                "Quản lý Khuyến mãi");
                        break;
                    case "Báo cáo Doanh thu":
                        showPage("BAOCAO", "Báo cáo Doanh thu", "Theo dõi doanh thu và hiệu suất kinh doanh",
                                "Báo cáo Doanh thu");
                        break;
                    case "Quản Lý Sơ Đồ Bàn":
                        showPage("SODOBAN", "Quản Lý Sơ Đồ Bàn", "Theo dõi bàn trống, bàn có khách và bàn đã đặt",
                                "Quản Lý Sơ Đồ Bàn");
                        break;
                    case "Cài đặt":
                        showPage("CAIDAT", "Cài đặt", "Quản lý tài khoản và cấu hình hệ thống", "Cài đặt");
                        break;
                    case "Thùng rác dữ liệu":
                        showPage("THUNGRAC", "Thùng rác dữ liệu", "Quản lý dữ liệu đã bị xóa", "Hệ thống");
                        break;
                }
            }
        });
    }

    private void updateMenuState() {
        for (Map.Entry<String, JPanel> entry : menuMap.entrySet()) {
            String label = entry.getKey();
            JPanel btn = entry.getValue();

            JLabel lbText = (JLabel) btn.getClientProperty("textLabel");
            boolean isActive = label.equals(activeMenu);

            btn.setBackground(isActive ? new Color(254, 242, 242) : SIDEBAR_BG);
            lbText.setForeground(isActive ? RED_MAIN : TEXT_DARK);
            lbText.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 13));
        }
    }

    // ///MAIN AREA
    private JPanel createMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_MAIN);

        main.add(createTopBar(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_MAIN);

        dashboardPanel = buildDashboardWrapper();
        pnlNhanVien = new FrmQLNhanVien();
        pnlMonAn = new FrmQLMonAn();
        pnlBanAn = new FrmQLBanAn();
        pnlKhuyenMai = new FrmQLKhuyenMai();
        pnlBaoCao = new FrmBaoCaoDoanhThu(tenNhanVien);
        pnlSoDoBan = new FrmQLSoDoBan();
        pnlCaiDat = new FrmCaiDat();
        pnlKhachHang = new FrmQLKhachHang();
        if ("Admin".equalsIgnoreCase(nhanVien != null ? nhanVien.getVaiTro() : "")) {
            pnlThungRac = new FrmThungRac();
            contentPanel.add(pnlThungRac, "THUNGRAC");
        }
        String vt = nhanVien != null ? nhanVien.getVaiTro() : "";
        if ("Quản lý".equalsIgnoreCase(vt) || "Admin".equalsIgnoreCase(vt)) {
            pnlBangGia = new FrmQLBangGia();
            contentPanel.add(pnlBangGia, "BANGGIA");
        }
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(pnlNhanVien, "NHANVIEN");
        contentPanel.add(pnlMonAn, "MONAN");
        contentPanel.add(pnlBanAn, "BANAN");
        contentPanel.add(pnlKhuyenMai, "KHUYENMAI");
        contentPanel.add(pnlBaoCao, "BAOCAO");
        contentPanel.add(pnlSoDoBan, "SODOBAN");
        contentPanel.add(pnlCaiDat, "CAIDAT");
        contentPanel.add(pnlKhachHang, "KHACHHANG");
        main.add(contentPanel, BorderLayout.CENTER);
        return main;
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(14, 24, 14, 24)));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        lbTitle = new JLabel("Dashboard");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbTitle.setForeground(TEXT_DARK);

        lbSub = new JLabel("Tổng quan hoạt động nhà hàng - Xin chào " + tenNhanVien);
        lbSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbSub.setForeground(TEXT_GRAY);

        left.add(lbTitle);
        left.add(lbSub);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"));

        // Panel phải: ngày + chuông thông báo
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JPanel dateBox = new JPanel();
        dateBox.setOpaque(false);
        dateBox.setLayout(new BoxLayout(dateBox, BoxLayout.Y_AXIS));

        JLabel lbToday = new JLabel("Hôm nay");
        lbToday.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbToday.setForeground(TEXT_GRAY);
        lbToday.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lbDate = new JLabel(today);
        lbDate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbDate.setForeground(TEXT_DARK);
        lbDate.setAlignmentX(Component.RIGHT_ALIGNMENT);

        dateBox.add(lbToday);
        dateBox.add(lbDate);

        // Chuông thông báo
        NotificationBellPanel bellPanel = new NotificationBellPanel();

        right.add(dateBox);
        right.add(bellPanel);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    private JPanel buildDashboardWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_MAIN);

        JScrollPane scroll = new JScrollPane(createDashboardContent());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(BG_MAIN);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private void showPage(String cardName, String title, String subTitle, String menuName) {
        // Khóa Món mở dialog riêng, không dùng CardLayout
        if ("KHOAMON".equals(cardName)) {
            new DlgQuanLyKhoaMon(this).setVisible(true);
            return;
        }
        
        if ("MONAN".equals(cardName) && pnlMonAn != null) {
            pnlMonAn.lamMoiDuLieuMenu();
        }
        
        lbTitle.setText(title);
        lbSub.setText(subTitle);
        activeMenu = menuName;
        updateMenuState();
        cardLayout.show(contentPanel, cardName);
    }

    //DASHBOARD CONTENT
    private JPanel createDashboardContent() {
        JPanel content = new JPanel();
        content.setBackground(BG_MAIN);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        long doanhThuHomNay = hoaDonDAO.getDoanhThuHomNay();
        int tongDonHomNay = hoaDonDAO.getTongDonHomNay();
        int soNhanVienDangLam = hoaDonDAO.getSoNhanVienDangLam();
        double tyLeHuy = hoaDonDAO.getTyLeHuy();

        cardsRow.add(createStatCard("💰", "Doanh thu hôm nay", formatTien(doanhThuHomNay) + "đ", "", false,
                new Color(34, 197, 94)));

        cardsRow.add(createStatCard("🛒", "Tổng đơn hàng", String.valueOf(tongDonHomNay), "", false,
                new Color(99, 102, 241)));

        cardsRow.add(createStatCard("👤", "Nhân viên đang làm", String.valueOf(soNhanVienDangLam), "", false,
                new Color(168, 85, 247)));

        cardsRow.add(
                createStatCard("⚠", "Tỷ lệ hủy", String.format("%.1f%%", tyLeHuy), "", false, new Color(251, 146, 60)));

        content.add(cardsRow);
        content.add(Box.createVerticalStrut(18));

        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 14, 0));
        chartsRow.setOpaque(false);
        chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        List<String[]> dsDoanhThu7Ngay = hoaDonDAO.getDoanhThu7NgayGanNhat();
        List<String[]> dsTopMon = hoaDonDAO.getTop5MonBanChay();

        chartsRow.add(createRevenueChartCard("Doanh thu 7 ngày qua", dsDoanhThu7Ngay));
        chartsRow.add(createTopMonCard("Món ăn bán chạy nhất", dsTopMon));

        content.add(chartsRow);
        content.add(Box.createVerticalStrut(18));

        content.add(createLoginTodayTable());

        return content;
    }

    private JPanel createStatCard(String icon, String label, String value, String badge, boolean badgePositive,
                                  Color iconColor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 30);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(38, 38));
        iconBox.setOpaque(false);
        iconBox.setLayout(new GridBagLayout());

        JLabel lbIcon = new JLabel(icon);
        lbIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        iconBox.add(lbIcon);

        top.add(iconBox, BorderLayout.WEST);

        if (!badge.isEmpty()) {
            JLabel lbBadge = new JLabel(badge);
            lbBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbBadge.setForeground(badgePositive ? new Color(22, 163, 74) : new Color(220, 38, 38));

            JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
            badgeWrap.setOpaque(false);
            badgeWrap.add(lbBadge);

            top.add(badgeWrap, BorderLayout.EAST);
        }

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        JLabel lbLabel = new JLabel(label);
        lbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbLabel.setForeground(TEXT_GRAY);

        JLabel lbValue = new JLabel(value);
        lbValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbValue.setForeground(TEXT_DARK);

        bottom.add(lbLabel);
        bottom.add(Box.createVerticalStrut(4));
        bottom.add(lbValue);

        card.add(top, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createChartPlaceholder(String title, String hint) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbTitle.setForeground(TEXT_DARK);
        lbTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel placeholder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
                        new float[]{6, 4}, 0));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                g2.setColor(new Color(180, 180, 180));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(hint)) / 2;
                int ty = getHeight() / 2 + fm.getAscent() / 2;
                g2.drawString(hint, tx, ty);
                g2.dispose();
            }
        };
        placeholder.setOpaque(false);

        card.add(lbTitle, BorderLayout.NORTH);
        card.add(placeholder, BorderLayout.CENTER);

        return card;
    }

    private JPanel createLoginTodayTable() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        JLabel lbTitle = new JLabel("Nhân viên đã đăng nhập hôm nay");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbTitle.setForeground(TEXT_DARK);
        lbTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        String[] cols = {"Mã NV", "Họ tên", "Vai trò", "Giờ đăng nhập"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<String[]> ds = nhatKyDangNhapDAO.getDanhSachDangNhapHomNay();
        for (String[] row : ds) {
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(254, 242, 242));
        table.setSelectionForeground(TEXT_DARK);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        header.setPreferredSize(new Dimension(0, 36));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
                                                           int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setForeground(TEXT_DARK);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        tableScroll.getViewport().setBackground(Color.WHITE);

        card.add(lbTitle, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel createRevenueChartCard(String title, List<String[]> data) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbTitle.setForeground(TEXT_DARK);
        lbTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int left = 45;
                int right = 20;
                int top = 20;
                int bottom = 40;

                g2.setColor(new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, w, h, 12, 12);

                if (data == null || data.isEmpty()) {
                    g2.setColor(new Color(160, 160, 160));
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    String msg = "Chưa có dữ liệu";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
                    g2.dispose();
                    return;
                }

                long max = 1;
                for (String[] row : data) {
                    long val = Long.parseLong(row[1]);
                    if (val > max)
                        max = val;
                }

                g2.setColor(new Color(210, 210, 210));
                g2.drawLine(left, h - bottom, w - right, h - bottom);
                g2.drawLine(left, top, left, h - bottom);

                int count = data.size();
                int availableWidth = w - left - right - 20;
                int barGap = 18;
                int barWidth = Math.max(30, (availableWidth - (count - 1) * barGap) / count);

                for (int i = 0; i < count; i++) {
                    String ngay = data.get(i)[0];
                    long doanhThu = Long.parseLong(data.get(i)[1]);

                    int x = left + 10 + i * (barWidth + barGap);
                    int barHeight = (int) ((double) doanhThu / max * (h - top - bottom - 20));
                    int y = h - bottom - barHeight;

                    g2.setColor(new Color(220, 38, 38));
                    g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

                    g2.setColor(TEXT_DARK);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(ngay, x + (barWidth - fm.stringWidth(ngay)) / 2, h - 15);

                    String valueText = formatTien(doanhThu);
                    g2.setColor(new Color(90, 90, 90));
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    FontMetrics fm2 = g2.getFontMetrics();
                    g2.drawString(valueText, x + (barWidth - fm2.stringWidth(valueText)) / 2, y - 5);
                }

                g2.dispose();
            }
        };

        chartPanel.setPreferredSize(new Dimension(0, 220));
        chartPanel.setOpaque(false);

        card.add(lbTitle, BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTopMonCard(String title, List<String[]> data) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbTitle.setForeground(TEXT_DARK);
        lbTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        String[] cols = {"Tên món", "SL bán", "Doanh thu"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (data != null) {
            for (String[] row : data) {
                model.addRow(new Object[]{row[0], row[1], formatTien(Long.parseLong(row[2])) + "đ"});
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        header.setPreferredSize(new Dimension(0, 34));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
                                                           int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setForeground(TEXT_DARK);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(lbTitle, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private String formatTien(long soTien) {
        return String.format("%,d", soTien).replace(",", ".");
    }

}