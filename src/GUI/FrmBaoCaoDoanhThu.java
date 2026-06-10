package GUI;

import DAO.HoaDonDAO;
import connectDatabase.ConnectDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

// Thư viện PDF
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

// Thư viện Excel (Apache POI)
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Thư viện JCalendar
import com.toedter.calendar.JDateChooser;

import java.io.File;
import java.io.FileOutputStream;

public class FrmBaoCaoDoanhThu extends JPanel {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final String tenNhanVien;

    private JLabel lbTongDT, lbSoDon, lbGiaTBDon, lbKhachHang;

    private LineChartPanel lineChart;
    private PieChartPanel pieChart;

    private DefaultTableModel tblModel;


    private JComboBox<String> cboPeriod;
    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;
    private ModernButton btnLoc;

    private long currentTongDT;
    private int currentSoDon;
    private int currentSoKH;
    private long currentGiaTB;
    private List<long[]> currentChartData = new ArrayList<>();
    private List<String[]> currentTopMon = new ArrayList<>();
    private List<String[]> currentPhanBo = new ArrayList<>();

    public FrmBaoCaoDoanhThu(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_APP);

        // Panel chứa toàn bộ nội dung
        JPanel allContent = new JPanel(new BorderLayout(0, 0));
        allContent.setOpaque(false);
        allContent.setBorder(new EmptyBorder(20, 24, 24, 24));

        JPanel north = new JPanel(new BorderLayout(0, 16));
        north.setOpaque(false);
        north.add(createTopActions(), BorderLayout.NORTH);
        north.add(createStatCards(), BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(16, 0, 0, 0));
        center.add(createChartsRow(), BorderLayout.NORTH);
        center.add(createBottomTable(), BorderLayout.CENTER);

        allContent.add(north, BorderLayout.NORTH);
        allContent.add(center, BorderLayout.CENTER);

