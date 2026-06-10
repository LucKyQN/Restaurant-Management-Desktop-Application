package GUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import DAO.BanAnDAO;
import DAO.DonDatMonDAO;
import DAO.MonAnDAO;
import DAO.PhieuDatBanDAO;
import Entity.BanAn;
import Entity.MonAn;
import Entity.PhieuDatBan;
import connectDatabase.ConnectDB;
import com.toedter.calendar.JCalendar;
import java.beans.PropertyChangeListener;

public class FrmTaoDatCho extends JDialog {

    private static final Color RED_MAIN = new Color(220, 38, 38);
    private static final Color BORDER_CLR = new Color(230, 230, 230);
    private static final Color TEXT_GRAY = new Color(120, 120, 120);

    private static final int PHI_DAT_BAN_CO_DINH = 250000;

    private DefaultTableModel tbModelDaChon;
    private JLabel lblTongTien;
    private JLabel lblPhiDatBan;
    private JLabel lblCocMon;
    private JLabel lblTongTienCoc;

    private int tongTien = 0;

    private List<BanAn> selectedTables = new ArrayList<>();

    private JTextField txtTenKhach;
    private JTextField txtSDT;
    private JTextField txtSoLuong;

    private JTextArea txtNote;
    private JComboBox<String> cboGio;
    private JComboBox<String> cboPut;
    private JTextField txtTimKiemMon;
    private JPanel listFood;
    private List<MonAn> dsMonToanBo;
    private com.toedter.calendar.JDateChooser dateChooser;


    private JTabbedPane tabbedMap;

    public FrmTaoDatCho(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setSize(1000, 780);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBody(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Tạo đặt chỗ mới");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton btnClose = new JButton("");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        header.add(title, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);

        JPanel bottomLine = new JPanel(new BorderLayout());
        bottomLine.add(header, BorderLayout.CENTER);
        bottomLine.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));

