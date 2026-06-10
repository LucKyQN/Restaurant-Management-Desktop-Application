package GUI;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Entity.BanComboItem;
import Entity.BanAn;
import DAO.BanAnDAO;
import DAO.HoaDonDAO;

public class FrmChuyenBan extends JDialog {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(40, 40, 40);

    private JComboBox<BanComboItem> cbBanCu;
    private JComboBox<BanComboItem> cbBanMoi;
    
    private FrmLeTan parentFrm;
    private BanAnDAO banAnDAO = new BanAnDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    
    private List<BanAn> listBan;

    public FrmChuyenBan(FrmLeTan parent) {
        super(parent, true);
        this.parentFrm = parent;
        setUndecorated(true);
        setSize(500, 350);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 2));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBody(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        
        loadData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Chuyển bàn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnClose = new JButton("");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> this.dispose()); 

        header.add(title, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);
        
        JPanel bottomLine = new JPanel(new BorderLayout());
        bottomLine.add(header, BorderLayout.CENTER);
        bottomLine.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        return bottomLine;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new GridLayout(2, 1, 0, 15));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 30, 20, 30));

        cbBanCu = new JComboBox<>();
        cbBanCu.setBackground(Color.WHITE);
        
        cbBanMoi = new JComboBox<>();
        cbBanMoi.setBackground(Color.WHITE);

        body.add(createInputGroup("Từ bàn (Đang có khách):", cbBanCu));
        body.add(createInputGroup("Đến bàn (Bàn trống):", cbBanMoi));

        return body;
    }

    private void loadData() {
        listBan = banAnDAO.getAllBanAn();
        cbBanCu.removeAllItems();
        cbBanMoi.removeAllItems();
        
        for (BanAn ban : listBan) {
            String trangThai = ban.getTrangThai().trim();
            if (trangThai.equalsIgnoreCase("Có khách")) {
                cbBanCu.addItem(new BanComboItem(ban.getMaBan(), ban.getTenBan()));
            } 
            else if (trangThai.equalsIgnoreCase("Trống")) {
                cbBanMoi.addItem(new BanComboItem(ban.getMaBan(), ban.getTenBan()));
            }
        }
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 35));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.addActionListener(e -> this.dispose());

        JButton btnXacNhan = new JButton("Xác nhận chuyển");
        btnXacNhan.setPreferredSize(new Dimension(150, 35));
        btnXacNhan.setBackground(RED_MAIN);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setBorderPainted(false);
        
        btnXacNhan.addActionListener(e -> {
            BanComboItem tuBan = (BanComboItem) cbBanCu.getSelectedItem();
            BanComboItem denBan = (BanComboItem) cbBanMoi.getSelectedItem();

            if (tuBan == null || denBan == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ bàn đi và bàn đến hợp lệ!");
                return;
            }


            
            String[] infoKhach = hoaDonDAO.getThongTinKhachVuaMo(tuBan.getMaBan());
            int soKhachHienTai = 0;
            if (infoKhach != null && infoKhach[2] != null) {
                try {
                    soKhachHienTai = Integer.parseInt(infoKhach[2]);
                } catch (NumberFormatException ex) {
                    soKhachHienTai = 0;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu hóa đơn của " + tuBan.getTenBan(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int sucChuaBanDich = 0;
            for (BanAn ban : listBan) {
                if (ban.getMaBan().equals(denBan.getMaBan())) {
                    sucChuaBanDich = ban.getSucChua();
                    break;
                }
            }

            if (soKhachHienTai > sucChuaBanDich) {
                String canhBao = "Không thể chuyển! Bàn đích không đủ chỗ.\n\n"
                               + "- Số khách cần chuyển: " + soKhachHienTai + " người\n"
                               + "- Sức chứa của " + denBan.getTenBan() + ": " + sucChuaBanDich + " người";
                
                JOptionPane.showMessageDialog(this, canhBao, "Cảnh báo quá tải", JOptionPane.WARNING_MESSAGE);
                return;
            }


            int confirm = JOptionPane.showConfirmDialog(this, 
                "Chuyển " + soKhachHienTai + " khách từ " + tuBan.getTenBan() + " sang " + denBan.getTenBan() + "?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                boolean chuyenHD = hoaDonDAO.chuyenBan(tuBan.getMaBan(), denBan.getMaBan());

                if (chuyenHD) {

                    banAnDAO.capNhatTrangThai(tuBan.getMaBan(), "Trống");
                    banAnDAO.capNhatTrangThai(denBan.getMaBan(), "Có khách");

                    JOptionPane.showMessageDialog(this, "Đã chuyển khách từ " + tuBan.getTenBan() + " sang " + denBan.getTenBan() + " thành công!");
                    

                    if (parentFrm != null) {
                        parentFrm.refreshSoDoBan();
                    }
                    
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL hoặc bàn chưa có hóa đơn chưa thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        footer.add(btnHuy);
        footer.add(btnXacNhan);
        return footer;
    }

    private JPanel createInputGroup(String title, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_DARK);
        
        input.setPreferredSize(new Dimension(0, 38));
        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }
}