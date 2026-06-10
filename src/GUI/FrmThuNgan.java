package GUI;

import DAO.HoaDonDAO;
import Model.BanAnModel;
import Model.MonAnModel;
import Entity.NhanVien;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.BaseFont;

@SuppressWarnings("serial")
public class FrmThuNgan extends JFrame {

    private final String tenNhanVien;
    private final HoaDonDAO dao = new HoaDonDAO();

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BG_MAIN = new Color(248, 248, 248);
    private static final Color TEXT_DARK = new Color(20, 20, 20);
    private static final Color TEXT_GRAY = new Color(120, 120, 120);
    private static final Color BORDER_CLR = new Color(225, 225, 225);

    private List<BanAnModel> danhSachBan = new ArrayList<>();
    private BanAnModel banDangChon = null;
    private JPanel pnlDanhSachBan;
    private JPanel pnlHoaDon;
    private JLabel lbTienThua;
    private JTextField txtKhachDua;
    private JLabel lbTienCoc;
    private JLabel lbConPhaiThanhToan;
    private JLabel lbTienHoanKhach;
    private long tongCuoiCung = 0;
    private long tienGiamHienTai = 0;
    private long phiDichVuHienTai = 0;
    private long vatHienTai = 0;
    private long soTienCanThu = 0;
    private long soTienHoanKhach = 0;

    private JComboBox<String> cboKM_Current;
    private JComboBox<String> cboPhongThucTT;
    private List<String[]> dsKM_Current = new ArrayList<>();
    private JLabel lbTamTinh, lbGiamGia, lbPhiDV, lbVAT, lbTongTien;
    private long giaTriTamTinh = 0;

    private List<MonAnModel> dsMonHienTai = new ArrayList<>();

    public FrmThuNgan(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
        initUI();
        taiDanhSachBan();

        // Khởi động polling thông báo (3 giây/lần)
        Service.NotificationService.getInstance().startPolling(3000);

        Timer timer = new Timer(5000, e -> {
            System.out.println("Đang cập nhật danh sách bàn");
            taiDanhSachBan();
        });
        timer.start();
    }

    public FrmThuNgan(NhanVien nhanVien) {
        this(nhanVien.getHoTenNV());
    }

