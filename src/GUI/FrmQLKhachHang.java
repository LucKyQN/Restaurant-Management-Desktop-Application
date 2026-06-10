package GUI;

import DAO.KhachHangDAO;
import Entity.KhachHang;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class FrmQLKhachHang extends JPanel {
    private JTable tblKH;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private KhachHangDAO khDAO = new KhachHangDAO();

    private Color RED_MAIN = new Color(220, 38, 38);

    public FrmQLKhachHang() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblTitle = new JLabel("Quản lý thông tin khách hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // Thanh tìm kiếm
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setOpaque(false);
        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setToolTipText("Nhập tên hoặc số điện thoại");

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(RED_MAIN);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> performSearch());

        pnlSearch.add(new JLabel("🔍"));
        pnlSearch.add(txtSearch);
        pnlSearch.add(btnSearch);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        // Bảng dữ liệu
        String[] cols = {"Mã KH", "Tên khách hàng", "Số điện thoại", "Ngày tham gia", "Điểm tích lũy", "Trạng thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKH = new JTable(model);
        tblKH.setRowHeight(35);
        tblKH.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(tblKH);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        scroll.getViewport().setBackground(Color.WHITE);

        add(pnlHeader, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        List<KhachHang> ds = khDAO.getAllKhachHang();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (KhachHang kh : ds) {
            model.addRow(new Object[]{
                    kh.getMaKH(), kh.getTen(), kh.getSoDienThoai(),
                    sdf.format(kh.getNgayThamGia()), kh.getDiemTichLuy(),
                    kh.isTrangThai() ? "Hoạt động" : "Ngưng"
            });
        }
    }

    private void performSearch() {
        String key = txtSearch.getText().trim();
        model.setRowCount(0);
        List<KhachHang> ds = khDAO.timKiemKhachHang(key);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (KhachHang kh : ds) {
            model.addRow(new Object[]{
                    kh.getMaKH(), kh.getTen(), kh.getSoDienThoai(),
                    sdf.format(kh.getNgayThamGia()), kh.getDiemTichLuy(),
                    kh.isTrangThai() ? "Hoạt động" : "Ngưng"
            });
        }
    }
}