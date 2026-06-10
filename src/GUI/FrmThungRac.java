package GUI;

import DAO.NhanVienDAO;
import Entity.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmThungRac extends JPanel {
    private JTabbedPane tabbedPane;
    private JPanel pnlNhanVien;
    private JTable tblNhanVien;
    private DefaultTableModel modelNhanVien;
    private JButton btnKhoiPhucNhanVien, btnXoaVinhVienNhanVien;
    private NhanVienDAO nhanVienDAO;

    public FrmThungRac() {
        nhanVienDAO = new NhanVienDAO();
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        initTabNhanVien();
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initTabNhanVien() {
        pnlNhanVien = new JPanel(new BorderLayout());
        
        String[] cols = {"Mã NV", "Họ tên", "Chức vụ", "SĐT"};
        modelNhanVien = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblNhanVien = new JTable(modelNhanVien);
        tblNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblNhanVien.setRowHeight(30);
        tblNhanVien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JScrollPane scroll = new JScrollPane(tblNhanVien);
        pnlNhanVien.add(scroll, BorderLayout.CENTER);
        
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnKhoiPhucNhanVien = new JButton("Khôi phục");
        btnKhoiPhucNhanVien.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKhoiPhucNhanVien.setBackground(new Color(34, 197, 94));
        btnKhoiPhucNhanVien.setForeground(Color.WHITE);
        btnKhoiPhucNhanVien.setFocusPainted(false);
        
        btnXoaVinhVienNhanVien = new JButton("Xóa vĩnh viễn");
        btnXoaVinhVienNhanVien.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXoaVinhVienNhanVien.setBackground(new Color(220, 38, 38));
        btnXoaVinhVienNhanVien.setForeground(Color.WHITE);
        btnXoaVinhVienNhanVien.setFocusPainted(false);
        
        pnlBottom.add(btnKhoiPhucNhanVien);
        pnlBottom.add(btnXoaVinhVienNhanVien);
        pnlNhanVien.add(pnlBottom, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Nhân viên", pnlNhanVien);
        
        loadDataNhanVien();
        
        btnKhoiPhucNhanVien.addActionListener(e -> khoiPhucNhanVien());
        btnXoaVinhVienNhanVien.addActionListener(e -> xoaVinhVienNhanVien());
    }

    private void loadDataNhanVien() {
        modelNhanVien.setRowCount(0);
        List<NhanVien> list = nhanVienDAO.getNhanVienDaXoa();
        for (NhanVien nv : list) {
            modelNhanVien.addRow(new Object[]{
                nv.getMaNV(), nv.getHoTenNV(), nv.getVaiTro(), nv.getSoDienThoai()
            });
        }
    }

    private void khoiPhucNhanVien() {
        int row = tblNhanVien.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần khôi phục!");
            return;
        }
        String maNV = tblNhanVien.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn khôi phục nhân viên " + maNV + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (nhanVienDAO.khoiPhucNhanVien(maNV)) {
                JOptionPane.showMessageDialog(this, "Khôi phục thành công!");
                loadDataNhanVien();
            } else {
                JOptionPane.showMessageDialog(this, "Khôi phục thất bại!");
            }
        }
    }

    private void xoaVinhVienNhanVien() {
        int row = tblNhanVien.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa vĩnh viễn!");
            return;
        }
        String maNV = tblNhanVien.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn XÓA VĨNH VIỄN nhân viên " + maNV + "? Dữ liệu không thể khôi phục!", "Xác nhận xóa vĩnh viễn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (nhanVienDAO.xoaVinhVienNhanVien(maNV)) {
                JOptionPane.showMessageDialog(this, "Xóa vĩnh viễn thành công!");
                loadDataNhanVien();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa vĩnh viễn thất bại!");
            }
        }
    }
}
