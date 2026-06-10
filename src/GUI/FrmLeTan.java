package GUI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Entity.PhieuDatBan;
import Entity.DlgNhapThongTinKhach;
import Entity.LuuLog;
import Entity.BanAn;
import Model.NhanVienModel;

public class FrmLeTan extends JFrame {

	private static final Color RED_MAIN = new Color(220, 38, 38);
	private static final Color BG_MAIN = new Color(248, 248, 248);
	private static final Color BORDER_CLR = new Color(230, 230, 230);
	private static final Color TEXT_DARK = new Color(40, 40, 40);
	private static final Color TEXT_GRAY = new Color(120, 120, 120);

	// Màu cho bàn TRỐNG (Xanh lá)
	private static final Color BG_TRONG = new Color(220, 252, 231);
	private static final Color BORDER_TRONG = new Color(34, 197, 94);

	// Màu cho bàn CÓ KHÁCH (Đỏ)
	private static final Color BG_KHACH = new Color(254, 226, 226);
	private static final Color BORDER_KHACH = new Color(239, 68, 68);

	// Màu cho bàn ĐÃ ĐẶT (Vàng)
	private static final Color BG_DAT = new Color(254, 249, 195);
	private static final Color BORDER_DAT = new Color(234, 179, 8);

	// Màu cho BÀN ĐANG GHÉP (Cam)
	private static final Color BG_GHEP = new Color(255, 237, 213);
	private static final Color BORDER_GHEP = new Color(249, 115, 22);

	// THÊM: Màu cho bàn ĐÃ ĐẶT nhưng CÒN AN TOÀN ĐỂ MỞ (Xanh Dương)
	private static final Color BG_SAFE = new Color(224, 242, 254);
	private static final Color BORDER_SAFE = new Color(59, 130, 246);

	private JPanel gridMap;
	private JLabel lblClock;
	private String currentTab = "Tầng 1";
	private JPanel tabsContainer;
	private JLabel lblMapTitle;
	private JComboBox<String> cboLocSucChua;

	private JPanel pnlDanhSachDatCho;

	private final DAO.BanAnDAO banAnDAO = new DAO.BanAnDAO();
	private final DAO.HoaDonDAO hoaDonDAO = new DAO.HoaDonDAO();
	private final DAO.PhieuDatBanDAO phieuDAO = new DAO.PhieuDatBanDAO();
	private JTextField txtTimKiemDatCho;
	private JComboBox<String> cboLocThoiGian;
	private DefaultTableModel modelDatCho;

	public FrmLeTan() {
		initUI();
		startClock();
		// Khởi động polling thông báo (3 giây/lần)
		Service.NotificationService.getInstance().startPolling(3000);
	}

	private void initUI() {
		setTitle("Nhà Hàng Ngói Đỏ - Lễ Tân");
		setSize(1440, 860);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(BG_MAIN);
		root.add(createTopBar(), BorderLayout.NORTH);

		JPanel centerWrap = new JPanel(new BorderLayout());
		centerWrap.setOpaque(false);
		centerWrap.add(createTabs(), BorderLayout.NORTH);
		centerWrap.add(createMapArea(), BorderLayout.CENTER);

		root.add(centerWrap, BorderLayout.CENTER);
		root.add(createRightSidebar(), BorderLayout.EAST);
		setContentPane(root);

		loadDanhSachDatCho();
	}