        return bottomLine;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new GridLayout(1, 2, 40, 0));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel pnlLeft = new JPanel(new BorderLayout(0, 15));
        pnlLeft.setBackground(Color.WHITE);


        JPanel pnlLeftTop = new JPanel();
        pnlLeftTop.setLayout(new BoxLayout(pnlLeftTop, BoxLayout.Y_AXIS));
        pnlLeftTop.setBackground(Color.WHITE);

        JLabel lblInfoTitle = new JLabel("Thông tin khách hàng");
        lblInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlLeftTop.add(lblInfoTitle);
        pnlLeftTop.add(Box.createVerticalStrut(15));

        txtTenKhach = new JTextField();
        txtSDT = new JTextField();

        txtSDT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String sdt = txtSDT.getText().trim();
                if (sdt.length() >= 10) {
                    timVaDienTenKhachHang(sdt);
                }
            }
        });

        pnlLeftTop.add(createInputGroup("Số điện thoại (Nhập để tự động tìm khách cũ) *", txtSDT));
        pnlLeftTop.add(Box.createVerticalStrut(15));
        pnlLeftTop.add(createInputGroup("Tên khách hàng *", txtTenKhach));
        pnlLeftTop.add(Box.createVerticalStrut(15));

        JPanel rowTime = new JPanel(new GridLayout(1, 3, 10, 0));
        rowTime.setBackground(Color.WHITE);

        txtSoLuong = new JTextField("2");

        // JCalendar for date selection
        dateChooser = new com.toedter.calendar.JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new java.util.Date());
        dateChooser.setPreferredSize(new Dimension(0, 35));

        // Hour and minute combos
        String[] hours = new String[24];
        String[] minutes = new String[60];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i);
        }
        for (int i = 0; i < 60; i++) {
            minutes[i] = String.format("%02d", i);
        }
        cboGio = new JComboBox<>(hours);
        cboGio.setSelectedItem("09");
        cboPut = new JComboBox<>(minutes);
        cboPut.setSelectedItem("00");

        dateChooser.addPropertyChangeListener("date", e -> refreshSodoMiniMap());

        cboGio.addActionListener(ev -> refreshSodoMiniMap());
        cboPut.addActionListener(ev -> refreshSodoMiniMap());

        JPanel pnlGio = new JPanel(new GridLayout(1, 2, 5, 0));
        pnlGio.setBackground(Color.WHITE);
        pnlGio.add(cboGio);
        pnlGio.add(cboPut);

        rowTime.add(createInputGroup("Số lượng", txtSoLuong));
        rowTime.add(createInputGroup("Giờ đến (hh:mm)", pnlGio));
        rowTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pnlLeftTop.add(rowTime);
        rowTime.add(createInputGroup("Ngày đến", dateChooser));


        JPanel pnlMapWrap = new JPanel(new BorderLayout(0, 5));
        pnlMapWrap.setBackground(Color.WHITE);

        JLabel lblMapTitle = new JLabel("Chọn bàn");
        lblMapTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnlMapWrap.add(lblMapTitle, BorderLayout.NORTH);

        tabbedMap = new JTabbedPane();
        tabbedMap.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedMap.setBackground(Color.WHITE);
        tabbedMap.setFocusable(false);

        pnlMapWrap.add(tabbedMap, BorderLayout.CENTER);

        refreshSodoMiniMap();

        txtNote = new JTextArea();
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setBorder(new EmptyBorder(5, 5, 5, 5));
        JScrollPane scrollNote = new JScrollPane(txtNote);
        scrollNote.setBorder(BorderFactory.createLineBorder(BORDER_CLR));

        JPanel pnlNoteWrap = createInputGroup("Ghi chú", scrollNote);
        pnlNoteWrap.setPreferredSize(new Dimension(0, 90));

        pnlLeft.add(pnlLeftTop, BorderLayout.NORTH);
        pnlLeft.add(pnlMapWrap, BorderLayout.CENTER);
        pnlLeft.add(pnlNoteWrap, BorderLayout.SOUTH);


        JPanel pnlRight = new JPanel(new BorderLayout(0, 15));
        pnlRight.setBackground(Color.WHITE);

        JPanel pnlMenu = new JPanel(new BorderLayout(0, 5));
        pnlMenu.setPreferredSize(new Dimension(0, 240));
        pnlMenu.setBackground(Color.WHITE);

        JPanel pnlMenuHeader = new JPanel(new BorderLayout(0, 5));
        pnlMenuHeader.setBackground(Color.WHITE);
        JLabel lblMenuTitle = new JLabel("Thực đơn");
        lblMenuTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtTimKiemMon = new JTextField();
        txtTimKiemMon.setPreferredSize(new Dimension(0, 32));
        txtTimKiemMon.setToolTipText("Tìm kiếm món ăn");
        txtTimKiemMon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                hienThiDanhSachMon();
            }
        });

        pnlMenuHeader.add(lblMenuTitle, BorderLayout.NORTH);
        pnlMenuHeader.add(txtTimKiemMon, BorderLayout.CENTER);
        pnlMenu.add(pnlMenuHeader, BorderLayout.NORTH);

        listFood = new JPanel();
        listFood.setLayout(new BoxLayout(listFood, BoxLayout.Y_AXIS));
        listFood.setBackground(Color.WHITE);

        MonAnDAO monAnDAO = new MonAnDAO();
        dsMonToanBo = monAnDAO.getAllMonAn();
        hienThiDanhSachMon();

        JScrollPane scrollFood = new JScrollPane(listFood);
        scrollFood.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        scrollFood.getVerticalScrollBar().setUnitIncrement(16);
        pnlMenu.add(scrollFood, BorderLayout.CENTER);

        JPanel pnlCart = new JPanel(new BorderLayout(0, 8));
        pnlCart.setBackground(Color.WHITE);

        JLabel lblCartTitle = new JLabel("Món đã chọn");
        lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlCart.add(lblCartTitle, BorderLayout.NORTH);

        String[] columns = {"Mã món", "Tên món", "SL", "Đơn giá"};
        tbModelDaChon = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tbDaChon = new JTable(tbModelDaChon);
        tbDaChon.setRowHeight(25);
        tbDaChon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tbDaChon.getSelectedRow();
                    if (row != -1) {
                        int slCu = Integer.parseInt(tbModelDaChon.getValueAt(row, 2).toString());
                        long donGia = Long.parseLong(tbModelDaChon.getValueAt(row, 3).toString());

                        tongTien -= donGia;

                        if (slCu > 1) {
                            tbModelDaChon.setValueAt(slCu - 1, row, 2);
                        } else {
                            tbModelDaChon.removeRow(row);
                        }
                        capNhatTienCoc();
                    }
                }
            }
        });
        tbDaChon.getColumnModel().getColumn(0).setMinWidth(0);
        tbDaChon.getColumnModel().getColumn(0).setMaxWidth(0);
        tbDaChon.getColumnModel().getColumn(0).setWidth(0);
        tbDaChon.getColumnModel().getColumn(1).setPreferredWidth(180);
        tbDaChon.getColumnModel().getColumn(2).setPreferredWidth(40);
        tbDaChon.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollCart = new JScrollPane(tbDaChon);
        scrollCart.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        pnlCart.add(scrollCart, BorderLayout.CENTER);

        JPanel pnlBottomRight = new JPanel();
        pnlBottomRight.setLayout(new BoxLayout(pnlBottomRight, BoxLayout.Y_AXIS));
        pnlBottomRight.setBackground(Color.WHITE);

        lblTongTien = new JLabel("Tổng cộng: 0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien.setForeground(RED_MAIN);
        lblTongTien.setAlignmentX(Component.RIGHT_ALIGNMENT);

        pnlBottomRight.add(Box.createVerticalStrut(8));
        pnlBottomRight.add(lblTongTien);
        pnlBottomRight.add(Box.createVerticalStrut(12));
        pnlBottomRight.add(createDepositPanel());

        pnlRight.add(pnlMenu, BorderLayout.NORTH);
        pnlRight.add(pnlCart, BorderLayout.CENTER);
        pnlRight.add(pnlBottomRight, BorderLayout.SOUTH);

        body.add(pnlLeft);
        body.add(pnlRight);
        return body;
    }

    private void timVaDienTenKhachHang(String sdt) {
        String sql = "SELECT tenKH FROM KhachHang WHERE soDienThoai = ?";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String ten = rs.getString("tenKH");
                txtTenKhach.setText(ten);
                txtTenKhach.setForeground(new Color(22, 163, 74));
            } else {
                txtTenKhach.setForeground(Color.BLACK);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshSodoMiniMap() {
        tabbedMap.removeAll();
        selectedTables.clear();

        java.util.Date thoiGianDat = getThoiGianDatBan();
        long diffMinutes = (thoiGianDat.getTime() - System.currentTimeMillis()) / 60000;

        BanAnDAO dao = new BanAnDAO();
        List<BanAn> dsBan = dao.getAllBanAn();

        String[] tangList = {"Tầng 1", "Tầng 2", "Phòng VIP"};

        for (String tang : tangList) {
            JPanel pnlFloor = new JPanel(new GridLayout(0, 4, 8, 8));
            pnlFloor.setBackground(Color.WHITE);
            pnlFloor.setBorder(new EmptyBorder(8, 8, 8, 8));

            for (BanAn ban : dsBan) {
                if (ban.getViTri() != null && ban.getViTri().trim().equalsIgnoreCase(tang)) {
                    String status = ban.getTrangThai() != null ? ban.getTrangThai().trim() : "";

                    boolean hienThiBan = false;
                    boolean laBanSapTrong = false;

                    if (status.equalsIgnoreCase("Trống")) {
                        hienThiBan = true;
                    } else if (status.equalsIgnoreCase("Có khách")) {
                        if (diffMinutes >= 150) {
                            hienThiBan = true;
                            laBanSapTrong = true;
                        }
                    }

                    if (hienThiBan) {
                        pnlFloor.add(createMiniTableCard(ban, laBanSapTrong));
                    }
                }
            }

            JPanel wrapTop = new JPanel(new BorderLayout());
            wrapTop.setBackground(Color.WHITE);
            wrapTop.add(pnlFloor, BorderLayout.NORTH);

            JScrollPane scrollMap = new JScrollPane(wrapTop);
            scrollMap.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
            scrollMap.getVerticalScrollBar().setUnitIncrement(16);

            tabbedMap.addTab(tang, scrollMap);
        }
    }

    private JPanel createMiniTableCard(BanAn ban, boolean laBanSapTrong) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
        card.setPreferredSize(new Dimension(80, 65));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblName = new JLabel(ban.getTenBan(), SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(new Color(40, 40, 40));

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlBottom.setOpaque(false);

        JLabel lblCap = new JLabel("" + ban.getSucChua());
        lblCap.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        lblCap.setForeground(TEXT_GRAY);
        pnlBottom.add(lblCap);

        if (laBanSapTrong) {
            JLabel lblNote = new JLabel("(Sẽ trống)");
            lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            lblNote.setForeground(new Color(239, 68, 68));
            pnlBottom.add(lblNote);
        }

        card.add(lblName, BorderLayout.CENTER);
        card.add(pnlBottom, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedTables.contains(ban)) {
                    selectedTables.remove(ban);
                    card.setBackground(Color.WHITE);
                    card.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
                    lblName.setForeground(new Color(40, 40, 40));
                } else {
                    selectedTables.add(ban);
                    card.setBackground(new Color(254, 226, 226));
                    card.setBorder(BorderFactory.createLineBorder(RED_MAIN, 2, true));
                    lblName.setForeground(RED_MAIN);
                }
            }
        });
        return card;
    }

    private java.util.Date getThoiGianDatBan() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(dateChooser.getDate());

        int hour = Integer.parseInt(cboGio.getSelectedItem().toString());
        int minute = Integer.parseInt(cboPut.getSelectedItem().toString());

        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        cal.set(java.util.Calendar.MINUTE, minute);
        cal.set(java.util.Calendar.SECOND, 0);

        return cal.getTime();
    }


    private void hienThiDanhSachMon() {
        listFood.removeAll();
        String keyword = txtTimKiemMon.getText().toLowerCase();

        for (MonAn mon : dsMonToanBo) {
            if (!mon.isTinhTrang()) continue;

            String ten = mon.getTenMon();
            if (keyword.isEmpty() || ten.toLowerCase().contains(keyword)) {
                int gia = (int) mon.getGiaMon();
                String icon = getIconByName(ten);
                listFood.add(createFoodItem(mon.getMaMonAn(), icon, ten, gia, "Món ăn"));
                listFood.add(Box.createVerticalStrut(10));
            }
        }
        listFood.revalidate();
        listFood.repaint();
    }

    private JPanel createDepositPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 252, 235));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 203, 92), 1, true), new EmptyBorder(14, 16, 14, 16)));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Tiền cọc phải thu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(146, 64, 14));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(14));

        lblPhiDatBan = new JLabel("250.000 đ");
        lblCocMon = new JLabel("0 đ");
        lblTongTienCoc = new JLabel("250.000 đ");

        content.add(createMoneyInfoRow("Phí đặt bàn:", lblPhiDatBan));
        content.add(Box.createVerticalStrut(8));
        content.add(createMoneyInfoRow("Cọc món đặt trước:", lblCocMon));
        content.add(Box.createVerticalStrut(10));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(245, 203, 92));
        content.add(sep);
        content.add(Box.createVerticalStrut(10));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);

        JLabel lblTotalText = new JLabel("Tổng tiền cọc:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalText.setForeground(new Color(146, 64, 14));

        lblTongTienCoc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTienCoc.setForeground(RED_MAIN);

        totalRow.add(lblTotalText, BorderLayout.WEST);
        totalRow.add(lblTongTienCoc, BorderLayout.EAST);

        content.add(totalRow);
        content.add(Box.createVerticalStrut(10));

        JLabel lblNote = new JLabel("<html><div style='text-align:right'><i>* Phí đặt bàn: 250.000đ<br/>* Đặt món trước: thu 30% tổng tiền món</i></div></html>");
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNote.setForeground(TEXT_GRAY);
        content.add(lblNote);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMoneyInfoRow(String label, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(80, 80, 80));

        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueLabel.setForeground(new Color(80, 80, 80));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lbl, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private void capNhatTienCoc() {
        int tienCocMon = (int) Math.round(tongTien * 0.3);
        int tongTienCoc = PHI_DAT_BAN_CO_DINH + tienCocMon;

        lblTongTien.setText("Tổng cộng: " + formatMoney(tongTien));
        lblPhiDatBan.setText(formatMoney(PHI_DAT_BAN_CO_DINH));
        lblCocMon.setText(formatMoney(tienCocMon));
        lblTongTienCoc.setText(formatMoney(tongTienCoc));
    }

    private String getIconByName(String ten) {
        if (ten == null) return "🍽️";
        ten = ten.toLowerCase();

        if (ten.contains("bò") || ten.contains("heo") || ten.contains("nướng")) return "🥩";
        if (ten.contains("gà") || ten.contains("vịt")) return "🍗";
        if (ten.contains("lẩu") || ten.contains("canh")) return "🍲";
        if (ten.contains("bia") || ten.contains("nước") || ten.contains("trà") || ten.contains("cà phê")) return "🥤";
        if (ten.contains("salad") || ten.contains("rau")) return "🥗";

        return "🍽️";
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(150, 40));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.addActionListener(e -> dispose());

        JButton btnXacNhan = new JButton("Xác nhận đặt chỗ");
        btnXacNhan.setPreferredSize(new Dimension(200, 40));
        btnXacNhan.setBackground(RED_MAIN);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setBorderPainted(false);

        btnXacNhan.addActionListener(e -> {
            String tenKhach = txtTenKhach.getText().trim();
            String sdt = txtSDT.getText().trim();
            String soLuongStr = txtSoLuong.getText().trim();
            String ghiChu = txtNote.getText().trim();

            if (tenKhach.isEmpty() || sdt.isEmpty() || soLuongStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedTables.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn ít nhất 1 bàn trên sơ đồ!", "Lưu ý", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int soLuongKhach;
            try {
                soLuongKhach = Integer.parseInt(soLuongStr);
                if (soLuongKhach <= 0) throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Số lượng khách phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int tongSucChua = 0;
            StringBuilder tenCacBanBuilder = new StringBuilder();
            for(BanAn ban : selectedTables) {
                tongSucChua += ban.getSucChua();
                tenCacBanBuilder.append(ban.getTenBan()).append(", ");
            }
            String tenCacBan = tenCacBanBuilder.toString();
            if(tenCacBan.endsWith(", ")) tenCacBan = tenCacBan.substring(0, tenCacBan.length() - 2);

            if (soLuongKhach > tongSucChua) {
                String msg = "Khách đặt " + soLuongKhach + " người, nhưng các bàn đã chọn chỉ chứa được tổng " + tongSucChua + " người.\n"
                        + "Bạn có muốn tiếp tục (kê thêm ghế) không?";
                int choice = JOptionPane.showConfirmDialog(this, msg, "Cảnh báo vượt sức chứa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice != JOptionPane.YES_OPTION) return;
            }

            java.util.Date thoiGianDen = getThoiGianDatBan();

            java.util.Date now = new java.util.Date();
            long diffMillis = thoiGianDen.getTime() - now.getTime();
            long diffMinutes = diffMillis / (60 * 1000);

            if (diffMinutes < 60) {
                JOptionPane.showMessageDialog(this,
                        "Thời gian đặt bàn phải cách hiện tại ít nhất 60 phút!\n"
                                + "Sớm nhất: " + new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy").format(new java.util.Date(now.getTime() + 60 * 60 * 1000)),
                        "Thời gian không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }


            PhieuDatBanDAO phieuDAO = new PhieuDatBanDAO();
            for (BanAn ban : selectedTables) {
                if (phieuDAO.kiemTraTrungGioDatBan(ban.getMaBan(), thoiGianDen)) {
                    JOptionPane.showMessageDialog(this,
                        "Bàn [" + ban.getTenBan() + "] đã có khách đặt trong khung giờ này (Cách nhau chưa tới 2.5 tiếng).\n"
                      + "Vui lòng chọn bàn khác hoặc thay đổi giờ đến!",
                        "Lỗi đụng giờ", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            double tienMonDatTruoc = tongTien;
            double tienCoc = PHI_DAT_BAN_CO_DINH + Math.round(tongTien * 0.3);

            String maBanDauTien = selectedTables.get(0).getMaBan();

            PhieuDatBan phieu = new PhieuDatBan();
            String maPhieu = "PDB" + System.currentTimeMillis();
            phieu.setMaPhieu(maPhieu);
            phieu.setTenKhachHang(tenKhach);
            phieu.setSoDienThoai(sdt);
            phieu.setSoLuongKhach(soLuongKhach);
            phieu.setThoiGianDen(thoiGianDen);
            phieu.setGhiChu(ghiChu);
            phieu.setMaBan(maBanDauTien);
            phieu.setTienMonDatTruoc(tienMonDatTruoc);
            phieu.setTienCoc(tienCoc);

            BanAnDAO banDAO = new BanAnDAO();
            DonDatMonDAO donDAO = new DonDatMonDAO();

            String maDon = "DDM" + System.currentTimeMillis();
            String maNV = (Entity.LuuLog.nhanVienDangNhap != null) ? Entity.LuuLog.nhanVienDangNhap.getMaNV() : "NV001";

            boolean phieuOk = phieuDAO.taoPhieuDatBan(phieu);
            boolean donOk = true;
            boolean ctOk = true;
            boolean chiTietBanOk = true;

            if (phieuOk) {
                try {
                    java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
                    java.sql.PreparedStatement psCT = con.prepareStatement("INSERT INTO ChiTietDatBan (maPhieu, maBan) VALUES (?, ?)");

                    for(BanAn ban : selectedTables) {
                        psCT.setString(1, maPhieu);
                        psCT.setString(2, ban.getMaBan());
                        psCT.executeUpdate();

                        banDAO.capNhatTrangThai(ban.getMaBan(), "Đã đặt");
                    }
                    psCT.close();
                } catch (Exception ex) {
                    chiTietBanOk = false;
                    ex.printStackTrace();
                    System.err.println("Lỗi lưu ChiTietDatBan. Vui lòng tạo bảng này trong SQL Server!");
                }

                if (tbModelDaChon.getRowCount() > 0) {
                    donOk = donDAO.taoDonDatMon(maDon, maNV, maBanDauTien, ghiChu);
                    if (donOk) {
                        for (int i = 0; i < tbModelDaChon.getRowCount(); i++) {
                            String maMonAn = tbModelDaChon.getValueAt(i, 0).toString();
                            int soLuongMon = Integer.parseInt(tbModelDaChon.getValueAt(i, 2).toString());
                            long donGia = Long.parseLong(tbModelDaChon.getValueAt(i, 3).toString());

                            if (!donDAO.themChiTietDonDatMon(maDon, maMonAn, soLuongMon, donGia, "")) {
                                ctOk = false;
                                break;
                            }
                        }
                    }
                }
            }

            if (phieuOk && donOk && ctOk && chiTietBanOk) {
                JOptionPane.showMessageDialog(this,
                        "Đặt chỗ thành công cho " + tenKhach + " tại các bàn: " + tenCacBan + "!\n"
                                + "Tiền món đặt trước: " + formatMoney((int) tienMonDatTruoc) + "\n"
                                + "Tổng tiền cọc cần thu: " + formatMoney((int) tienCoc));

                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (parentFrame instanceof FrmLeTan) {
                    ((FrmLeTan) parentFrame).refreshSoDoBan();
                    ((FrmLeTan) parentFrame).loadDanhSachDatCho();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Lỗi: Không thể lưu thông tin đặt chỗ. Vui lòng kiểm tra lại CSDL!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(btnHuy);
        footer.add(btnXacNhan);
        return footer;
    }

    private JPanel createInputGroup(String title, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        if (input instanceof JTextField) {
            ((JTextField) input).setPreferredSize(new Dimension(0, 35));
            input.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR),
                    new EmptyBorder(0, 10, 0, 10)));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        }

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFoodItem(String maMonAn, String emoji, String name, int price, String category) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(10, 10, 10, 10));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel lblImg = new JLabel(emoji, SwingConstants.CENTER);
        lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        lblImg.setPreferredSize(new Dimension(60, 60));
        lblImg.setBorder(BorderFactory.createLineBorder(BORDER_CLR));

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setBackground(Color.WHITE);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblPrice = new JLabel(formatMoney(price));
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPrice.setForeground(RED_MAIN);

        JLabel lblCat = new JLabel(category);
        lblCat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCat.setForeground(TEXT_GRAY);

        info.add(lblName);
        info.add(lblPrice);
        info.add(lblCat);

        JButton btnAdd = new JButton("Thêm");
        btnAdd.setBackground(RED_MAIN);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);

        btnAdd.addActionListener(e -> {
            boolean isExist = false;

            for (int i = 0; i < tbModelDaChon.getRowCount(); i++) {
                String maMonDangCo = tbModelDaChon.getValueAt(i, 0).toString();
                if (maMonDangCo.equals(maMonAn)) {
                    int slCu = Integer.parseInt(tbModelDaChon.getValueAt(i, 2).toString());
                    tbModelDaChon.setValueAt(slCu + 1, i, 2);
                    isExist = true;
                    break;
                }
            }

            if (!isExist) {
                tbModelDaChon.addRow(new Object[]{maMonAn, name, 1, price});
            }

            tongTien += price;
            capNhatTienCoc();
        });

        item.add(lblImg, BorderLayout.WEST);
        item.add(info, BorderLayout.CENTER);
        item.add(btnAdd, BorderLayout.EAST);

        return item;
    }

    private String formatMoney(int amount) {
        return String.format("%,d đ", amount).replace(',', '.');
    }
}
