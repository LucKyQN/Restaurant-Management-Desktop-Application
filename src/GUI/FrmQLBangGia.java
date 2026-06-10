package GUI;

import DAO.BangGiaDAO;
import DAO.ChiTietMonAnDAO;
import DAO.MonAnDAO;
import Entity.BangGia;
import Entity.ChiTietMonAn;
import Entity.MonAn;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FrmQLBangGia extends JPanel {
    private JTable tblBangGia, tblChiTiet;
    private DefaultTableModel modelBangGia, modelChiTiet;
    private JDateChooser dateTuNgay, dateDenNgay;
    private JTextField txtMaBG, txtTenBG;
    private JButton btnThemBG, btnSuaBG, btnXoaBG, btnTrangThai;
    private JButton btnThemMon, btnCapNhatGia, btnXoaMon, btnDieuChinhGia;
    
    private BangGiaDAO bangGiaDAO = new BangGiaDAO();
    private ChiTietMonAnDAO chiTietDAO = new ChiTietMonAnDAO();
    private MonAnDAO monAnDAO = new MonAnDAO();
    
    private BangGia bangGiaSelected = null;
    
    public FrmQLBangGia() {
        initUI();
        loadDataBangGia();
    }
    
    private JButton createFlatButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg.darker());
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg);
                }
            }
        });
        return btn;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return txt;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(229, 243, 255));
        table.setSelectionForeground(Color.BLACK);
        
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(new Color(55, 65, 81));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(100, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(209, 213, 219)));
        ((javax.swing.table.DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    private void initUI() {
        Color bgMain = new Color(248, 248, 248);
        setLayout(new BorderLayout());
        setBackground(bgMain);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerSize(5);
        splitPane.setBackground(bgMain);
        splitPane.setBorder(null);
        
        // --- TRÁI: QUẢN LÝ BẢNG GIÁ ---
        JPanel pnlTrai = new JPanel(new BorderLayout(10, 10));
        pnlTrai.setBorder(new EmptyBorder(15, 20, 15, 20));
        pnlTrai.setBackground(bgMain);
        
        JLabel lblTitleTrai = new JLabel("Danh sách Bảng Giá");
        lblTitleTrai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitleTrai.setForeground(new Color(55, 65, 81));
        
        JPanel pnlInputBG = new JPanel(new GridLayout(4, 2, 8, 12));
        pnlInputBG.setBackground(bgMain);
        
        JLabel lbl1 = new JLabel("Mã Bảng Giá:");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlInputBG.add(lbl1);
        txtMaBG = createTextField();
        txtMaBG.setEditable(false);
        txtMaBG.setBackground(new Color(243, 244, 246));
        pnlInputBG.add(txtMaBG);
        
        JLabel lbl2 = new JLabel("Tên Bảng Giá:");
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlInputBG.add(lbl2);
        txtTenBG = createTextField();
        pnlInputBG.add(txtTenBG);
        
        JLabel lbl3 = new JLabel("Từ Ngày:");
        lbl3.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlInputBG.add(lbl3);
        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlInputBG.add(dateTuNgay);
        
        JLabel lbl4 = new JLabel("Đến Ngày:");
        lbl4.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlInputBG.add(lbl4);
        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlInputBG.add(dateDenNgay);
        
        JPanel pnlBtnBG = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        pnlBtnBG.setBackground(bgMain);
        btnThemBG = createFlatButton("Thêm BG", Color.WHITE, new Color(34, 197, 94));
        btnSuaBG = createFlatButton("Sửa BG", Color.WHITE, new Color(59, 130, 246));
        btnXoaBG = createFlatButton("Xóa BG", Color.WHITE, new Color(239, 68, 68));
        btnTrangThai = createFlatButton("Đổi Trạng Thái", Color.WHITE, new Color(245, 158, 11));
        JButton btnLamMoiBG = createFlatButton("Làm mới", new Color(55, 65, 81), new Color(229, 231, 235));
        
        pnlBtnBG.add(btnLamMoiBG);
        pnlBtnBG.add(btnThemBG);
        pnlBtnBG.add(btnSuaBG);
        pnlBtnBG.add(btnXoaBG);
        pnlBtnBG.add(btnTrangThai);
        
        JPanel pnlTopTraiWrap = new JPanel(new BorderLayout(0, 10));
        pnlTopTraiWrap.setBackground(bgMain);
        pnlTopTraiWrap.add(lblTitleTrai, BorderLayout.NORTH);
        pnlTopTraiWrap.add(pnlInputBG, BorderLayout.CENTER);
        pnlTopTraiWrap.add(pnlBtnBG, BorderLayout.SOUTH);
        pnlTrai.add(pnlTopTraiWrap, BorderLayout.NORTH);
        
        modelBangGia = new DefaultTableModel(new String[]{"Mã BG", "Tên BG", "Từ Ngày", "Đến Ngày", "Trạng Thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblBangGia = new JTable(modelBangGia);
        styleTable(tblBangGia);
        
        JScrollPane scrollTrai = new JScrollPane(tblBangGia);
        scrollTrai.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollTrai.getViewport().setBackground(Color.WHITE);
        pnlTrai.add(scrollTrai, BorderLayout.CENTER);
        
        tblBangGia.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblBangGia.getSelectedRow();
                if (row >= 0) {
                    bangGiaSelected = new BangGia();
                    bangGiaSelected.setMaBangGia((String) modelBangGia.getValueAt(row, 0));
                    bangGiaSelected.setTenBangGia((String) modelBangGia.getValueAt(row, 1));
                    String trangThaiStr = (String) modelBangGia.getValueAt(row, 4);
                    bangGiaSelected.setTrangThai(trangThaiStr);
                    
                    txtMaBG.setText(bangGiaSelected.getMaBangGia());
                    txtTenBG.setText(bangGiaSelected.getTenBangGia());
                    try {
                        String tuNgayStr = (String) modelBangGia.getValueAt(row, 2);
                        String denNgayStr = (String) modelBangGia.getValueAt(row, 3);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        if(tuNgayStr != null && !tuNgayStr.isEmpty()) dateTuNgay.setDate(sdf.parse(tuNgayStr));
                        if(denNgayStr != null && !denNgayStr.isEmpty()) dateDenNgay.setDate(sdf.parse(denNgayStr));
                    } catch(Exception ex) {}
                    
                    if ("BG001".equals(bangGiaSelected.getMaBangGia())) {
                        btnSuaBG.setEnabled(false);
                        btnXoaBG.setEnabled(false);
                        btnTrangThai.setEnabled(false);
                        btnThemMon.setEnabled(false);
                        btnCapNhatGia.setEnabled(false);
                        btnXoaMon.setEnabled(false);
                        btnDieuChinhGia.setEnabled(false);
                        btnTrangThai.setText("Mặc định");
                    } else {
                        btnSuaBG.setEnabled(true);
                        btnXoaBG.setEnabled(true);
                        btnTrangThai.setEnabled(true);
                        btnThemMon.setEnabled(true);
                        btnCapNhatGia.setEnabled(true);
                        btnXoaMon.setEnabled(true);
                        btnDieuChinhGia.setEnabled(true);
                        
                        if ("Đang hoạt động".equalsIgnoreCase(trangThaiStr)) {
                            btnTrangThai.setText("Ngưng áp dụng");
                        } else {
                            btnTrangThai.setText("Áp dụng");
                        }
                    }
                    loadChiTietBangGia(bangGiaSelected.getMaBangGia());
                }
            }
        });
        
        btnThemBG.addActionListener(e -> themBangGia());
        btnSuaBG.addActionListener(e -> suaBangGia());
        btnXoaBG.addActionListener(e -> xoaBangGia());
        
        // --- PHẢI: CHI TIẾT BẢNG GIÁ ---
        JPanel pnlPhai = new JPanel(new BorderLayout(10, 10));
        pnlPhai.setBorder(new EmptyBorder(15, 20, 15, 20));
        pnlPhai.setBackground(bgMain);
        
        JLabel lblTitlePhai = new JLabel("Chi tiết Bảng Giá (Món ăn)");
        lblTitlePhai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitlePhai.setForeground(new Color(55, 65, 81));
        
        JPanel pnlBtnCT = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        pnlBtnCT.setBackground(bgMain);
        btnThemMon = createFlatButton("Thêm Món", Color.WHITE, new Color(34, 197, 94));
        btnCapNhatGia = createFlatButton("Cập Nhật Giá", Color.WHITE, new Color(59, 130, 246));
        btnXoaMon = createFlatButton("Xóa Món", Color.WHITE, new Color(239, 68, 68));
        btnDieuChinhGia = createFlatButton("Tăng/Giảm giá (%)", Color.WHITE, new Color(139, 92, 246));
        pnlBtnCT.add(btnThemMon);
        pnlBtnCT.add(btnCapNhatGia);
        pnlBtnCT.add(btnXoaMon);
        pnlBtnCT.add(btnDieuChinhGia);
        
        JPanel pnlTopPhaiWrap = new JPanel(new BorderLayout(0, 10));
        pnlTopPhaiWrap.setBackground(bgMain);
        pnlTopPhaiWrap.add(lblTitlePhai, BorderLayout.NORTH);
        pnlTopPhaiWrap.add(pnlBtnCT, BorderLayout.CENTER);
        pnlPhai.add(pnlTopPhaiWrap, BorderLayout.NORTH);
        
        modelChiTiet = new DefaultTableModel(new String[]{"Mã Món", "Tên Món", "Giá Gốc", "Giá Mới"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblChiTiet = new JTable(modelChiTiet);
        styleTable(tblChiTiet);
        
        JScrollPane scrollPhai = new JScrollPane(tblChiTiet);
        scrollPhai.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPhai.getViewport().setBackground(Color.WHITE);
        pnlPhai.add(scrollPhai, BorderLayout.CENTER);
        
        btnThemMon.addActionListener(e -> hienThiDialogThemMon());
        btnDieuChinhGia.addActionListener(e -> dieuChinhGiaHangLoat());
        
        splitPane.setLeftComponent(pnlTrai);
        splitPane.setRightComponent(pnlPhai);
        
        add(splitPane, BorderLayout.CENTER);
        
        btnTrangThai.addActionListener(e -> doiTrangThai());
        btnLamMoiBG.addActionListener(e -> lamMoiFormBangGia());
        lamMoiFormBangGia();
    }
    
    private void loadDataBangGia() {
        modelBangGia.setRowCount(0);
        List<BangGia> list = bangGiaDAO.getAllBangGia();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (BangGia bg : list) {
            modelBangGia.addRow(new Object[]{
                bg.getMaBangGia(),
                bg.getTenBangGia(),
                bg.getNgayBatDau() != null ? sdf.format(bg.getNgayBatDau()) : "",
                bg.getNgayKetThuc() != null ? sdf.format(bg.getNgayKetThuc()) : "",
                bg.getTrangThai()
            });
        }
    }
    
    private void loadChiTietBangGia(String maBangGia) {
        modelChiTiet.setRowCount(0);
        List<Object[]> list = chiTietDAO.getDanhSachMonTheoBangGia(maBangGia);
        for (Object[] row : list) {
            double giaGoc = (double) row[2];
            double giaMoi = (double) row[3];
            modelChiTiet.addRow(new Object[]{
                row[0], 
                row[1], 
                String.format("%,.0f", giaGoc), 
                String.format("%,.0f", giaMoi)
            });
        }
    }
    
    private void lamMoiFormBangGia() {
        txtMaBG.setText(bangGiaDAO.phatSinhMaBangGia());
        txtTenBG.setText("");
        dateTuNgay.setDate(null);
        dateDenNgay.setDate(null);
        
        btnSuaBG.setEnabled(true);
        btnXoaBG.setEnabled(true);
        btnTrangThai.setEnabled(true);
        btnThemMon.setEnabled(true);
        btnCapNhatGia.setEnabled(true);
        btnXoaMon.setEnabled(true);
        btnDieuChinhGia.setEnabled(true);
        btnTrangThai.setText("Đổi Trạng Thái");
    }
    
    private void dieuChinhGiaHangLoat() {
        if(bangGiaSelected == null || "BG001".equals(bangGiaSelected.getMaBangGia())) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bảng Giá hợp lệ bên trái!");
            return;
        }
        
        String input = JOptionPane.showInputDialog(this, "Nhập phần trăm Tăng/Giảm (VD: Nhập 10 để tăng 10%, -15 để giảm 15%):");
        if(input == null || input.trim().isEmpty()) return;
        
        try {
            double phanTram = Double.parseDouble(input.trim());
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Hệ thống sẽ cập nhật giá của TOÀN BỘ thực đơn vào bảng giá này.\nBạn có chắc chắn?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
                
            if(confirm == JOptionPane.YES_OPTION) {
                if(chiTietDAO.apDungGiaHangLoat(bangGiaSelected.getMaBangGia(), phanTram)) {
                    JOptionPane.showMessageDialog(this, "Áp dụng giá hàng loạt thành công!");
                    loadChiTietBangGia(bangGiaSelected.getMaBangGia());
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi áp dụng giá hàng loạt.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        }
    }
    
    private void doiTrangThai() {
        if(bangGiaSelected == null || "BG001".equals(bangGiaSelected.getMaBangGia())) return;
        
        int trangThaiMoi = "Đang hoạt động".equalsIgnoreCase(bangGiaSelected.getTrangThai()) ? 0 : 1;
        if(bangGiaDAO.doiTrangThaiBangGia(bangGiaSelected.getMaBangGia(), trangThaiMoi)) {
            JOptionPane.showMessageDialog(this, "Đổi trạng thái thành công!");
            loadDataBangGia();
            lamMoiFormBangGia(); 
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi đổi trạng thái!");
        }
    }
    
    private void themBangGia() {
        String ma = txtMaBG.getText().trim();
        String ten = txtTenBG.getText().trim();
        Date tu = dateTuNgay.getDate();
        Date den = dateDenNgay.getDate();
        
        if(ma.isEmpty() || ten.isEmpty() || tu == null || den == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (Mã, Tên, Từ ngày, Đến ngày)!");
            return;
        }
        
        BangGia bg = new BangGia(ma, ten, tu, den, "", "Đang hoạt động");
        if(bangGiaDAO.themBangGia(bg)) {
            JOptionPane.showMessageDialog(this, "Thêm Bảng giá thành công!");
            loadDataBangGia();
            lamMoiFormBangGia();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại. Mã Bảng Giá có thể đã tồn tại.");
        }
    }
    
    private void suaBangGia() {
        if(bangGiaSelected == null || "BG001".equals(bangGiaSelected.getMaBangGia())) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bảng Giá hợp lệ bên trái để sửa!");
            return;
        }
        
        String ten = txtTenBG.getText().trim();
        Date tu = dateTuNgay.getDate();
        Date den = dateDenNgay.getDate();
        
        if(ten.isEmpty() || tu == null || den == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (Tên, Từ ngày, Đến ngày)!");
            return;
        }
        
        if (tu.after(den)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }
        
        BangGia bg = new BangGia(bangGiaSelected.getMaBangGia(), ten, tu, den, "", "");
        if(bangGiaDAO.suaBangGia(bg)) {
            JOptionPane.showMessageDialog(this, "Sửa Bảng giá thành công!");
            loadDataBangGia();
            for (int i = 0; i < tblBangGia.getRowCount(); i++) {
                if (modelBangGia.getValueAt(i, 0).equals(bg.getMaBangGia())) {
                    tblBangGia.setRowSelectionInterval(i, i);
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa bảng giá.");
        }
    }
    
    private void xoaBangGia() {
        if(bangGiaSelected == null || "BG001".equals(bangGiaSelected.getMaBangGia())) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bảng Giá hợp lệ bên trái để xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "<html><p style='color:red; font-weight:bold;'>Xóa Bảng giá này sẽ XÓA TOÀN BỘ chi tiết giá món ăn bên trong.</p>Bạn có chắc chắn muốn xóa vĩnh viễn?</html>", 
            "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if(confirm == JOptionPane.YES_OPTION) {
            if(bangGiaDAO.xoaBangGia(bangGiaSelected.getMaBangGia())) {
                JOptionPane.showMessageDialog(this, "Xóa Bảng giá thành công!");
                loadDataBangGia();
                lamMoiFormBangGia();
                modelChiTiet.setRowCount(0);
                bangGiaSelected = null;
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa bảng giá.");
            }
        }
    }
    
    private void hienThiDialogThemMon() {
        if(bangGiaSelected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Bảng Giá bên trái trước khi thêm món!");
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm món vào Bảng giá", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel pnlMain = new JPanel(new GridLayout(4, 2, 10, 15));
        pnlMain.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JComboBox<String> cboMonAn = new JComboBox<>();
        List<MonAn> listMon = monAnDAO.getAllMonAn();
        for (MonAn m : listMon) {
            cboMonAn.addItem(m.getMaMonAn() + " - " + m.getTenMon());
        }
        
        JLabel lblGiaGoc = new JLabel("0");
        lblGiaGoc.setForeground(Color.RED);
        lblGiaGoc.setFont(new Font("Arial", Font.BOLD, 12));
        JTextField txtGiaMoi = new JTextField();
        
        cboMonAn.addActionListener(e -> {
            int idx = cboMonAn.getSelectedIndex();
            if(idx >= 0) {
                lblGiaGoc.setText(String.format("%,.0f đ", listMon.get(idx).getGiaMon()));
            }
        });
        
        if(!listMon.isEmpty()) {
            cboMonAn.setSelectedIndex(0);
        }
        
        pnlMain.add(new JLabel("Chọn món ăn:"));
        pnlMain.add(cboMonAn);
        
        pnlMain.add(new JLabel("Giá gốc hiện tại:"));
        pnlMain.add(lblGiaGoc);
        
        pnlMain.add(new JLabel("Nhập giá bán mới:"));
        pnlMain.add(txtGiaMoi);
        
        JButton btnLuu = new JButton("Lưu");
        JButton btnHuy = new JButton("Hủy");
        
        btnLuu.addActionListener(e -> {
            int idx = cboMonAn.getSelectedIndex();
            if(idx < 0) return;
            MonAn monSelected = listMon.get(idx);
            
            try {
                double giaMoi = Double.parseDouble(txtGiaMoi.getText().trim());
                ChiTietMonAn ct = new ChiTietMonAn();
                ct.setMaCTBangGia("CT" + System.currentTimeMillis()); 
                ct.setBangGia(bangGiaSelected);
                ct.setMonAn(monSelected);
                ct.setGiaBan(giaMoi);
                
                if(chiTietDAO.themChiTiet(ct)) {
                    JOptionPane.showMessageDialog(dialog, "Thêm giá món ăn thành công!");
                    loadChiTietBangGia(bangGiaSelected.getMaBangGia());
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm chi tiết món. Có thể món này đã được thêm.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá mới là một số hợp lệ!");
            }
        });
        
        btnHuy.addActionListener(e -> dialog.dispose());
        
        pnlMain.add(btnLuu);
        pnlMain.add(btnHuy);
        
        dialog.add(pnlMain);
        dialog.setVisible(true);
    }
}
