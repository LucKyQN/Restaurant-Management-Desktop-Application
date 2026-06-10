
package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;
import connectDatabase.ConnectDB;

public class FrmMenu extends JFrame {

	private JPanel panelMenu;
	private JPanel panelCategory;
	private String currentCategory = "Tất cả";

	private static final Color BG        = new Color(248, 248, 248);
	private static final Color WHITE     = Color.WHITE;
	private static final Color RED       = new Color(220, 38, 38);
	private static final Color GREEN     = new Color(22, 163, 74);
	private static final Color GRAY_TEXT = new Color(107, 114, 128);
	private static final Color BORDER    = new Color(229, 231, 235);

	public FrmMenu() {
		setTitle("Thực đơn nhà hàng");
		setSize(1280, 800);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		initUI();
		lamMoiDuLieuMenu();
	}

	private void initUI() {
		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(BG);
		root.setBorder(new EmptyBorder(24, 28, 24, 28));
		root.add(createHeader(), BorderLayout.NORTH);
		root.add(createScrollArea(), BorderLayout.CENTER);
		setContentPane(root);
	}

	private JPanel createHeader() {
		JPanel wrap = new JPanel();
		wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
		wrap.setOpaque(false);
		wrap.setBorder(new EmptyBorder(0, 0, 20, 0));

		JLabel lblTitle = new JLabel("THỰC ĐƠN NHÀ HÀNG");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTitle.setForeground(new Color(20, 20, 20));
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel lblSub = new JLabel("Khám phá các món ăn hấp dẫn của Nhà hàng Ngói Đỏ");
		lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblSub.setForeground(GRAY_TEXT);
		lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

		panelCategory = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		panelCategory.setOpaque(false);
		panelCategory.setAlignmentX(Component.LEFT_ALIGNMENT);

		String[] cats = {"Tất cả", "Món khai vị", "Món chính", "Đồ uống", "Tráng miệng"};
		for (String cat : cats) panelCategory.add(makeCatBtn(cat));

		wrap.add(lblTitle);
		wrap.add(Box.createVerticalStrut(6));
		wrap.add(lblSub);
		wrap.add(Box.createVerticalStrut(18));
		wrap.add(panelCategory);
		return wrap;
	}

	private JButton makeCatBtn(String text) {
		JButton btn = new JButton(text) {
			@Override protected void paintComponent(Graphics g) {
				boolean active = text.equals(currentCategory);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(active ? RED : WHITE);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				if (!active) {
					g2.setColor(BORDER);
					g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
				}
				g2.dispose();
				setForeground(active ? WHITE : new Color(40, 40, 40));
				super.paintComponent(g);
			}
		};
		btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setOpaque(false);
		btn.setBorder(new EmptyBorder(8, 20, 8, 20));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.addActionListener(e -> {
			currentCategory = text;
			panelCategory.repaint();
			lamMoiDuLieuMenu();
		});
		return btn;
	}

	private JScrollPane createScrollArea() {
		panelMenu = new JPanel();
		panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
		panelMenu.setBackground(BG);

		JScrollPane scroll = new JScrollPane(panelMenu);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(BG);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		return scroll;
	}

