package GUI;

import DAO.NhanVienDAO;
import Entity.LuuLog;
import Entity.NhanVien;
import connectDatabase.ConnectDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FrmCaiDat extends JPanel {

	private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
	private NhanVien nhanVienHienTai;

	private static final LocalDateTime thoiDiemDangNhap = LocalDateTime.now();

	private JLabel lbThoiGianLamViec;
	private Timer timerThoiGian;

	public FrmCaiDat() {
		taiDuLieuNhanVien();
		initUI();
	}



	private void taiDuLieuNhanVien() {
		try {
			if (LuuLog.nhanVienDangNhap != null && LuuLog.nhanVienDangNhap.getMaNV() != null) {
				nhanVienHienTai = nhanVienDAO.getNhanVienTheoMa(LuuLog.nhanVienDangNhap.getMaNV());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean laQuanLy() {
		return nhanVienHienTai != null
				&& nhanVienHienTai.getVaiTro() != null
				&& ("Quản lý".equalsIgnoreCase(nhanVienHienTai.getVaiTro().trim()) || "Admin".equalsIgnoreCase(nhanVienHienTai.getVaiTro().trim()));
	}
	private boolean laAdmin() {
		return nhanVienHienTai != null
				&& nhanVienHienTai.getVaiTro() != null
				&& "Admin".equalsIgnoreCase(nhanVienHienTai.getVaiTro().trim());
	}
	private boolean kiemTraKetNoiDB() {
		try {
			ConnectDB.getInstance().connect();
			java.sql.Connection con = ConnectDB.getInstance().getConnection();
			return con != null && !con.isClosed();
		} catch (Exception e) {
			return false;
		}
	}

	private String loadConfig(String key, String defaultValue) {
		java.util.Properties props = new java.util.Properties();
		try (java.io.FileInputStream in = new java.io.FileInputStream("config.properties")) {
			props.load(in);
			String val = props.getProperty(key);
			return (val != null && !val.trim().isEmpty()) ? val : defaultValue;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private void saveConfig(String key, String value) {
		java.util.Properties props = new java.util.Properties();
		java.io.File file = new java.io.File("config.properties");
		if (file.exists()) {
			try (java.io.FileInputStream in = new java.io.FileInputStream(file)) {
				props.load(in);
			} catch (Exception e) { e.printStackTrace(); }
		}
		props.setProperty(key, value);
		try (java.io.FileOutputStream out = new java.io.FileOutputStream(file)) {
			props.store(out, "Application Configuration");
		} catch (Exception e) { e.printStackTrace(); }
	}

	private String tinhThoiGianLamViec() {
		Duration duration = Duration.between(thoiDiemDangNhap, LocalDateTime.now());
		long gio  = duration.toHours();
		long phut = duration.toMinutesPart();
		if (gio == 0) return phut + " phút";
		return gio + " giờ " + phut + " phút";
	}
	private void initUI() {
		setLayout(new BorderLayout());
		setBackground(UIConstants.BG_APP);

		JPanel root = new JPanel(new GridBagLayout());
		root.setBackground(UIConstants.BG_APP);
		root.setBorder(new EmptyBorder(25, 25, 25, 25));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTH;

		// Left column: Account Card
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.40;
		gbc.insets = new Insets(0, 0, 0, 20);

		JPanel accountCardWrapper = new JPanel(new BorderLayout());
		accountCardWrapper.setOpaque(false);
		accountCardWrapper.add(createAccountCard(), BorderLayout.NORTH);
		root.add(accountCardWrapper, gbc);

		// Right column: Restaurant & System Cards
		if (laQuanLy()) {
			gbc.gridx = 1;
			gbc.weightx = 0.60;
			gbc.insets = new Insets(0, 20, 0, 0);

			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
			rightPanel.setOpaque(false);

			rightPanel.add(createRestaurantCard());
			rightPanel.add(Box.createVerticalStrut(30));
			rightPanel.add(createThongTinHeThongCard());

			JPanel rightWrapper = new JPanel(new BorderLayout());
			rightWrapper.setOpaque(false);
			rightWrapper.add(rightPanel, BorderLayout.NORTH);

			root.add(rightWrapper, gbc);
		}

		JScrollPane scrollPane = new JScrollPane(root);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(UIConstants.BG_APP);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		add(scrollPane, BorderLayout.CENTER);
	}

	private JPanel createAccountCard() {
		RoundedPanel card = createCard();
		card.add(createCardHeader("Thông tin tài khoản"));
		card.add(Box.createVerticalStrut(30));

		String maNV       = "...";
		String hoTen      = "Chưa cập nhật";
		String sdt        = "Chưa cập nhật";
		String vaiTro     = "Chưa cập nhật";
		String tenDangNhap = "Chưa cập nhật";

		if (nhanVienHienTai != null) {
			if (nhanVienHienTai.getMaNV() != null)        maNV        = nhanVienHienTai.getMaNV();
			if (nhanVienHienTai.getHoTenNV() != null)     hoTen       = nhanVienHienTai.getHoTenNV();
			if (nhanVienHienTai.getSoDienThoai() != null) sdt         = nhanVienHienTai.getSoDienThoai();
			if (nhanVienHienTai.getVaiTro() != null)      vaiTro      = nhanVienHienTai.getVaiTro();
			if (nhanVienHienTai.getTenDangNhap() != null) tenDangNhap = nhanVienHienTai.getTenDangNhap();
		}

		String firstLetter = hoTen.isEmpty() ? "U" : hoTen.substring(0, 1).toUpperCase();
		JPanel pnlAvatar = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(230, 240, 255)); // Light blue bg
				g2.fillOval(0, 0, getWidth(), getHeight());

				g2.setColor(UIConstants.PRIMARY); // Text color
				g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
				FontMetrics fm = g2.getFontMetrics();
				int x = (getWidth() - fm.stringWidth(firstLetter)) / 2;
				int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
				g2.drawString(firstLetter, x, y);
				g2.dispose();
			}
		};
		pnlAvatar.setPreferredSize(new Dimension(80, 80));
		pnlAvatar.setMaximumSize(new Dimension(80, 80));
		pnlAvatar.setOpaque(false);

		JPanel pnlProfile = new JPanel();
		pnlProfile.setLayout(new BoxLayout(pnlProfile, BoxLayout.X_AXIS));
		pnlProfile.setOpaque(false);
		pnlProfile.add(pnlAvatar);
		pnlProfile.add(Box.createHorizontalStrut(20));

		JPanel pnlProfileInfo = new JPanel();
		pnlProfileInfo.setLayout(new BoxLayout(pnlProfileInfo, BoxLayout.Y_AXIS));
		pnlProfileInfo.setOpaque(false);

		JLabel lblName = new JLabel(hoTen);
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblName.setForeground(UIConstants.TEXT_DARK);
		lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel lblRole = new JLabel(vaiTro);
		lblRole.setFont(UIConstants.FONT_PLAIN_14);
		lblRole.setForeground(UIConstants.PRIMARY);
		lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

		pnlProfileInfo.add(Box.createVerticalGlue());
		pnlProfileInfo.add(lblName);
		pnlProfileInfo.add(Box.createVerticalStrut(5));
		pnlProfileInfo.add(lblRole);
		pnlProfileInfo.add(Box.createVerticalGlue());

		pnlProfile.add(pnlProfileInfo);
		
		JPanel pnlProfileWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		pnlProfileWrapper.setOpaque(false);
		pnlProfileWrapper.add(pnlProfile);

		card.add(pnlProfileWrapper);
		card.add(Box.createVerticalStrut(40));

		JPanel pnlInfoList = new JPanel();
		pnlInfoList.setLayout(new BoxLayout(pnlInfoList, BoxLayout.Y_AXIS));
		pnlInfoList.setOpaque(false);

		pnlInfoList.add(createStaticItem("Mã nhân viên", maNV, null));
		pnlInfoList.add(createStaticItem("Họ và tên", hoTen, null));
		pnlInfoList.add(createStaticItem("Tên đăng nhập", tenDangNhap, null));
		pnlInfoList.add(createStaticItem("Số điện thoại", sdt, null));

		card.add(pnlInfoList);
		card.add(Box.createVerticalStrut(30));

		ModernButton btnDoiMatKhau = new ModernButton("Đổi mật khẩu", ModernButton.Style.OUTLINE);
		btnDoiMatKhau.setPreferredSize(new Dimension(160, 36));
		btnDoiMatKhau.addActionListener(e -> hienThiDialogDoiMatKhau());

		JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		btnWrap.setOpaque(false);
		btnWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		btnWrap.add(btnDoiMatKhau);
		card.add(btnWrap);

		return card;
	}

	private JPanel createCustomItem(String title, JComponent valueComponent, JComponent actionComponent) {
		JPanel row = new JPanel(new BorderLayout());
		row.setOpaque(false);
		row.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER),
				new EmptyBorder(15, 5, 15, 5)
		));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

		JPanel pnlLeft = new JPanel();
		pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
		pnlLeft.setOpaque(false);
		
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(UIConstants.FONT_PLAIN_12);
		lblTitle.setForeground(UIConstants.TEXT_GRAY);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

		valueComponent.setAlignmentX(Component.LEFT_ALIGNMENT);

		pnlLeft.add(lblTitle);
		pnlLeft.add(Box.createVerticalStrut(6));
		pnlLeft.add(valueComponent);

		row.add(pnlLeft, BorderLayout.CENTER);

		if (actionComponent != null) {
			JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
			pnlRight.setOpaque(false);
			pnlRight.add(actionComponent);
			
			JPanel pnlRightWrapper = new JPanel(new GridBagLayout());
			pnlRightWrapper.setOpaque(false);
			pnlRightWrapper.add(pnlRight);
			row.add(pnlRightWrapper, BorderLayout.EAST);
		}

		return row;
	}

	private JPanel createStaticItem(String title, String value, JComponent actionComponent) {
		JLabel lblValue = new JLabel(value);
		lblValue.setFont(UIConstants.FONT_PLAIN_15);
		lblValue.setForeground(UIConstants.TEXT_DARK);
		return createCustomItem(title, lblValue, actionComponent);
	}

	private JPanel createEditableItem(String labelTitle, String configKey, String defaultValue, JComponent extraAction) {
		String currentValue = loadConfig(configKey, defaultValue);
		JLabel lblValue = new JLabel(currentValue);
		lblValue.setFont(UIConstants.FONT_PLAIN_15);
		lblValue.setForeground(UIConstants.TEXT_DARK);

		JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		pnlAction.setOpaque(false);

		if (laAdmin()) {
			JButton btnEdit = new JButton("✏️");
			btnEdit.setContentAreaFilled(false);
			btnEdit.setBorderPainted(false);
			btnEdit.setFocusPainted(false);
			btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
			btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
			btnEdit.addActionListener(e -> {
				String newValue = JOptionPane.showInputDialog(this, "Cập nhật " + labelTitle, lblValue.getText());
				if (newValue != null && !newValue.trim().isEmpty() && !newValue.equals(lblValue.getText())) {
					int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận lưu thay đổi?", "Xác nhận", JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						saveConfig(configKey, newValue.trim());
						lblValue.setText(newValue.trim());
						JOptionPane.showMessageDialog(this, "Lưu thành công!");
					}
				}
			});
			pnlAction.add(btnEdit);
		}

		if (extraAction != null) {
			if (laAdmin()) pnlAction.add(Box.createHorizontalStrut(10));
			pnlAction.add(extraAction);
		}

		return createCustomItem(labelTitle, lblValue, pnlAction.getComponentCount() > 0 ? pnlAction : null);
	}

	private JPanel createRestaurantCard() {
		RoundedPanel card = createCard();
		card.add(createCardHeader("Thông tin nhà hàng"));
		card.add(Box.createVerticalStrut(15));

		JPanel pnlList = new JPanel();
		pnlList.setLayout(new BoxLayout(pnlList, BoxLayout.Y_AXIS));
		pnlList.setOpaque(false);

		pnlList.add(createEditableItem("Tên nhà hàng", "res.name", "Nhà hàng Ngói Đỏ", null));
		pnlList.add(createEditableItem("Số điện thoại", "res.phone", "0123 456 789", null));
		pnlList.add(createEditableItem("Email liên hệ", "res.email", "ngoido@gmail.com", null));

		ModernButton btnMap = new ModernButton("Xem bản đồ", ModernButton.Style.OUTLINE);
		btnMap.setPreferredSize(new Dimension(130, 32));
		btnMap.addActionListener(e -> {
			try {
				String diaChi = loadConfig("res.address", "123 Đường Trung Tâm, TP. Hồ Chí Minh");
				String url = "https://www.google.com/maps/search/?api=1&query="
						+ java.net.URLEncoder.encode(diaChi, "UTF-8");
				java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
			} catch (Exception ex) { ex.printStackTrace(); }
		});
		pnlList.add(createEditableItem("Địa chỉ", "res.address", "123 Đường Trung Tâm, TP. Hồ Chí Minh", btnMap));

		ModernButton btnGoWeb = new ModernButton("Tới trang web", ModernButton.Style.OUTLINE);
		btnGoWeb.setPreferredSize(new Dimension(130, 32));
		btnGoWeb.addActionListener(e -> {
			try {
				java.io.File htmlFile = new java.io.File("index.html"); 
				if (htmlFile.exists()) {
					java.awt.Desktop.getDesktop().open(htmlFile);
				} else {
					JOptionPane.showMessageDialog(this, 
						"Không tìm thấy file trang web (index.html) trong thư mục dự án!", 
						"Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		pnlList.add(createEditableItem("Website", "res.website", "www.ngoido.vn", btnGoWeb));
		
		card.add(pnlList);
		return card;
	}

	private JPanel createThongTinHeThongCard() {
		RoundedPanel card = createCard();
		card.add(createCardHeader("Thông tin hệ thống"));
		card.add(Box.createVerticalStrut(15));

		JPanel pnlList = new JPanel();
		pnlList.setLayout(new BoxLayout(pnlList, BoxLayout.Y_AXIS));
		pnlList.setOpaque(false);

		pnlList.add(createStaticItem("Tên phần mềm", "Hệ thống Quản lý Nhà hàng Ngói Đỏ", null));
		pnlList.add(createStaticItem("Phiên bản", "1.0.0", null));
		pnlList.add(createStaticItem("Nhà phát triển", "Nhóm PTUD N8 — ĐH Công Nghiệp TPHCM", null));
		pnlList.add(createStaticItem("Năm phát hành", "2026", null));
		pnlList.add(createStaticItem("Máy chủ", "localhost:1433", null));
		pnlList.add(createStaticItem("Tên cơ sở dữ liệu", "QuanLyNhaHang", null));

		final boolean[] isConnected = { kiemTraKetNoiDB() };

		JPanel pnlBadge = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (isConnected[0]) {
					g2.setColor(new Color(220, 252, 231));
					g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
					g2.setColor(new Color(34, 197, 94));
					g2.fillOval(10, (getHeight() - 8) / 2, 8, 8);
					g2.setFont(UIConstants.FONT_BOLD_13);
					g2.drawString("Đang kết nối", 26, (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent());
				} else {
					g2.setColor(new Color(254, 242, 242));
					g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
					g2.setColor(new Color(239, 68, 68));
					g2.fillOval(10, (getHeight() - 8) / 2, 8, 8);
					g2.setFont(UIConstants.FONT_BOLD_13);
					g2.drawString("Mất kết nối", 26, (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent());
				}
				g2.dispose();
			}
		};
		pnlBadge.setPreferredSize(new Dimension(120, 32));
		pnlBadge.setMaximumSize(new Dimension(120, 32));
		pnlBadge.setOpaque(false);

		ModernButton btnKiemTra = new ModernButton("Kiểm tra lại", ModernButton.Style.OUTLINE);
		btnKiemTra.setPreferredSize(new Dimension(130, 32));
		btnKiemTra.addActionListener(e -> {
			isConnected[0] = kiemTraKetNoiDB();
			pnlBadge.repaint();
		});
		
		pnlList.add(createCustomItem("Trạng thái kết nối", pnlBadge, btnKiemTra));

		String thoiDiemStr = thoiDiemDangNhap.format(DateTimeFormatter.ofPattern("HH:mm  dd/MM/yyyy"));
		pnlList.add(createStaticItem("Đăng nhập lúc", thoiDiemStr, null));

		lbThoiGianLamViec = new JLabel(tinhThoiGianLamViec());
		lbThoiGianLamViec.setFont(UIConstants.FONT_PLAIN_15);
		lbThoiGianLamViec.setForeground(UIConstants.TEXT_DARK);
		
		timerThoiGian = new Timer(60_000, e -> lbThoiGianLamViec.setText(tinhThoiGianLamViec()));
		timerThoiGian.start();
		
		pnlList.add(createCustomItem("Thời gian làm việc", lbThoiGianLamViec, null));

		card.add(pnlList);
		return card;
	}

	private RoundedPanel createCard() {
		RoundedPanel card = new RoundedPanel(16, true, Color.WHITE);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(new EmptyBorder(28, 28, 28, 28));
		return card;
	}

	private JPanel createCardHeader(String title) {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnl.setOpaque(false);
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(UIConstants.FONT_BOLD_18);
		lblTitle.setForeground(UIConstants.TEXT_DARK);
		pnl.add(lblTitle);
		pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		return pnl;
	}

	private JPanel createDivider() {
		JPanel line = new JPanel();
		line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		line.setPreferredSize(new Dimension(0, 1));
		line.setBackground(UIConstants.BORDER);
		return line;
	}



	private void hienThiDialogDoiMatKhau() {
		if (nhanVienHienTai == null) {
			JOptionPane.showMessageDialog(this, "Không lấy được thông tin tài khoản hiện tại!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		JPasswordField txtOld     = new JPasswordField();
		JPasswordField txtNew     = new JPasswordField();
		JPasswordField txtConfirm = new JPasswordField();
		panel.add(new JLabel("Mật khẩu cũ:"));     panel.add(txtOld);
		panel.add(new JLabel("Mật khẩu mới:"));    panel.add(txtNew);
		panel.add(new JLabel("Xác nhận mật khẩu mới:")); panel.add(txtConfirm);

		int result = JOptionPane.showConfirmDialog(this, panel, "Đổi mật khẩu",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) return;

		String oldPass     = new String(txtOld.getPassword()).trim();
		String newPass     = new String(txtNew.getPassword()).trim();
		String confirmPass = new String(txtConfirm.getPassword()).trim();

		if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!newPass.equals(confirmPass)) {
			JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (newPass.length() < 4) {
			JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 4 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (nhanVienDAO.doiMatKhau(nhanVienHienTai.getMaNV(), oldPass, newPass)) {
			JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
		} else {
			JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng hoặc cập nhật thất bại!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
