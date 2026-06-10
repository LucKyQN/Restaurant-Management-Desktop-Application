//package GUI;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//
//
//public class DlgBaoCaoCuoiNgay extends JDialog {
//
//    private static final Color RED_MAIN = new Color(220, 38, 38);
//    private static final Color GREEN_EXCEL = new Color(34, 177, 76);
//    private static final Color BLUE_TRANSFER = new Color(52, 152, 219);
//    private static final Color ORANGE_BILL = new Color(230, 126, 34);
//    private static final Color BG_MAIN = new Color(248, 248, 248);
//    private static final Color BORDER_CLR = new Color(225, 225, 225);
//    private static final Color TEXT_GRAY = new Color(120, 120, 120);
//
//    public DlgBaoCaoCuoiNgay(JFrame parent) {
//        super(parent, "Báo cáo doanh thu chốt ca", true);
//        initUI();
//    }
//
//    private void initUI() {
//        setSize(900, 650);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout());
//        getContentPane().setBackground(BG_MAIN);
//
//
//        add(createNorthPanel(), BorderLayout.NORTH);
//
//
//        add(createCenterPanel(), BorderLayout.CENTER);
//
//
//        add(createSouthPanel(), BorderLayout.SOUTH);
//    }
//
//    /**
//     * Tạo vùng NORTH: Tiêu đề + 4 thẻ thống kê
//     */
//    private JPanel createNorthPanel() {
//        JPanel north = new JPanel(new BorderLayout(0, 15));
//        north.setBackground(BG_MAIN);
//        north.setBorder(new EmptyBorder(20, 20, 20, 20));
//
//        // ── Tiêu đề ──
//        JLabel title = new JLabel("BÁO CÁO DOANH THU CHỐT CA");
//        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
//        title.setForeground(new Color(40, 40, 40));
//
//        // ── Vùng 4 thẻ thống kê ──
//        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
//        statsPanel.setOpaque(false);
//
//        // Thẻ 1: Tổng doanh thu
//        statsPanel.add(createStatCard(
//                "Tổng doanh thu",
//                "25.500.000 đ",
//                new Color(254, 242, 242), // Màu nền nhạt đỏ
//                RED_MAIN
//        ));
//
//        // Thẻ 2: Thu tiền mặt
//        statsPanel.add(createStatCard(
//                "Thu tiền mặt",
//                "10.000.000 đ",
//                new Color(241, 250, 238), // Màu nền nhạt xanh lá
//                GREEN_EXCEL
//        ));
//
//        // Thẻ 3: Thu chuyển khoản
//        statsPanel.add(createStatCard(
//                "Thu chuyển khoản",
//                "15.500.000 đ",
//                new Color(235, 245, 250), // Màu nền nhạt xanh dương
//                BLUE_TRANSFER
//        ));
//
//        // Thẻ 4: Số lượng hóa đơn
//        statsPanel.add(createStatCard(
//                "Số lượng hóa đơn",
//                "45 HĐ",
//                new Color(255, 244, 235), // Màu nền nhạt cam
//                ORANGE_BILL
//        ));
//
//        north.add(title, BorderLayout.NORTH);
//        north.add(statsPanel, BorderLayout.CENTER);
//        return north;
//    }
//
//    /**
//     * Tạo một thẻ thống kê (Card)
//     */
//    private JPanel createStatCard(String label, String value, Color bgColor, Color textColor) {
//        JPanel card = new JPanel(new BorderLayout(0, 10));
//        card.setBackground(bgColor);
//        card.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
//                new EmptyBorder(16, 14, 16, 14)
//        ));
//
//        // Label
//        JLabel lblLabel = new JLabel(label);
//        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        lblLabel.setForeground(TEXT_GRAY);
//
//        // Value
//        JLabel lblValue = new JLabel(value);
//        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
//        lblValue.setForeground(textColor);
//
//        card.add(lblLabel, BorderLayout.NORTH);
//        card.add(lblValue, BorderLayout.CENTER);
//        return card;
//    }
//
//    /**
//     * Tạo vùng CENTER: Bảng chi tiết hóa đơn
//     */
//    private JPanel createCenterPanel() {
//        JPanel center = new JPanel(new BorderLayout());
//        center.setBackground(BG_MAIN);
//        center.setBorder(new EmptyBorder(0, 20, 0, 20));
//
//        // ── Khởi tạo bảng ──
//        String[] columns = {"STT", "Mã HĐ", "Thời gian", "Tên Bàn", "Phương thức TT", "Tổng tiền", "Trạng thái"};
//        DefaultTableModel model = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//
//        // ── Thêm mock data (5 dòng giả) ──
//        model.addRow(new Object[]{"1", "HD001", "08:30", "Bàn A1", "Tiền mặt", "250.000", "Hoàn thành"});
//        model.addRow(new Object[]{"2", "HD002", "09:15", "Bàn A2", "Chuyển khoản", "450.000", "Hoàn thành"});
//        model.addRow(new Object[]{"3", "HD003", "10:00", "Bàn B1", "Tiền mặt", "320.000", "Hoàn thành"});
//        model.addRow(new Object[]{"4", "HD004", "10:45", "Bàn B2", "Chuyển khoản", "680.000", "Hoàn thành"});
//        model.addRow(new Object[]{"5", "HD005", "11:30", "Bàn C1", "Tiền mặt", "520.000", "Hủy"});
//
//        // ── Tạo JTable với styling ──
//        JTable table = new JTable(model);
//        table.setRowHeight(35);
//        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
//        table.getTableHeader().setBackground(new Color(245, 245, 245));
//        table.getTableHeader().setForeground(new Color(40, 40, 40));
//        table.setSelectionBackground(new Color(254, 242, 242));
//        table.setSelectionForeground(new Color(40, 40, 40));
//        table.setGridColor(BORDER_CLR);
//
//        // ── Thêm vào JScrollPane ──
//        JScrollPane scrollPane = new JScrollPane(table);
//        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
//        scrollPane.getViewport().setBackground(Color.WHITE);
//        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
//
//        center.add(scrollPane, BorderLayout.CENTER);
//        return center;
//    }
//
//    /**
//     * Tạo vùng SOUTH: Nút hành động (Xuất Excel, Xuất PDF, Đóng)
//     */
//    private JPanel createSouthPanel() {
//        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
//        south.setBackground(BG_MAIN);
//        south.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
//                new EmptyBorder(15, 20, 15, 20)
//        ));
//
//        // ── Nút "Xuất Excel" ──
//        JButton btnExcel = new JButton("Xuất Excel");
//        btnExcel.setPreferredSize(new Dimension(140, 40));
//        btnExcel.setBackground(GREEN_EXCEL);
//        btnExcel.setForeground(Color.WHITE);
//        btnExcel.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        btnExcel.setFocusPainted(false);
//        btnExcel.setBorderPainted(false);
//        btnExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btnExcel.addActionListener(e -> {
//            JOptionPane.showMessageDialog(this,
//                    "Đang xuất dữ liệu ra file Excel",
//                    "Xuất Excel", JOptionPane.INFORMATION_MESSAGE);
//            // TODO: Gọi logic xuất Excel từ DAO
//        });
//
//        // ── Nút "Xuất PDF" ──
//        JButton btnPDF = new JButton("Xuất PDF");
//        btnPDF.setPreferredSize(new Dimension(140, 40));
//        btnPDF.setBackground(RED_MAIN);
//        btnPDF.setForeground(Color.WHITE);
//        btnPDF.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        btnPDF.setFocusPainted(false);
//        btnPDF.setBorderPainted(false);
//        btnPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btnPDF.addActionListener(e -> {
//            JOptionPane.showMessageDialog(this,
//                    "Đang xuất dữ liệu ra file PDF",
//                    "Xuất PDF", JOptionPane.INFORMATION_MESSAGE);
//            // TODO: Gọi logic xuất PDF từ DAO
//        });
//
//        // ── Nút "Đóng" ──
//        JButton btnClose = new JButton("Đóng");
//        btnClose.setPreferredSize(new Dimension(100, 40));
//        btnClose.setBackground(new Color(240, 240, 240));
//        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        btnClose.setFocusPainted(false);
//        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btnClose.addActionListener(e -> dispose());
//
//        south.add(btnExcel);
//        south.add(btnPDF);
//        south.add(btnClose);
//
//        return south;
//    }
//}
//
package GUI;