	public void lamMoiDuLieuMenu() {
		panelMenu.removeAll();

		String sql = "SELECT m.tenMonAn, ISNULL(ct.giaBan, m.giaBan) AS giaBan, m.donVi, m.moTa, m.anhMon, dm.tenDM "
				+ "FROM MonAn m JOIN DanhMucMonAn dm ON m.maDM = dm.maDM "
				+ "LEFT JOIN ChiTietBangGia ct ON m.maMonAn = ct.maMonAn "
				+ "    AND ct.maBangGia = (SELECT TOP 1 maBangGia FROM BangGia WHERE trangThai = 1) "
				+ "WHERE m.tinhTrang = 1 ";
		boolean locLoai = !currentCategory.equalsIgnoreCase("Tất cả");
		if (locLoai) sql += "AND dm.tenDM = ? ";
		sql += "ORDER BY dm.tenDM, m.tenMonAn";

		try {
			ConnectDB.getInstance().connect();
			Connection con = ConnectDB.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			if (locLoai) ps.setString(1, currentCategory);
			ResultSet rs = ps.executeQuery();

			java.util.LinkedHashMap<String, java.util.List<Object[]>> groups = new java.util.LinkedHashMap<>();
			while (rs.next()) {
				String dm  = rs.getString("tenDM");
				groups.computeIfAbsent(dm, k -> new java.util.ArrayList<>()).add(new Object[]{
						rs.getString("tenMonAn"),
						String.format("%,.0f đ", rs.getDouble("giaBan")),
						rs.getString("donVi"),
						rs.getString("moTa"),
						rs.getString("anhMon"),
						dm
				});
			}
			rs.close(); ps.close();

			for (var entry : groups.entrySet()) {
				panelMenu.add(makeSectionHeader(entry.getKey()));
				panelMenu.add(Box.createVerticalStrut(12));
				panelMenu.add(makeCardGrid(entry.getValue()));
				panelMenu.add(Box.createVerticalStrut(28));
			}

			if (groups.isEmpty()) {
				JLabel empty = new JLabel("Không có món nào.", SwingConstants.CENTER);
				empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
				empty.setForeground(GRAY_TEXT);
				empty.setAlignmentX(Component.CENTER_ALIGNMENT);
				panelMenu.add(Box.createVerticalStrut(40));
				panelMenu.add(empty);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}

		panelMenu.revalidate();
		panelMenu.repaint();
	}

	private JPanel makeSectionHeader(String tenDM) {
		JPanel row = new JPanel(new BorderLayout(12, 0));
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		row.setBorder(new EmptyBorder(0, 0, 0, 0));

		JLabel lbl = new JLabel(tenDM);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lbl.setForeground(new Color(20, 20, 20));

		JSeparator sep = new JSeparator();
		sep.setForeground(BORDER);

		row.add(lbl, BorderLayout.WEST);
		row.add(sep, BorderLayout.CENTER);
		return row;
	}

	private JPanel makeCardGrid(java.util.List<Object[]> items) {
		JPanel grid = new JPanel(new GridLayout(0, 4, 16, 16));
		grid.setBackground(BG);

		for (Object[] item : items) {
			grid.add(makeCard(
					(String)item[0], (String)item[1],
					(String)item[2], (String)item[3],
					(String)item[4], (String)item[5]));
		}

		// Điền ô trống hàng cuối
		int rem = items.size() % 4;
		if (rem != 0) {
			for (int i = 0; i < 4 - rem; i++) {
				JPanel ph = new JPanel(); ph.setOpaque(false); grid.add(ph);
			}
		}

		JPanel wrap = new JPanel(new BorderLayout());
		wrap.setBackground(BG);
		wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		wrap.add(grid, BorderLayout.CENTER);
		return wrap;
	}

	private JPanel makeCard(String ten, String gia, String donVi,
							String moTa, String anh, String danhMuc) {
		JPanel card = new JPanel(new BorderLayout()) {
			boolean hover = false;
			{ addMouseListener(new MouseAdapter() {
				@Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
				@Override public void mouseExited (MouseEvent e) { hover = false; repaint(); }
				@Override public void mouseClicked(MouseEvent e) { showDetail(ten, gia, donVi, moTa, anh, danhMuc); }
			}); }
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(WHITE);
				g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
				g2.setColor(hover ? RED : BORDER);
				g2.setStroke(new BasicStroke(hover ? 1.5f : 1f));
				g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
				g2.dispose();
			}
		};
		card.setOpaque(false);
		card.setPreferredSize(new Dimension(220, 300));
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// Ảnh
		JLabel lblImg = new JLabel();
		lblImg.setPreferredSize(new Dimension(220, 140));
		lblImg.setHorizontalAlignment(SwingConstants.CENTER);
		lblImg.setBackground(new Color(249, 250, 251));
		lblImg.setOpaque(true);
		hienThiAnh(lblImg, anh, 220, 140);

		// Info
		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.setOpaque(false);
		info.setBorder(new EmptyBorder(10, 12, 12, 12));

		JLabel lblDM = new JLabel(danhMuc != null ? danhMuc.toUpperCase() : "");
		lblDM.setFont(new Font("Segoe UI", Font.BOLD, 11));
		lblDM.setForeground(RED);
		lblDM.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblTen = new JLabel("<html><div style='width:170px'>" + ten + "</div></html>");
		lblTen.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblTen.setForeground(new Color(20, 20, 20));
		lblTen.setAlignmentX(LEFT_ALIGNMENT);

		String mt = (moTa == null || moTa.isBlank()) ? "Chưa có mô tả." : moTa;
		if (mt.length() > 60) mt = mt.substring(0, 60) + "…";
		JLabel lblMoTa = new JLabel("<html><div style='width:170px;color:#6b7280'>" + mt + "</div></html>");
		lblMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblMoTa.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblGia = new JLabel(gia);
		lblGia.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblGia.setForeground(GREEN);
		lblGia.setAlignmentX(LEFT_ALIGNMENT);

		info.add(lblDM);
		info.add(Box.createVerticalStrut(4));
		info.add(lblTen);
		info.add(Box.createVerticalStrut(6));
		info.add(lblMoTa);
		info.add(Box.createVerticalGlue());
		info.add(lblGia);

		card.add(lblImg, BorderLayout.NORTH);
		card.add(info,   BorderLayout.CENTER);

		MouseAdapter click = new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) { showDetail(ten, gia, donVi, moTa, anh, danhMuc); }
		};
		lblImg.addMouseListener(click);
		info.addMouseListener(click);

		return card;
	}

	private void hienThiAnh(JLabel lbl, String path, int w, int h) {
		lbl.setIcon(null);
		lbl.setText("🍽");
		lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
		lbl.setForeground(new Color(180, 180, 180));
		if (path == null || path.isBlank()) return;
		try {
			File f = new File(path);
			ImageIcon icon = f.exists()
					? new ImageIcon(path)
					: new ImageIcon(getClass().getResource("/images/monan/" + path));
			Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
			lbl.setText("");
			lbl.setIcon(new ImageIcon(img));
		} catch (Exception ignored) {}
	}

	private void showDetail(String ten, String gia, String donVi,
							String moTa, String anh, String danhMuc) {
		JPanel panel = new JPanel(new BorderLayout(12, 12));
		panel.setPreferredSize(new Dimension(420, 440));
		panel.setBackground(WHITE);

		JLabel lblImg = new JLabel();
		lblImg.setHorizontalAlignment(SwingConstants.CENTER);
		lblImg.setPreferredSize(new Dimension(400, 210));
		hienThiAnh(lblImg, anh, 400, 210);

		String html = "<html><div style='width:360px;font-family:Segoe UI;font-size:13px'>"
				+ "<h2 style='margin:0 0 8px'>" + ten + "</h2>"
				+ "<b>Danh mục:</b> " + safe(danhMuc) + "<br>"
				+ "<b>Giá:</b> <span style='color:#16a34a'>" + gia + "</span><br>"
				+ "<b>Đơn vị:</b> " + safe(donVi) + "<br><br>"
				+ "<b>Mô tả:</b><br>" + (moTa == null || moTa.isBlank() ? "Chưa có mô tả." : moTa)
				+ "</div></html>";

		JLabel lblInfo = new JLabel(html);
		lblInfo.setBorder(new EmptyBorder(4, 4, 4, 4));
		panel.add(lblImg,  BorderLayout.NORTH);
		panel.add(lblInfo, BorderLayout.CENTER);
		JOptionPane.showMessageDialog(this, panel, "Chi tiết món ăn", JOptionPane.PLAIN_MESSAGE);
	}

	private String safe(String s) { return s == null ? "---" : s; }

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new FrmMenu().setVisible(true));
	}
}