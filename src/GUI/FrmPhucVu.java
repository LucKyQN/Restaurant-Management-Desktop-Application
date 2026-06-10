package GUI;

import DAO.PhucVuService;
import DAO.PhucVuServiceDb;
import Entity.MonAn;
import Entity.NhanVien;
import Model.BanAnModel;
import Model.MonAnModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FrmPhucVu extends JFrame {

	private final NhanVien nhanVien;
	private final PhucVuService phucVuService = new PhucVuServiceDb();

	private static final Color RED_MAIN   = new Color(220, 38, 38);
	private static final Color BG_MAIN    = new Color(248, 248, 248);
	private static final Color TEXT_DARK  = new Color(20, 20, 20);
	private static final Color TEXT_GRAY  = new Color(120, 120, 120);
	private static final Color BORDER_CLR = new Color(225, 225, 225);

	private List<BanAnModel> danhSachBan;
	private BanAnModel banDangChon;
	private JPanel pnlDanhSachBan;
	private JPanel pnlChiTiet;
	private JTable tblMon;

	private DefaultTableModel tblModel;

	private JButton btnYeuCauTT;
	private JButton btnThemMon;
	private SwingWorker<List<BanAnModel>, Void> currentWorker;

	public FrmPhucVu(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
		initUI();
		taiDanhSachBan();

		// Khởi động polling thông báo (3 giây/lần)
		Service.NotificationService.getInstance().startPolling(3000);

		Timer timer = new Timer(5000, e -> {
			if (tblMon != null && !tblMon.isEditing()) {
				taiDanhSachBan();
			}
		});
		timer.start();
	}



	private void initUI() {
		setTitle("Phục vụ & Gọi món - Nhà Hàng Ngói Đỏ");
		setSize(1440, 860);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(BG_MAIN);
		root.add(createTopBar(), BorderLayout.NORTH);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
		split.setDividerLocation(300);
		split.setDividerSize(1);
		split.setBorder(null);
		root.add(split, BorderLayout.CENTER);
		setContentPane(root);
	}



	private void taiDanhSachBan() {
		if (currentWorker != null && !currentWorker.isDone()) {
			currentWorker.cancel(true);
		}

		currentWorker = new SwingWorker<>() {
			@Override
			protected List<BanAnModel> doInBackground() {
				return phucVuService.getDanhSachBanCanPhucVu();
			}

			@Override
			protected void done() {
				if (isCancelled())
					return;
				try {
					danhSachBan = get();
					veLaiDanhSachBan();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		currentWorker.execute();
	}



	private JPanel createTopBar() {
		JPanel bar = new JPanel(new BorderLayout(16, 0));
		bar.setBackground(Color.WHITE);
		bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
				new EmptyBorder(14, 20, 14, 24)));

		JPanel west = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		west.setOpaque(false);
		JLabel lblLogo = new JLabel("🏮");
		lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
		lblLogo.setForeground(RED_MAIN);

		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		JLabel lbTitle = new JLabel("Phục vụ — Quản lý món theo bàn");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		JLabel lbRole = new JLabel("Nhân viên: " + nhanVien.getHoTenNV());
		lbRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lbRole.setForeground(TEXT_GRAY);
		info.add(lbTitle);
		info.add(lbRole);

		west.add(lblLogo);
		west.add(info);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		right.setOpaque(false);

		// Chuông thông báo
		NotificationBellPanel bellPanel = new NotificationBellPanel();

		JButton btnCaiDat = new ModernButton("Cài đặt", ModernButton.Style.OUTLINE);
		btnCaiDat.setPreferredSize(new Dimension(100, 34));
		btnCaiDat.addActionListener(e -> {
			JDialog dialog = new JDialog(this, "Cài đặt cá nhân", true);
			dialog.setSize(900, 700);
			dialog.setLocationRelativeTo(this);
			dialog.setContentPane(new GUI.FrmCaiDat());
			dialog.setVisible(true);
		});

		JButton btnMenu = new ModernButton("Xem Menu", ModernButton.Style.PRIMARY);
		btnMenu.setPreferredSize(new Dimension(120, 34));
		btnMenu.addActionListener(e -> {
			FrmMenu frmMenu = new FrmMenu();
			frmMenu.setVisible(true);
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

		right.add(bellPanel);
		right.add(btnCaiDat);
		right.add(btnMenu);
		right.add(btnLogout);

		bar.add(west, BorderLayout.WEST);
		bar.add(right, BorderLayout.EAST);
		return bar;
	}



	private JPanel createLeftPanel() {
		JPanel left = new JPanel(new BorderLayout());
		left.setBackground(Color.WHITE);
		left.setPreferredSize(new Dimension(300, 0));
		left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR));

		JLabel lbTitle = new JLabel("Bàn đang phục vụ");
		lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lbTitle.setBorder(new EmptyBorder(16, 16, 12, 16));

		pnlDanhSachBan = new JPanel();
		pnlDanhSachBan.setBackground(Color.WHITE);
		pnlDanhSachBan.setLayout(new BoxLayout(pnlDanhSachBan, BoxLayout.Y_AXIS));
		pnlDanhSachBan.setBorder(new EmptyBorder(0, 12, 12, 12));

		JScrollPane scroll = new JScrollPane(pnlDanhSachBan);
		scroll.setBorder(null);

		left.add(lbTitle, BorderLayout.NORTH);
		left.add(scroll, BorderLayout.CENTER);
		return left;
	}

	private void veLaiDanhSachBan() {
		SwingUtilities.invokeLater(() -> {
			pnlDanhSachBan.removeAll();

			if (danhSachBan != null && !danhSachBan.isEmpty()) {
				java.util.Set<String> processedIds = new java.util.HashSet<>();

				for (BanAnModel ban : danhSachBan) {
					if (!processedIds.contains(ban.maBan)) {
						pnlDanhSachBan.add(taoTheBan(ban));
						pnlDanhSachBan.add(Box.createVerticalStrut(10));
						processedIds.add(ban.maBan);
					}
				}
			}

			pnlDanhSachBan.revalidate();
			pnlDanhSachBan.repaint();
		});
	}

	private JPanel taoTheBan(BanAnModel ban) {
		boolean selected = ban == banDangChon;

		RoundedPanel card = new RoundedPanel(10, true, selected ? new Color(0xFEF2F2) : Color.WHITE);
		card.setLayout(new BorderLayout(0, 4));
		card.setBorder(new EmptyBorder(12, 14, 12, 14));
		card.setCursor(new Cursor(Cursor.HAND_CURSOR));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

		JLabel lbBan = new JLabel(ban.tenBan);
		lbBan.setFont(UIConstants.FONT_BOLD_14);
		lbBan.setForeground(selected ? UIConstants.PRIMARY : UIConstants.TEXT_DARK);

		JLabel lbTong = new JLabel(formatTien(ban.tamTinh) + " đ");
		lbTong.setFont(UIConstants.FONT_BOLD_13);
		lbTong.setForeground(UIConstants.PRIMARY);

		card.add(lbBan, BorderLayout.NORTH);
		card.add(lbTong, BorderLayout.SOUTH);

		card.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				banDangChon = ban;
				veLaiDanhSachBan();
				hienThiChiTietBan(ban);
			}
		});
		return card;
	}


	private JPanel createRightPanel() {
		pnlChiTiet = new JPanel(new BorderLayout());
		pnlChiTiet.setBackground(BG_MAIN);
		hienThiGoiChonBan();
		return pnlChiTiet;
	}

	private void hienThiGoiChonBan() {
		pnlChiTiet.removeAll();
		JLabel lb = new JLabel("Chọn một bàn để xem chi tiết.");
		lb.setHorizontalAlignment(SwingConstants.CENTER);
		pnlChiTiet.add(lb, BorderLayout.CENTER);
		pnlChiTiet.revalidate();
		pnlChiTiet.repaint();
	}

	private void hienThiChiTietBan(BanAnModel ban) {
		pnlChiTiet.removeAll();

		JPanel wrap = new JPanel(new BorderLayout(0, 12));
		wrap.setBorder(new EmptyBorder(20, 24, 20, 24));
		wrap.setBackground(BG_MAIN);

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		JLabel lbHd = new JLabel(ban.tenBan + " — HĐ " + (ban.maHD == null ? "---" : ban.maHD));
		lbHd.setFont(new Font("Segoe UI", Font.BOLD, 18));
		header.add(lbHd, BorderLayout.WEST);

		tblModel = new DefaultTableModel(
				new String[] { "ID", "Mã món", "Tên món", "SL", "Đơn giá", "Thành tiền", "Trạng thái" }, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return c == 6 && !"Chờ thanh toán".equalsIgnoreCase(ban.trangThai);
			}
		};

		tblMon = new JTable(tblModel);
		tblMon.setRowHeight(52);
		tblMon.setShowGrid(false);
		tblMon.setIntercellSpacing(new Dimension(0, 0));
		tblMon.setFont(UIConstants.FONT_PLAIN_14);
		tblMon.getTableHeader().setFont(UIConstants.FONT_BOLD_13);
		tblMon.getTableHeader().setBackground(UIConstants.BG_APP);
		tblMon.getTableHeader().setForeground(UIConstants.TEXT_DARK);
		tblMon.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER));
		tblMon.setSelectionBackground(UIConstants.PRIMARY_TINT);
		tblMon.setSelectionForeground(UIConstants.TEXT_DARK);
		tblMon.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		tblMon.getColumnModel().getColumn(0).setMinWidth(0);
		tblMon.getColumnModel().getColumn(0).setMaxWidth(0);
		tblMon.getColumnModel().getColumn(1).setMinWidth(0);
		tblMon.getColumnModel().getColumn(1).setMaxWidth(0);

		// Pill badge renderer for status column
		tblMon.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				String text = value == null ? "" : value.toString();
				JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
					@Override protected void paintComponent(Graphics g) {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g2.setColor(getBackground());
						int arc = getHeight() - 4;
						g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 12, arc, arc);
						g2.dispose();
						super.paintComponent(g);
					}
				};
				lbl.setFont(UIConstants.FONT_BOLD_12);
				lbl.setOpaque(false);
				switch (text) {
					case "Đã lên"   -> { lbl.setBackground(UIConstants.BADGE_GREEN_BG); lbl.setForeground(UIConstants.BADGE_GREEN_FG); }
					case "Chưa lên" -> { lbl.setBackground(UIConstants.BADGE_RED_BG);   lbl.setForeground(UIConstants.BADGE_RED_FG);   }
					case "Mang về"  -> { lbl.setBackground(UIConstants.BADGE_BLUE_BG);  lbl.setForeground(UIConstants.BADGE_BLUE_FG);  }
					default          -> { lbl.setBackground(UIConstants.BADGE_GRAY_BG);  lbl.setForeground(UIConstants.BADGE_GRAY_FG);  }
				}
				if (isSelected) { lbl.setBackground(UIConstants.PRIMARY_TINT); lbl.setForeground(UIConstants.PRIMARY); }
				return lbl;
			}
		});

		JComboBox<String> cboStatus = new JComboBox<>(new String[] { "Chưa lên", "Đã lên", "Mang về", "Hủy" });
		tblMon.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(cboStatus));

		btnYeuCauTT = new ModernButton("YÊU CẦU THANH TOÁN", ModernButton.Style.PRIMARY);
		btnYeuCauTT.setPreferredSize(new Dimension(220, 40));

		btnThemMon = new ModernButton("Thêm món", ModernButton.Style.OUTLINE);
		btnThemMon.setPreferredSize(new Dimension(120, 40));

		Runnable checkNutThanhToan = () -> {
			boolean conMonChuaLen = false;

			for (int i = 0; i < tblModel.getRowCount(); i++) {
				Object obj = tblModel.getValueAt(i, 6);
				String trangThai = obj == null ? "" : obj.toString().trim();
				if ("Chưa lên".equalsIgnoreCase(trangThai)) {
					conMonChuaLen = true;
					break;
				}
			}

			boolean choThanhToan = "Chờ thanh toán".equalsIgnoreCase(ban.trangThai);

			if (btnYeuCauTT != null) {
				boolean enable = !conMonChuaLen && !choThanhToan && tblModel.getRowCount() > 0;
				btnYeuCauTT.setEnabled(enable);
			}

			if (btnThemMon != null) {
				btnThemMon.setEnabled(!choThanhToan);
			}
		};

		tblModel.addTableModelListener(e -> {
			if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 6) {
				int row = e.getFirstRow();

				Object idObj = tblModel.getValueAt(row, 0);
				if (idObj == null) {
					System.err.println("LỖI: Cột ID đang bị trống (null)!");
					return;
				}

				int idCTHD = Integer.parseInt(idObj.toString());
				String ttMoi = String.valueOf(tblModel.getValueAt(row, 6));

				boolean success = phucVuService.capNhatTrangThaiMon(idCTHD, ttMoi);

				if (success) {
					SwingUtilities.invokeLater(checkNutThanhToan);
				} else {
					System.err.println("SQL: Cập nhật trạng thái thất bại!");
				}
			}
		});

		napBangMonTuHoaDon(ban.maHD);

		JScrollPane scroll = new JScrollPane(tblMon);
		scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		actions.setOpaque(false);

		btnThemMon.addActionListener(e -> {
			if ("Chờ thanh toán".equalsIgnoreCase(ban.trangThai)) {
				JOptionPane.showMessageDialog(this, "Bàn này đã yêu cầu thanh toán, không thể thêm món nữa.",
						"Không thể thêm món", JOptionPane.WARNING_MESSAGE);
				return;
			}

			boolean daThem = moHopThoaiThemNhieuMon(ban);
			if (daThem) {
				napBangMonTuHoaDon(ban.maHD);
				tblModel.fireTableDataChanged();
				SwingUtilities.invokeLater(checkNutThanhToan);
				taiDanhSachBan();
			}
		});

		btnYeuCauTT.addActionListener(e -> {
			int c = JOptionPane.showConfirmDialog(this, "Xác nhận yêu cầu thanh toán cho " + ban.tenBan + "?",
					"Xác nhận", JOptionPane.YES_NO_OPTION);

			if (c == JOptionPane.YES_OPTION) {
				if (phucVuService.yeuCauThanhToan(ban.maHD, ban.maBan)) {
					JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu!");

					ban.trangThai = "Chờ thanh toán";

					btnThemMon.setEnabled(false);
					btnYeuCauTT.setEnabled(false);

					napBangMonTuHoaDon(ban.maHD);
					tblModel.fireTableDataChanged();
					SwingUtilities.invokeLater(checkNutThanhToan);
					taiDanhSachBan();
				}
			}
		});

		actions.add(btnThemMon);
		actions.add(Box.createHorizontalStrut(100));
		actions.add(btnYeuCauTT);

		wrap.add(header, BorderLayout.NORTH);
		wrap.add(scroll, BorderLayout.CENTER);
		wrap.add(actions, BorderLayout.SOUTH);
		pnlChiTiet.add(wrap, BorderLayout.CENTER);

		checkNutThanhToan.run();

		pnlChiTiet.revalidate();
		pnlChiTiet.repaint();
	}


	private void napBangMonTuHoaDon(String maHD) {
		tblModel.setRowCount(0);

		List<MonAnModel> ds = phucVuService.getChiTietHoaDon(maHD);

		for (MonAnModel m : ds) {
			tblModel.addRow(new Object[] {
					m.id_cthd,
					m.maMonAn,
					m.tenMonAn,
					m.soLuong,
					formatTien(m.giaBan),
					formatTien(m.thanhTien),
					m.trangThaiPhucVu
			});
		}
	}


	private boolean moHopThoaiThemNhieuMon(BanAnModel ban) {

		if (ban.maHD == null || ban.maHD.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Bàn " + ban.tenBan + " chưa có hóa đơn (có thể là khách đặt trước).\n"
							+ "Vui lòng liên hệ Lễ Tân Check-in để có Mã HD trước khi thêm món!",
					"Không tìm thấy Hóa Đơn", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if ("Chờ thanh toán".equalsIgnoreCase(ban.trangThai)) {
			JOptionPane.showMessageDialog(this, "Bàn này đã yêu cầu thanh toán, không thể thêm món nữa.",
					"Không thể thêm món", JOptionPane.WARNING_MESSAGE);
			return false;
		}


		List<MonAn> dsMon = phucVuService.getMonAnDangPhucVu();


		DefaultTableModel modelTam = new DefaultTableModel(
				new String[] { "Mã món", "Tên món", "SL", "Đơn giá", "Ghi chú" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 2 || column == 4; // SL và GhiChu
			}
		};


		JDialog dlg = new JDialog(this, "Gọi món — " + ban.tenBan, true);
		dlg.setSize(1100, 700);
		dlg.setLocationRelativeTo(this);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


		Color POS_BG        = new Color(0xF5F6FA); // light gray page bg
		Color POS_SURFACE   = Color.WHITE;          // card / panel surface
		Color POS_CARD      = new Color(0xEEF0F5); // table header bg
		Color POS_ACCENT    = new Color(0xE94560); // keep: vibrant red-pink accent
		Color POS_GREEN     = new Color(0x27AE60); // keep: confirm green
		Color POS_GRAY_BTN  = new Color(0x95A5A6); // medium gray button
		Color POS_TEXT      = new Color(0x1F2937); // near-black text
		Color POS_SUBTEXT   = new Color(0x6B7280); // secondary gray text
		Color POS_DIVIDER   = new Color(0xE5E7EB); // light border
		@SuppressWarnings("unused")
		Color POS_HIGHLIGHT = POS_ACCENT;           // for hover (unused directly)


		// ROOT layout of dialog

		JPanel dlgRoot = new JPanel(new BorderLayout());
		dlgRoot.setBackground(POS_BG);


		JPanel titleBar = new JPanel(new BorderLayout(12, 0));
		titleBar.setBackground(Color.WHITE);
		titleBar.setBorder(new EmptyBorder(16, 20, 12, 20));

		JLabel lblTitle = new JLabel("Gọi Món — " + ban.tenBan);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblTitle.setForeground(new Color(0x1F2937));

		JLabel lblHD = new JLabel("Hóa đơn: " + ban.maHD);
		lblHD.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblHD.setForeground(new Color(0x6B7280));

		JPanel titleWest = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
		titleWest.setBackground(Color.WHITE);
		titleWest.add(lblTitle);
		titleWest.add(lblHD);
		titleBar.add(titleWest, BorderLayout.WEST);

		JSeparator sep = new JSeparator();
		sep.setForeground(POS_DIVIDER);
		sep.setBackground(POS_DIVIDER);

		JPanel topZone = new JPanel(new BorderLayout());
		topZone.setBackground(Color.WHITE);
		topZone.add(titleBar, BorderLayout.CENTER);
		topZone.add(sep, BorderLayout.SOUTH);


		JPanel searchBar = new JPanel(new BorderLayout(10, 0));
		searchBar.setBackground(POS_BG);
		searchBar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, POS_DIVIDER),
				new EmptyBorder(10, 20, 14, 20)));

		JTextField txtSearch = new JTextField();
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtSearch.setForeground(POS_TEXT);
		txtSearch.setCaretColor(POS_TEXT);
		txtSearch.setBackground(Color.WHITE);
		txtSearch.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(POS_DIVIDER, 1, true),
				new EmptyBorder(8, 14, 8, 14)));
		txtSearch.putClientProperty("JTextField.placeholderText", "Nhập tên món để tìm kiếm");

		JButton btnTimKiem = new JButton("Tìm kiếm") {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(POS_ACCENT);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnTimKiem.setForeground(Color.WHITE);
		btnTimKiem.setOpaque(false);
		btnTimKiem.setContentAreaFilled(false);
		btnTimKiem.setBorderPainted(false);
		btnTimKiem.setPreferredSize(new Dimension(140, 40));
		btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

		searchBar.add(txtSearch, BorderLayout.CENTER);
		searchBar.add(btnTimKiem, BorderLayout.EAST);



		// Category filter buttons
		JPanel pnlCategories = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
		pnlCategories.setBackground(POS_BG);
		pnlCategories.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, POS_DIVIDER),
				new EmptyBorder(4, 8, 4, 8)));

		// Build category list from dsMon
		Map<String, List<MonAn>> monTheoLoai = new LinkedHashMap<>();
		monTheoLoai.put("Tất cả", new ArrayList<>(dsMon));
		for (MonAn mon : dsMon) {
			String tenDM = (mon.getDanhMuc() != null) ? mon.getDanhMuc().getTenDM() : "Khác";
			monTheoLoai.computeIfAbsent(tenDM, k -> new ArrayList<>()).add(mon);
		}

		// Menu item list panel (scrollable)
		JPanel pnlMonList = new JPanel();
		pnlMonList.setBackground(POS_BG);
		pnlMonList.setLayout(new BoxLayout(pnlMonList, BoxLayout.Y_AXIS));
		pnlMonList.setBorder(new EmptyBorder(4, 8, 8, 8));

		JScrollPane scrollMon = new JScrollPane(pnlMonList);
		scrollMon.getViewport().setBackground(POS_BG);
		scrollMon.setBorder(null);
		scrollMon.getVerticalScrollBar().setUnitIncrement(16);


		JTable tblCart = new JTable(modelTam);
		tblCart.setRowHeight(38);
		tblCart.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblCart.setForeground(POS_TEXT);
		tblCart.setBackground(Color.WHITE);
		tblCart.setSelectionBackground(new Color(0xFEE2E2)); // light red selection
		tblCart.setSelectionForeground(new Color(0xB91C1C));
		tblCart.setGridColor(POS_DIVIDER);
		tblCart.setShowHorizontalLines(true);
		tblCart.setShowVerticalLines(false);
		tblCart.getTableHeader().setBackground(POS_CARD);
		tblCart.getTableHeader().setForeground(POS_TEXT);
		tblCart.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		tblCart.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		// Hide Mã món column
		tblCart.getColumnModel().getColumn(0).setMinWidth(0);
		tblCart.getColumnModel().getColumn(0).setMaxWidth(0);
		tblCart.getColumnModel().getColumn(0).setWidth(0);
		// Column widths
		tblCart.getColumnModel().getColumn(1).setPreferredWidth(130);
		tblCart.getColumnModel().getColumn(2).setPreferredWidth(40);
		tblCart.getColumnModel().getColumn(3).setPreferredWidth(75);
		tblCart.getColumnModel().getColumn(4).setPreferredWidth(70);

		// ── Total label ───────────────────────────────────────────────────────
		JLabel lblTotal = new JLabel("Tổng: 0 đ");
		lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTotal.setForeground(new Color(0x16A34A)); // dark green, readable on white

		// ── Recompute total ───────────────────────────────────────────────────
		Runnable tinhTong = () -> {
			long tong = 0;
			for (int i = 0; i < modelTam.getRowCount(); i++) {
				try {
					int sl = Integer.parseInt(modelTam.getValueAt(i, 2).toString());
					long gia = (long) Double.parseDouble(modelTam.getValueAt(i, 3).toString().replaceAll("[^\\d]", ""));
					tong += (long) sl * gia;
				} catch (Exception ignored) {}
			}
			lblTotal.setText("Tổng: " + formatTien(tong) + " đ");
		};

		modelTam.addTableModelListener(e -> tinhTong.run());

		tblCart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = tblCart.rowAtPoint(e.getPoint());
					if (row < 0 || row >= modelTam.getRowCount()) return;
					// Dừng editor nếu đang chỉnh sửa
					if (tblCart.isEditing()) tblCart.getCellEditor().cancelCellEditing();
					try {
						int slHienTai = Integer.parseInt(modelTam.getValueAt(row, 2).toString());
						if (slHienTai > 1) {
							// Giảm số lượng đi 1
							modelTam.setValueAt(slHienTai - 1, row, 2);
						} else {
							// SL == 1 → xóa luôn dòng
							modelTam.removeRow(row);
						}
						// Cập nhật lại tổng tiền
						tinhTong.run();
					} catch (NumberFormatException ignored) {}
				}
			}
		});

		// ── Add-to-cart action (same logic as old btnThemVaoDS) ──────────────
		// Wrapped as a lambda so item cards can call it
		java.util.function.Consumer<MonAn> themVaoGio = mon -> {
			int soLuong = 1;
			String ghiChu = "";

			boolean daCo = false;
			for (int i = 0; i < modelTam.getRowCount(); i++) {
				String maMon = modelTam.getValueAt(i, 0).toString();
				String ghiChuCu = modelTam.getValueAt(i, 4) == null ? "" : modelTam.getValueAt(i, 4).toString();

				if (maMon.equals(mon.getMaMonAn()) && ghiChuCu.equals(ghiChu)) {
					int slCu = Integer.parseInt(modelTam.getValueAt(i, 2).toString());
					modelTam.setValueAt(slCu + soLuong, i, 2);
					daCo = true;
					break;
				}
			}

			if (!daCo) {
				modelTam.addRow(new Object[] { mon.getMaMonAn(), mon.getTenMon(), soLuong, (long) mon.getGiaMon(), ghiChu });
			}
		};

		// ── Render item card (POS style) ──────────────────────────────────────
		java.util.function.Consumer<List<MonAn>> renderMonList = (list) -> {
			pnlMonList.removeAll();
			for (MonAn mon : list) {
				JPanel card = new JPanel(new BorderLayout(12, 0)) {
					boolean hovered = false;
					{
						addMouseListener(new MouseAdapter() {
							@Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
							@Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
						});
					}
					@Override protected void paintComponent(Graphics g) {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						// Light: trắng khi thường, xám nhạt khi hover
						g2.setColor(hovered ? new Color(0xF0F4FF) : Color.WHITE);
						g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
						g2.setColor(POS_DIVIDER);
						g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
						g2.dispose();
					}
				};
				card.setOpaque(false);
				card.setBorder(new EmptyBorder(10, 12, 10, 12));
				card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
				card.setCursor(new Cursor(Cursor.HAND_CURSOR));

				// ── Food image (left) ─────────────────────────────────────────
				JLabel lblImg = new JLabel();
				lblImg.setPreferredSize(new Dimension(48, 48));
				lblImg.setHorizontalAlignment(SwingConstants.CENTER);
				lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
				// Try to load image from anhMon field; fallback to emoji
				boolean imgLoaded = false;
				if (mon.getAnhMon() != null && !mon.getAnhMon().isBlank()) {
					try {
						java.io.File imgFile = new java.io.File(mon.getAnhMon());
						if (!imgFile.exists()) {
							// try relative to project
							imgFile = new java.io.File("src/image/" + mon.getAnhMon());
						}
						if (imgFile.exists()) {
							ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
							Image scaled = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
							lblImg.setIcon(new ImageIcon(scaled));
							imgLoaded = true;
						}
					} catch (Exception ignored) {}
				}
				if (!imgLoaded) {
					lblImg.setText("🍽");
				}

				// ── Info center (name + price) ────────────────────────────────
				JPanel info = new JPanel();
				info.setOpaque(false);
				info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

				JLabel lblName = new JLabel(mon.getTenMon());
				lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
				lblName.setForeground(new Color(0x1F2937)); // dark text

				JLabel lblPrice = new JLabel(formatTien((long) mon.getGiaMon()) + " đ");
				lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
				lblPrice.setForeground(new Color(0x2563EB)); // blue price, readable on white

				info.add(lblName);
				info.add(Box.createVerticalStrut(3));
				info.add(lblPrice);

				JButton btnPlus = new JButton("+") {
					@Override protected void paintComponent(Graphics g) {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g2.setColor(getModel().isRollover() ? new Color(0xC0392B) : POS_ACCENT);
						g2.fillOval(0, 0, getWidth(), getHeight());
						g2.setColor(Color.WHITE);
						g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
						FontMetrics fm = g2.getFontMetrics();
						String txt = "+";
						int x = (getWidth() - fm.stringWidth(txt)) / 2;
						int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
						g2.drawString(txt, x, y);
						g2.dispose();
					}
				};
				btnPlus.setPreferredSize(new Dimension(40, 40));
				btnPlus.setOpaque(false);
				btnPlus.setContentAreaFilled(false);
				btnPlus.setBorderPainted(false);
				btnPlus.setCursor(new Cursor(Cursor.HAND_CURSOR));
				btnPlus.setFocusPainted(false);
				// ★ BIND to add-to-cart logic (existing logic preserved)
				btnPlus.addActionListener(e -> themVaoGio.accept(mon));

				card.add(lblImg, BorderLayout.WEST);
				card.add(info,   BorderLayout.CENTER);
				card.add(btnPlus, BorderLayout.EAST);

				pnlMonList.add(card);
				pnlMonList.add(Box.createVerticalStrut(6));
			}
			pnlMonList.revalidate();
			pnlMonList.repaint();
		};

		// Initial render (all items)
		renderMonList.accept(dsMon);

		// ── Category filter buttons ───────────────────────────────────────────
		ButtonGroup catGroup = new ButtonGroup();
		String[] catNames = monTheoLoai.keySet().toArray(new String[0]);
		for (String cat : catNames) {
			JToggleButton btnCat = new JToggleButton(cat) {
				@Override protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					if (isSelected()) {
						g2.setColor(POS_ACCENT);
					} else {
						g2.setColor(POS_GRAY_BTN);
					}
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
					g2.setColor(isSelected() ? Color.WHITE : POS_SUBTEXT);
					g2.setFont(getFont());
					FontMetrics fm = g2.getFontMetrics();
					g2.drawString(getText(),
							(getWidth() - fm.stringWidth(getText())) / 2,
							(getHeight() + fm.getAscent() - fm.getDescent()) / 2);
					g2.dispose();
				}
			};
			btnCat.setFont(new Font("Segoe UI", Font.BOLD, 12));
			btnCat.setOpaque(false);
			btnCat.setContentAreaFilled(false);
			btnCat.setBorderPainted(false);
			btnCat.setFocusPainted(false);
			btnCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
			btnCat.setPreferredSize(new Dimension(Math.max(90, cat.length() * 9 + 20), 32));
			if (cat.equals("Tất cả")) btnCat.setSelected(true);

			// ★ BIND filter: calls renderMonList with filtered data
			btnCat.addActionListener(e -> renderMonList.accept(monTheoLoai.get(cat)));

			catGroup.add(btnCat);
			pnlCategories.add(btnCat);
		}

		// ── Search logic — calls renderMonList (fulfills requirement 3) ───────
		Runnable doSearch = () -> {
			String kw = txtSearch.getText().toLowerCase().trim();
			if (kw.isEmpty()) {
				renderMonList.accept(dsMon);
			} else {
				List<MonAn> filtered = new ArrayList<>();
				for (MonAn m : dsMon) {
					if (m.getTenMon().toLowerCase().contains(kw)) filtered.add(m);
				}
				renderMonList.accept(filtered);
			}
		};

		btnTimKiem.addActionListener(e -> doSearch.run());
		txtSearch.addActionListener(e -> doSearch.run());
		// Also live-filter as user types
		txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void insertUpdate(javax.swing.event.DocumentEvent e) { doSearch.run(); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { doSearch.run(); }
			public void changedUpdate(javax.swing.event.DocumentEvent e) { doSearch.run(); }
		});

		// ── Assemble LEFT panel ───────────────────────────────────────────────
		JPanel leftZone = new JPanel(new BorderLayout());
		leftZone.setBackground(POS_BG);
		leftZone.add(pnlCategories, BorderLayout.NORTH);
		leftZone.add(scrollMon,     BorderLayout.CENTER);

		JPanel rightZone = new JPanel(new BorderLayout(0, 0));
		rightZone.setBackground(Color.WHITE);
		rightZone.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, POS_DIVIDER));

		// Cart header
		JPanel cartHeader = new JPanel(new BorderLayout(8, 0));
		cartHeader.setBackground(POS_CARD);
		cartHeader.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, POS_DIVIDER),
				new EmptyBorder(12, 16, 12, 16)));
		JLabel lblCartTitle = new JLabel("Giỏ hàng");
		lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblCartTitle.setForeground(POS_TEXT);
		JLabel lblHint = new JLabel("(Double-click để giảm SL)");
		lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
		lblHint.setForeground(POS_SUBTEXT);
		cartHeader.add(lblCartTitle, BorderLayout.WEST);
		cartHeader.add(lblHint, BorderLayout.EAST);

		// Cart table in scroll
		JScrollPane scrollCart = new JScrollPane(tblCart);
		scrollCart.getViewport().setBackground(Color.WHITE);
		scrollCart.setBorder(null);
		scrollCart.getVerticalScrollBar().setUnitIncrement(12);

		// Cart bottom actions
		JPanel cartBottom = new JPanel(new BorderLayout(0, 8));
		cartBottom.setBackground(Color.WHITE);
		cartBottom.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, POS_DIVIDER),
				new EmptyBorder(10, 12, 14, 12)));

		// Total display
		JPanel totalRow = new JPanel(new BorderLayout());
		totalRow.setBackground(Color.WHITE);
		totalRow.add(lblTotal, BorderLayout.EAST);
		totalRow.setBorder(new EmptyBorder(0, 0, 8, 4));

		// ★ "Xóa tất cả" button — GRAY, bound to clear-cart logic
		JButton btnXoaTatCa = new JButton("Xóa tất cả") {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getModel().isRollover() ? new Color(0x7F8C8D) : POS_GRAY_BTN);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		btnXoaTatCa.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnXoaTatCa.setForeground(Color.WHITE);
		btnXoaTatCa.setOpaque(false);
		btnXoaTatCa.setContentAreaFilled(false);
		btnXoaTatCa.setBorderPainted(false);
		btnXoaTatCa.setFocusPainted(false);
		btnXoaTatCa.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnXoaTatCa.setPreferredSize(new Dimension(0, 46));
		// ★ ActionListener — xóa toàn bộ giỏ hàng
		btnXoaTatCa.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(dlg, "Xóa toàn bộ món đã chọn?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				modelTam.setRowCount(0);
			}
		});

		// ★ "Xác nhận gọi món" button — GREEN, bound to save-to-DB logic
		JButton btnXacNhan = new JButton("Xác nhận gọi món") {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getModel().isRollover() ? new Color(0x15803D) : POS_GREEN);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnXacNhan.setForeground(Color.WHITE);
		btnXacNhan.setOpaque(false);
		btnXacNhan.setContentAreaFilled(false);
		btnXacNhan.setBorderPainted(false);
		btnXacNhan.setFocusPainted(false);
		btnXacNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnXacNhan.setPreferredSize(new Dimension(0, 52));
		// ★ ActionListener — lưu vào DB (same logic as old OK button)
		btnXacNhan.addActionListener(e -> {
			if (modelTam.getRowCount() == 0) {
				JOptionPane.showMessageDialog(dlg, "Bạn chưa thêm món nào vào giỏ hàng.");
				return;
			}
			dlg.dispose();
		});

		// Also allow removing a selected row from cart
		JButton btnXoaDong = new JButton("Xóa dòng đã chọn");
		btnXoaDong.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnXoaDong.setForeground(new Color(0x6B7280));
		btnXoaDong.setOpaque(false);
		btnXoaDong.setContentAreaFilled(false);
		btnXoaDong.setBorderPainted(false);
		btnXoaDong.setFocusPainted(false);
		btnXoaDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnXoaDong.addActionListener(e -> {
			int row = tblCart.getSelectedRow();
			if (row >= 0) modelTam.removeRow(row);
		});

		JPanel btnRow = new JPanel(new GridLayout(2, 1, 0, 8));
		btnRow.setBackground(Color.WHITE);
		btnRow.add(btnXoaTatCa);
		btnRow.add(btnXacNhan);

		cartBottom.add(totalRow, BorderLayout.NORTH);
		cartBottom.add(btnRow, BorderLayout.CENTER);
		cartBottom.add(btnXoaDong, BorderLayout.SOUTH);

		rightZone.add(cartHeader,  BorderLayout.NORTH);
		rightZone.add(scrollCart,  BorderLayout.CENTER);
		rightZone.add(cartBottom,  BorderLayout.SOUTH);


		JSplitPane posSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftZone, rightZone);
		posSplit.setDividerSize(2);
		posSplit.setBorder(null);
		posSplit.setOpaque(false);
		posSplit.setResizeWeight(0.70);
		posSplit.setDividerLocation(0.70);


		dlgRoot.add(topZone,    BorderLayout.NORTH);
		dlgRoot.add(posSplit,   BorderLayout.CENTER);
		dlgRoot.add(searchBar,  BorderLayout.SOUTH);

		dlg.setContentPane(dlgRoot);

		dlg.setVisible(true);


		if (modelTam.getRowCount() == 0) {
			return false;
		}

		boolean allOk = true;

		for (int i = 0; i < modelTam.getRowCount(); i++) {
			String maMonAn = modelTam.getValueAt(i, 0).toString();
			int soLuong    = Integer.parseInt(modelTam.getValueAt(i, 2).toString());
			String ghiChu  = modelTam.getValueAt(i, 4) == null ? "" : modelTam.getValueAt(i, 4).toString();

			boolean ok = phucVuService.themMonMoiTachDong(ban.maHD, maMonAn, soLuong, ghiChu);
			if (!ok) {
				allOk = false;
				break;
			}
		}

		if (allOk) {
			return true;
		} else {
			JOptionPane.showMessageDialog(this, "Có lỗi khi thêm món vào hóa đơn!");
			return false;
		}
	}

	
	private static String formatTien(long so) {
		return NumberFormat.getInstance(new Locale("vi", "VN")).format(so);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			NhanVien nv = new NhanVien();
			nv.setHoTenNV("Nguyễn Văn A");
			new FrmPhucVu(nv).setVisible(true);
		});
	}
}
