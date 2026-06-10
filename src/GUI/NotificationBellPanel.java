package GUI;

import Entity.ThongBao;
import Service.NotificationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;


public class NotificationBellPanel extends JPanel
        implements NotificationService.NotificationListener {

    private final NotificationService service = NotificationService.getInstance();

    private final JButton btnBell;
    private final JLabel  lblBadge;

    private JDialog popup;
    private JPanel  panelList;

    public NotificationBellPanel() {
        setOpaque(false);


        btnBell = new JButton("\uD83D\uDD14"); // 🔔
        btnBell.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnBell.setContentAreaFilled(false);
        btnBell.setBorderPainted(false);
        btnBell.setFocusPainted(false);
        btnBell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBell.setToolTipText("Thông báo");
        btnBell.addActionListener(e -> togglePopup());


        lblBadge = new JLabel();
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setHorizontalAlignment(SwingConstants.CENTER);
        lblBadge.setVerticalAlignment(SwingConstants.CENTER);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(220, 38, 38));
        lblBadge.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        lblBadge.setVisible(false);


        setLayout(new OverlayLayout(this));
        add(lblBadge);
        lblBadge.setAlignmentX(1.0f);
        lblBadge.setAlignmentY(0.0f);
        add(btnBell);

        service.addListener(this);
        updateBadge(service.getSoChuaDoc());
    }



    private void togglePopup() {
        if (popup != null && popup.isVisible()) {
            popup.dispose();
            popup = null;
            return;
        }
        openPopup();
    }

    private void openPopup() {
        Window w  = SwingUtilities.getWindowAncestor(this);
        Point  p  = btnBell.getLocationOnScreen();
        int    x  = p.x + btnBell.getWidth() - 380;
        int    y  = p.y + btnBell.getHeight() + 4;

        popup = new JDialog(w);
        popup.setModal(false);
        popup.setUndecorated(true);
        popup.setSize(390, 460);
        popup.setLocation(x, y);


        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(new LineBorder(new Color(210, 210, 210), 1));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 245, 245));
        header.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("Thông báo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(40, 40, 40));

        JButton btnDong = new JButton("\u2715");
        btnDong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnDong.setContentAreaFilled(false);
        btnDong.setBorderPainted(false);
        btnDong.setFocusPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDong.addActionListener(e -> { popup.dispose(); popup = null; });

        JButton btnDanhDau = new JButton("Đánh dấu đã đọc tất cả");
        btnDanhDau.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnDanhDau.setContentAreaFilled(false);
        btnDanhDau.setBorderPainted(false);
        btnDanhDau.setForeground(new Color(59, 130, 246));
        btnDanhDau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDanhDau.addActionListener(e -> {
            service.danhDauTatCaDaDoc();
            refreshList();
        });

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        headerRight.setOpaque(false);
        headerRight.add(btnDanhDau);
        headerRight.add(btnDong);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        content.add(header, BorderLayout.NORTH);

        // Danh sách thông báo
        panelList = new JPanel();
        panelList.setLayout(new BoxLayout(panelList, BoxLayout.Y_AXIS));
        panelList.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(panelList);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        content.add(scroll, BorderLayout.CENTER);

        refreshList();

        popup.setContentPane(content);
        popup.setAlwaysOnTop(true);

        // Đóng khi mất focus
        popup.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                if (popup != null) { popup.dispose(); popup = null; }
            }
        });
        popup.setVisible(true);
    }


    private void refreshList() {
        panelList.removeAll();
        List<ThongBao> list = service.getTatCaThongBao();

        if (list.isEmpty()) {
            JLabel empty = new JLabel("Không có thông báo nào", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(120, 120, 120));
            empty.setBorder(new EmptyBorder(30, 0, 30, 0));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelList.add(empty);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM");
            for (ThongBao tb : list) {
                panelList.add(createItem(tb, sdf));
            }
        }
        panelList.revalidate();
        panelList.repaint();
    }



    private JPanel createItem(ThongBao tb, SimpleDateFormat sdf) {
        Color bg = tb.isDaDoc() ? Color.WHITE : new Color(245, 247, 255);

        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(bg);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235)),
                new EmptyBorder(10, 15, 10, 15)));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel lblIcon = new JLabel(tb.getLoaiIcon());
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcon.setVerticalAlignment(SwingConstants.TOP);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JLabel lblTieuDe = new JLabel(tb.getTieuDe());
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTieuDe.setForeground(new Color(30, 30, 30));

        JLabel lblNoiDung = new JLabel(
                "<html><div style='width:280px'>" + tb.getNoiDung() + "</div></html>");
        lblNoiDung.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNoiDung.setForeground(new Color(80, 80, 80));

        String thoiGian = (tb.getThoiGian() != null) ? sdf.format(tb.getThoiGian()) : "";
        JLabel lblTime = new JLabel(thoiGian);
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTime.setForeground(new Color(150, 150, 150));

        right.add(lblTieuDe);
        right.add(Box.createVerticalStrut(2));
        right.add(lblNoiDung);
        right.add(Box.createVerticalStrut(4));
        right.add(lblTime);

        item.add(lblIcon, BorderLayout.WEST);
        item.add(right,   BorderLayout.CENTER);

        // Click → đánh dấu đã đọc
        if (!tb.isDaDoc()) {
            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    service.danhDauDaDoc(tb.getId());
                    tb.setDaDoc(true);
                    item.setBackground(Color.WHITE);
                    item.repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    item.setBackground(new Color(235, 240, 255));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    item.setBackground(tb.isDaDoc() ? Color.WHITE : new Color(245, 247, 255));
                }
            });
        }

        return item;
    }



    private void updateBadge(int count) {
        if (count > 0) {
            lblBadge.setText(count > 99 ? "99+" : String.valueOf(count));
            lblBadge.setVisible(true);
        } else {
            lblBadge.setVisible(false);
        }
    }


    @Override
    public void onNotificationReceived(List<ThongBao> notifications) {
        SwingUtilities.invokeLater(() -> {
            if (popup != null && popup.isVisible()) {
                refreshList();
            }
        });
    }

    @Override
    public void onNotificationCountChanged(int count) {
        SwingUtilities.invokeLater(() -> updateBadge(count));
    }
}