    private void initUI() {
        setTitle("Thanh toán & Hóa đơn - Nhà Hàng Ngói Đỏ");
        setSize(1440, 860);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_MAIN);
        root.add(createTopBar(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        split.setDividerLocation(350);
        split.setDividerSize(1);
        split.setBorder(null);

        // ... Bọc split pane và thêm side bar nút bấm ...
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(split, BorderLayout.CENTER);
        centerWrapper.add(createRightSideBarButtons(), BorderLayout.EAST);

        root.add(centerWrapper, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void taiDanhSachBan() {
        new SwingWorker<List<BanAnModel>, Void>() {
            @Override
            protected List<BanAnModel> doInBackground() {
                return dao.getDanhSachBanChuaThanhToan();
            }

            @Override
            protected void done() {
                try {
                    danhSachBan = get();
                    capNhatGiaoDienDanhSachBan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void capNhatGiaoDienDanhSachBan() {
        pnlDanhSachBan.removeAll();
        if (danhSachBan == null || danhSachBan.isEmpty()) {
            JLabel lbNull = new JLabel("Không có bàn chờ thanh toán");
            lbNull.setForeground(TEXT_GRAY);
            lbNull.setBorder(new EmptyBorder(20, 20, 0, 0));
            pnlDanhSachBan.add(lbNull);
        } else {
            for (BanAnModel ban : danhSachBan) {
                pnlDanhSachBan.add(createBanCard(ban));
                pnlDanhSachBan.add(Box.createVerticalStrut(12));
            }
        }
        pnlDanhSachBan.revalidate();
        pnlDanhSachBan.repaint();
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(15, 25, 15, 25)));

        JPanel west = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        west.setOpaque(false);

        JLabel lblLogo = new JLabel("🏮");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblLogo.setForeground(RED_MAIN);

        JLabel lbTitle = new JLabel("Nhà Hàng Ngói Đỏ - Thu Ngân");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        west.add(lblLogo);
        west.add(lbTitle);

        JLabel lbUser = new JLabel(
                "" + tenNhanVien + " | " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lbUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbUser.setForeground(TEXT_DARK);

        JButton btnCaiDat = new ModernButton("Cài đặt", ModernButton.Style.OUTLINE);
        btnCaiDat.setPreferredSize(new Dimension(100, 34));
        btnCaiDat.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Cài đặt cá nhân", true);
            dialog.setSize(900, 700);
            dialog.setLocationRelativeTo(this);
            dialog.setContentPane(new GUI.FrmCaiDat());
            dialog.setVisible(true);
        });

        JButton btnLogout = new ModernButton("Đăng xuất", ModernButton.Style.GHOST);
        btnLogout.setPreferredSize(new Dimension(110, 34));
        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất không?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                dispose();
                new FrmDangNhap().setVisible(true);
            }
        });

        JButton btnLichSu = new JButton("Lịch sử hóa đơn");
        btnLichSu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLichSu.setBackground(new Color(219, 234, 254));
        btnLichSu.setForeground(new Color(30, 58, 138));
        btnLichSu.setFocusPainted(false);
        btnLichSu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLichSu.addActionListener(e -> moLichSuHoaDon());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Chuông thông báo
        NotificationBellPanel bellPanel = new NotificationBellPanel();

        rightPanel.add(lbUser);
        rightPanel.add(btnLichSu);
        rightPanel.add(bellPanel);
        rightPanel.add(btnCaiDat);
        rightPanel.add(btnLogout);

        bar.add(west, BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    private void moLichSuHoaDon() {
        JDialog dialog = new JDialog(this, "Lịch sử Hóa Đơn", true);
        dialog.setSize(1200, 680);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(18, 20, 12, 20));

        JLabel title = new JLabel("Lịch sử giao dịch");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // ── DÒNG 1: Ngày + nút
        JPanel pnlFilter1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlFilter1.setOpaque(false);

        JLabel lblTuNgay = new JLabel("Từ ngày:"); lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner spinTuNgay = new JSpinner(new SpinnerDateModel());
        spinTuNgay.setEditor(new JSpinner.DateEditor(spinTuNgay, "dd/MM/yyyy"));
        spinTuNgay.setPreferredSize(new Dimension(120, 32));

        JLabel lblDenNgay = new JLabel("Đến ngày:"); lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner spinDenNgay = new JSpinner(new SpinnerDateModel());
        spinDenNgay.setEditor(new JSpinner.DateEditor(spinDenNgay, "dd/MM/yyyy"));
        spinDenNgay.setPreferredSize(new Dimension(120, 32));

        JButton btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(RED_MAIN); btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 13)); btnTimKiem.setFocusPainted(false);

        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setBackground(new Color(240, 240, 240));
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 13)); btnLamMoi.setFocusPainted(false);

        pnlFilter1.add(lblTuNgay); pnlFilter1.add(spinTuNgay);
        pnlFilter1.add(lblDenNgay); pnlFilter1.add(spinDenNgay);
        pnlFilter1.add(btnTimKiem); pnlFilter1.add(btnLamMoi);

        // ── DÒNG 2: Phương thức TT + SĐT
        JPanel pnlFilter2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlFilter2.setOpaque(false);

        JLabel lblPhuongThuc = new JLabel("Phương thức TT:"); lblPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComboBox<String> cboPhuongThuc = new JComboBox<>(new String[]{"Tất cả", "Tiền mặt", "Chuyển khoản", "Quẹt thẻ"});
        cboPhuongThuc.setPreferredSize(new Dimension(150, 32)); cboPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblSDT = new JLabel("SĐT khách:"); lblSDT.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField txtSDT = new JTextField();
        txtSDT.setPreferredSize(new Dimension(140, 32)); txtSDT.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSDT.setToolTipText("Nhập SĐT để tìm (có thể nhập một phần)");

        pnlFilter2.add(lblPhuongThuc); pnlFilter2.add(cboPhuongThuc);
        pnlFilter2.add(Box.createHorizontalStrut(10));
        pnlFilter2.add(lblSDT); pnlFilter2.add(txtSDT);

        JPanel pnlFilters = new JPanel();
        pnlFilters.setLayout(new BoxLayout(pnlFilters, BoxLayout.Y_AXIS));
        pnlFilters.setOpaque(false);
        pnlFilters.add(pnlFilter1);
        pnlFilters.add(Box.createVerticalStrut(8));
        pnlFilters.add(pnlFilter2);

        header.add(title, BorderLayout.NORTH);
        header.add(pnlFilters, BorderLayout.CENTER);

        // ── BẢNG: thêm cột SĐT và Phương thức TT
        // Cols: maHD(0), tenBan(1), tenKhach(2), sdt(3), phuongThuc(4), thoiGian(5), tongTien(6), trangThai(7)
        String[] cols = {"Mã Hóa Đơn", "Bàn", "Khách hàng", "SĐT", "Phương thức TT", "Thời gian TT", "Tổng tiền", "Trạng thái"};
        javax.swing.table.DefaultTableModel modelList = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(modelList);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(254, 242, 242));
        table.setSelectionForeground(TEXT_DARK);
        // Độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 20, 20, 20), BorderFactory.createLineBorder(BORDER_CLR)));
        scroll.getViewport().setBackground(Color.WHITE);

        // Double-click → xem chi tiết (col indices đã thay đổi)
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String maHD    = table.getValueAt(row, 0).toString();
                        String tenBan  = table.getValueAt(row, 1).toString();
                        String tenKhach = table.getValueAt(row, 2).toString();
                        String tongTien = table.getValueAt(row, 6).toString(); // col 6
                        hienThiChiTietHoaDonLichSu(maHD, tenBan, tenKhach, tongTien);
                    }
                }
            }
        });

        // Hàm tải dữ liệu với tất cả filter
        Runnable loadData = () -> {
            modelList.setRowCount(0);
            List<String[]> data = dao.getLichSuHoaDonTheoNgay(null, null);
            for (String[] row : data) modelList.addRow(row);
        };

        // Tìm kiếm
        btnTimKiem.addActionListener(e -> {
            java.util.Date tuNgay  = (java.util.Date) spinTuNgay.getValue();
            java.util.Date denNgay = (java.util.Date) spinDenNgay.getValue();
            if (tuNgay.after(denNgay)) {
                JOptionPane.showMessageDialog(dialog, "'Từ ngày' không được lớn hơn 'Đến ngày'!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String pt  = cboPhuongThuc.getSelectedIndex() == 0 ? null : cboPhuongThuc.getSelectedItem().toString();
            String sdt = txtSDT.getText().trim().isEmpty() ? null : txtSDT.getText().trim();
            modelList.setRowCount(0);
            List<String[]> data = dao.getLichSuHoaDonTheoNgay(tuNgay, denNgay, pt, sdt);
            for (String[] row : data) modelList.addRow(row);
            if (data.isEmpty())
                JOptionPane.showMessageDialog(dialog, "Không tìm thấy hóa đơn phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnLamMoi.addActionListener(e -> {
            spinTuNgay.setValue(new java.util.Date());
            spinDenNgay.setValue(new java.util.Date());
            cboPhuongThuc.setSelectedIndex(0);
            txtSDT.setText("");
            loadData.run();
        });

        loadData.run();
        dialog.add(header, BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(Color.WHITE);
        left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR));

        JLabel lbTitle = new JLabel("DANH SÁCH CHỜ THANH TOÁN");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbTitle.setForeground(TEXT_GRAY);
        lbTitle.setBorder(new EmptyBorder(25, 20, 15, 20));

        pnlDanhSachBan = new JPanel();
        pnlDanhSachBan.setBackground(Color.WHITE);
        pnlDanhSachBan.setLayout(new BoxLayout(pnlDanhSachBan, BoxLayout.Y_AXIS));
        pnlDanhSachBan.setBorder(new EmptyBorder(0, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(pnlDanhSachBan);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        left.add(lbTitle, BorderLayout.NORTH);
        left.add(scroll, BorderLayout.CENTER);
        return left;
    }

    private JPanel createBanCard(BanAnModel ban) {
        boolean isSelected = (banDangChon != null && ban.maHD != null && ban.maHD.equals(banDangChon.maHD));
        RoundedPanel card = new RoundedPanel(10, true, isSelected ? new Color(0xFEF2F2) : Color.WHITE);
        card.setLayout(new BorderLayout(15, 5));
        card.setBorder(new EmptyBorder(15, 18, 15, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(320, 60));
        card.setPreferredSize(new Dimension(320, 60));

        JLabel lbTen = new JLabel(ban.tenBan);
        lbTen.setFont(UIConstants.FONT_BOLD_16);
        lbTen.setForeground(isSelected ? UIConstants.PRIMARY : UIConstants.TEXT_DARK);

        JLabel lbTong = new JLabel(formatTien(ban.tamTinh) + " đ");
        lbTong.setFont(UIConstants.FONT_BOLD_14);
        lbTong.setForeground(isSelected ? UIConstants.PRIMARY : UIConstants.TEXT_GRAY);

        card.add(lbTen, BorderLayout.WEST);
        card.add(lbTong, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                banDangChon = ban;
                capNhatGiaoDienDanhSachBan();
                hienThiHoaDon(ban);
            }
        });
        return card;
    }

    private JPanel createRightPanel() {
        pnlHoaDon = new JPanel(new BorderLayout());
        pnlHoaDon.setBackground(BG_MAIN);
        hienThiChoChon();
        return pnlHoaDon;
    }

    private JPanel createRightSideBarButtons() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_CLR));

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnBaoCao = new ModernButton("Báo cáo cuối ngày", ModernButton.Style.OUTLINE);
        btnBaoCao.setPreferredSize(new Dimension(270, 45));
        btnBaoCao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnBaoCao.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn chốt ca và xem báo cáo cuối ngày không?",
                    "Xác nhận báo cáo cuối ngày",
                    JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                // Mở dialog báo cáo doanh thu chốt ca
                new DlgBaoCaoCuoiNgay(FrmThuNgan.this).setVisible(true);
            }
        });

        JButton btnTachHD = new ModernButton("Tách hóa đơn", ModernButton.Style.PRIMARY);
        btnTachHD.setPreferredSize(new Dimension(270, 45));
        btnTachHD.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnTachHD.addActionListener(e -> {
            if (banDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn đang chờ thanh toán!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Tất cả hóa đơn trong danh sách này đều ở trạng thái "Chờ thanh toán"
            hienThiDialogTachHoaDon();
        });

        buttons.add(btnBaoCao);
        buttons.add(Box.createVerticalStrut(15));
        buttons.add(btnTachHD);

        sidebar.add(buttons, BorderLayout.NORTH);
        return sidebar;
    }

    private void hienThiChoChon() {
        pnlHoaDon.removeAll();
        JLabel lbHint = new JLabel(
                "<html><center><font size='5' color='#777777'>🏮</font><br>Vui lòng chọn bàn cần thanh toán bên trái</center></html>");
        lbHint.setHorizontalAlignment(SwingConstants.CENTER);
        pnlHoaDon.add(lbHint, BorderLayout.CENTER);
        pnlHoaDon.revalidate();
        pnlHoaDon.repaint();
    }

    private void hienThiHoaDon(BanAnModel ban) {
        pnlHoaDon.removeAll();

        List<MonAnModel> dsMon = dao.getChiTietHoaDon(ban.maHD);
        dsMonHienTai = dsMon;
        dsKM_Current = dao.getKhuyenMaiHieuLuc();

        String[] tenKMs = dsKM_Current.stream().map(k -> k[1]).toArray(String[]::new);
        cboKM_Current = new JComboBox<>(tenKMs);
        cboKM_Current.setPreferredSize(new Dimension(250, 35));
        cboKM_Current.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboKM_Current.addActionListener(e -> tinhToanLai());

        JPanel container = new JPanel(new BorderLayout(0, 0));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(15, 25, 10, 25));

        // ── PHẦN TRÊN: danh sách món (có thể cuộn) ──
        JPanel itemsPaper = buildItemsPaper(ban, dsMon);
        JScrollPane scrollBill = new JScrollPane(itemsPaper);
        scrollBill.setBorder(null);
        scrollBill.getViewport().setBackground(BG_MAIN);
        scrollBill.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollBill.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollBill.getVerticalScrollBar().setUnitIncrement(16);

        // ── PHẦN DƯỚI: tổng kết + nút (luôn hiển thị) ──
        JPanel bottomFixed = new JPanel(new BorderLayout(0, 8));
        bottomFixed.setOpaque(false);
        bottomFixed.add(buildSummaryPanel(), BorderLayout.CENTER);
        bottomFixed.add(buildFooterActions(ban), BorderLayout.SOUTH);

        container.add(scrollBill, BorderLayout.CENTER);
        container.add(bottomFixed, BorderLayout.SOUTH);

        pnlHoaDon.add(container, BorderLayout.CENTER);
        pnlHoaDon.revalidate();
        pnlHoaDon.repaint();
        tinhToanLai();
    }

    // Phần TRÊN: tiêu đề + thông tin khách + danh sách món (cuộn được)
    private JPanel buildItemsPaper(BanAnModel ban, List<MonAnModel> dsMon) {
        JPanel paper = new JPanel();
        paper.setBackground(Color.WHITE);
        paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));
        paper.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 2), new EmptyBorder(30, 50, 20, 50)));

        JLabel lbHdTitle = new JLabel("HÓA ĐƠN CHI TIẾT");
        lbHdTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbHdTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        paper.add(lbHdTitle);
        paper.add(Box.createVerticalStrut(15));

        String ten    = (ban.tenKH != null) ? ban.tenKH : "Khách vãng lai";
        String sdt    = (ban.sdt != null && !ban.sdt.trim().isEmpty()) ? ban.sdt : "Trống";
        String gioVao = (ban.gioVao != null) ? ban.gioVao : "--:--";

        JPanel pnlInfo = new JPanel(new GridLayout(2, 2, 30, 10));
        pnlInfo.setOpaque(false);
        JLabel lbInfoKhach = new JLabel("Khách: " + ten);   lbInfoKhach.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel lbInfoVao   = new JLabel("Giờ vào: " + gioVao); lbInfoVao.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel lbInfoSDT   = new JLabel("SĐT: " + sdt);     lbInfoSDT.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel lbInfoRa    = new JLabel("Giờ ra: " + new SimpleDateFormat("HH:mm").format(new Date()));
        lbInfoRa.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        pnlInfo.add(lbInfoKhach); pnlInfo.add(lbInfoVao);
        pnlInfo.add(lbInfoSDT);   pnlInfo.add(lbInfoRa);

        paper.add(pnlInfo);
        paper.add(Box.createVerticalStrut(20));
        paper.add(new JSeparator());
        paper.add(Box.createVerticalStrut(15));

        giaTriTamTinh = 0;
        for (MonAnModel mon : dsMon) {
            paper.add(createItemRow(mon));
            paper.add(Box.createVerticalStrut(12));
            giaTriTamTinh += mon.thanhTien;
        }
        paper.add(Box.createVerticalStrut(10));
        return paper;
    }

    // Phần DƯỚI: tổng kết, phương thức TT, khách đưa (cố định, luôn hiển thị)
    private JPanel buildSummaryPanel() {
        JPanel paper = new JPanel();
        paper.setBackground(Color.WHITE);
        paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));
        paper.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 2), new EmptyBorder(15, 50, 15, 50)));

        lbTamTinh = new JLabel(formatTien(giaTriTamTinh) + " đ");
        lbGiamGia = new JLabel("0 đ");
        lbPhiDV   = new JLabel("0 đ");
        lbVAT     = new JLabel("0 đ");
        lbTongTien = new JLabel("0 đ");

        lbTienCoc = new JLabel("0 đ");
        lbTienCoc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbTienCoc.setForeground(new Color(0, 128, 0));

        lbConPhaiThanhToan = new JLabel("0 đ");
        lbConPhaiThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbConPhaiThanhToan.setForeground(RED_MAIN);

        lbTienHoanKhach = new JLabel("0 đ");
        lbTienHoanKhach.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbTienHoanKhach.setForeground(new Color(34, 197, 94));

        lbTongTien.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbTongTien.setForeground(RED_MAIN);

        txtKhachDua = new JTextField("");
        txtKhachDua.setPreferredSize(new Dimension(200, 36));
        txtKhachDua.setMaximumSize(new Dimension(200, 36));
        txtKhachDua.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtKhachDua.setHorizontalAlignment(JTextField.RIGHT);
        txtKhachDua.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { tinhTienThua(); }
        });

        lbTienThua = new JLabel("0 đ");
        lbTienThua.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbTienThua.setForeground(new Color(0, 150, 0));

        // --- Dòng 1: Tóm tắt số tiền ---
        paper.add(createSummaryRow("Tạm tính tổng món:", lbTamTinh));
        paper.add(createSummaryRow("Khuyến mãi:", cboKM_Current, lbGiamGia));
        paper.add(createSummaryRow("Phí dịch vụ (5%):", lbPhiDV));
        paper.add(createSummaryRow("VAT (10%):", lbVAT));
        paper.add(createSummaryRow("Tổng cộng:", lbTongTien));
        paper.add(createSummaryRow("Đã cọc:", lbTienCoc));
        paper.add(createSummaryRow("Còn phải thanh toán:", lbConPhaiThanhToan));
        paper.add(createSummaryRow("Hoàn lại khách:", lbTienHoanKhach));

        // --- Phương thức thanh toán ---
        cboPhongThucTT = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Quẹt thẻ"});
        cboPhongThucTT.setPreferredSize(new Dimension(200, 36));
        cboPhongThucTT.setMaximumSize(new Dimension(200, 36));
        cboPhongThucTT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        paper.add(createSummaryRow("Phương thức thanh toán:", cboPhongThucTT));

        // --- Khách đưa / Tiền thừa ---
        paper.add(Box.createVerticalStrut(10));
        paper.add(new JSeparator());
        paper.add(Box.createVerticalStrut(10));
        paper.add(createSummaryRow("Khách đưa:", txtKhachDua));
        paper.add(Box.createVerticalStrut(8));
        paper.add(createSummaryRow("Tiền thừa trả khách:", lbTienThua));
        return paper;
    }

    private void tinhToanLai() {
        if (dsKM_Current == null || dsKM_Current.isEmpty() || cboKM_Current == null) {
            return;
        }

        int idx = cboKM_Current.getSelectedIndex();
        double giaTriKM = Double.parseDouble(dsKM_Current.get(idx)[2]);
        String loaiKM = dsKM_Current.get(idx)[3];

        long tienGiam;
        if ("Phần trăm".equalsIgnoreCase(loaiKM)) {
            tienGiam = (long) (giaTriTamTinh * giaTriKM / 100.0);
        } else {
            tienGiam = (long) giaTriKM;
        }

        if (tienGiam < 0)
            tienGiam = 0;
        if (tienGiam > giaTriTamTinh)
            tienGiam = giaTriTamTinh;

        long sauGiam = giaTriTamTinh - tienGiam;
        long phiDV = (long) (sauGiam * 0.05);
        long vat = (long) (sauGiam * 0.10);

        tienGiamHienTai = tienGiam;
        phiDichVuHienTai = phiDV;
        vatHienTai = vat;

        tongCuoiCung = Math.max(0, sauGiam + phiDV + vat);

        long tienCoc = (banDangChon != null) ? banDangChon.tienCoc : 0;
        soTienCanThu = Math.max(0, tongCuoiCung - tienCoc);
        soTienHoanKhach = Math.max(0, tienCoc - tongCuoiCung);

        lbGiamGia.setText("-" + formatTien(tienGiamHienTai) + " đ");
        lbPhiDV.setText(formatTien(phiDichVuHienTai) + " đ");
        lbVAT.setText(formatTien(vatHienTai) + " đ");
        lbTongTien.setText(formatTien(tongCuoiCung) + " đ");
        lbTienCoc.setText("-" + formatTien(tienCoc) + " đ");
        lbConPhaiThanhToan.setText(formatTien(soTienCanThu) + " đ");
        lbTienHoanKhach.setText(formatTien(soTienHoanKhach) + " đ");

        tinhTienThua();
    }

    private void tinhTienThua() {
        try {
            if (soTienCanThu == 0) {
                lbTienThua.setText("0 đ");
                lbTienThua.setForeground(new Color(0, 150, 0));
                return;
            }

            String s = txtKhachDua.getText().trim().replace(".", "").replace(",", "");
            if (s.isEmpty()) {
                lbTienThua.setText("0 đ");
                lbTienThua.setForeground(new Color(0, 150, 0));
                return;
            }

            long khachDua = Long.parseLong(s);
            long thua = khachDua - soTienCanThu;

            if (thua < 0) {
                lbTienThua.setText("Chưa đủ tiền");
                lbTienThua.setForeground(Color.RED);
            } else {
                lbTienThua.setText(formatTien(thua) + " đ");
                lbTienThua.setForeground(new Color(0, 150, 0));
            }
        } catch (Exception e) {
            lbTienThua.setText("Số tiền không hợp lệ");
            lbTienThua.setForeground(Color.RED);
        }
    }

    private JPanel buildFooterActions(BanAnModel ban) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        pnl.setOpaque(false);

        JButton btnHuy = new ModernButton("Hủy hóa đơn", ModernButton.Style.OUTLINE);
        btnHuy.setPreferredSize(new Dimension(150, 50));
        btnHuy.addActionListener(e -> xacNhanHuyDon(ban));

        // Payment button with red gradient via Graphics2D
        JButton btnPay = new JButton("XÁC NHẬN THANH TOÁN") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.PRIMARY, 0, getHeight(), UIConstants.PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fill(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() / 2f, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnPay.setPreferredSize(new Dimension(280, 50));
        btnPay.setFocusPainted(false);
        btnPay.setBorderPainted(false);
        btnPay.setContentAreaFilled(false);
        btnPay.setOpaque(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPay.addActionListener(e -> xuLyThanhToan(ban));

        pnl.add(btnHuy);
        pnl.add(btnPay);
        return pnl;
    }

    private void xuLyThanhToan(BanAnModel ban) {
        long khachDua = 0;
        long tienThua = 0;

        if (soTienCanThu > 0) {
            String s = txtKhachDua.getText().trim().replace(".", "").replace(",", "");

            if (s.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền khách đưa.");
                txtKhachDua.requestFocus();
                return;
            }

            try {
                khachDua = Long.parseLong(s);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số tiền khách đưa không hợp lệ.");
                txtKhachDua.requestFocus();
                txtKhachDua.selectAll();
                return;
            }

            if (khachDua <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền khách đưa phải lớn hơn 0.");
                txtKhachDua.requestFocus();
                txtKhachDua.selectAll();
                return;
            }

            if (khachDua < soTienCanThu) {
                JOptionPane.showMessageDialog(this, "Khách đưa chưa đủ tiền để thanh toán.\n" + "Khách đưa: "
                        + formatTien(khachDua) + " đ\n" + "Còn phải thanh toán: " + formatTien(soTienCanThu) + " đ");
                txtKhachDua.requestFocus();
                txtKhachDua.selectAll();
                return;
            }

            tienThua = khachDua - soTienCanThu;
        }

        String msg;
        if (soTienHoanKhach > 0) {
            msg = "Xác nhận thanh toán cho " + ban.tenBan + "?\n\n" + "Tổng cộng: " + formatTien(tongCuoiCung) + " đ\n"
                    + "Đã cọc: " + formatTien(ban.tienCoc) + " đ\n" + "Còn phải thanh toán: 0 đ\n" + "Hoàn lại khách: "
                    + formatTien(soTienHoanKhach) + " đ";
        } else {
            msg = "Xác nhận thanh toán cho " + ban.tenBan + "?\n\n" + "Tổng cộng: " + formatTien(tongCuoiCung) + " đ\n"
                    + "Đã cọc: " + formatTien(ban.tienCoc) + " đ\n" + "Còn phải thanh toán: " + formatTien(soTienCanThu)
                    + " đ\n" + "Khách đưa: " + formatTien(khachDua) + " đ\n" + "Tiền thừa: " + formatTien(tienThua)
                    + " đ";
        }

        int opt = JOptionPane.showConfirmDialog(this, msg, "Thanh toán", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) {
            return;
        }

        String maKM = dsKM_Current.get(cboKM_Current.getSelectedIndex())[0];
        String phuongThucTT = cboPhongThucTT != null
                ? (String) cboPhongThucTT.getSelectedItem()
                : "Tiền mặt";

        if (dao.thanhToan(ban.maHD, soTienCanThu, tienGiamHienTai, maKM, khachDua, tienThua, phuongThucTT)) {
            // Lấy danh sách tất cả mã bàn liên quan đến hóa đơn (bao gồm bàn chính và bàn phụ bị ghép)
            List<String> dsBan = new DAO.BanAnDAO().getDanhSachMaBanTheoHoaDon(ban.maHD);
            for (String maBanGhep : dsBan) {
                new DAO.BanAnDAO().capNhatTrangThai(maBanGhep, "Trống");
            }

            JOptionPane.showMessageDialog(this, "Đã lưu hóa đơn và giải phóng bàn!");

            int export = JOptionPane.showConfirmDialog(this, "Bạn có muốn xuất hóa đơn PDF không?",
                    "Xuất hóa đơn PDF", JOptionPane.YES_NO_OPTION);

            if (export == JOptionPane.YES_OPTION) {
                xuatHoaDonPDF(ban, dsMonHienTai, khachDua, tienThua, phuongThucTT);
            }

            banDangChon = null;
            taiDanhSachBan();
            hienThiChoChon();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi: Không thể kết nối cơ sở dữ liệu!");
        }
    }

    private void xacNhanHuyDon(BanAnModel ban) {
        int opt = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn HỦY đơn hàng này?\nBàn sẽ quay về trạng thái Trống.", "Cảnh báo",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION && dao.huyHoaDon(ban.maHD)) {
            banDangChon = null;
            taiDanhSachBan();
            hienThiChoChon();
        }
    }

    private JPanel createItemRow(MonAnModel mon) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel lbTen = new JLabel(mon.tenMonAn + " (x" + mon.soLuong + ")");
        lbTen.setFont(new Font("Segoe UI", Font.PLAIN, 18));  // Tăng từ 15

        JLabel lbGia = new JLabel(formatTien(mon.thanhTien) + " đ");
        lbGia.setFont(new Font("Segoe UI", Font.BOLD, 18));  // Tăng từ 15

        p.add(lbTen, BorderLayout.WEST);
        p.add(lbGia, BorderLayout.EAST);
        return p;
    }

    private JPanel createSummaryRow(String label, JComponent... components) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));  // Tăng từ 40

        JLabel lb = new JLabel(label);
        lb.setFont(new Font("Segoe UI", Font.PLAIN, 17));  // Tăng từ 15
        lb.setForeground(new Color(80, 80, 80));
        p.add(lb, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);
        for (JComponent c : components) {
            right.add(c);
        }
        p.add(right, BorderLayout.EAST);
        return p;
    }


    private void xuatHoaDonPDF(BanAnModel ban, List<MonAnModel> dsMon, long khachDua, long tienThua, String phuongThucTT) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu hóa đơn PDF");
        chooser.setSelectedFile(new File("HoaDon_" + ban.tenBan.replaceAll("\\s+", "_") + "_" + ban.maHD + ".pdf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }

        Document document = new Document(PageSize.A5, 20, 20, 30, 30);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();


            BaseFont bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            com.lowagie.text.Font fontTitle = new com.lowagie.text.Font(bf, 16, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font fontBold = new com.lowagie.text.Font(bf, 11, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font fontNormal = new com.lowagie.text.Font(bf, 10, com.lowagie.text.Font.NORMAL);
            com.lowagie.text.Font fontItalic = new com.lowagie.text.Font(bf, 10, com.lowagie.text.Font.ITALIC);

            // 2. HEADER NHÀ HÀNG
            Paragraph title = new Paragraph("NHÀ HÀNG NGÓI ĐỎ", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph sub = new Paragraph("HÓA ĐƠN THANH TOÁN\n---------------------------------------------------", fontNormal);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(10);
            document.add(sub);

            // 3. THÔNG TIN HÓA ĐƠN
            document.add(new Paragraph("Mã hóa đơn: " + ban.maHD, fontNormal));
            document.add(new Paragraph("Bàn: " + ban.tenBan, fontNormal));
            document.add(new Paragraph("Khách hàng: " + (ban.tenKH != null ? ban.tenKH : "Khách vãng lai"), fontNormal));
            document.add(new Paragraph("Giờ vào: " + (ban.gioVao != null ? ban.gioVao : "--:--"), fontNormal));
            document.add(new Paragraph("Giờ ra: " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date()), fontNormal));
            document.add(new Paragraph("Thu ngân: " + tenNhanVien, fontNormal));
            document.add(new Paragraph("-----------------------------------------------------------------------------------------", fontNormal));

            // 4. BẢNG MÓN ĂN
            PdfPTable table = new PdfPTable(new float[]{4f, 1f, 2f});
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);
            table.setSpacingAfter(5);

            table.addCell(createCell("Tên món", fontBold, Element.ALIGN_LEFT, true));
            table.addCell(createCell("SL", fontBold, Element.ALIGN_CENTER, true));
            table.addCell(createCell("Thành tiền", fontBold, Element.ALIGN_RIGHT, true));

            for (MonAnModel mon : dsMon) {
                table.addCell(createCell(mon.tenMonAn, fontNormal, Element.ALIGN_LEFT, false));
                table.addCell(createCell(String.valueOf(mon.soLuong), fontNormal, Element.ALIGN_CENTER, false));
                table.addCell(createCell(formatTien(mon.thanhTien), fontNormal, Element.ALIGN_RIGHT, false));
            }
            document.add(table);
            document.add(new Paragraph("-----------------------------------------------------------------------------------------", fontNormal));

            // 5. PHẦN TỔNG TIỀN (Căn lề phải)
            PdfPTable tSummary = new PdfPTable(new float[]{3f, 2f});
            tSummary.setWidthPercentage(100);

            String tenKM = cboKM_Current != null && cboKM_Current.getSelectedItem() != null ? cboKM_Current.getSelectedItem().toString() : "Không";

            tSummary.addCell(createCell("Tạm tính:", fontNormal, Element.ALIGN_RIGHT, false));
            tSummary.addCell(createCell(formatTien(giaTriTamTinh), fontNormal, Element.ALIGN_RIGHT, false));

            tSummary.addCell(createCell("Khuyến mãi (" + tenKM + "):", fontNormal, Element.ALIGN_RIGHT, false));
            tSummary.addCell(createCell(lbGiamGia.getText().replace(" đ", ""), fontNormal, Element.ALIGN_RIGHT, false));

            tSummary.addCell(createCell("Phí dịch vụ:", fontNormal, Element.ALIGN_RIGHT, false));
            tSummary.addCell(createCell(lbPhiDV.getText().replace(" đ", ""), fontNormal, Element.ALIGN_RIGHT, false));

            tSummary.addCell(createCell("VAT:", fontNormal, Element.ALIGN_RIGHT, false));
            tSummary.addCell(createCell(lbVAT.getText().replace(" đ", ""), fontNormal, Element.ALIGN_RIGHT, false));

            tSummary.addCell(createCell("TỔNG CỘNG:", fontBold, Element.ALIGN_RIGHT, false));
            tSummary.addCell(createCell(formatTien(tongCuoiCung), fontBold, Element.ALIGN_RIGHT, false));

            tSummary.addCell(createCell("Đã cọc:", fontNormal, Element.ALIGN_RIGHT, false));
            tSummary.addCell(createCell("-" + formatTien(ban.tienCoc), fontNormal, Element.ALIGN_RIGHT, false));

            tSummary.addCell(createCell("CẦN THANH TOÁN:", fontBold, Element.ALIGN_RIGHT, false));
            tSummary.addCell(createCell(formatTien(soTienCanThu), fontBold, Element.ALIGN_RIGHT, false));

            tSummary.addCell(createCell("Phương thức TT:", fontBold, Element.ALIGN_RIGHT, false));
            tSummary.addCell(createCell(phuongThucTT != null ? phuongThucTT : "Tiền mặt", fontBold, Element.ALIGN_RIGHT, false));

            document.add(tSummary);
            document.add(new Paragraph("-----------------------------------------------------------------------------------------", fontNormal));

            // 6. TIỀN KHÁCH ĐƯA
            PdfPTable tCus = new PdfPTable(new float[]{3f, 2f});
            tCus.setWidthPercentage(100);
            tCus.addCell(createCell("Khách đưa:", fontNormal, Element.ALIGN_RIGHT, false));
            tCus.addCell(createCell(formatTien(khachDua), fontNormal, Element.ALIGN_RIGHT, false));
            tCus.addCell(createCell("Tiền thừa:", fontNormal, Element.ALIGN_RIGHT, false));
            tCus.addCell(createCell(formatTien(tienThua), fontNormal, Element.ALIGN_RIGHT, false));
            document.add(tCus);

            // 7. LỜI CẢM ƠN
            document.add(new Paragraph("\n"));
            Paragraph thanks = new Paragraph("Cảm ơn Quý khách và hẹn gặp lại!", fontItalic);
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            JOptionPane.showMessageDialog(this, "Xuất PDF thành công:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF. Đảm bảo máy tính có font Arial.\nChi tiết: " + e.getMessage());
        } finally {
            if (document.isOpen()) document.close();
        }
    }

    private PdfPCell createCell(String text, com.lowagie.text.Font font, int alignment, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        if (isHeader) {
            cell.setBorder(com.lowagie.text.Rectangle.BOTTOM);
            cell.setBorderWidthBottom(1f);
            cell.setPaddingBottom(8);
        }
        return cell;
    }




    private void hienThiDialogTachHoaDon() {
        if (banDangChon == null || dsMonHienTai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bàn này không có món nào để tách!");
            return;
        }

        JDialog dialog = new JDialog(this, "Tách hóa đơn - " + banDangChon.tenBan, true);
        dialog.setSize(760, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(15, 20, 15, 20)));
        JLabel title = new JLabel("Chọn món và số lượng để tách sang hóa đơn mới");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel lblTongMoi = new JLabel("Tổng HĐ mới: 0 đ");
        lblTongMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongMoi.setForeground(RED_MAIN);
        header.add(title, BorderLayout.WEST);
        header.add(lblTongMoi, BorderLayout.EAST);

        // === TABLE MODEL ===
        // Cols: Chọn | Tên món | SL gốc | SL tách | Đơn giá | [ẩn: ID_CTHD] | [ẩn: giaBan]
        String[] columns = {"Chọn", "Tên món", "SL gốc", "SL tách", "Đơn giá", "ID_CTHD", "giaBan"};
        javax.swing.table.DefaultTableModel modelSplit = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 0) return Boolean.class;
                if (col == 2 || col == 3 || col == 5) return Integer.class;
                if (col == 6) return Long.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                if (col == 0) return true;
                if (col == 3) {
                    Object v = getValueAt(row, 0);
                    return v instanceof Boolean && (Boolean) v;
                }
                return false;
            }
        };

        for (MonAnModel mon : dsMonHienTai) {
            modelSplit.addRow(new Object[]{
                    false, mon.tenMonAn, mon.soLuong, 0,
                    formatTien(mon.giaBan) + " đ", mon.id_cthd, mon.giaBan
            });
        }

        // Khi check/uncheck hoặc sửa SL tách → cập nhật preview
        modelSplit.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 0) {
                Boolean checked = (Boolean) modelSplit.getValueAt(row, 0);
                int slGoc = (Integer) modelSplit.getValueAt(row, 2);
                modelSplit.setValueAt(checked ? slGoc : 0, row, 3);
            }
            // Tính tổng HĐ mới
            long tong = 0;
            for (int i = 0; i < modelSplit.getRowCount(); i++) {
                Boolean ck = (Boolean) modelSplit.getValueAt(i, 0);
                if (ck != null && ck) {
                    Object slObj = modelSplit.getValueAt(i, 3);
                    int sl = (slObj instanceof Integer) ? (Integer) slObj : 0;
                    long gia = (Long) modelSplit.getValueAt(i, 6);
                    tong += sl * gia;
                }
            }
            lblTongMoi.setText("Tổng HĐ mới: " + formatTien(tong) + " đ");
        });

        JTable tableSplit = new JTable(modelSplit);
        tableSplit.setRowHeight(32);
        tableSplit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableSplit.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableSplit.getTableHeader().setBackground(new Color(245, 245, 245));
        tableSplit.setSelectionBackground(new Color(254, 242, 242));

        // Ẩn cột ID_CTHD và giaBan
        tableSplit.getColumnModel().getColumn(5).setMinWidth(0); tableSplit.getColumnModel().getColumn(5).setMaxWidth(0);
        tableSplit.getColumnModel().getColumn(6).setMinWidth(0); tableSplit.getColumnModel().getColumn(6).setMaxWidth(0);

        // Độ rộng cột
        tableSplit.getColumnModel().getColumn(0).setPreferredWidth(55);
        tableSplit.getColumnModel().getColumn(1).setPreferredWidth(250);
        tableSplit.getColumnModel().getColumn(2).setPreferredWidth(75);
        tableSplit.getColumnModel().getColumn(3).setPreferredWidth(80);
        tableSplit.getColumnModel().getColumn(4).setPreferredWidth(110);

        JScrollPane scroll = new JScrollPane(tableSplit);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        scroll.getViewport().setBackground(Color.WHITE);

        // === FOOTER ===
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 38));
        btnHuy.addActionListener(e -> dialog.dispose());

        JButton btnTach = new JButton("Xác nhận tách");
        btnTach.setPreferredSize(new Dimension(165, 38));
        btnTach.setBackground(RED_MAIN);
        btnTach.setForeground(Color.WHITE);
        btnTach.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTach.setFocusPainted(false);
        btnTach.setBorderPainted(false);
        btnTach.addActionListener(e -> {
            // Dừng cell editing nếu đang nhập
            if (tableSplit.isEditing()) tableSplit.getCellEditor().stopCellEditing();

            java.util.Map<Integer, Integer> dsMonTach = new java.util.LinkedHashMap<>();

            for (int i = 0; i < modelSplit.getRowCount(); i++) {
                Boolean checked = (Boolean) modelSplit.getValueAt(i, 0);
                if (checked == null || !checked) continue;

                String tenMon = modelSplit.getValueAt(i, 1).toString();
                int slGoc  = (Integer) modelSplit.getValueAt(i, 2);
                Object slObj = modelSplit.getValueAt(i, 3);
                int slTach = (slObj instanceof Integer) ? (Integer) slObj : 0;
                int idCTHD = (Integer) modelSplit.getValueAt(i, 5);

                if (slTach <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Số lượng tách phải > 0 cho món: " + tenMon);
                    return;
                }
                if (slTach > slGoc) {
                    JOptionPane.showMessageDialog(dialog, "SL tách không được vượt quá SL gốc (" + slGoc + ") cho món: " + tenMon);
                    return;
                }
                dsMonTach.put(idCTHD, slTach);
            }

            if (dsMonTach.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn ít nhất 1 món để tách!");
                return;
            }

            // Kiểm tra HĐ gốc phải còn ít nhất 1 món
            boolean conMonGoc = false;
            for (int i = 0; i < modelSplit.getRowCount(); i++) {
                Boolean checked = (Boolean) modelSplit.getValueAt(i, 0);
                int slGoc = (Integer) modelSplit.getValueAt(i, 2);
                Object slObj = modelSplit.getValueAt(i, 3);
                int slTach = (slObj instanceof Integer) ? (Integer) slObj : 0;
                if (checked == null || !checked || slTach < slGoc) {
                    conMonGoc = true;
                    break;
                }
            }
            if (!conMonGoc) {
                JOptionPane.showMessageDialog(dialog,
                        "Không thể tách toàn bộ món!\nHóa đơn gốc phải còn ít nhất 1 món.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int opt = JOptionPane.showConfirmDialog(dialog,
                    "Xác nhận tách " + dsMonTach.size() + " loại món sang hóa đơn mới?\n"
                    + "Hóa đơn mới sẽ xuất hiện trong danh sách chờ thanh toán.",
                    "Xác nhận tách hóa đơn", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;

            String maHDMoi = dao.tachHoaDon(banDangChon.maHD, dsMonTach);
            if (maHDMoi != null) {
                JOptionPane.showMessageDialog(dialog,
                        "✅ Tách hóa đơn thành công!\nMã HĐ mới: " + maHDMoi,
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                banDangChon = null;
                taiDanhSachBan();
                hienThiChoChon();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "❌ Tách hóa đơn thất bại! Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(btnHuy);
        footer.add(btnTach);

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    private void hienThiChiTietHoaDonLichSu(String maHD, String tenBan, String tenKhach, String tongTienStr) {
        Object[] info = dao.getThongTinChiTietHoaDonLichSu(maHD);
        String gioVao      = info[0] != null ? info[0].toString() : "--:--";
        String gioRa       = info[1] != null ? info[1].toString() : "--:--";
        String thuNgan     = info[2] != null ? info[2].toString() : "---";
        long   tienCoc     = info[3] != null ? (long) info[3] : 0;
        long   tienGiam    = info[4] != null ? (long) info[4] : 0;
        String tenKM       = info[5] != null ? info[5].toString() : "Không";
        long   tienKhachDua      = info[6] != null ? (long) info[6] : 0;
        long   tienThuaTraKhach  = info[7] != null ? (long) info[7] : 0;
        String phuongThuc  = info[8] != null ? info[8].toString() : "Không xác định";
        String sdtKhach    = info[9] != null ? info[9].toString() : "---";

        JDialog dialog = new JDialog(this, "Chi tiết Hóa Đơn: " + maHD, true);
        dialog.setSize(880, 800);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel paper = new JPanel();
        paper.setBackground(Color.WHITE);
        paper.setLayout(new BoxLayout(paper, BoxLayout.Y_AXIS));
        paper.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lbTitle = new JLabel("HÓA ĐƠN CHI TIẾT");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        paper.add(lbTitle);
        paper.add(Box.createVerticalStrut(20));

        // Grid 4 hàng x 2 cột: thêm SĐT và Phương thức TT
        JPanel pnlInfo = new JPanel(new GridLayout(4, 2, 20, 10));
        pnlInfo.setOpaque(false);
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Mã Hóa Đơn:</span> <b>" + maHD + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Thu ngân:</span> <b>" + thuNgan + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Khách hàng:</span> <b>" + tenKhach + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>SĐT:</span> <b>" + sdtKhach + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Bàn:</span> <b>" + tenBan + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Phương thức TT:</span> <b style='color:#16a34a'>" + phuongThuc + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Giờ vào:</span> <b>" + gioVao + "</b></html>"));
        pnlInfo.add(new JLabel("<html><span style='color:gray'>Giờ ra:</span> <b>" + gioRa + "</b></html>"));
        paper.add(pnlInfo);

        paper.add(Box.createVerticalStrut(20));
        paper.add(new JSeparator());
        paper.add(Box.createVerticalStrut(20));

        long tamTinh = 0;
        List<MonAnModel> dsMon = dao.getChiTietHoaDon(maHD);
        JPanel pnlMonAn = new JPanel();
        pnlMonAn.setLayout(new BoxLayout(pnlMonAn, BoxLayout.Y_AXIS));
        pnlMonAn.setOpaque(false);
        for (MonAnModel m : dsMon) {
            JPanel pRow = new JPanel(new BorderLayout());
            pRow.setOpaque(false);
            JLabel lbTen = new JLabel(m.tenMonAn + " (x" + m.soLuong + ")");
            lbTen.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            JLabel lbGia = new JLabel(formatTien(m.thanhTien) + " đ");
            lbGia.setFont(new Font("Segoe UI", Font.BOLD, 15));
            pRow.add(lbTen, BorderLayout.WEST);
            pRow.add(lbGia, BorderLayout.EAST);
            pnlMonAn.add(pRow);
            pnlMonAn.add(Box.createVerticalStrut(10));
            tamTinh += m.thanhTien;
        }
        paper.add(pnlMonAn);

        paper.add(Box.createVerticalStrut(15));
        paper.add(new JSeparator());
        paper.add(Box.createVerticalStrut(15));

        long sauGiam = Math.max(0, tamTinh - tienGiam);
        long phiDV   = (long) (sauGiam * 0.05);
        long vat     = (long) (sauGiam * 0.10);
        long tongCong = sauGiam + phiDV + vat;
        long soTienCanThu    = Math.max(0, tongCong - tienCoc);
        long soTienThucThu   = Math.max(0, tienKhachDua - tienThuaTraKhach);
        long hoanCoc         = Math.max(0, tienCoc - tongCong);

        paper.add(createSummaryRow("Tạm tính:", new JLabel(formatTien(tamTinh) + " đ")));
        paper.add(createSummaryRow("Khuyến mãi (" + tenKM + "):", new JLabel("-" + formatTien(tienGiam) + " đ")));
        paper.add(createSummaryRow("Phí dịch vụ (5%):", new JLabel(formatTien(phiDV) + " đ")));
        paper.add(createSummaryRow("VAT (10%):", new JLabel(formatTien(vat) + " đ")));

        JLabel lbTong = new JLabel(formatTien(tongCong) + " đ");
        lbTong.setFont(new Font("Segoe UI", Font.BOLD, 22)); lbTong.setForeground(RED_MAIN);
        paper.add(createSummaryRow("TỔNG CỘNG:", lbTong));

        JLabel lbCoc = new JLabel("-" + formatTien(tienCoc) + " đ");
        lbCoc.setFont(new Font("Segoe UI", Font.BOLD, 16)); lbCoc.setForeground(new Color(22, 163, 74));
        paper.add(createSummaryRow("Đã cọc:", lbCoc));
        paper.add(createSummaryRow("Cần thanh toán:", new JLabel(formatTien(soTienCanThu) + " đ")));

        // Phương thức TT nổi bật
        JLabel lbPT = new JLabel(phuongThuc);
        lbPT.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbPT.setForeground(new Color(22, 101, 52));
        paper.add(createSummaryRow("Phương thức thanh toán:", lbPT));

        paper.add(createSummaryRow("Khách đưa:", new JLabel(formatTien(tienKhachDua) + " đ")));
        paper.add(createSummaryRow("Tiền thừa trả khách:", new JLabel(formatTien(tienThuaTraKhach) + " đ")));
        if (hoanCoc > 0) paper.add(createSummaryRow("Hoàn lại từ tiền cọc:", new JLabel(formatTien(hoanCoc) + " đ")));

        JLabel lbDaThu = new JLabel(formatTien(soTienThucThu) + " đ");
        lbDaThu.setFont(new Font("Segoe UI", Font.BOLD, 18));
        paper.add(createSummaryRow("SỐ TIỀN THỰC THU:", lbDaThu));

        JScrollPane scroll = new JScrollPane(paper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private PdfPCell createBodyCell(String text, int align) {
        com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA, 11);
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        return cell;
    }

    private String formatTien(long so) {
        return String.format("%,d", so).replace(",", ".");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new FrmThuNgan("Quản trị viên").setVisible(true);
        });
    }
}