        // Toàn bộ nội dung vào 1 scroll duy nhất
        JScrollPane scroll = new JScrollPane(allContent);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIConstants.BG_APP);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createTopActions() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel leftBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBox.setOpaque(false);

        JLabel lbKieuLoc = new JLabel("Lọc theo:");
        lbKieuLoc.setFont(UIConstants.FONT_BOLD_13);
        lbKieuLoc.setForeground(UIConstants.TEXT_DARK);

        cboPeriod = new JComboBox<>(new String[]{"7 ngày qua", "30 ngày qua", "Tháng này", "Tùy chỉnh"});
        cboPeriod.setFont(UIConstants.FONT_PLAIN_13);
        cboPeriod.setPreferredSize(new Dimension(120, 32));

        JLabel lbTuNgay = new JLabel("Từ:");
        lbTuNgay.setFont(UIConstants.FONT_PLAIN_13);
        lbTuNgay.setForeground(UIConstants.TEXT_DARK);
        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setPreferredSize(new Dimension(130, 32));
        dateTuNgay.setEnabled(false);

        JLabel lbDenNgay = new JLabel("Đến:");
        lbDenNgay.setFont(UIConstants.FONT_PLAIN_13);
        lbDenNgay.setForeground(UIConstants.TEXT_DARK);
        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setPreferredSize(new Dimension(130, 32));
        dateDenNgay.setEnabled(false);

        btnLoc = new ModernButton("Lọc", ModernButton.Style.PRIMARY);
        btnLoc.setPreferredSize(new Dimension(100, 32));

        cboPeriod.addActionListener(e -> {
            boolean isCustom = cboPeriod.getSelectedIndex() == 3;
            dateTuNgay.setEnabled(isCustom);
            dateDenNgay.setEnabled(isCustom);
            if (!isCustom) {
                loadData();
            }
        });

        btnLoc.addActionListener(e -> loadData());

        leftBox.add(lbKieuLoc);
        leftBox.add(cboPeriod);
        leftBox.add(lbTuNgay);
        leftBox.add(dateTuNgay);
        leftBox.add(lbDenNgay);
        leftBox.add(dateDenNgay);
        leftBox.add(btnLoc);

        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBox.setOpaque(false);

        ModernButton btnXuatPDF = new ModernButton("Xuất PDF", ModernButton.Style.PRIMARY);
        btnXuatPDF.setPreferredSize(new Dimension(110, 32));
        btnXuatPDF.addActionListener(e -> xuatBaoCaoPDF());

        ModernButton btnXuatExcel = new ModernButton("Xuất Excel", ModernButton.Style.PRIMARY);
        btnXuatExcel.setPreferredSize(new Dimension(110, 32));
        btnXuatExcel.addActionListener(e -> xuatBaoCaoExcel());

        rightBox.add(btnXuatPDF);
        rightBox.add(btnXuatExcel);

        p.add(leftBox, BorderLayout.WEST);
        p.add(rightBox, BorderLayout.EAST);
        return p;
    }


    private JPanel createStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);

        lbTongDT = new JLabel("...");
        lbSoDon = new JLabel("...");
        lbGiaTBDon = new JLabel("...");
        lbKhachHang = new JLabel("...");

        row.add(createStatCard("💰", "Tổng doanh thu", lbTongDT, "", true, new Color(34, 197, 94)));
        row.add(createStatCard("🛒", "Số đơn hàng", lbSoDon, "", false, new Color(99, 102, 241)));
        row.add(createStatCard("📊", "Giá trị TB/đơn", lbGiaTBDon, "", true, new Color(168, 85, 247)));
        row.add(createStatCard("👥", "Khách hàng", lbKhachHang, "", false, new Color(251, 146, 60)));

        return row;
    }

    private JPanel createStatCard(String icon, String label, JLabel valueLabel, String badge, boolean positive,
                                  Color color) {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setBackground(UIConstants.CARD_BG);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(42, 42));
        iconBox.setOpaque(false);
        iconBox.setLayout(new GridBagLayout());
        JLabel lbIcon = new JLabel(icon);
        lbIcon.setFont(UIConstants.FONT_EMOJI_28);
        iconBox.add(lbIcon);
        top.add(iconBox, BorderLayout.WEST);

        JLabel lbBadge = new JLabel(badge);
        lbBadge.setFont(UIConstants.FONT_BOLD_12);
        lbBadge.setForeground(positive ? new Color(22, 163, 74) : UIConstants.PRIMARY);
        JPanel bw = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        bw.setOpaque(false);
        bw.add(lbBadge);
        top.add(bw, BorderLayout.EAST);

        JPanel bot = new JPanel();
        bot.setOpaque(false);
        bot.setLayout(new BoxLayout(bot, BoxLayout.Y_AXIS));
        JLabel lbLab = new JLabel(label);
        lbLab.setFont(UIConstants.FONT_PLAIN_12);
        lbLab.setForeground(UIConstants.TEXT_GRAY);
        valueLabel.setFont(UIConstants.FONT_BOLD_20);
        valueLabel.setForeground(UIConstants.TEXT_DARK);
        bot.add(lbLab);
        bot.add(Box.createVerticalStrut(4));
        bot.add(valueLabel);

        card.add(top, BorderLayout.NORTH);
        card.add(bot, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createChartsRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 320));

        RoundedPanel lineCard = new RoundedPanel(14);
        lineCard.setLayout(new BorderLayout());
        lineCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        lineCard.setBackground(UIConstants.CARD_BG);
        JLabel lbLC = new JLabel("Xu hướng doanh thu");
        lbLC.setFont(UIConstants.FONT_BOLD_14);
        lbLC.setForeground(UIConstants.TEXT_DARK);
        lbLC.setBorder(new EmptyBorder(0, 0, 10, 0));
        lineChart = new LineChartPanel();
        lineCard.add(lbLC, BorderLayout.NORTH);
        lineCard.add(lineChart, BorderLayout.CENTER);

        RoundedPanel pieCard = new RoundedPanel(14);
        pieCard.setLayout(new BorderLayout());
        pieCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        pieCard.setBackground(UIConstants.CARD_BG);
        JLabel lbPC = new JLabel("Phân bố theo danh mục");
        lbPC.setFont(UIConstants.FONT_BOLD_14);
        lbPC.setForeground(UIConstants.TEXT_DARK);
        lbPC.setBorder(new EmptyBorder(0, 0, 10, 0));
        pieChart = new PieChartPanel();
        pieCard.add(lbPC, BorderLayout.NORTH);
        pieCard.add(pieChart, BorderLayout.CENTER);

        row.add(lineCard);
        row.add(pieCard);
        return row;
    }

    private JPanel createBottomTable() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setBackground(UIConstants.CARD_BG);

        JLabel lbTitle = new JLabel("Món ăn bán chạy nhất");
        lbTitle.setFont(UIConstants.FONT_BOLD_15);
        lbTitle.setForeground(UIConstants.TEXT_DARK);
        lbTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        tblModel = new DefaultTableModel(
                new String[]{"Món ăn", "Danh mục", "Số lượng bán", "Doanh thu", "% Tổng ĐT"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable tbl = new JTable(tblModel);
        tbl.setFont(UIConstants.FONT_PLAIN_13);
        tbl.setRowHeight(45);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setBackground(UIConstants.CARD_BG);
        tbl.setFocusable(false);
        tbl.setSelectionBackground(UIConstants.PRIMARY_TINT);

        JTableHeader hdr = tbl.getTableHeader();
        hdr.setFont(UIConstants.FONT_BOLD_12);
        hdr.setBackground(new Color(249, 250, 251));
        hdr.setForeground(UIConstants.TEXT_GRAY);
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER));
        hdr.setPreferredSize(new Dimension(0, 42));
        ((DefaultTableCellRenderer) hdr.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
                                                           int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(sel ? UIConstants.PRIMARY_TINT : UIConstants.CARD_BG);
                setFont(new Font("Segoe UI", col == 0 ? Font.BOLD : Font.PLAIN, 13));
                setForeground(UIConstants.TEXT_DARK);
                setBorder(new EmptyBorder(8, 12, 8, 12));
                return this;
            }
        });

        JScrollPane sc = new JScrollPane(tbl);
        sc.setBorder(null);
        sc.getViewport().setBackground(UIConstants.CARD_BG);

        card.add(lbTitle, BorderLayout.NORTH);
        card.add(sc, BorderLayout.CENTER);
        return card;
    }


    private String getSqlDateCondition() {
        int index = cboPeriod.getSelectedIndex();
        if (index == 0) return "ngayGioThanhToan >= DATEADD(DAY, -7, CAST(GETDATE() AS DATE))";
        if (index == 1) return "ngayGioThanhToan >= DATEADD(DAY, -30, CAST(GETDATE() AS DATE))";
        if (index == 2) {
            return "MONTH(ngayGioThanhToan) = MONTH(GETDATE()) AND YEAR(ngayGioThanhToan) = YEAR(GETDATE())";
        }
        if (index == 3) {
            // Tùy chỉnh (Dùng param ?)
            return "CAST(ngayGioThanhToan AS DATE) >= ? AND CAST(ngayGioThanhToan AS DATE) <= ?";
        }
        return "1=1"; // Phòng hờ
    }

    private void setDateParameters(PreparedStatement ps, int paramStartIndex) throws SQLException {
        if (cboPeriod.getSelectedIndex() == 3) {
            Date tNgay = dateTuNgay.getDate();
            Date dNgay = dateDenNgay.getDate();

            if (tNgay == null) tNgay = new Date();
            if (dNgay == null) dNgay = new Date();

            ps.setDate(paramStartIndex, new java.sql.Date(tNgay.getTime()));
            ps.setDate(paramStartIndex + 1, new java.sql.Date(dNgay.getTime()));
        }
    }

    private void loadData() {
        // Kiểm tra logic nếu chọn tùy chỉnh
        if (cboPeriod.getSelectedIndex() == 3) {
            if (dateTuNgay.getDate() == null || dateDenNgay.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ 'Từ ngày' và 'Đến ngày'!");
                return;
            }
            if (dateTuNgay.getDate().after(dateDenNgay.getDate())) {
                JOptionPane.showMessageDialog(this, "'Từ ngày' không được lớn hơn 'Đến ngày'!");
                return;
            }
        }

        SwingWorker<Void, Void> w = new SwingWorker<>() {
            long tongDT;
            int soDon, soKH;
            long giaTB;
            List<long[]> chartData = new ArrayList<>();
            List<String[]> topMon = new ArrayList<>();
            List<String[]> phanBo = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                try {
                    Connection con = ConnectDB.getInstance().getConnection();
                    String dateCondition = getSqlDateCondition();

                    // 1. Tổng doanh thu
                    String sqlDT = "SELECT ISNULL(SUM(tongTien),0) FROM HoaDon WHERE trangThaiThanhToan=N'Đã thanh toán' AND " + dateCondition;
                    PreparedStatement ps = con.prepareStatement(sqlDT);
                    setDateParameters(ps, 1);
                    ResultSet rs = ps.executeQuery();
                    tongDT = rs.next() ? (long) rs.getDouble(1) : 0;
                    rs.close();
                    ps.close();

                    // 2. Số đơn
                    String sqlDon = "SELECT COUNT(*) FROM HoaDon WHERE trangThaiThanhToan=N'Đã thanh toán' AND " + dateCondition;
                    ps = con.prepareStatement(sqlDon);
                    setDateParameters(ps, 1);
                    rs = ps.executeQuery();
                    soDon = rs.next() ? rs.getInt(1) : 0;
                    rs.close();
                    ps.close();

                    giaTB = soDon > 0 ? tongDT / soDon : 0;

                    // 3. Khách hàng
                    String sqlKH = "SELECT COUNT(DISTINCT ISNULL(tenKhachLe,'?')) FROM HoaDon WHERE trangThaiThanhToan=N'Đã thanh toán' AND " + dateCondition;
                    ps = con.prepareStatement(sqlKH);
                    setDateParameters(ps, 1);
                    rs = ps.executeQuery();
                    soKH = rs.next() ? rs.getInt(1) : 0;
                    rs.close();
                    ps.close();

                    // 4. Line chart
                    String sqlLine = "SELECT CAST(ngayGioThanhToan AS DATE) as ngay, ISNULL(SUM(tongTien),0) as dt "
                            + "FROM HoaDon WHERE trangThaiThanhToan=N'Đã thanh toán' AND " + dateCondition
                            + " GROUP BY CAST(ngayGioThanhToan AS DATE) ORDER BY ngay";
                    ps = con.prepareStatement(sqlLine);
                    setDateParameters(ps, 1);
                    rs = ps.executeQuery();
                    while (rs.next())
                        chartData.add(new long[]{rs.getDate("ngay").getTime(), (long) rs.getDouble("dt")});
                    rs.close();
                    ps.close();

                    // 5. Top món bán chạy
                    String sqlTop = "SELECT TOP 10 m.tenMonAn, dm.tenDM, SUM(c.soLuong) as sl, SUM(c.thanhTien) as dt "
                            + "FROM ChiTietHoaDon c JOIN MonAn m ON c.maMonAn=m.maMonAn "
                            + "JOIN DanhMucMonAn dm ON m.maDM=dm.maDM JOIN HoaDon h ON c.maHD=h.maHD "
                            + "WHERE h.trangThaiThanhToan=N'Đã thanh toán' AND " + dateCondition.replace("ngayGioThanhToan", "h.ngayGioThanhToan")
                            + " GROUP BY m.tenMonAn, dm.tenDM ORDER BY sl DESC";
                    ps = con.prepareStatement(sqlTop);
                    setDateParameters(ps, 1);
                    rs = ps.executeQuery();
                    while (rs.next())
                        topMon.add(new String[]{rs.getString("tenMonAn"), rs.getString("tenDM"),
                                String.valueOf(rs.getInt("sl")),
                                String.format("%,.0fđ", rs.getDouble("dt")).replace(",", "."), ""});
                    rs.close();
                    ps.close();

                    // 6. Phân bố danh mục
                    String sqlPie = "SELECT dm.tenDM, SUM(c.thanhTien) as dt "
                            + "FROM ChiTietHoaDon c JOIN MonAn m ON c.maMonAn=m.maMonAn "
                            + "JOIN DanhMucMonAn dm ON m.maDM=dm.maDM JOIN HoaDon h ON c.maHD=h.maHD "
                            + "WHERE h.trangThaiThanhToan=N'Đã thanh toán' AND " + dateCondition.replace("ngayGioThanhToan", "h.ngayGioThanhToan")
                            + " GROUP BY dm.tenDM ORDER BY dt DESC";
                    ps = con.prepareStatement(sqlPie);
                    setDateParameters(ps, 1);
                    rs = ps.executeQuery();
                    while (rs.next())
                        phanBo.add(new String[]{rs.getString("tenDM"), String.valueOf((long) rs.getDouble("dt"))});
                    rs.close();
                    ps.close();

                    // Tính % cho topMon
                    if (!topMon.isEmpty() && tongDT > 0) {
                        for (String[] row : topMon) {
                            long dt = Long.parseLong(row[3].replaceAll("[^0-9]", ""));
                            row[4] = String.format("%.1f%%", (double) dt / tongDT * 100);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                lbTongDT.setText(tongDT == 0 ? "0đ" : String.format("%,.0fđ", (double) tongDT).replace(",", "."));
                lbSoDon.setText(String.valueOf(soDon));
                lbGiaTBDon.setText(giaTB == 0 ? "0đ" : String.format("%,.0fđ", (double) giaTB).replace(",", "."));
                lbKhachHang.setText(String.valueOf(soKH));

                lineChart.setData(chartData);
                pieChart.setData(phanBo);

                tblModel.setRowCount(0);
                for (String[] r : topMon)
                    tblModel.addRow(r);

                currentTongDT = tongDT;
                currentSoDon = soDon;
                currentSoKH = soKH;
                currentGiaTB = giaTB;
                currentChartData = new ArrayList<>(chartData);
                currentTopMon = new ArrayList<>(topMon);
                currentPhanBo = new ArrayList<>(phanBo);
            }
        };
        w.execute();
    }

    private void xuatBaoCaoExcel() {
        if (currentTopMon == null || currentTopMon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất Excel.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu báo cáo Excel");

        // Đặt tên mặc định tùy theo bộ lọc
        String tenKy = cboPeriod.getSelectedItem().toString().replace(" ", "_");
        if (cboPeriod.getSelectedIndex() == 3) tenKy = "Tuy_Chinh";
        chooser.setSelectedFile(new File("Bao_Cao_Doanh_Thu_" + tenKy + ".xlsx"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            try (Workbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(file)) {
                Sheet sheet = workbook.createSheet("Bao Cao Doanh Thu");

                // Header File
                Row r0 = sheet.createRow(0);
                r0.createCell(0).setCellValue("BÁO CÁO DOANH THU NHÀ HÀNG NGÓI ĐỎ");

                Row r1 = sheet.createRow(1);
                r1.createCell(0).setCellValue("Kỳ báo cáo: " + cboPeriod.getSelectedItem());

                Row r2 = sheet.createRow(2);
                r2.createCell(0).setCellValue("Ngày xuất: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

                Row r3 = sheet.createRow(3);
                r3.createCell(0).setCellValue("Nhân viên xuất: " + tenNhanVien);

                // Khoảng trống
                sheet.createRow(4);

                // 1. Thống kê tổng quan
                Row r5 = sheet.createRow(5);
                r5.createCell(0).setCellValue("1. THỐNG KÊ TỔNG QUAN");
                Row r6 = sheet.createRow(6);
                r6.createCell(0).setCellValue("Tổng doanh thu");
                r6.createCell(1).setCellValue("Số đơn hàng");
                r6.createCell(2).setCellValue("Giá trị TB/Đơn");
                r6.createCell(3).setCellValue("Số lượng khách");

                Row r7 = sheet.createRow(7);
                r7.createCell(0).setCellValue(currentTongDT);
                r7.createCell(1).setCellValue(currentSoDon);
                r7.createCell(2).setCellValue(currentGiaTB);
                r7.createCell(3).setCellValue(currentSoKH);

                sheet.createRow(8);

                // 2. Bảng xếp hạng món ăn
                Row r9 = sheet.createRow(9);
                r9.createCell(0).setCellValue("2. TOP MÓN ĂN BÁN CHẠY");

                Row r10 = sheet.createRow(10);
                r10.createCell(0).setCellValue("Tên món");
                r10.createCell(1).setCellValue("Danh mục");
                r10.createCell(2).setCellValue("Số lượng bán");
                r10.createCell(3).setCellValue("Doanh thu");
                r10.createCell(4).setCellValue("% Tổng ĐT");

                int rowNum = 11;
                for (String[] mon : currentTopMon) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(mon[0]);
                    row.createCell(1).setCellValue(mon[1]);
                    row.createCell(2).setCellValue(Integer.parseInt(mon[2]));
                    // Loại bỏ chữ 'đ' và dấu '.' để Excel hiểu là số
                    long dt = Long.parseLong(mon[3].replace("đ", "").replace(".", ""));
                    row.createCell(3).setCellValue(dt);
                    row.createCell(4).setCellValue(mon[4]);
                }

                // Tự động chỉnh độ rộng cột
                for (int i = 0; i <= 4; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(out);
                JOptionPane.showMessageDialog(this, "Xuất báo cáo Excel thành công!\n" + file.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                java.awt.Desktop.getDesktop().open(file); // Tự động mở file

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất file Excel: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private String formatTienPDF(long soTien) {
        return String.format("%,d", soTien).replace(",", ".");
    }

    private com.lowagie.text.Font taoFontUnicode(float size, int style) throws Exception {
        BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        return new com.lowagie.text.Font(bf, size, style);
    }

    private PdfPCell taoCell(String text, com.lowagie.text.Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8f);
        return cell;
    }

    private void xuatBaoCaoPDF() {
        if (currentTopMon == null || currentTopMon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để xuất.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn nơi lưu báo cáo PDF");
        String tenKy = cboPeriod.getSelectedItem().toString().replace(" ", "_");
        if (cboPeriod.getSelectedIndex() == 3) tenKy = "Tuy_Chinh";
        chooser.setSelectedFile(new File("bao_cao_doanh_thu_" + tenKy + ".pdf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            com.lowagie.text.Font fontTitle = taoFontUnicode(18, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font fontSub = taoFontUnicode(12, com.lowagie.text.Font.NORMAL);
            com.lowagie.text.Font fontBold = taoFontUnicode(12, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font fontNormal = taoFontUnicode(11, com.lowagie.text.Font.NORMAL);

            Paragraph title = new Paragraph("BÁO CÁO DOANH THU NHÀ HÀNG NGÓI ĐỎ", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Kỳ báo cáo: " + cboPeriod.getSelectedItem(), fontSub));

            // Hiện thêm từ ngày - đến ngày nếu chọn Tùy chỉnh
            if (cboPeriod.getSelectedIndex() == 3) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String tgText = "Từ: " + sdf.format(dateTuNgay.getDate()) + " - Đến: " + sdf.format(dateDenNgay.getDate());
                document.add(new Paragraph(tgText, fontSub));
            }

            document.add(new Paragraph("Ngày xuất báo cáo: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), fontSub));
            document.add(new Paragraph("Người xuất: " + tenNhanVien, fontSub));
            document.add(new Paragraph("---------------------------------------------------------------------------------------------------------", fontNormal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("1. THỐNG KÊ TỔNG QUAN KINH DOANH", fontBold));
            document.add(new Paragraph(" "));

            PdfPTable tTongQuan = new PdfPTable(4);
            tTongQuan.setWidthPercentage(100);
            tTongQuan.addCell(taoCell("TỔNG DOANH THU", fontBold, Element.ALIGN_CENTER));
            tTongQuan.addCell(taoCell("SỐ ĐƠN HÀNG", fontBold, Element.ALIGN_CENTER));
            tTongQuan.addCell(taoCell("GIÁ TRỊ TRUNG BÌNH/ĐƠN", fontBold, Element.ALIGN_CENTER));
            tTongQuan.addCell(taoCell("SỐ KHÁCH HÀNG", fontBold, Element.ALIGN_CENTER));

            tTongQuan.addCell(taoCell(formatTienPDF(currentTongDT) + " đ", fontNormal, Element.ALIGN_CENTER));
            tTongQuan.addCell(taoCell(String.valueOf(currentSoDon), fontNormal, Element.ALIGN_CENTER));
            tTongQuan.addCell(taoCell(formatTienPDF(currentGiaTB) + " đ", fontNormal, Element.ALIGN_CENTER));
            tTongQuan.addCell(taoCell(String.valueOf(currentSoKH), fontNormal, Element.ALIGN_CENTER));
            document.add(tTongQuan);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("2. BIỂU ĐỒ TRỰC QUAN", fontBold));
            document.add(new Paragraph(" "));

            PdfPTable tChart = new PdfPTable(2);
            tChart.setWidthPercentage(100);
            tChart.setWidths(new float[]{1f, 1f});

            try {
                int wLine = lineChart.getWidth();
                int hLine = lineChart.getHeight();
                java.awt.image.BufferedImage imgLine = new java.awt.image.BufferedImage(wLine, hLine, java.awt.image.BufferedImage.TYPE_INT_RGB);
                Graphics2D gLine = imgLine.createGraphics();
                lineChart.paint(gLine);
                gLine.dispose();
                com.lowagie.text.Image pdfImgLine = com.lowagie.text.Image.getInstance(imgLine, null);

                int wPie = pieChart.getWidth();
                int hPie = pieChart.getHeight();
                java.awt.image.BufferedImage imgPie = new java.awt.image.BufferedImage(wPie, hPie, java.awt.image.BufferedImage.TYPE_INT_RGB);
                Graphics2D gPie = imgPie.createGraphics();
                pieChart.paint(gPie);
                gPie.dispose();
                com.lowagie.text.Image pdfImgPie = com.lowagie.text.Image.getInstance(imgPie, null);

                PdfPCell cellLine = new PdfPCell(pdfImgLine, true);
                cellLine.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                cellLine.setPadding(5);
                tChart.addCell(cellLine);

                PdfPCell cellPie = new PdfPCell(pdfImgPie, true);
                cellPie.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                cellPie.setPadding(5);
                tChart.addCell(cellPie);

                document.add(tChart);
            } catch (Exception e) {
                System.err.println("Lỗi chụp biểu đồ: " + e.getMessage());
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("3. PHÂN BỐ DOANH THU THEO DANH MỤC", fontBold));
            if (currentPhanBo.isEmpty()) {
                document.add(new Paragraph("Chưa có dữ liệu.", fontNormal));
            } else {
                for (String[] row : currentPhanBo) {
                    document.add(new Paragraph("    • " + row[0] + ": " + formatTienPDF(Long.parseLong(row[1])) + " đ", fontNormal));
                }
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("4. BẢNG XẾP HẠNG TOP MÓN ĂN BÁN CHẠY NHẤT", fontBold));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.2f, 2.2f, 1.6f, 2.0f, 1.4f});

            table.addCell(taoCell("Món ăn", fontBold, Element.ALIGN_LEFT));
            table.addCell(taoCell("Danh mục", fontBold, Element.ALIGN_LEFT));
            table.addCell(taoCell("SL bán", fontBold, Element.ALIGN_CENTER));
            table.addCell(taoCell("Doanh thu", fontBold, Element.ALIGN_RIGHT));
            table.addCell(taoCell("% Tổng ĐT", fontBold, Element.ALIGN_CENTER));

            if (currentTopMon.isEmpty()) {
                PdfPCell empty = taoCell("Chưa có dữ liệu", fontNormal, Element.ALIGN_CENTER);
                empty.setColspan(5);
                table.addCell(empty);
            } else {
                for (String[] row : currentTopMon) {
                    table.addCell(taoCell(row[0], fontNormal, Element.ALIGN_LEFT));
                    table.addCell(taoCell(row[1], fontNormal, Element.ALIGN_LEFT));
                    table.addCell(taoCell(row[2], fontNormal, Element.ALIGN_CENTER));
                    table.addCell(taoCell(row[3], fontNormal, Element.ALIGN_RIGHT));
                    table.addCell(taoCell(row[4], fontNormal, Element.ALIGN_CENTER));
                }
            }

            document.add(table);

            document.add(new Paragraph("\n"));
            Paragraph signature = new Paragraph("Người lập báo cáo\n\n\n(Ký và ghi rõ họ tên)", fontNormal);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);

            document.close();

            JOptionPane.showMessageDialog(this, "Xuất báo cáo PDF thành công!\nFile: " + file.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            java.awt.Desktop.getDesktop().open(file);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF!\n" + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }


    static class LineChartPanel extends JPanel {
        private List<long[]> data = new ArrayList<>();
        private static final Color LINE_COLOR = UIConstants.PRIMARY;

        public void setData(List<long[]> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (data == null || data.isEmpty()) {
                g2.setColor(UIConstants.TEXT_PLACEHOLDER);
                g2.setFont(UIConstants.FONT_PLAIN_13);
                String msg = "Chưa có dữ liệu";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                g2.dispose();
                return;
            }

            int pad = 40, chartW = getWidth() - pad * 2, chartH = getHeight() - pad * 2;
            long maxDT = data.stream().mapToLong(d -> d[1]).max().orElse(1);
            if (maxDT == 0)
                maxDT = 1;

            g2.setColor(new Color(240, 240, 240));
            for (int i = 0; i <= 4; i++) {
                int y = pad + chartH * i / 4;
                g2.drawLine(pad, y, pad + chartW, y);
            }

            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(LINE_COLOR);
            int n = data.size();
            int[] xs = new int[n], ys = new int[n];
            for (int i = 0; i < n; i++) {
                xs[i] = pad + chartW * i / Math.max(n - 1, 1);
                ys[i] = pad + chartH - (int) (chartH * data.get(i)[1] / maxDT);
            }
            for (int i = 0; i < n - 1; i++)
                g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
            g2.setFont(UIConstants.FONT_PLAIN_12);
            for (int i = 0; i < n; i++) {
                g2.setColor(LINE_COLOR);
                g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
                g2.setColor(UIConstants.TEXT_GRAY);
                String lbl = sdf.format(new Date(data.get(i)[0]));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(lbl, xs[i] - fm.stringWidth(lbl) / 2, pad + chartH + 14);
            }

            g2.setColor(UIConstants.TEXT_GRAY);
            String maxStr = String.format("%.0fTr đ", (double) maxDT / 1_000_000);
            g2.drawString(maxStr, 2, pad + 8);
            g2.drawString("→ Doanh thu", pad + chartW / 2 - 30, pad + chartH + 28);
            g2.dispose();
        }
    }

    static class PieChartPanel extends JPanel {
        private List<String[]> data = new ArrayList<>();
        private static final Color[] COLORS = {UIConstants.PRIMARY, new Color(251, 146, 60), new Color(234, 179, 8),
                new Color(34, 197, 94), new Color(99, 102, 241)};

        public void setData(List<String[]> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (data == null || data.isEmpty()) {
                g2.setColor(UIConstants.TEXT_PLACEHOLDER);
                g2.setFont(UIConstants.FONT_PLAIN_13);
                String msg = "Chưa có dữ liệu";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                g2.dispose();
                return;
            }

            long total = data.stream().mapToLong(d -> Long.parseLong(d[1])).sum();
            if (total == 0) {
                g2.dispose();
                return;
            }

            int sz = Math.min(getWidth() / 2, getHeight()) - 20;
            int cx = getWidth() / 4, cy = getHeight() / 2;
            int x = cx - sz / 2, y = cy - sz / 2;

            double angle = 0;
            for (int i = 0; i < data.size() && i < COLORS.length; i++) {
                double pct = (double) Long.parseLong(data.get(i)[1]) / total;
                double sweep = 360 * pct;
                g2.setColor(COLORS[i]);
                g2.fillArc(x, y, sz, sz, (int) angle, (int) sweep);
                angle += sweep;
            }

            g2.setFont(UIConstants.FONT_PLAIN_12);
            int lx = getWidth() / 2 + 10, ly = cy - data.size() * 18 / 2;
            for (int i = 0; i < data.size() && i < COLORS.length; i++) {
                g2.setColor(COLORS[i]);
                g2.fillRoundRect(lx, ly + i * 22, 12, 12, 4, 4);
                g2.setColor(UIConstants.TEXT_DARK);
                double pct = (double) Long.parseLong(data.get(i)[1]) / total * 100;
                g2.drawString(data.get(i)[0] + " (" + String.format("%.0f%%", pct) + ")", lx + 18, ly + i * 22 + 11);
            }
            g2.dispose();
        }
    }
}