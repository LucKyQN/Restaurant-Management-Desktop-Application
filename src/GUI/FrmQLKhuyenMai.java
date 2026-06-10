package GUI;

import DAO.KhuyenMaiDAO;
import Entity.KhuyenMai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class FrmQLKhuyenMai extends JPanel {

	private final KhuyenMaiDAO dao = new KhuyenMaiDAO();

	private static final Color RED_MAIN = new Color(220, 38, 38);
	private static final Color BG_MAIN = new Color(248, 248, 248);
	private static final Color TEXT_DARK = new Color(30, 30, 30);
	private static final Color TEXT_GRAY = new Color(110, 110, 110);
	private static final Color BORDER_CLR = new Color(230, 230, 230);

	private JTable tblKM;
	private DefaultTableModel model;
	private List<KhuyenMai> dsKM = new ArrayList<>();

	// Stat labels
	private JLabel lbDangChay, lbSapToi, lbLuotDung;

	public FrmQLKhuyenMai() {
		initUI();
		loadData();
	}

	private void initUI() {
		setLayout(new BorderLayout(0, 16));
		setBackground(BG_MAIN);
		setBorder(new EmptyBorder(20, 24, 24, 24));

		add(createTopBar(), BorderLayout.NORTH);
		add(createCenter(), BorderLayout.CENTER);
	}


	private JPanel createTopBar() {
		JPanel wrap = new JPanel(new BorderLayout(0, 16));
		wrap.setOpaque(false);

		JPanel topAction = new JPanel(new BorderLayout());
		topAction.setOpaque(false);

		JButton btnTao = createPrimaryButton("+ Tạo khuyến mãi mới");
		btnTao.addActionListener(e -> moDialogThem());

		topAction.add(Box.createHorizontalStrut(0), BorderLayout.WEST);
		topAction.add(btnTao, BorderLayout.EAST);

		JPanel statsRow = new JPanel(new GridLayout(1, 3, 14, 0));
		statsRow.setOpaque(false);
		statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

		lbDangChay = new JLabel("...");
		lbSapToi = new JLabel("...");
		lbLuotDung = new JLabel("...");

		statsRow.add(createStatCard("🏷", "Khuyến mãi đang chạy", lbDangChay, new Color(99, 102, 241)));
		statsRow.add(createStatCard("📅", "Khuyến mãi sắp tới", lbSapToi, new Color(34, 197, 94)));
		statsRow.add(createStatCard("👤", "Lượt sử dụng hôm nay", lbLuotDung, new Color(168, 85, 247)));

		wrap.add(topAction, BorderLayout.NORTH);
		wrap.add(statsRow, BorderLayout.SOUTH);
		return wrap;
	}

	private JPanel createStatCard(String icon, String label, JLabel valueLabel, Color color) {
		JPanel card = new JPanel(new BorderLayout(0, 6));
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
				new EmptyBorder(16, 18, 16, 18)));

		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		JPanel iconBox = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.dispose();
			}
		};
		iconBox.setPreferredSize(new Dimension(38, 38));
		iconBox.setOpaque(false);
		iconBox.setLayout(new GridBagLayout());
		JLabel lbIcon = new JLabel(icon);
		lbIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
		iconBox.add(lbIcon);
		top.add(iconBox, BorderLayout.WEST);

		JPanel bottom = new JPanel();
		bottom.setOpaque(false);
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		JLabel lbLabel = new JLabel(label);
		lbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lbLabel.setForeground(TEXT_GRAY);
		valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
		valueLabel.setForeground(TEXT_DARK);
		bottom.add(lbLabel);
		bottom.add(Box.createVerticalStrut(4));
		bottom.add(valueLabel);

		card.add(top, BorderLayout.NORTH);
		card.add(bottom, BorderLayout.SOUTH);
		return card;
	}


	private JPanel createCenter() {
		JPanel card = new JPanel(new BorderLayout(0, 12));
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true),
				new EmptyBorder(16, 16, 16, 16)));


		model = new DefaultTableModel(new String[] { "Tên khuyến mãi", "Mô tả", "Giá trị", "Thời gian", "Trạng thái",
				"Đã sử dụng", "Thao tác" }, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return c == 6;
			}
		};

		tblKM = new JTable(model);
		tblKM.setRowHeight(56);
		tblKM.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblKM.setShowGrid(false);
		tblKM.setIntercellSpacing(new Dimension(0, 0));
		tblKM.setSelectionBackground(new Color(254, 242, 242));
		tblKM.setFocusable(false);

		JTableHeader header = tblKM.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(249, 250, 251));
		header.setForeground(TEXT_GRAY);
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
		header.setPreferredSize(new Dimension(0, 44));
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

		tblKM.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
					int col) {
				JPanel p = new JPanel();
				p.setOpaque(true);
				p.setBackground(sel ? new Color(254, 242, 242) : Color.WHITE);
				p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
				p.setBorder(new EmptyBorder(8, 12, 8, 12));
				if (row < dsKM.size()) {
					KhuyenMai km = dsKM.get(row);
					JLabel lbNam = new JLabel(km.getTenKM() != null ? km.getTenKM() : "");
					lbNam.setFont(new Font("Segoe UI", Font.BOLD, 13));
					lbNam.setForeground(TEXT_DARK);
					JLabel lbDk = new JLabel(km.getDieuKienApDung() != null ? km.getDieuKienApDung() : "");
					lbDk.setFont(new Font("Segoe UI", Font.PLAIN, 11));
					lbDk.setForeground(TEXT_GRAY);
					p.add(lbNam);
					p.add(lbDk);
				}
				return p;
			}
		});

		tblKM.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
					int col) {
				super.getTableCellRendererComponent(t, val, sel, foc, row, col);
				setForeground(RED_MAIN);
				setFont(new Font("Segoe UI", Font.BOLD, 14));
				setBackground(sel ? new Color(254, 242, 242) : Color.WHITE);
				setBorder(new EmptyBorder(0, 12, 0, 12));
				return this;
			}
		});

		tblKM.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
					int col) {
				JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
				p.setBackground(sel ? new Color(254, 242, 242) : Color.WHITE);
				p.setBorder(new EmptyBorder(14, 12, 14, 12));
				String status = val != null ? val.toString() : "";
				JLabel badge = new JLabel(status);
				badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
				badge.setOpaque(true);
				badge.setBorder(new EmptyBorder(4, 10, 4, 10));
				switch (status) {
				case "Đang chạy" -> {
					badge.setBackground(new Color(220, 252, 231));
					badge.setForeground(new Color(22, 163, 74));
				}
				case "Sắp tới" -> {
					badge.setBackground(new Color(219, 234, 254));
					badge.setForeground(new Color(37, 99, 235));
				}
				default -> {
					badge.setBackground(new Color(243, 244, 246));
					badge.setForeground(TEXT_GRAY);
				}
				}
				p.add(badge);
				return p;
			}
		});

		tblKM.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());
		tblKM.getColumnModel().getColumn(6).setCellEditor(new ActionCellEditor());

		tblKM.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
					int col) {
				super.getTableCellRendererComponent(t, val, sel, foc, row, col);
				setBackground(sel ? new Color(254, 242, 242) : Color.WHITE);
				setFont(new Font("Segoe UI", Font.PLAIN, 13));
				setForeground(TEXT_DARK);
				setBorder(new EmptyBorder(0, 12, 0, 12));
				return this;
			}
		});


		int[] widths = { 220, 280, 80, 160, 100, 90, 80 };
		for (int i = 0; i < widths.length; i++)
			tblKM.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

		JScrollPane scroll = new JScrollPane(tblKM);
		scroll.setBorder(null);
		scroll.getViewport().setBackground(Color.WHITE);

		card.add(scroll, BorderLayout.CENTER);
		return card;
	}

	class ActionCellRenderer implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
				int col) {
			return buildActionPanel(row, null);
		}
	}

	class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
		@Override
		public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int row, int col) {
			return buildActionPanel(row, this);
		}

		@Override
		public Object getCellEditorValue() {
			return "";
		}

		public void stopCellEditingNow() {
			fireEditingStopped();
		}
	}

	private JPanel buildActionPanel(int row, ActionCellEditor editor) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 10));
		p.setBackground(Color.WHITE);

		JButton btnSua = new JButton("✎");
		btnSua.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnSua.setPreferredSize(new Dimension(32, 32));
		btnSua.setFocusPainted(false);
		btnSua.setBorderPainted(false);
		btnSua.setBackground(new Color(243, 244, 246));
		btnSua.setForeground(TEXT_DARK);
		btnSua.setCursor(new Cursor(Cursor.HAND_CURSOR));

		JButton btnXoa = new JButton("🗑");
		btnXoa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
		btnXoa.setPreferredSize(new Dimension(32, 32));
		btnXoa.setFocusPainted(false);
		btnXoa.setBorderPainted(false);
		btnXoa.setBackground(new Color(254, 226, 226));
		btnXoa.setForeground(RED_MAIN);
		btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));

		if (editor != null) {
			btnSua.addActionListener(e -> {
				editor.stopCellEditingNow();
				moDialogSua(row);
			});

			btnXoa.addActionListener(e -> {
				editor.stopCellEditingNow();
				xoaKhuyenMai(row);
			});
		}

		p.add(btnSua);
		p.add(btnXoa);
		return p;
	}


	private void loadData() {
		SwingWorker<Void, Void> w = new SwingWorker<>() {
			int dangChay, sapToi, luot;
			List<KhuyenMai> data;

			@Override
			protected Void doInBackground() {
				data = dao.getAllKhuyenMai();
				dangChay = dao.getSoDangChay();
				sapToi = dao.getSoSapToi();
				luot = dao.getLuotSuDungHomNay();
				return null;
			}

			@Override
			protected void done() {
				dsKM = data;
				lbDangChay.setText(String.valueOf(dangChay));
				lbSapToi.setText(String.valueOf(sapToi));
				lbLuotDung.setText(String.valueOf(luot));
				napBang();
			}
		};
		w.execute();
	}

	private void napBang() {
		model.setRowCount(0);
		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
		for (KhuyenMai km : dsKM) {

			String giaTriHD;
			if ("Tiền mặt".equalsIgnoreCase(km.getLoaiKM()))
				giaTriHD = String.format("%,.0fK", km.getGiaTriKM() / 1000).replace(",", ".");
			else
				giaTriHD = String.format("%.0f%%", km.getGiaTriKM());


			String thoiGian = (km.getNgayBatDau() != null ? sdf.format(km.getNgayBatDau()) : "?") + "\n→ "
					+ (km.getNgayKetThuc() != null ? sdf.format(km.getNgayKetThuc()) : "?");


			String tt;
			if (!km.isTrangThai())
				tt = "Đã kết thúc";
			else {
				Date now = new Date();
				tt = km.getNgayBatDau() != null && now.before(km.getNgayBatDau()) ? "Sắp tới" : "Đang chạy";
			}

			int luotDung = dao.getLuotSuDungCuaKhuyenMai(km.getMaKM());

			model.addRow(new Object[] { km.getTenKM(), km.getMoTaKM() != null ? km.getMoTaKM() : "", giaTriHD, thoiGian, tt, luotDung + " lần", "" });
		}
	}


	private void moDialogThem() {
		Window w = SwingUtilities.getWindowAncestor(this);
		Frame owner = w instanceof Frame ? (Frame) w : null;
		KhuyenMaiDialog dlg = new KhuyenMaiDialog(owner, dao, null);
		dlg.setVisible(true);
		if (dlg.isSucceeded())
			loadData();
	}

	private void moDialogSua(int row) {
		if (row < 0 || row >= dsKM.size())
			return;
		Window w = SwingUtilities.getWindowAncestor(this);
		Frame owner = w instanceof Frame ? (Frame) w : null;
		KhuyenMaiDialog dlg = new KhuyenMaiDialog(owner, dao, dsKM.get(row));
		dlg.setVisible(true);
		if (dlg.isSucceeded())
			loadData();
	}

	private void xoaKhuyenMai(int row) {
		if (row < 0 || row >= dsKM.size())
			return;
		KhuyenMai km = dsKM.get(row);
		int c = JOptionPane.showConfirmDialog(this, "Vô hiệu hóa khuyến mãi \"" + km.getTenKM() + "\"?", "Xác nhận",
				JOptionPane.YES_NO_OPTION);
		if (c == JOptionPane.YES_OPTION) {
			if (dao.xoaKhuyenMai(km.getMaKM()))
				loadData();
			else
				JOptionPane.showMessageDialog(this, "Xóa thất bại.");
		}
	}


	private JButton createPrimaryButton(String text) {
		JButton btn = new JButton(text);
		btn.setBackground(RED_MAIN);
		btn.setForeground(Color.WHITE);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setBorder(new EmptyBorder(10, 18, 10, 18));
		return btn;
	}


	static class KhuyenMaiDialog extends JDialog {
		private final KhuyenMaiDAO dao;
		private final KhuyenMai kmSua;
		private boolean succeeded = false;

		private JTextField txtMa, txtTen, txtMoTa, txtGiaTri, txtDieuKien;
		private JComboBox<String> cboLoai;
		private JSpinner spNgayBD, spNgayKT;
		private JCheckBox chkTrangThai;

		public KhuyenMaiDialog(Frame owner, KhuyenMaiDAO dao, KhuyenMai kmSua) {
			super(owner, kmSua == null ? "Tạo khuyến mãi mới" : "Sửa khuyến mãi", true);
			this.dao = dao;
			this.kmSua = kmSua;
			initUI();
			if (kmSua != null)
				doDuLieu();
			else
				txtMa.setText(dao.getMaKMTuDong());
			setSize(560, 480);
			setLocationRelativeTo(owner);
		}

		public boolean isSucceeded() {
			return succeeded;
		}

		private void initUI() {
			JPanel root = new JPanel(new BorderLayout(16, 16));
			root.setBorder(new EmptyBorder(20, 24, 20, 24));
			root.setBackground(Color.WHITE);

			JPanel fields = new JPanel(new GridLayout(0, 2, 12, 12));
			fields.setOpaque(false);

			txtMa = new JTextField();
			txtMa.setEditable(false);
			txtMa.setBackground(new Color(245, 245, 245));
			txtTen = new JTextField();
			txtMoTa = new JTextField();
			txtGiaTri = new JTextField();
			txtDieuKien = new JTextField();
			cboLoai = new JComboBox<>(new String[] { "Phần trăm", "Tiền mặt" });
			chkTrangThai = new JCheckBox("Kích hoạt");
			chkTrangThai.setSelected(true);
			chkTrangThai.setOpaque(false);

			// Date spinners
			SpinnerDateModel mdBD = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
			SpinnerDateModel mdKT = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
			spNgayBD = new JSpinner(mdBD);
			spNgayKT = new JSpinner(mdKT);
			JSpinner.DateEditor edBD = new JSpinner.DateEditor(spNgayBD, "dd/MM/yyyy");
			JSpinner.DateEditor edKT = new JSpinner.DateEditor(spNgayKT, "dd/MM/yyyy");
			spNgayBD.setEditor(edBD);
			spNgayKT.setEditor(edKT);

			fields.add(cf("Mã KM", txtMa));
			fields.add(cf("Tên khuyến mãi", txtTen));
			fields.add(cf("Mô tả", txtMoTa));
			fields.add(cf("Loại KM", cboLoai));
			fields.add(cf("Giá trị", txtGiaTri));
			fields.add(cf("Điều kiện áp dụng", txtDieuKien));
			fields.add(cf("Ngày bắt đầu", spNgayBD));
			fields.add(cf("Ngày kết thúc", spNgayKT));
			fields.add(cf("Trạng thái", chkTrangThai));

			JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
			btnRow.setOpaque(false);
			JButton btnHuy = new JButton("Hủy");
			JButton btnLuu = new JButton(kmSua == null ? "Tạo mới" : "Lưu");
			btnLuu.setBackground(new Color(220, 38, 38));
			btnLuu.setForeground(Color.WHITE);
			btnLuu.setFocusPainted(false);
			btnLuu.setBorderPainted(false);
			btnHuy.addActionListener(e -> dispose());
			btnLuu.addActionListener(e -> luu());
			btnRow.add(btnHuy);
			btnRow.add(btnLuu);

			root.add(fields, BorderLayout.CENTER);
			root.add(btnRow, BorderLayout.SOUTH);
			setContentPane(root);
		}

		private JPanel cf(String label, JComponent comp) {
			JPanel p = new JPanel(new BorderLayout(4, 4));
			p.setOpaque(false);
			JLabel lb = new JLabel(label);
			lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			p.add(lb, BorderLayout.NORTH);
			p.add(comp, BorderLayout.CENTER);
			return p;
		}

		private void doDuLieu() {
			txtMa.setText(kmSua.getMaKM());
			txtTen.setText(kmSua.getTenKM());
			txtMoTa.setText(kmSua.getMoTaKM() != null ? kmSua.getMoTaKM() : "");
			txtGiaTri.setText(String.valueOf((int) kmSua.getGiaTriKM()));
			txtDieuKien.setText(kmSua.getDieuKienApDung() != null ? kmSua.getDieuKienApDung() : "");
			cboLoai.setSelectedItem(kmSua.getLoaiKM() != null ? kmSua.getLoaiKM() : "Phần trăm");
			chkTrangThai.setSelected(kmSua.isTrangThai());
			if (kmSua.getNgayBatDau() != null)
				spNgayBD.setValue(kmSua.getNgayBatDau());
			if (kmSua.getNgayKetThuc() != null)
				spNgayKT.setValue(kmSua.getNgayKetThuc());
		}

		private void luu() {
			try {
				String ma = txtMa.getText().trim();
				String ten = txtTen.getText().trim();
				String giaText = txtGiaTri.getText().trim();
				if (ma.isEmpty() || ten.isEmpty() || giaText.isEmpty())
					throw new Exception("Vui lòng nhập đầy đủ thông tin bắt buộc.");
				double gia = Double.parseDouble(giaText);
				if (gia <= 0)
					throw new Exception("Giá trị phải lớn hơn 0.");

				KhuyenMai km = new KhuyenMai();
				km.setMaKM(ma);
				km.setTenKM(ten);
				km.setMoTaKM(txtMoTa.getText().trim());
				km.setGiaTriKM(gia);
				km.setLoaiKM((String) cboLoai.getSelectedItem());
				km.setDieuKienApDung(txtDieuKien.getText().trim());
				km.setNgayBatDau((Date) spNgayBD.getValue());
				km.setNgayKetThuc((Date) spNgayKT.getValue());
				km.setTrangThai(chkTrangThai.isSelected());

				boolean ok = kmSua == null ? dao.themKhuyenMai(km) : dao.suaKhuyenMai(km);
				if (ok) {
					JOptionPane.showMessageDialog(this,
							kmSua == null ? "Tạo khuyến mãi thành công" : "Cập nhật thành công!");
					succeeded = true;
					dispose();
				} else {
					JOptionPane.showMessageDialog(this, "Thao tác thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}