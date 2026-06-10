package GUI;

import DAO.BanAnDAO;
import DAO.HoaDonDAO;
import DAO.PhieuDatBanDAO;
import Entity.BanAn;
import Entity.PhieuDatBan;
import Model.MonAnModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FrmQLSoDoBan extends JPanel {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BG_MAIN = new Color(248, 248, 248);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(40, 40, 40);
    private static final Color TEXT_GRAY = new Color(120, 120, 120);

    private static final Color BG_TRONG = new Color(220, 252, 231);
    private static final Color BORDER_TRONG = new Color(34, 197, 94);

    private static final Color BG_KHACH = new Color(254, 226, 226);
    private static final Color BORDER_KHACH = new Color(239, 68, 68);

    private static final Color BG_DAT = new Color(254, 249, 195);
    private static final Color BORDER_DAT = new Color(234, 179, 8);

    private final BanAnDAO banAnDAO = new BanAnDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final PhieuDatBanDAO phieuDAO = new PhieuDatBanDAO();

    private JPanel gridMap;
    private JPanel tabsContainer;
    private JPanel pnlDanhSachDatCho;
    private JLabel lblMapTitle;
    private String currentTab = "Tầng 1";

    public FrmQLSoDoBan() {
        initUI();
        refreshSoDoBan();
        loadDanhSachDatCho();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        JPanel root = new JPanel(new BorderLayout(16, 0));
        root.setBackground(BG_MAIN);
        root.setBorder(new EmptyBorder(20, 24, 24, 24));

        JPanel mainCard = new JPanel(new BorderLayout());
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                BorderFactory.createEmptyBorder()));

        mainCard.add(createTabs(), BorderLayout.NORTH);
        mainCard.add(createMapArea(), BorderLayout.CENTER);

        root.add(mainCard, BorderLayout.CENTER);
        root.add(createRightSidebar(), BorderLayout.EAST);

        add(root, BorderLayout.CENTER);
    }

    private JPanel createTabs() {
        tabsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        tabsContainer.setBackground(Color.WHITE);
        tabsContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(12, 20, 0, 20)));

        String[] tabNames = { "Tầng 1", "Tầng 2", "Phòng VIP" };
        for (int i = 0; i < tabNames.length; i++) {
            JLabel lbl = new JLabel(tabNames[i]);
            lbl.setFont(new Font("Segoe UI", i == 0 ? Font.BOLD : Font.PLAIN, 14));
            lbl.setForeground(i == 0 ? RED_MAIN : TEXT_DARK);
            lbl.setBorder(i == 0
                    ? BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 3, 0, RED_MAIN),
                            new EmptyBorder(0, 0, 8, 0))
                    : new EmptyBorder(0, 0, 11, 0));
            lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));

            lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    currentTab = lbl.getText();
                    updateTabUI();
                    refreshSoDoBan();
                }
            });

            tabsContainer.add(lbl);
        }
        return tabsContainer;
    }

    private void updateTabUI() {
        for (Component c : tabsContainer.getComponents()) {
            if (c instanceof JLabel lbl) {
                boolean selected = lbl.getText().equals(currentTab);
                lbl.setFont(new Font("Segoe UI", selected ? Font.BOLD : Font.PLAIN, 14));
                lbl.setForeground(selected ? RED_MAIN : TEXT_DARK);
                lbl.setBorder(selected
                        ? BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(0, 0, 3, 0, RED_MAIN),
                                new EmptyBorder(0, 0, 8, 0))
                        : new EmptyBorder(0, 0, 11, 0));
            }
        }
    }

    private JPanel createMapArea() {
        JPanel mapWrap = new JPanel(new BorderLayout());
        mapWrap.setBackground(Color.WHITE);
        mapWrap.setBorder(new EmptyBorder(18, 20, 20, 20));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        lblMapTitle = new JLabel("Sơ đồ bàn - " + currentTab);
        lblMapTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel sub = new JLabel("Chọn bàn để xem thông tin phục vụ / đặt chỗ");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_GRAY);

        header.add(lblMapTitle);
        header.add(sub);

        gridMap = new JPanel(new GridLayout(0, 4, 15, 15));
        gridMap.setOpaque(false);

        JPanel wrapGrid = new JPanel(new BorderLayout());
        wrapGrid.setOpaque(false);
        wrapGrid.add(gridMap, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapGrid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        mapWrap.add(header, BorderLayout.NORTH);
        mapWrap.add(scroll, BorderLayout.CENTER);

        return mapWrap;
    }

    private JPanel createTableCard(String maBan, String tenBan, int capacity, String status) {
        Color bg;
        Color border;
        String displayStatus = status == null ? "" : status.trim();

        if (displayStatus.equalsIgnoreCase("Trống")) {
            bg = BG_TRONG;
            border = BORDER_TRONG;
        } else if (displayStatus.equalsIgnoreCase("Có khách")
                || displayStatus.equalsIgnoreCase("Đang dùng")
                || displayStatus.equalsIgnoreCase("Chờ thanh toán")) {
            bg = BG_KHACH;
            border = BORDER_KHACH;
            displayStatus = "Có khách";
        } else if (displayStatus.toLowerCase().startsWith("đang ghép")) {
            bg = new Color(255, 237, 213);
            border = new Color(249, 115, 22);
        } else {
            bg = BG_DAT;
            border = BORDER_DAT;
            if (displayStatus.equalsIgnoreCase("Đã đặt")) {
                displayStatus = "Đã đặt";
            }
        }

        String finalStatus = displayStatus;

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(220, 132));
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel("🪑", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        icon.setForeground(border);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(tenBan, SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCap = new JLabel("Sức chứa: " + capacity + " người", SwingConstants.CENTER);
        lblCap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCap.setForeground(new Color(80, 80, 80));
        lblCap.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblStatus = new JLabel(finalStatus, SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(15));
        card.add(icon);
        card.add(Box.createVerticalStrut(5));
        card.add(lblName);
        card.add(Box.createVerticalStrut(5));
        card.add(lblCap);
        card.add(Box.createVerticalStrut(5));
        card.add(lblStatus);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(border, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (finalStatus.equalsIgnoreCase("Trống")) {
                    hienThiThongTinBanTrong(maBan, tenBan, capacity);
                } else if (finalStatus.equalsIgnoreCase("Có khách")) {
                    hienThiChiTietBanCoKhach(maBan, tenBan, capacity);
                } else if (finalStatus.equalsIgnoreCase("Đã đặt")) {
                    hienThiThongTinBanDaDat(maBan, tenBan, capacity);
                } else if (finalStatus.toLowerCase().startsWith("đang ghép")) {
                    String tenBanChinh = banAnDAO.getTenBanGhepChung(maBan);
                    String msg = "Bàn này đang được ghép chung hóa đơn với " + tenBanChinh + ".\n"
                            + "Khách đã dời sang bàn chính, bạn có muốn giải phóng bàn này thành bàn TRỐNG để đón khách mới không?";
                    int choice = JOptionPane.showConfirmDialog(FrmQLSoDoBan.this, msg, "Giải phóng bàn ghép",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (choice == JOptionPane.YES_OPTION) {
                        banAnDAO.capNhatTrangThai(maBan, "Trống");
                        refreshSoDoBan();
                        JOptionPane.showMessageDialog(FrmQLSoDoBan.this, "Đã dọn dẹp " + tenBan + " về trạng thái Trống!");
                    }
                } else {
                    hienThiThongTinBanTrong(maBan, tenBan, capacity);
                }
            }
        });

        return card;
    }

    public void refreshSoDoBan() {
        if (gridMap == null)
            return;

        gridMap.removeAll();
        if (lblMapTitle != null) {
            lblMapTitle.setText("Sơ đồ bàn - " + currentTab);
        }

        List<BanAn> danhSachBan = banAnDAO.getAllBanAn();
        for (BanAn ban : danhSachBan) {
            String viTri = ban.getViTri();
            if (viTri != null && viTri.trim().equalsIgnoreCase(currentTab)) {
                gridMap.add(createTableCard(
                        ban.getMaBan(),
                        ban.getTenBan(),
                        ban.getSucChua(),
                        ban.getTrangThai() == null ? "" : ban.getTrangThai().trim()));
            }
        }

        gridMap.revalidate();
        gridMap.repaint();
    }

    private JPanel createRightSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(340, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                BorderFactory.createEmptyBorder()));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Đặt chỗ hôm nay");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtSearch = new JTextField();
        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtSearch.setPreferredSize(new Dimension(0, 36));
        txtSearch.setText("Tìm khách hàng / SĐT");
        txtSearch.setForeground(TEXT_GRAY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(0, 10, 0, 10)));

        JLabel subTitle = new JLabel("Danh sách chờ khách");
        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subTitle.setBorder(new EmptyBorder(18, 0, 10, 0));
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        top.add(title);
        top.add(Box.createVerticalStrut(15));
        top.add(txtSearch);
        top.add(subTitle);

        pnlDanhSachDatCho = new JPanel();
        pnlDanhSachDatCho.setLayout(new BoxLayout(pnlDanhSachDatCho, BoxLayout.Y_AXIS));
        pnlDanhSachDatCho.setBackground(Color.WHITE);
        pnlDanhSachDatCho.setBorder(new EmptyBorder(0, 20, 0, 20));

        JScrollPane scroll = new JScrollPane(pnlDanhSachDatCho);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(scroll, BorderLayout.CENTER);
        return sidebar;
    }

    public void loadDanhSachDatCho() {
        if (pnlDanhSachDatCho == null)
            return;

        pnlDanhSachDatCho.removeAll();
        List<PhieuDatBan> danhSachPhieu = phieuDAO.getDanhSachDatChoChuaCheckIn();
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

        if (danhSachPhieu.isEmpty()) {
            JLabel emptyLabel = new JLabel("Không có khách đặt trước.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            emptyLabel.setForeground(TEXT_GRAY);
            pnlDanhSachDatCho.add(emptyLabel);
        } else {
            for (PhieuDatBan phieu : danhSachPhieu) {
                String timeStr = sdfTime.format(phieu.getThoiGianDen());
                String tenBanHienThi = layTenTatCaBanTheoPhieu(phieu.getMaPhieu(), phieu.getTenBan());

                pnlDanhSachDatCho.add(createBookingCard(
                        phieu.getMaPhieu(),
                        phieu.getTenKhachHang(),
                        phieu.getSoDienThoai(),
                        timeStr,
                        phieu.getMaBan(),
                        tenBanHienThi,
                        phieu.getSoLuongKhach()
                ));
                pnlDanhSachDatCho.add(Box.createVerticalStrut(12));
            }
        }

        pnlDanhSachDatCho.revalidate();
        pnlDanhSachDatCho.repaint();
    }

    private JPanel createBookingCard(String maPhieu, String name, String phone, String time,
            String maBan, String tenBan, int guests) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 252, 232));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(new Color(253, 224, 71));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(10, 10));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel info = new JPanel(new GridLayout(3, 2));
        info.setOpaque(false);

        JLabel lName = new JLabel(name);
        lName.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lTime = new JLabel(time, SwingConstants.RIGHT);
        lTime.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel lPhone = new JLabel(phone);
        lPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lPhone.setForeground(TEXT_GRAY);

        JLabel lTable = new JLabel("Bàn: " + tenBan);
        lTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lTable.setForeground(TEXT_GRAY);

        JLabel lGuests = new JLabel(guests + " người", SwingConstants.RIGHT);
        lGuests.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        info.add(lName);
        info.add(lTime);
        info.add(lPhone);
        info.add(new JLabel(""));
        info.add(lTable);
        info.add(lGuests);

        JLabel lbView = new JLabel("Xem chi tiết", SwingConstants.RIGHT);
        lbView.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbView.setForeground(RED_MAIN);

        card.add(info, BorderLayout.CENTER);
        card.add(lbView, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BanAn ban = timBanTheoMa(maBan);
                int sucChua = ban != null ? ban.getSucChua() : 0;
                hienThiThongTinBanDaDat(maBan, tenBan, sucChua);
            }
        });

        return card;
    }

    private BanAn timBanTheoMa(String maBan) {
        List<BanAn> ds = banAnDAO.getAllBanAn();
        for (BanAn b : ds) {
            if (b.getMaBan() != null && b.getMaBan().equalsIgnoreCase(maBan)) {
                return b;
            }
        }
        return null;
    }
    private String layTenTatCaBanTheoPhieu(String maPhieu, String tenBanMacDinh) {
        java.util.LinkedHashSet<String> dsTenBan = new java.util.LinkedHashSet<>();

        try {
            java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
            java.sql.PreparedStatement ps = con.prepareStatement(
                    "SELECT b.tenBan " +
                            "FROM ChiTietDatBan ct " +
                            "JOIN BanAn b ON ct.maBan = b.maBan " +
                            "WHERE ct.maPhieu = ?"
            );
            ps.setString(1, maPhieu);
            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String tenBan = rs.getString("tenBan");
                if (tenBan != null && !tenBan.trim().isEmpty()) {
                    dsTenBan.add(tenBan.trim());
                }
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!dsTenBan.isEmpty()) {
            return String.join(", ", dsTenBan);
        }

        return tenBanMacDinh != null ? tenBanMacDinh : "";
    }

    private void hienThiThongTinBanTrong(String maBan, String tenBan, int capacity) {
        JDialog dialog = createBaseDialog("Chi tiết " + tenBan, currentTab);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        content.add(createInfoSectionForTable(tenBan, capacity, currentTab, "Trống"));
        content.add(Box.createVerticalStrut(18));

        JPanel note = createSimpleCard();
        note.setLayout(new BorderLayout());
        note.add(new JLabel("Bàn hiện đang trống, chưa có khách và chưa có đặt chỗ."), BorderLayout.CENTER);
        content.add(note);

        dialog.getContentPane().add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void hienThiThongTinBanDaDat(String maBan, String tenBan, int capacity) {

        PhieuDatBan phieuMatch = phieuDAO.getPhieuDatBanByMaBan(maBan);

        if (phieuMatch == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin đặt chỗ cho " + tenBan,
                    "Thông tin đặt chỗ",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = createBaseDialog("Chi tiết " + tenBan, currentTab);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        content.add(createInfoSectionForTable(tenBan, capacity, currentTab, "Đã đặt"));
        content.add(Box.createVerticalStrut(20));

        JPanel bookingSection = createSimpleCard();
        bookingSection.setLayout(new BoxLayout(bookingSection, BoxLayout.Y_AXIS));
        bookingSection.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel lbSection = new JLabel("Thông tin đặt bàn");
        lbSection.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookingSection.add(lbSection);
        bookingSection.add(Box.createVerticalStrut(14));

        bookingSection.add(createDetailRow("Tên khách hàng", safe(phieuMatch.getTenKhachHang())));
        bookingSection.add(Box.createVerticalStrut(12));
        bookingSection.add(createDetailRow("Số điện thoại", safe(phieuMatch.getSoDienThoai())));
        bookingSection.add(Box.createVerticalStrut(12));

        String timeStr = "";
        if (phieuMatch.getThoiGianDen() != null) {
            timeStr = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(phieuMatch.getThoiGianDen());
        }
        bookingSection.add(createDetailRow("Thời gian đặt", timeStr));
        bookingSection.add(Box.createVerticalStrut(12));
        bookingSection.add(createDetailRow("Số khách", phieuMatch.getSoLuongKhach() + " người"));
        bookingSection.add(Box.createVerticalStrut(14));

        JPanel line = new JPanel();
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(0, 1));
        line.setBackground(BORDER_CLR);
        bookingSection.add(line);
        bookingSection.add(Box.createVerticalStrut(14));

        JPanel ttRow = new JPanel(new BorderLayout());
        ttRow.setOpaque(false);

        JLabel lbTt = new JLabel("Trạng thái");
        lbTt.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        ttRow.add(lbTt, BorderLayout.NORTH);
        ttRow.add(createBadgePanel("Đã xác nhận", new Color(220, 252, 231), new Color(22, 163, 74)), BorderLayout.WEST);

        bookingSection.add(ttRow);

        if (phieuMatch.getGhiChu() != null && !phieuMatch.getGhiChu().trim().isEmpty()) {
            bookingSection.add(Box.createVerticalStrut(14));
            bookingSection.add(createDetailRow("Ghi chú", phieuMatch.getGhiChu()));
        }

        content.add(bookingSection);

        dialog.getContentPane().add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void hienThiChiTietBanCoKhach(String maBan, String tenBan, int capacity) {
        String[] infoKhach = hoaDonDAO.getThongTinKhachVuaMo(maBan);

        String maHD = hoaDonDAO.getMaHoaDonDangPhucVuTheoBan(maBan);
        List<MonAnModel> dsMon = (maHD != null)
                ? hoaDonDAO.getChiTietHoaDon(maHD)
                : new java.util.ArrayList<>();

        JDialog dialog = createBaseDialog("Chi tiết " + tenBan, currentTab);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        content.add(createInfoSectionForTable(tenBan, capacity, currentTab, "Có khách"));
        content.add(Box.createVerticalStrut(20));

        JPanel guestCard = createSimpleCard();
        guestCard.setLayout(new BoxLayout(guestCard, BoxLayout.Y_AXIS));
        guestCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel lbGuestTitle = new JLabel("Thông tin khách");
        lbGuestTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        guestCard.add(lbGuestTitle);
        guestCard.add(Box.createVerticalStrut(14));

        String tenKhach = infoKhach != null ? safe(infoKhach[0]) : "Khách lẻ";
        String sdt = infoKhach != null ? safe(infoKhach[1]) : "";
        String soLuong = infoKhach != null ? safe(infoKhach[2]) + " người" : "";
        String gioVao = infoKhach != null ? safe(infoKhach[3]) : "";

        guestCard.add(createDetailRow("Tên khách hàng", tenKhach));
        guestCard.add(Box.createVerticalStrut(12));
        guestCard.add(createDetailRow("Số điện thoại", sdt));
        guestCard.add(Box.createVerticalStrut(12));
        guestCard.add(createDetailRow("Số khách", soLuong));
        guestCard.add(Box.createVerticalStrut(12));
        guestCard.add(createDetailRow("Thời gian", gioVao));

        content.add(guestCard);
        content.add(Box.createVerticalStrut(20));

        JLabel lbOrderTitle = new JLabel("Thông tin đơn hàng");
        lbOrderTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbOrderTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(lbOrderTitle);
        content.add(Box.createVerticalStrut(12));

        long tamTinh = 0;
        if (dsMon != null && !dsMon.isEmpty()) {
            for (MonAnModel mon : dsMon) {
                long tien = mon.thanhTien;
                tamTinh += tien;
                content.add(createMonItem(mon.tenMonAn, mon.soLuong, tien, mon.trangThaiPhucVu));
                content.add(Box.createVerticalStrut(12));
            }
        } else {
            JPanel empty = createSimpleCard();
            empty.setLayout(new BorderLayout());
            empty.add(new JLabel("Chưa có món nào trong bàn này."), BorderLayout.CENTER);
            content.add(empty);
            content.add(Box.createVerticalStrut(12));
        }

        long phiDichVu = Math.round(tamTinh * 0.05);
        long vat = Math.round(tamTinh * 0.08);
        long tongCong = tamTinh + phiDichVu + vat;

        JPanel totalCard = createSimpleCard();
        totalCard.setLayout(new BoxLayout(totalCard, BoxLayout.Y_AXIS));
        totalCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        totalCard.add(createMoneyRow("Tạm tính", formatTien(tamTinh) + " đ", false));
        totalCard.add(Box.createVerticalStrut(10));
        totalCard.add(createMoneyRow("Phí dịch vụ (5%)", formatTien(phiDichVu) + " đ", false));
        totalCard.add(Box.createVerticalStrut(10));
        totalCard.add(createMoneyRow("VAT (8%)", formatTien(vat) + " đ", false));
        totalCard.add(Box.createVerticalStrut(12));

        JPanel line = new JPanel();
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(0, 1));
        line.setBackground(BORDER_CLR);
        totalCard.add(line);
        totalCard.add(Box.createVerticalStrut(12));

        totalCard.add(createMoneyRow("Tổng cộng", formatTien(tongCong) + " đ", true));

        content.add(Box.createVerticalStrut(8));
        content.add(totalCard);
        content.add(Box.createVerticalStrut(16));

        JPanel timeFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timeFooter.setOpaque(false);
        JLabel lbTime = new JLabel("Thời gian: " + gioVao);
        lbTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbTime.setForeground(TEXT_GRAY);
        timeFooter.add(lbTime);
        content.add(timeFooter);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        dialog.getContentPane().add(scroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JDialog createBaseDialog(String title, String subTitle) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(680, 760);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(20, 24, 20, 24)));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbTitle.setForeground(TEXT_DARK);

        JLabel lbSub = new JLabel(subTitle);
        lbSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbSub.setForeground(TEXT_GRAY);

        titleBox.add(lbTitle);
        titleBox.add(Box.createVerticalStrut(6));
        titleBox.add(lbSub);

        JButton btnClose = new JButton("");
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dialog.dispose());

        top.add(titleBox, BorderLayout.WEST);
        top.add(btnClose, BorderLayout.EAST);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(12, 16, 12, 16)));

        JButton btnDong = new JButton("Đóng");
        btnDong.setFocusPainted(false);
        btnDong.setBorderPainted(false);
        btnDong.setContentAreaFilled(false);
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDong.setForeground(new Color(30, 64, 175));
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDong.addActionListener(e -> dialog.dispose());
        bottom.add(btnDong);

        dialog.add(top, BorderLayout.NORTH);
        dialog.add(bottom, BorderLayout.SOUTH);

        return dialog;
    }

    private JPanel createInfoSectionForTable(String tenBan, int capacity, String viTri, String trangThai) {
        JPanel infoSection = createSimpleCard();
        infoSection.setLayout(new GridLayout(2, 2, 18, 18));
        infoSection.setBorder(new EmptyBorder(18, 18, 18, 18));

        infoSection.add(createInfoItem("Số bàn", tenBan));
        infoSection.add(createInfoItem("Sức chứa", capacity + " người"));
        infoSection.add(createInfoItem("Vị trí", viTri));

        JPanel statusItem = new JPanel();
        statusItem.setOpaque(false);
        statusItem.setLayout(new BoxLayout(statusItem, BoxLayout.Y_AXIS));

        JLabel lbTitle = new JLabel("Trạng thái");
        lbTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbTitle.setForeground(TEXT_GRAY);
        lbTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlBadge = createStatusBadge(trangThai);
        pnlBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusItem.add(lbTitle);
        statusItem.add(Box.createVerticalStrut(4));
        statusItem.add(pnlBadge);

        infoSection.add(statusItem);
        return infoSection;
    }

    private JPanel createInfoItem(String label, String value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel lb = new JLabel(label);
        lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lb.setForeground(TEXT_GRAY);
        lb.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 15));
        val.setForeground(TEXT_DARK);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lb);
        p.add(Box.createVerticalStrut(4));
        p.add(val);

        return p;
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel lb = new JLabel(label);
        lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lb.setForeground(TEXT_GRAY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(TEXT_DARK);
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        p.add(lb, BorderLayout.WEST);
        p.add(val, BorderLayout.EAST);
        return p;
    }

    private JPanel createSimpleCard() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(249, 250, 251));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(12, 12, 12, 12)));
        return panel;
    }

    private JPanel createMonItem(String tenMon, int soLuong, long giaTien, String trangThaiMon) {
        JPanel item = createSimpleCard();
        item.setLayout(new BorderLayout(0, 8));
        item.setBorder(new EmptyBorder(12, 16, 12, 16));


        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lbTen = new JLabel(tenMon != null ? tenMon : "");
        lbTen.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbTen.setForeground(TEXT_DARK);

        JLabel lbTien = new JLabel(formatTien(giaTien) + " đ");
        lbTien.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbTien.setForeground(RED_MAIN);

        top.add(lbTen, BorderLayout.WEST);
        top.add(lbTien, BorderLayout.EAST);


        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel lbQty = new JLabel("Số lượng: " + soLuong);
        lbQty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbQty.setForeground(TEXT_GRAY);

        bottom.add(createStatusBadge(trangThaiMon != null ? trangThaiMon : "Chưa lên"), BorderLayout.WEST);
        bottom.add(lbQty, BorderLayout.EAST);

        item.add(top, BorderLayout.NORTH);
        item.add(bottom, BorderLayout.SOUTH);

        return item;
    }

    private JPanel createMoneyRow(String leftText, String rightText, boolean highlight) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel left = new JLabel(leftText);
        left.setFont(new Font("Segoe UI", highlight ? Font.BOLD : Font.PLAIN, highlight ? 15 : 14));
        left.setForeground(TEXT_DARK);

        JLabel right = new JLabel(rightText);
        right.setFont(new Font("Segoe UI", Font.BOLD, highlight ? 18 : 14));
        right.setForeground(highlight ? RED_MAIN : TEXT_DARK);

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JPanel createStatusBadge(String status) {
        String text = safe(status);
        Color bg = new Color(243, 244, 246);
        Color fg = TEXT_GRAY;

        if (text.equalsIgnoreCase("Có khách")) {
            bg = new Color(254, 226, 226);
            fg = new Color(220, 38, 38);
        } else if (text.equalsIgnoreCase("Đã đặt")) {
            bg = new Color(254, 249, 195);
            fg = new Color(202, 138, 4);
        } else if (text.equalsIgnoreCase("Trống")) {
            bg = new Color(220, 252, 231);
            fg = new Color(22, 163, 74);
        } else if (text.equalsIgnoreCase("Chưa lên")) {
            bg = new Color(254, 242, 242);
            fg = new Color(220, 38, 38);
        } else if (text.equalsIgnoreCase("Đã lên")) {
            bg = new Color(220, 252, 231);
            fg = new Color(22, 163, 74);
        } else if (text.equalsIgnoreCase("Mang về")) {
            bg = new Color(219, 234, 254);
            fg = new Color(37, 99, 235);
        } else if (text.equalsIgnoreCase("Hủy")) {
            bg = new Color(243, 244, 246);
            fg = new Color(107, 114, 128);
        }

        return createBadgePanel(text, bg, fg);
    }

    private JPanel createBadgePanel(String text, Color bg, Color fg) {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setOpaque(false);

        JLabel badge = new JLabel(" " + text + " ");
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));

        wrap.add(badge);
        return wrap;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String formatTien(long so) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(so);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Quản Lý Sơ Đồ Bàn");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1450, 860);
            f.setLocationRelativeTo(null);
            f.setContentPane(new FrmQLSoDoBan());
            f.setVisible(true);
        });
    }
}