package GUI;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Entity.BanComboItem;
import Entity.BanAn;
import DAO.BanAnDAO;
import DAO.HoaDonDAO;

public class FrmGopBan extends JDialog {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_DARK = new Color(40, 40, 40);

    private JComboBox<BanComboItem> cbBanNguon;
    private JComboBox<BanComboItem> cbBanDich;

    private FrmLeTan parentFrm;
    private BanAnDAO banAnDAO = new BanAnDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private List<BanAn> listBan;

    public FrmGopBan(FrmLeTan parent) {
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

        JLabel title = new JLabel("Gộp bàn");
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

        cbBanNguon = new JComboBox<>();
        cbBanNguon.setBackground(Color.WHITE);
        
        cbBanDich = new JComboBox<>();
        cbBanDich.setBackground(Color.WHITE);

        cbBanNguon.addActionListener(e -> reloadBanDich());

        body.add(createInputGroup("Từ bàn (Bàn bị gộp):", cbBanNguon));
        body.add(createInputGroup("Đến bàn (Bàn gộp vào):", cbBanDich));

        return body;
    }

    // --- LOGIC LOAD DỮ LIỆU ---
    private void loadData() {
        listBan = banAnDAO.getAllBanAn();
        cbBanNguon.removeAllItems();
        
        for (BanAn ban : listBan) {
            if (ban.getTrangThai().trim().equalsIgnoreCase("Có khách")) {
                cbBanNguon.addItem(new BanComboItem(ban.getMaBan(), ban.getTenBan()));
            }
        }
        
        reloadBanDich();
    }

    private void reloadBanDich() {
        cbBanDich.removeAllItems();
        BanComboItem selectedNguon = (BanComboItem) cbBanNguon.getSelectedItem();
        
        for (BanAn ban : listBan) {
            if (ban.getTrangThai().trim().equalsIgnoreCase("Có khách")) {
                if (selectedNguon != null && !ban.getMaBan().equals(selectedNguon.getMaBan())) {
                    cbBanDich.addItem(new BanComboItem(ban.getMaBan(), ban.getTenBan()));
                }
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

        JButton btnXacNhan = new JButton("Xác nhận gộp");
        btnXacNhan.setPreferredSize(new Dimension(150, 35));
        btnXacNhan.setBackground(RED_MAIN);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setBorderPainted(false);
        
        btnXacNhan.addActionListener(e -> {
            BanComboItem banNguon = (BanComboItem) cbBanNguon.getSelectedItem();
            BanComboItem banDich = (BanComboItem) cbBanDich.getSelectedItem();

            if (banNguon == null || banDich == null) {
                JOptionPane.showMessageDialog(this, "Không đủ dữ liệu để gộp bàn! Cần ít nhất 2 bàn có khách.");
                return;
            }


            String[] infoNguon = hoaDonDAO.getThongTinKhachVuaMo(banNguon.getMaBan());
            String[] infoDich = hoaDonDAO.getThongTinKhachVuaMo(banDich.getMaBan());

            int soKhachNguon = 0;
            int soKhachDich = 0;

            if (infoNguon != null && infoNguon[2] != null) {
                try { soKhachNguon = Integer.parseInt(infoNguon[2]); } catch (Exception ex) {}
            }
            if (infoDich != null && infoDich[2] != null) {
                try { soKhachDich = Integer.parseInt(infoDich[2]); } catch (Exception ex) {}
            }

            int tongKhach = soKhachNguon + soKhachDich;

            int sucChuaBanDich = 0;
            for (BanAn ban : listBan) {
                if (ban.getMaBan().equals(banDich.getMaBan())) {
                    sucChuaBanDich = ban.getSucChua();
                    break;
                }
            }

            if (tongKhach > sucChuaBanDich) {
                String canhBao = "Bàn đích (" + banDich.getTenBan() + " - sức chứa " + sucChuaBanDich + " người) "
                               + "sẽ bị quá tải với tổng cộng " + tongKhach + " khách.\n\n"
                               + "Bạn có muốn tiếp tục gộp (kéo 2 bàn lại với nhau) không?";
                               
                int luaChon = JOptionPane.showConfirmDialog(this, canhBao, "Cảnh báo quá tải", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (luaChon != JOptionPane.YES_OPTION) {
                    return;
                }
            }


            int confirm = JOptionPane.showConfirmDialog(this, 
                "Chuyển toàn bộ món ăn từ " + banNguon.getTenBan() + " sang " + banDich.getTenBan() + "?", 
                "Xác nhận gộp", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean thanhCong = hoaDonDAO.gopBan(banNguon.getMaBan(), banDich.getMaBan());

                if (thanhCong) {
                    banAnDAO.capNhatTrangThai(banNguon.getMaBan(), "Đang ghép với " + banDich.getTenBan());

                    JOptionPane.showMessageDialog(this, "Đã gộp toàn bộ món của " + banNguon.getTenBan() + " sang " + banDich.getTenBan() + " thành công!");
                    
                    if (parentFrm != null) {
                        parentFrm.refreshSoDoBan();
                    }
                    
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL khi gộp bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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