	private JPanel createTopBar() {
		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground(Color.WHITE);
		bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
				new EmptyBorder(10, 20, 10, 20)));

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		left.setOpaque(false);
		JLabel lblLogo = new JLabel("🏮");
		lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
		lblLogo.setForeground(RED_MAIN);
		JPanel textWrap = new JPanel(new GridLayout(2, 1));
		textWrap.setOpaque(false);
		JLabel lblName = new JLabel("Nhà Hàng Ngói Đỏ - Lễ Tân");
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
		JLabel lblSub = new JLabel("Quản lý đặt chỗ & Check-in");
		lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblSub.setForeground(TEXT_GRAY);
		textWrap.add(lblName);
		textWrap.add(lblSub);
		left.add(lblLogo);
		left.add(textWrap);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
		right.setOpaque(false);
		lblClock = new JLabel("00:00:00");
		lblClock.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		String tenNV = (Entity.LuuLog.nhanVienDangNhap != null)
				? Entity.LuuLog.nhanVienDangNhap.getTenNV()
				: "Lễ tân";
		JLabel lblUser = new JLabel("" + tenNV);
		lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		JButton btnCaiDat = new ModernButton("Cài đặt", ModernButton.Style.OUTLINE);
		btnCaiDat.setPreferredSize(new Dimension(100, 34));
		btnCaiDat.addActionListener(e -> {
			JDialog dialog = new JDialog(this, "Cài đặt cá nhân", true);
			dialog.setSize(900, 700);
			dialog.setLocationRelativeTo(this);
			dialog.setContentPane(new GUI.FrmCaiDat());
			dialog.setVisible(true);
		});
		JButton btnLogout = new ModernButton("Đăng xuất", ModernButton.Style.GHOST);
		btnLogout.setPreferredSize(new Dimension(110, 34));
		btnLogout.addActionListener(e -> {
			int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất không?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (c == JOptionPane.YES_OPTION) {
				dispose();
				new FrmDangNhap().setVisible(true);
			}
		});

		// Chuông thông báo
		NotificationBellPanel bellPanel = new NotificationBellPanel();

		right.add(lblClock);
		right.add(lblUser);
		right.add(bellPanel);
		right.add(btnCaiDat);
		right.add(btnLogout);

		bar.add(left, BorderLayout.WEST);
		bar.add(right, BorderLayout.EAST);
		return bar;
	}

	private void startClock() {
		new Timer(1000, e -> lblClock.setText("" + new SimpleDateFormat("HH:mm:ss").format(new Date()))).start();
	}

	private JPanel createTabs() {
		tabsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
		tabsContainer.setBackground(Color.WHITE);
		tabsContainer.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR), new EmptyBorder(10, 20, 0, 20)));

		String[] tabNames = { "Tầng 1", "Tầng 2", "Phòng VIP" };
		for (int i = 0; i < tabNames.length; i++) {
			JLabel lbl = new JLabel(tabNames[i]);
			lbl.setFont(new Font("Segoe UI", i == 0 ? Font.BOLD : Font.PLAIN, 14));
			lbl.setForeground(i == 0 ? RED_MAIN : TEXT_DARK);
			lbl.setBorder(
					i == 0 ? BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, RED_MAIN),
							new EmptyBorder(0, 0, 7, 0)) : new EmptyBorder(0, 0, 10, 0));
			lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					currentTab = lbl.getText();
					updateTabUI();
					refreshSoDoBan();
				}
			});
			tabsContainer.add(lbl);
		}
		return tabsContainer;
	}

	private void updateTabUI() {
		for (Component c : tabsContainer.getComponents()) {
			if (c instanceof JLabel lbl) {
				boolean sel = lbl.getText().equals(currentTab);
				lbl.setFont(new Font("Segoe UI", sel ? Font.BOLD : Font.PLAIN, 14));
				lbl.setForeground(sel ? RED_MAIN : TEXT_DARK);
				lbl.setBorder(
						sel ? BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, RED_MAIN),
								new EmptyBorder(0, 0, 7, 0)) : new EmptyBorder(0, 0, 10, 0));
			}
		}
	}

	private JPanel createMapArea() {
		JPanel mapWrap = new JPanel(new BorderLayout());
		mapWrap.setOpaque(false);
		mapWrap.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 20, 0));

		JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 5));
		pnlTitle.setOpaque(false);
		lblMapTitle = new JLabel("Sơ đồ bàn  " + currentTab);
		lblMapTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
		JLabel sub = new JLabel("Click vào bàn trống hoặc đã đặt để bắt đầu phục vụ");
		sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		sub.setForeground(TEXT_GRAY);
		pnlTitle.add(lblMapTitle);
		pnlTitle.add(sub);

		JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		pnlFilter.setOpaque(false);
		JLabel lblFilter = new JLabel("Tìm bàn trống:");
		lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblFilter.setForeground(TEXT_DARK);

		String[] filterOptions = { "Tất cả", ">= 2 người", ">= 4 người", ">= 6 người", ">= 8 người", ">= 10 người" };
		cboLocSucChua = new JComboBox<>(filterOptions);
		cboLocSucChua.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cboLocSucChua.setPreferredSize(new Dimension(120, 32));
		cboLocSucChua.setBackground(Color.WHITE);
		cboLocSucChua.setCursor(new Cursor(Cursor.HAND_CURSOR));

		cboLocSucChua.addActionListener(e -> refreshSoDoBan());

		pnlFilter.add(lblFilter);
		pnlFilter.add(cboLocSucChua);

		header.add(pnlTitle, BorderLayout.WEST);
		header.add(pnlFilter, BorderLayout.EAST);

		gridMap = new JPanel(new GridLayout(0, 4, 15, 15));
		gridMap.setOpaque(false);
		JPanel wrapGrid = new JPanel(new BorderLayout());
		wrapGrid.setOpaque(false);
		wrapGrid.add(gridMap, BorderLayout.NORTH);

		refreshSoDoBan();

		JScrollPane scroll = new JScrollPane(wrapGrid);
		scroll.setBorder(null);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		mapWrap.add(header, BorderLayout.NORTH);
		mapWrap.add(scroll, BorderLayout.CENTER);
		return mapWrap;
	}

	private JPanel createTableCard(String maBan, String tenBan, int capacity, String displayStatus, String dbStatus) {
		Color bg, border;

		if (displayStatus.equalsIgnoreCase("Trống")) {
			bg = BG_TRONG;
			border = BORDER_TRONG;
		} else if (displayStatus.equalsIgnoreCase("Có khách")) {
			bg = BG_KHACH;
			border = BORDER_KHACH;
		} else if (displayStatus.toLowerCase().startsWith("đang ghép")) {
			bg = BG_GHEP;
			border = BORDER_GHEP;
		} else if (displayStatus.startsWith("Trống đến")) {
			bg = BG_SAFE;
			border = BORDER_SAFE;
		} else {
			bg = BG_DAT;
			border = BORDER_DAT;
		}

		// Hover state holder
		boolean[] hovering = { false };

		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// Shadow
				g2.setColor(new Color(0, 0, 0, 8));
				g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 14, 14);
				g2.setColor(new Color(0, 0, 0, 5));
				g2.fillRoundRect(1, 2, getWidth() - 2, getHeight() - 2, 14, 14);
				// Card fill
				g2.setColor(bg);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
				// Border — thicker on hover
				g2.setColor(border);
				g2.setStroke(new BasicStroke(hovering[0] ? 2.5f : 1.5f));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
				g2.dispose();
			}
		};
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setPreferredSize(new Dimension(220, 145));
		card.setOpaque(false);
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));

		JLabel icon = new JLabel("🪑", SwingConstants.CENTER);
		icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
		icon.setForeground(border);
		icon.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel lblName = new JLabel(tenBan, SwingConstants.CENTER);
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel lblCap = new JLabel("Sức chứa: " + capacity + " người", SwingConstants.CENTER);
		lblCap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblCap.setForeground(TEXT_GRAY);
		lblCap.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Pill badge for status
		Color badgeFg = border;
		if (displayStatus.startsWith("Trống đến"))
			badgeFg = BORDER_SAFE;
		final Color badgeFgFinal = badgeFg;
		JLabel lblStatus = new JLabel(displayStatus, SwingConstants.CENTER) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(badgeFgFinal.getRed(), badgeFgFinal.getGreen(), badgeFgFinal.getBlue(), 30));
				g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
				g2.dispose();
				super.paintComponent(g);
			}
		};
		lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
		lblStatus.setForeground(badgeFg);
		lblStatus.setOpaque(false);
		lblStatus.setBorder(new EmptyBorder(3, 10, 3, 10));
		lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

		card.add(Box.createVerticalStrut(14));
		card.add(icon);
		card.add(Box.createVerticalStrut(6));
		card.add(lblName);
		card.add(Box.createVerticalStrut(4));
		card.add(lblCap);
		card.add(Box.createVerticalStrut(8));
		card.add(lblStatus);
		card.add(Box.createVerticalStrut(10));

		card.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hovering[0] = true;
				card.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hovering[0] = false;
				card.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (dbStatus.equalsIgnoreCase("Trống")) {
					xuLyMoBan(maBan, tenBan, capacity);
				} else if (dbStatus.equalsIgnoreCase("Có khách")) {
					hienThiThongTinBan(maBan, tenBan);
				} else if (dbStatus.equalsIgnoreCase("Đã đặt")) {
					xuLyClickBanDaDat(maBan, tenBan, capacity);
				} else if (dbStatus.toLowerCase().startsWith("đang ghép")) {
					String tenBanChinh = banAnDAO.getTenBanGhepChung(maBan);
					String msg = "Bàn này đang được ghép chung hóa đơn với " + tenBanChinh + ".\n"
							+ "Khách đã dời sang bàn chính, bạn có muốn giải phóng bàn này thành bàn TRỐNG để đón khách mới không?";
					int choice = JOptionPane.showConfirmDialog(FrmLeTan.this, msg, "Giải phóng bàn ghép",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (choice == JOptionPane.YES_OPTION) {
						banAnDAO.capNhatTrangThai(maBan, "Trống");
						refreshSoDoBan();
						JOptionPane.showMessageDialog(FrmLeTan.this, "Đã dọn dẹp " + tenBan + " về trạng thái Trống!");
					}
				}
			}
		});
		return card;
	}

	private void xuLyClickBanDaDat(String maBan, String tenBan, int capacity) {
		List<PhieuDatBan> dsPhieu = phieuDAO.getDanhSachDatChoChuaCheckIn();
		PhieuDatBan phieuCuaBanNay = null;
		long minDiff = Long.MAX_VALUE;
		long hienTai = System.currentTimeMillis();
		for (PhieuDatBan p : dsPhieu) {
			if (p.getThoiGianDen() == null) continue;

			// Check bàn đại diện
			boolean laBanCuaPhieu = p.getMaBan() != null && p.getMaBan().equals(maBan);

			// Check ChiTietDatBan nếu chưa khớp
			if (!laBanCuaPhieu) {
				try {
					java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
					java.sql.PreparedStatement ps = con.prepareStatement(
							"SELECT 1 FROM ChiTietDatBan WHERE maPhieu = ? AND maBan = ?");
					ps.setString(1, p.getMaPhieu());
					ps.setString(2, maBan);
					java.sql.ResultSet rs = ps.executeQuery();
					if (rs.next()) laBanCuaPhieu = true;
					rs.close();
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (laBanCuaPhieu) {
				long diff = p.getThoiGianDen().getTime() - hienTai;
				if (diff > 0 && diff < minDiff) {
					minDiff = diff;
					phieuCuaBanNay = p;
				}
			}
		}
		if (phieuCuaBanNay != null && phieuCuaBanNay.getThoiGianDen() != null) {
			java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat("yyyy-MM-dd");
			String today = sdfDate.format(new java.util.Date());
			String phieuDate = sdfDate.format(phieuCuaBanNay.getThoiGianDen());

			if (today.equals(phieuDate)) {
				long thoiGianConLai_Phut = minDiff / (60 * 1000);

				if (thoiGianConLai_Phut >= 150) {
					java.text.SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm");
					String thoiGianDatStr = sdfTime.format(phieuCuaBanNay.getThoiGianDen());

					long tieng = thoiGianConLai_Phut / 60;
					long phut = thoiGianConLai_Phut % 60;

					String msg = "Bàn " + tenBan + " thuộc nhóm bàn được khách đặt trước lúc " + thoiGianDatStr + ".\n"
							+ "Hiện tại còn trống " + tieng + " tiếng " + phut + " phút nữa khách mới đến.\n"
							+ "ĐỦ THỜI GIAN AN TOÀN (>= 2.5 tiếng) để đón khách vãng lai.\n\n"
							+ "Bạn có muốn mở bàn này cho khách vãng lai ngồi tạm không?";

					int choice = JOptionPane.showConfirmDialog(this, msg, "Mở bàn an toàn", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (choice == JOptionPane.YES_OPTION) {
						xuLyMoBan(maBan, tenBan, capacity);
					}
				} else {
					java.text.SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm");
					String thoiGianDatStr = sdfTime.format(phieuCuaBanNay.getThoiGianDen());
					JOptionPane.showMessageDialog(this,
							"Bàn " + tenBan + " có khách đặt lúc " + thoiGianDatStr + ".\n"
									+ "Chỉ còn " + thoiGianConLai_Phut + " phút nữa khách sẽ đến.\n"
									+ "KHÔNG ĐỦ thời gian an toàn để nhận thêm khách vãng lai!",
							"Từ chối mở bàn", JOptionPane.WARNING_MESSAGE);
				}
			} else {
				String msg = "Bàn " + tenBan
						+ " được đặt cho ngày khác, không phải hôm nay.\nHôm nay vẫn có thể sử dụng bình thường.\nBạn có muốn mở bàn không?";
				int choice = JOptionPane.showConfirmDialog(this, msg, "Mở bàn", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					xuLyMoBan(maBan, tenBan, capacity);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Hệ thống đang tải dữ liệu hoặc bàn này không có phiếu hợp lệ!");
		}
	}

	private void xuLyMoBan(String maBan, String tenBan, int capacity) {
		DlgNhapThongTinKhach dlg = new DlgNhapThongTinKhach(FrmLeTan.this);
		dlg.setVisible(true);

		if (dlg.isConfirmed()) {
			String ten = dlg.getTen();
			String sdt = dlg.getSDT();
			int soNguoi = dlg.getSoNguoi();

			if (soNguoi > capacity) {
				String msgCanhBao = "Bàn " + tenBan + " chỉ có sức chứa " + capacity + " người.\n" + "Số lượng "
						+ soNguoi + " khách đã vượt quá mức quy định.\n\n"
						+ "Bạn có muốn tiếp tục xếp khách vào bàn này (kê thêm ghế) không?";

				int choice = JOptionPane.showConfirmDialog(this, msgCanhBao, "Cảnh báo vượt sức chứa",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

				if (choice != JOptionPane.YES_OPTION) {
					return; // Lễ tân chọn NO -> Hủy mở bàn
				}
			}

			String maHD = "HD" + System.currentTimeMillis();
			String maNV = "NV005";

			if (Entity.LuuLog.nhanVienDangNhap != null) {
				maNV = Entity.LuuLog.nhanVienDangNhap.getMaNV();
			}

			boolean result = hoaDonDAO.taoHoaDonMoi(maHD, maNV, maBan, ten, sdt, soNguoi, null);

			if (result) {
				banAnDAO.capNhatTrangThai(maBan, "Có khách");
				refreshSoDoBan();
				JOptionPane.showMessageDialog(this, "Mở bàn " + maBan + " thành công cho khách " + ten);
			} else {
				JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private JPanel createRightSidebar() {
		JPanel sidebar = new JPanel(new BorderLayout());
		sidebar.setBackground(Color.WHITE);
		sidebar.setPreferredSize(new Dimension(340, 0));
		sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_CLR));

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		top.setBackground(Color.WHITE);
		top.setBorder(new EmptyBorder(20, 20, 10, 20));

		JLabel title = new JLabel("Quản lý Đặt chỗ");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		title.setAlignmentX(Component.LEFT_ALIGNMENT);

		txtTimKiemDatCho = new JTextField();
		txtTimKiemDatCho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		txtTimKiemDatCho.setPreferredSize(new Dimension(0, 35));
		txtTimKiemDatCho.setToolTipText("Gõ SĐT hoặc Tên khách");
		txtTimKiemDatCho
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
						new EmptyBorder(0, 10, 0, 10)));

		txtTimKiemDatCho.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				loadDanhSachDatCho();
			}
		});

		JPanel pnlFilter = new JPanel(new BorderLayout(10, 0));
		pnlFilter.setOpaque(false);
		pnlFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

		JLabel subTitle = new JLabel("Hiển thị: ");
		subTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

		cboLocThoiGian = new JComboBox<>(new String[] { "Hôm nay", "Ngày mai", "Tất cả" });
		cboLocThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cboLocThoiGian.setBackground(Color.WHITE);
		cboLocThoiGian.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cboLocThoiGian.addActionListener(e -> loadDanhSachDatCho());

		pnlFilter.add(subTitle, BorderLayout.WEST);
		pnlFilter.add(cboLocThoiGian, BorderLayout.CENTER);

		top.add(title);
		top.add(Box.createVerticalStrut(15));
		top.add(txtTimKiemDatCho);
		top.add(Box.createVerticalStrut(15));
		top.add(pnlFilter);

		pnlDanhSachDatCho = new JPanel();
		pnlDanhSachDatCho.setLayout(new BoxLayout(pnlDanhSachDatCho, BoxLayout.Y_AXIS));
		pnlDanhSachDatCho.setBackground(Color.WHITE);
		pnlDanhSachDatCho.setBorder(new EmptyBorder(10, 20, 0, 20));

		JScrollPane scroll = new JScrollPane(pnlDanhSachDatCho);
		scroll.setBorder(null);

		JPanel actions = new JPanel(new GridLayout(6, 1, 0, 10));
		actions.setBackground(Color.WHITE);
		actions.setBorder(new EmptyBorder(20, 20, 20, 20));
		JLabel lblQuick = new JLabel("Thao tác nhanh");
		lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 14));
		actions.add(lblQuick);

		JButton btnTaoDatCho = new ModernButton("Tạo đặt chỗ mới", ModernButton.Style.PRIMARY);
		btnTaoDatCho.setPreferredSize(new Dimension(0, 38));
		btnTaoDatCho.addActionListener(e -> new FrmTaoDatCho(this).setVisible(true));
		actions.add(btnTaoDatCho);

		JButton btnMoNhomBan = new ModernButton("Mở nhóm bàn", ModernButton.Style.OUTLINE);
		btnMoNhomBan.setPreferredSize(new Dimension(0, 38));
		btnMoNhomBan.addActionListener(e -> new DlgMoNhomBan(this).setVisible(true));
		actions.add(btnMoNhomBan);

		JButton btnChuyenBan = new ModernButton("Chuyển bàn", ModernButton.Style.OUTLINE);
		btnChuyenBan.setPreferredSize(new Dimension(0, 38));
		btnChuyenBan.addActionListener(e -> new FrmChuyenBan(this).setVisible(true));
		actions.add(btnChuyenBan);

		JButton btnGopBan = new ModernButton("Gộp bàn", ModernButton.Style.OUTLINE);
		btnGopBan.setPreferredSize(new Dimension(0, 38));
		btnGopBan.addActionListener(e -> new FrmGopBan(this).setVisible(true));
		actions.add(btnGopBan);

		// Nút Khóa Món (chỉ Lễ Tân và Quản Lý)
		JButton btnKhoaMon = new ModernButton("Khóa Món", ModernButton.Style.OUTLINE);
		btnKhoaMon.setPreferredSize(new Dimension(0, 38));
		btnKhoaMon.setForeground(new Color(220, 38, 38));
		btnKhoaMon.addActionListener(e -> new DlgQuanLyKhoaMon(this).setVisible(true));
		actions.add(btnKhoaMon);

		sidebar.add(top, BorderLayout.NORTH);
		sidebar.add(scroll, BorderLayout.CENTER);
		sidebar.add(actions, BorderLayout.SOUTH);
		return sidebar;
	}

	public void loadDanhSachDatCho() {
		if (pnlDanhSachDatCho == null)
			return;
		pnlDanhSachDatCho.removeAll();

		String tuKhoa = (txtTimKiemDatCho != null) ? txtTimKiemDatCho.getText().trim() : "";
		String thoiGianLoc = (cboLocThoiGian != null && cboLocThoiGian.getSelectedItem() != null)
				? cboLocThoiGian.getSelectedItem().toString()
				: "Hôm nay";

		List<PhieuDatBan> danhSachPhieuToanBo = phieuDAO.getDanhSachDatChoChuaCheckIn();
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

		String todayDate = sdfDate.format(new Date());
		String tomorrowDate = sdfDate.format(new Date(System.currentTimeMillis() + 86400000L));

		int count = 0;

		for (PhieuDatBan phieu : danhSachPhieuToanBo) {
			String maBanDaiDien = phieu.getMaBan();
			String tenBanHienThi = phieu.getTenBan();

			try {
				java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
				java.sql.PreparedStatement ps = con.prepareStatement(
						"SELECT b.tenBan " +
								"FROM ChiTietDatBan ct " +
								"JOIN BanAn b ON ct.maBan = b.maBan " +
								"WHERE ct.maPhieu = ?");
				ps.setString(1, phieu.getMaPhieu());
				java.sql.ResultSet rs = ps.executeQuery();

				java.util.List<String> dsTenBan = new java.util.ArrayList<>();
				while (rs.next()) {
					dsTenBan.add(rs.getString("tenBan"));
				}
				rs.close();
				ps.close();

				if (!dsTenBan.isEmpty()) {
					tenBanHienThi = String.join(", ", dsTenBan);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			boolean matchesSearch = true;
			if (!tuKhoa.isEmpty()) {
				matchesSearch = (phieu.getTenKhachHang() != null
						&& phieu.getTenKhachHang().toLowerCase().contains(tuKhoa.toLowerCase())) ||
						(phieu.getSoDienThoai() != null && phieu.getSoDienThoai().contains(tuKhoa));
			}

			boolean matchesTime = true;
			if (phieu.getThoiGianDen() != null) {
				String phieuDate = sdfDate.format(phieu.getThoiGianDen());
				if ("Hôm nay".equals(thoiGianLoc) && !phieuDate.equals(todayDate)) {
					matchesTime = false;
				} else if ("Ngày mai".equals(thoiGianLoc) && !phieuDate.equals(tomorrowDate)) {
					matchesTime = false;
				}
			}

			if (matchesSearch && matchesTime) {
				String timeStr = (phieu.getThoiGianDen() != null) ? sdfTime.format(phieu.getThoiGianDen()) : "--:--";
				pnlDanhSachDatCho.add(createBookingCard(
						phieu.getMaPhieu(),
						phieu.getTenKhachHang(),
						phieu.getSoDienThoai(),
						timeStr,
						maBanDaiDien,
						tenBanHienThi,
						phieu.getSoLuongKhach()));
				pnlDanhSachDatCho.add(Box.createVerticalStrut(15));
				count++;
			}
		}

		if (count == 0) {
			JLabel emptyLabel = new JLabel("Không tìm thấy kết quả phù hợp.");
			emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
			emptyLabel.setForeground(TEXT_GRAY);
			emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			pnlDanhSachDatCho.add(emptyLabel);
		}

		pnlDanhSachDatCho.revalidate();
		pnlDanhSachDatCho.repaint();
	}

	private JPanel createBookingCard(String maPhieu, String name, String phone, String time, String maBan,
			String tenBan, int guests) {
		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(254, 252, 232));
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
				g2.setColor(new Color(253, 224, 71));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
				g2.dispose();
			}
		};
		card.setLayout(new BorderLayout(10, 10));
		card.setOpaque(false);
		card.setBorder(new EmptyBorder(10, 12, 10, 12));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

		JPanel info = new JPanel(new GridLayout(3, 2));
		info.setOpaque(false);
		JLabel lName = new JLabel(name);
		lName.setFont(new Font("Segoe UI", Font.BOLD, 14));
		JLabel lTime = new JLabel(time, SwingConstants.RIGHT);
		lTime.setFont(new Font("Segoe UI", Font.BOLD, 12));
		JLabel lPhone = new JLabel(phone);
		lPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lPhone.setForeground(TEXT_GRAY);
		JLabel lTable = new JLabel("Bàn: " + tenBan);
		lTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lTable.setForeground(TEXT_GRAY);
		JLabel lGuests = new JLabel(guests + " người", SwingConstants.RIGHT);
		lGuests.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		info.add(lName);
		info.add(lTime);
		info.add(lPhone);
		info.add(new JLabel(""));
		info.add(lTable);
		info.add(lGuests);

		JButton btnCheckIn = new ModernButton("Check-in Khách", ModernButton.Style.PRIMARY);
		btnCheckIn.setPreferredSize(new Dimension(0, 34));
		btnCheckIn.addActionListener(e -> xuLyCheckIn(maPhieu, name, maBan, tenBan, card));

		JButton btnDoiGio = new ModernButton("Đổi giờ", ModernButton.Style.OUTLINE);
		btnDoiGio.setPreferredSize(new Dimension(0, 34));
		btnDoiGio.addActionListener(e -> xuLyDoiGio(maPhieu));

		JButton btnHuy = new ModernButton("Hủy", ModernButton.Style.OUTLINE);
		btnHuy.setPreferredSize(new Dimension(0, 34));
		btnHuy.setForeground(RED_MAIN);
		btnHuy.addActionListener(e -> xuLyHuyDatBan(maPhieu));

		JPanel pnlActions = new JPanel(new GridLayout(2, 1, 0, 5));
		pnlActions.setOpaque(false);
		pnlActions.add(btnCheckIn);
		
		JPanel pnlPhu = new JPanel(new GridLayout(1, 2, 5, 0));
		pnlPhu.setOpaque(false);
		pnlPhu.add(btnDoiGio);
		pnlPhu.add(btnHuy);
		pnlActions.add(pnlPhu);

		card.add(info, BorderLayout.CENTER);
		card.add(pnlActions, BorderLayout.SOUTH);
		return card;
	}

	private void xuLyDoiGio(String maPhieu) {
		List<PhieuDatBan> dsPhieu = phieuDAO.getDanhSachDatChoChuaCheckIn();
		PhieuDatBan phieuHienTai = null;
		for (PhieuDatBan p : dsPhieu) {
			if (p.getMaPhieu().equals(maPhieu)) {
				phieuHienTai = p;
				break;
			}
		}
		if (phieuHienTai == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt bàn này hoặc đã hoàn thành/hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JDialog dialog = new JDialog(this, "Cập Nhật Thời Gian Đặt Bàn", true);
		dialog.setSize(400, 250);
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);
		
		JPanel pnlMain = new JPanel(new BorderLayout(0, 20));
		pnlMain.setBackground(Color.WHITE);
		pnlMain.setBorder(new EmptyBorder(15, 15, 15, 15));
		
		JPanel pnlForm = new JPanel(new GridLayout(2, 2, 10, 15));
		pnlForm.setBackground(Color.WHITE);
		
		JLabel lblNgay = new JLabel("Ngày đến:");
		lblNgay.setFont(new Font("Segoe UI", Font.BOLD, 14));
		com.toedter.calendar.JDateChooser dateChooser = new com.toedter.calendar.JDateChooser();
		dateChooser.setDateFormatString("dd/MM/yyyy");
		dateChooser.setDate(phieuHienTai.getThoiGianDen() != null ? phieuHienTai.getThoiGianDen() : new Date());
		dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		
		JLabel lblGio = new JLabel("Giờ đến:");
		lblGio.setFont(new Font("Segoe UI", Font.BOLD, 14));
		JSpinner spinThoiGian = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinThoiGian, "HH:mm");
		spinThoiGian.setEditor(timeEditor);
		spinThoiGian.setValue(phieuHienTai.getThoiGianDen() != null ? phieuHienTai.getThoiGianDen() : new Date());
		spinThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		
		pnlForm.add(lblNgay);
		pnlForm.add(dateChooser);
		pnlForm.add(lblGio);
		pnlForm.add(spinThoiGian);
		
		JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		pnlBtn.setBackground(Color.WHITE);
		
		JButton btnHuy = new JButton("Bỏ qua");
		btnHuy.setPreferredSize(new Dimension(100, 35));
		btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnHuy.setBackground(new Color(209, 213, 219));
		btnHuy.setForeground(Color.BLACK);
		btnHuy.setFocusPainted(false);
		btnHuy.setBorderPainted(false);
		btnHuy.addActionListener(e -> dialog.dispose());
		
		JButton btnLuu = new JButton("Lưu thay đổi");
		btnLuu.setPreferredSize(new Dimension(130, 35));
		btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnLuu.setBackground(new Color(59, 130, 246));
		btnLuu.setForeground(Color.WHITE);
		btnLuu.setFocusPainted(false);
		btnLuu.setBorderPainted(false);
		
		btnLuu.addActionListener(e -> {
			Date ngayChon = dateChooser.getDate();
			if (ngayChon == null) {
				JOptionPane.showMessageDialog(dialog, "Vui lòng chọn ngày hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			Date gioChon = (Date) spinThoiGian.getValue();
			
			java.util.Calendar calNgay = java.util.Calendar.getInstance();
			calNgay.setTime(ngayChon);
			
			java.util.Calendar calGio = java.util.Calendar.getInstance();
			calGio.setTime(gioChon);
			
			java.util.Calendar calKetQua = java.util.Calendar.getInstance();
			calKetQua.set(calNgay.get(java.util.Calendar.YEAR), calNgay.get(java.util.Calendar.MONTH), calNgay.get(java.util.Calendar.DATE),
						  calGio.get(java.util.Calendar.HOUR_OF_DAY), calGio.get(java.util.Calendar.MINUTE), 0);
			
			Date thoiGianMoi = calKetQua.getTime();
			
			if (thoiGianMoi.before(new Date())) {
				JOptionPane.showMessageDialog(dialog, "Không được chọn thời gian trong quá khứ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			boolean ok = phieuDAO.doiThoiGianDat(maPhieu, new java.sql.Timestamp(thoiGianMoi.getTime()));
			if (ok) {
				JOptionPane.showMessageDialog(dialog, "Cập nhật thời gian thành công!");
				dialog.dispose();
				loadDanhSachDatCho();
			} else {
				JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật thời gian!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		pnlBtn.add(btnHuy);
		pnlBtn.add(btnLuu);
		
		pnlMain.add(pnlForm, BorderLayout.CENTER);
		pnlMain.add(pnlBtn, BorderLayout.SOUTH);
		
		dialog.setContentPane(pnlMain);
		dialog.setVisible(true);
	}

	private void xuLyHuyDatBan(String maPhieu) {
		List<PhieuDatBan> dsPhieu = phieuDAO.getDanhSachDatChoChuaCheckIn();
		PhieuDatBan phieuHienTai = null;
		for (PhieuDatBan p : dsPhieu) {
			if (p.getMaPhieu().equals(maPhieu)) {
				phieuHienTai = p;
				break;
			}
		}
		if (phieuHienTai == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt bàn này hoặc đã hoàn thành/hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy phiếu đặt bàn này?\nBàn sẽ được chuyển về trạng thái Trống.", "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (choice == JOptionPane.YES_OPTION) {
			boolean ok = phieuDAO.capNhatTrangThaiPhieu(maPhieu, "Đã hủy");
			if (ok) {
				try {
					java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
					
					if (phieuHienTai.getMaBan() != null && !phieuHienTai.getMaBan().trim().isEmpty()) {
					    banAnDAO.capNhatTrangThai(phieuHienTai.getMaBan(), "Trống");
					}
					
					java.sql.PreparedStatement ps = con.prepareStatement("SELECT maBan FROM ChiTietDatBan WHERE maPhieu = ?");
					ps.setString(1, maPhieu);
					java.sql.ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						banAnDAO.capNhatTrangThai(rs.getString("maBan"), "Trống");
					}
					rs.close();
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				JOptionPane.showMessageDialog(this, "Hủy bàn đặt thành công!");
				loadDanhSachDatCho();
				refreshSoDoBan();
			} else {
				JOptionPane.showMessageDialog(this, "Lỗi khi hủy bàn đặt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}


private void xuLyCheckIn(String maPhieu, String tenKhach, String maBanDaiDien, String tenBanDaiDien, JPanel cardRef) {
	String sdtKhach = "0000000000";
	int soLuongKhach = 1;
	java.util.Date thoiGianDen = null;

	// 1. Lấy thông tin phiếu đặt
	List<PhieuDatBan> dsPhieu = phieuDAO.getDanhSachDatChoChuaCheckIn();
	for (PhieuDatBan p : dsPhieu) {
		if (p.getMaPhieu().equals(maPhieu)) {
			sdtKhach = p.getSoDienThoai();
			soLuongKhach = p.getSoLuongKhach();
			thoiGianDen = p.getThoiGianDen();
			break;
		}
	}

	List<String> danhSachMaBan = new ArrayList<>();
	try {
		java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
		java.sql.PreparedStatement ps = con.prepareStatement("SELECT maBan FROM ChiTietDatBan WHERE maPhieu = ?");
		ps.setString(1, maPhieu);
		java.sql.ResultSet rs = ps.executeQuery();
		while (rs.next()) danhSachMaBan.add(rs.getString("maBan"));
		rs.close();
		ps.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	if (danhSachMaBan.isEmpty()) danhSachMaBan.add(maBanDaiDien);

	List<BanAn> allBan = banAnDAO.getAllBanAn();



	if (thoiGianDen != null) {
		long diffPhut = (thoiGianDen.getTime() - System.currentTimeMillis()) / (60 * 1000);
		if (diffPhut > 30) {
			JOptionPane.showMessageDialog(cardRef,
					"Chưa đến giờ check-in!\n"
							+ "Khách đặt lúc: " + new java.text.SimpleDateFormat("HH:mm dd/MM").format(thoiGianDen) + "\n"
							+ "Còn " + diffPhut + " phút nữa mới đến giờ.\n"
							+ "Chỉ được check-in trước tối đa 30 phút.",
					"Chưa đến giờ", JOptionPane.WARNING_MESSAGE);
			return;
		}
	}



	List<BanAn> dsBanBiBan = new ArrayList<>();
	List<BanAn> dsBanThayTheDuocChon = new ArrayList<>();
	List<String> dsMaBanGocBiThayThe = new ArrayList<>();

	for (String idBan : danhSachMaBan) {
		for (BanAn ban : allBan) {
			if (ban.getMaBan().equals(idBan)) {
				if (ban.getTrangThai() != null && ban.getTrangThai().equalsIgnoreCase("Có khách")) {
					dsBanBiBan.add(ban);

					BanAn banGoiY = null;
					for (BanAn b : allBan) {
						if (b.getTrangThai() != null
								&& b.getTrangThai().equalsIgnoreCase("Trống")
								&& b.getViTri() != null
								&& b.getViTri().equalsIgnoreCase(ban.getViTri())
								&& b.getSucChua() >= ban.getSucChua()
								&& !danhSachMaBan.contains(b.getMaBan())
								&& !dsBanThayTheDuocChon.contains(b)) {
							banGoiY = b;
							break;
						}
					}

					if (banGoiY != null) {
						dsBanThayTheDuocChon.add(banGoiY);
						dsMaBanGocBiThayThe.add(idBan);
					}
				}
				break;
			}
		}
	}


	if (!dsBanBiBan.isEmpty()) {
		if (dsBanThayTheDuocChon.size() == dsBanBiBan.size()) {
			StringBuilder confirmMsg = new StringBuilder("Phát hiện bàn đặt ban đầu đang có khách vãng lai ngồi!\nHệ thống gợi ý đổi bàn tự động:\n");
			for (int i = 0; i < dsBanBiBan.size(); i++) {
				confirmMsg.append("  - Đổi ").append(dsBanBiBan.get(i).getTenBan())
						.append(" ➔ Sang bàn trống: ").append(dsBanThayTheDuocChon.get(i).getTenBan())
						.append(" (Sức chứa: ").append(dsBanThayTheDuocChon.get(i).getSucChua()).append(")\n");
			}
			confirmMsg.append("\nNí có muốn tự động đổi bàn và thực hiện Check-In luôn không?");

			int choice = JOptionPane.showConfirmDialog(cardRef, confirmMsg.toString(), "Gợi ý điều phối bàn thông minh", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (choice == JOptionPane.YES_OPTION) {
				try {
					java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
					java.sql.PreparedStatement psUpdate = con.prepareStatement(
							"UPDATE ChiTietDatBan SET maBan = ? WHERE maPhieu = ? AND maBan = ?");

					for (int i = 0; i < dsBanThayTheDuocChon.size(); i++) {
						psUpdate.setString(1, dsBanThayTheDuocChon.get(i).getMaBan());
						psUpdate.setString(2, maPhieu);
						psUpdate.setString(3, dsMaBanGocBiThayThe.get(i));
						psUpdate.addBatch();

						danhSachMaBan.remove(dsMaBanGocBiThayThe.get(i));
						danhSachMaBan.add(dsBanThayTheDuocChon.get(i).getMaBan());
					}
					psUpdate.executeBatch();
					psUpdate.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(cardRef, "Lỗi tự động chuyển dữ liệu bàn!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {
				return;
			}
		}
		else {
			StringBuilder errorMsg = new StringBuilder("Không thể nhận bàn! Các bàn sau đang có khách vãng lai:\n");
			for (BanAn b : dsBanBiBan) {
				errorMsg.append("  - ").append(b.getTenBan()).append("\n");
			}
			errorMsg.append("Hệ thống đã quét nhưng không tìm đủ bàn trống tương đương cùng tầng để thay thế.\nVui lòng hối thúc khách cũ thanh toán!");
			JOptionPane.showMessageDialog(cardRef, errorMsg.toString(), "Hết bàn trống tương đương", JOptionPane.WARNING_MESSAGE);
			return;
		}
	}



	int totalCapacity = 0;
	StringBuilder tenCacBan = new StringBuilder();
	for (String idBan : danhSachMaBan) {
		for (BanAn ban : allBan) {
			if (ban.getMaBan().equals(idBan)) {
				totalCapacity += ban.getSucChua();
				tenCacBan.append(ban.getTenBan()).append(", ");
				break;
			}
		}
	}
	String chuoiTenBan = tenCacBan.toString();
	if (chuoiTenBan.endsWith(", ")) chuoiTenBan = chuoiTenBan.substring(0, chuoiTenBan.length() - 2);

	// Kiểm tra sức chứa tổng
	if (soLuongKhach > totalCapacity) {
		String msgCanhBao = "Các bàn " + chuoiTenBan + " có tổng sức chứa " + totalCapacity + " người.\n"
				+ "Khách đặt " + soLuongKhach + " người, đã vượt quá sức chứa.\n\n"
				+ "Bạn có muốn tiếp tục check-in (kê thêm ghế) không?";
		if (JOptionPane.showConfirmDialog(cardRef, msgCanhBao, "Cảnh báo vượt sức chứa",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
	}

	String maHD = "HD" + System.currentTimeMillis();
	String maNV = (Entity.LuuLog.nhanVienDangNhap != null) ? Entity.LuuLog.nhanVienDangNhap.getMaNV() : "NV001";

	boolean hdOk = hoaDonDAO.taoHoaDonMoi(maHD, maNV, danhSachMaBan.get(0), tenKhach, sdtKhach, soLuongKhach, maPhieu);
	if (hdOk) hoaDonDAO.copyMonAnTuPhieuSangHoaDon(maPhieu, maHD);

	boolean banOk = true;
	for (String idBan : danhSachMaBan) {
		if (!banAnDAO.capNhatTrangThai(idBan, "Có khách")) banOk = false;
	}

	boolean phieuOk = phieuDAO.capNhatTrangThaiPhieu(maPhieu, "Đã đến");

	if (hdOk && banOk && phieuOk) {
		JOptionPane.showMessageDialog(cardRef, "✅ Check-in thành công cho khách " + tenKhach + "!\nCửa hàng đã bố trí vào bàn: " + chuoiTenBan, "Thành công", JOptionPane.INFORMATION_MESSAGE);
		refreshSoDoBan();
		loadDanhSachDatCho();
	} else {
		JOptionPane.showMessageDialog(cardRef, "❌ Lỗi hệ thống khi lưu trạng thái nhận bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	}
}

	private JButton createSolidButton(String text, Color bg, Color fg) {
		return new ModernButton(text, ModernButton.Style.PRIMARY);
	}

	private JButton createOutlineButton(String text) {
		return new ModernButton(text, ModernButton.Style.OUTLINE);
	}

	private void hienThiThongTinBan(String maBan, String tenBan) {
		String[] infoKhach = hoaDonDAO.getThongTinKhachVuaMo(maBan);

		if (infoKhach != null) {
			String ten = infoKhach[0];
			String sdt = infoKhach[1];
			String sl = infoKhach[2];
			String gio = infoKhach[3];

			String msg = "🏮 NHÀ HÀNG NGÓI ĐỎ 🏮\n" + "Bàn: " + tenBan + "\n" + "Khách hàng: "
					+ (ten != null ? ten : "Khách lẻ") + "\n" + "Số điện thoại: " + (sdt != null ? sdt : "Trống") + "\n"
					+ "Số lượng: " + sl + " người\n" + "Giờ vào bàn: " + gio + "\n";

			JOptionPane.showMessageDialog(this, msg, "Thông tin khách đang ngồi", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Bàn đang trống hoặc chưa có dữ liệu khách!");
		}
	}
	private String getGioAnToan(String maBan) {
		// Query cả PhieuDatBan.maBan lẫn ChiTietDatBan.maBan
		String sql = "SELECT p.thoiGianDen FROM PhieuDatBan p "
				+ "LEFT JOIN ChiTietDatBan ct ON p.maPhieu = ct.maPhieu "
				+ "WHERE (p.maBan = ? OR ct.maBan = ?) "
				+ "AND p.trangThai = N'Chờ khách' "
				+ "ORDER BY p.thoiGianDen ASC";

		long currentTime = System.currentTimeMillis();
		long minDiff = Long.MAX_VALUE;
		java.util.Date thoiGianSapToi = null;

		try {
			java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
			java.sql.PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, maBan);
			ps.setString(2, maBan);
			java.sql.ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				java.sql.Timestamp ts = rs.getTimestamp("thoiGianDen");
				if (ts != null) {
					long diff = ts.getTime() - currentTime;
					if (diff > 0 && diff < minDiff) {
						minDiff = diff;
						thoiGianSapToi = new java.util.Date(ts.getTime());
					}
				}
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (thoiGianSapToi != null) {
			java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat("yyyy-MM-dd");
			String today = sdfDate.format(new java.util.Date());
			String phieuDate = sdfDate.format(thoiGianSapToi);

			if (today.equals(phieuDate)) {
				long diffMins = minDiff / (60 * 1000);
				if (diffMins >= 150) {
					return new java.text.SimpleDateFormat("HH:mm").format(thoiGianSapToi);
				}
			} else {
				return "Mai";
			}
		}
		return null;
	}


	public void refreshSoDoBan() {
		if (gridMap == null)
			return;
		gridMap.removeAll();
		if (lblMapTitle != null)
			lblMapTitle.setText("Sơ đồ bàn - " + currentTab);

		int requiredCap = 0;
		if (cboLocSucChua != null && cboLocSucChua.getSelectedIndex() > 0) {
			String sel = (String) cboLocSucChua.getSelectedItem();
			requiredCap = Integer.parseInt(sel.replaceAll("[^0-9]", ""));
		}

		List<Entity.BanAn> danhSachBan = banAnDAO.getAllBanAn();

		for (Entity.BanAn ban : danhSachBan) {
			String viTri = ban.getViTri();
			if (viTri != null && viTri.trim().equalsIgnoreCase(currentTab)) {

				boolean showTable = true;
				String realStatus = ban.getTrangThai().trim();
				String displayStatus = realStatus;
				boolean isSafe = false;

				if (realStatus.equalsIgnoreCase("Đã đặt")) {
					String gioDen = getGioAnToan(ban.getMaBan());
					if (gioDen != null) {
						isSafe = true;
						displayStatus = gioDen.equals("Mai") ? "Trống đến mai" : "Trống đến " + gioDen;
					}
				}

				if (requiredCap > 0) {
					if (!(realStatus.equalsIgnoreCase("Trống") || isSafe) || ban.getSucChua() < requiredCap) {
						showTable = false;
					}
				}

				if (showTable) {
					gridMap.add(createTableCard(ban.getMaBan(), ban.getTenBan(), ban.getSucChua(), displayStatus,
							realStatus));
				}
			}
		}

		gridMap.revalidate();
		gridMap.repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new FrmLeTan().setVisible(true));
	}
}