import DAO.HoaDonDAO;
import connectDatabase.ConnectDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

// Thư viện Excel
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Thư viện PDF
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class DlgBaoCaoCuoiNgay extends JDialog {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color GREEN_EXCEL = new Color(34, 177, 76);
    private static final Color BLUE_TRANSFER = new Color(52, 152, 219);
    private static final Color ORANGE_BILL = new Color(230, 126, 34);
    private static final Color BG_MAIN = new Color(248, 248, 248);
    private static final Color BORDER_CLR = new Color(225, 225, 225);
    private static final Color TEXT_GRAY = new Color(120, 120, 120);

    private DefaultTableModel model;
    private JLabel lblTongDoanhThu, lblTienMat, lblChuyenKhoan, lblQuetThe, lblSoHoaDon;
    private JTable table;

    // Biến lưu trữ dữ liệu thực
    private long tongDoanhThu = 0;
    private long thuTienMat = 0;
    private long thuChuyenKhoan = 0;
    private long thuQuetThe = 0;
    private int soLuongHoaDon = 0;

    public DlgBaoCaoCuoiNgay(JFrame parent) {
        super(parent, "Báo cáo doanh thu chốt ca", true);
        initUI();
        loadData(); // Gọi hàm load dữ liệu thật từ SQL
    }

    private void initUI() {
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        add(createNorthPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);
    }

    private JPanel createNorthPanel() {
        JPanel north = new JPanel(new BorderLayout(0, 15));
        north.setBackground(BG_MAIN);
        north.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("BÁO CÁO DOANH THU CHỐT CA HÔM NAY");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(40, 40, 40));

        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        statsPanel.setOpaque(false);

        lblTongDoanhThu = new JLabel("0 đ");
        lblTienMat = new JLabel("0 đ");
        lblChuyenKhoan = new JLabel("0 đ");
        lblQuetThe = new JLabel("0 đ");
        lblSoHoaDon = new JLabel("0 HĐ");

        statsPanel.add(createStatCard("Tổng doanh thu", lblTongDoanhThu, new Color(254, 242, 242), RED_MAIN));
        statsPanel.add(createStatCard("Thu tiền mặt", lblTienMat, new Color(241, 250, 238), GREEN_EXCEL));
        statsPanel.add(createStatCard("Thu chuyển khoản", lblChuyenKhoan, new Color(235, 245, 250), BLUE_TRANSFER));
        statsPanel.add(createStatCard("Thu quẹt thẻ", lblQuetThe, new Color(245, 235, 255), new Color(139, 92, 246)));
        statsPanel.add(createStatCard("Số lượng hóa đơn", lblSoHoaDon, new Color(255, 244, 235), ORANGE_BILL));

        north.add(title, BorderLayout.NORTH);
        north.add(statsPanel, BorderLayout.CENTER);
        return north;
    }

    private JPanel createStatCard(String label, JLabel lblValue, Color bgColor, Color textColor) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 14, 16, 14)
        ));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(TEXT_GRAY);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(textColor);

        card.add(lblLabel, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG_MAIN);
        center.setBorder(new EmptyBorder(0, 20, 0, 20));

        String[] columns = {"STT", "Mã HĐ", "Thời gian", "Tên Bàn", "Phương thức TT", "Tổng tiền", "Trạng thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }

    private JPanel createSouthPanel() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        south.setBackground(BG_MAIN);
        south.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JButton btnExcel = new JButton("Xuất Excel");
        btnExcel.setPreferredSize(new Dimension(140, 40));
        btnExcel.setBackground(GREEN_EXCEL);
        btnExcel.setForeground(Color.WHITE);
        btnExcel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExcel.addActionListener(e -> xuatExcel());

        JButton btnPDF = new JButton("Xuất PDF");
        btnPDF.setPreferredSize(new Dimension(140, 40));
        btnPDF.setBackground(RED_MAIN);
        btnPDF.setForeground(Color.WHITE);
        btnPDF.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPDF.addActionListener(e -> xuatPDF());

        JButton btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 40));
        btnClose.addActionListener(e -> dispose());

        south.add(btnExcel);
        south.add(btnPDF);
        south.add(btnClose);
        return south;
    }



    private void loadData() {
        model.setRowCount(0);
        tongDoanhThu = 0; thuTienMat = 0; thuChuyenKhoan = 0; thuQuetThe = 0; soLuongHoaDon = 0;

        try {
            Connection con = ConnectDB.getInstance().getConnection();
            // Lấy danh sách hóa đơn trong ngày hôm nay
            String sql = "SELECT maHD, ngayGioThanhToan, b.tenBan, phuongThucThanhToan, tongTien, trangThaiThanhToan " +
                    "FROM HoaDon h JOIN BanAn b ON h.maBan = b.maBan " +
                    "WHERE CAST(ngayGioThanhToan AS DATE) = CAST(GETDATE() AS DATE)";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            int stt = 1;

            while (rs.next()) {
                String maHD = rs.getString("maHD");
                String thoiGian = rs.getTimestamp("ngayGioThanhToan") != null ? sdf.format(rs.getTimestamp("ngayGioThanhToan")) : "";
                String tenBan = rs.getString("tenBan");
                String phuongThuc = rs.getString("phuongThucThanhToan");
                long tongTien = (long) rs.getDouble("tongTien");
                String trangThai = rs.getString("trangThaiThanhToan");

                if (phuongThuc == null) phuongThuc = "Tiền mặt";

                // Đổ vào bảng
                model.addRow(new Object[]{
                        stt++, maHD, thoiGian, tenBan, phuongThuc, formatTien(tongTien), trangThai
                });

                // Cộng dồn thống kê (chỉ tính hóa đơn đã thanh toán)
                if ("Đã thanh toán".equalsIgnoreCase(trangThai) || "Hoàn thành".equalsIgnoreCase(trangThai)) {
                    tongDoanhThu += tongTien;
                    soLuongHoaDon++;
                    if (phuongThuc.toLowerCase().contains("chuyển khoản")) {
                        thuChuyenKhoan += tongTien;
                    } else if (phuongThuc.toLowerCase().contains("quẹt thẻ") || phuongThuc.toLowerCase().contains("quet the")) {
                        thuQuetThe += tongTien;
                    } else {
                        thuTienMat += tongTien;
                    }
                }
            }
            rs.close();
            ps.close();

            // Cập nhật lên UI
            lblTongDoanhThu.setText(formatTien(tongDoanhThu) + " đ");
            lblTienMat.setText(formatTien(thuTienMat) + " đ");
            lblChuyenKhoan.setText(formatTien(thuChuyenKhoan) + " đ");
            lblQuetThe.setText(formatTien(thuQuetThe) + " đ");
            lblSoHoaDon.setText(soLuongHoaDon + " HĐ");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu chốt ca: " + e.getMessage());
        }
    }


    // LOGIC XUẤT EXCEL

    private void xuatExcel() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("ChotCa_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".xlsx"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) file = new File(file.getAbsolutePath() + ".xlsx");

            try (Workbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(file)) {
                Sheet sheet = workbook.createSheet("Chot Ca");

                Row r0 = sheet.createRow(0);
                r0.createCell(0).setCellValue("BÁO CÁO CHỐT CA NGÀY: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

                // Thống kê
                sheet.createRow(2).createCell(0).setCellValue("Tổng doanh thu: " + formatTien(tongDoanhThu));
                sheet.createRow(3).createCell(0).setCellValue("Tiền mặt: " + formatTien(thuTienMat));
                sheet.createRow(4).createCell(0).setCellValue("Chuyển khoản: " + formatTien(thuChuyenKhoan));
                sheet.createRow(5).createCell(0).setCellValue("Quẹt thẻ: " + formatTien(thuQuetThe));

                // Header bảng
                Row headerRow = sheet.createRow(7);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    headerRow.createCell(i).setCellValue(model.getColumnName(i));
                }

                // Dữ liệu bảng
                for (int i = 0; i < model.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 8);
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        row.createCell(j).setCellValue(model.getValueAt(i, j) != null ? model.getValueAt(i, j).toString() : "");
                    }
                }

                for (int i = 0; i < model.getColumnCount(); i++) sheet.autoSizeColumn(i);

                workbook.write(out);
                JOptionPane.showMessageDialog(this, "Xuất Excel thành công!");
                java.awt.Desktop.getDesktop().open(file);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất Excel: " + ex.getMessage());
            }
        }
    }


    // LOGIC XUẤT PDF

    private void xuatPDF() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("ChotCa_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".pdf")) file = new File(file.getAbsolutePath() + ".pdf");

            Document document = new Document(PageSize.A4, 30, 30, 30, 30);
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                com.lowagie.text.Font fontTitle = new com.lowagie.text.Font(bf, 18, com.lowagie.text.Font.BOLD);
                com.lowagie.text.Font fontNormal = new com.lowagie.text.Font(bf, 11, com.lowagie.text.Font.NORMAL);
                com.lowagie.text.Font fontBold = new com.lowagie.text.Font(bf, 11, com.lowagie.text.Font.BOLD);

                Paragraph title = new Paragraph("BÁO CÁO CHỐT CA NGÀY " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()), fontTitle);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph(" "));

                // Thống kê
                document.add(new Paragraph("Tổng doanh thu: " + formatTien(tongDoanhThu) + " đ", fontBold));
                document.add(new Paragraph("Thu tiền mặt: " + formatTien(thuTienMat) + " đ", fontNormal));
                document.add(new Paragraph("Thu chuyển khoản: " + formatTien(thuChuyenKhoan) + " đ", fontNormal));
                document.add(new Paragraph("Thu quẹt thẻ: " + formatTien(thuQuetThe) + " đ", fontNormal));
                document.add(new Paragraph("Số lượng hóa đơn: " + soLuongHoaDon + " HĐ", fontNormal));
                document.add(new Paragraph(" "));

                // Bảng
                PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{1f, 2f, 2f, 2f, 2.5f, 2f, 2f});

                for (int i = 0; i < model.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(i), fontBold));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }

                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(model.getValueAt(i, j).toString(), fontNormal));
                        cell.setPadding(5);
                        pdfTable.addCell(cell);
                    }
                }
                document.add(pdfTable);

                document.close();
                JOptionPane.showMessageDialog(this, "Xuất PDF thành công!");
                java.awt.Desktop.getDesktop().open(file);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất PDF: " + ex.getMessage());
            }
        }
    }

    private String formatTien(long so) {
        return String.format("%,d", so).replace(",", ".");
    }
}