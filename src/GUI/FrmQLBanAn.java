package GUI;

import DAO.BanAnDAO;
import Entity.BanAn;
import Entity.LoaiBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class FrmQLBanAn extends JPanel {

	private final BanAnDAO dao = new BanAnDAO();
	private JTable tblBanAn;
	private DefaultTableModel model;
	private List<BanAn> dsBanAn = new ArrayList<>();

	public FrmQLBanAn() {
		initUI();
		loadTable();
	}

	private void initUI() {
		setLayout(new BorderLayout());
		setBackground(new Color(248, 248, 248));

		JPanel root = new JPanel(new BorderLayout(16, 16));
		root.setBackground(new Color(248, 248, 248));
		root.setBorder(new EmptyBorder(20, 24, 24, 24));

		root.add(createTopBar(), BorderLayout.NORTH);
		root.add(createTablePanel(), BorderLayout.CENTER);

		add(root, BorderLayout.CENTER);
	}

	// TOP BAR
	private JPanel createTopBar() {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
				new EmptyBorder(16, 16, 16, 16)));

		// Hàng trên: nút thêm/sửa/xóa
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		btnPanel.setOpaque(false);

		JButton btnThem = new JButton("Thêm");
		JButton btnSua = new JButton("Sửa");
		JButton btnXoaMem = new JButton("Xóa");

		btnThem.addActionListener(e -> moDialogThem());
		btnSua.addActionListener(e -> moDialogSua());
		btnXoaMem.addActionListener(e -> xoaMemBanAn());

		btnPanel.add(btnXoaMem);
		btnPanel.add(btnSua);
		btnPanel.add(btnThem);

		// Hàng dưới: bộ lọc
		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		filterPanel.setOpaque(false);

		JLabel lblLoc = new JLabel("Lọc:");
		lblLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));

		JComboBox<String> cboLocLoai = new JComboBox<>();
		cboLocLoai.addItem("Tất cả loại");
		for (Entity.LoaiBan lb : dao.getAllLoaiBan()) {
			cboLocLoai.addItem(lb.getTenLB());
		}
		cboLocLoai.setPreferredSize(new Dimension(160, 30));

		JComboBox<String> cboLocViTri = new JComboBox<>(
				new String[]{"Tất cả vị trí", "Tầng 1", "Tầng 2", "Phòng VIP"});
		cboLocViTri.setPreferredSize(new Dimension(140, 30));

		JComboBox<String> cboLocSucChua = new JComboBox<>(
				new String[]{"Tất cả sức chứa", "2", "4", "6", "8", "10", "12"});
		cboLocSucChua.setPreferredSize(new Dimension(150, 30));

		filterPanel.add(lblLoc);
		filterPanel.add(new JLabel("Loại bàn:"));
		filterPanel.add(cboLocLoai);
		filterPanel.add(new JLabel("Vị trí:"));
		filterPanel.add(cboLocViTri);
		filterPanel.add(new JLabel("Sức chứa:"));
		filterPanel.add(cboLocSucChua);

		// Listener lọc
		java.awt.event.ActionListener locListener = e -> {
			String loai = cboLocLoai.getSelectedItem().toString();
			String viTri = cboLocViTri.getSelectedItem().toString();
			String sucChuaStr = cboLocSucChua.getSelectedItem().toString();

			model.setRowCount(0);
			for (BanAn ban : dsBanAn) {
				boolean matchLoai = loai.equals("Tất cả loại") ||
						(ban.getLoaiBan() != null && ban.getLoaiBan().getTenLB().equals(loai));
				boolean matchViTri = viTri.equals("Tất cả vị trí") ||
						(ban.getViTri() != null && ban.getViTri().equalsIgnoreCase(viTri));
				boolean matchSuc = sucChuaStr.equals("Tất cả sức chứa") ||
						String.valueOf(ban.getSucChua()).equals(sucChuaStr);

				if (matchLoai && matchViTri && matchSuc) {
					model.addRow(new Object[]{
							ban.getMaBan(), ban.getTenBan(),
							ban.getLoaiBan() != null ? ban.getLoaiBan().getTenLB() : "",
							ban.getViTri(), ban.getSucChua(), ban.getTrangThai()
					});
				}
			}
		};

		cboLocLoai.addActionListener(locListener);
		cboLocViTri.addActionListener(locListener);
		cboLocSucChua.addActionListener(locListener);

		panel.add(btnPanel, BorderLayout.NORTH);
		panel.add(filterPanel, BorderLayout.SOUTH);

		return panel;
	}


	// BẢNG QUẢN LÝ BÀN
	private JPanel createTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(16, 16, 16, 16)));

		model = new DefaultTableModel(
				new String[] { "Mã bàn", "Tên bàn", "Loại bàn", "Vị trí", "Sức chứa", "Trạng thái" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblBanAn = new JTable(model);
		tblBanAn.setRowHeight(32);
		tblBanAn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblBanAn.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		tblBanAn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblBanAn.setGridColor(new Color(235, 235, 235));
		tblBanAn.setShowGrid(true);


		JLabel lblHint = new JLabel("  Nhấn đúp chuột vào một bàn để xem chi tiết");
		lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		lblHint.setForeground(new Color(150, 150, 150));
		lblHint.setBorder(new EmptyBorder(6, 0, 0, 0));

		// SỰ KIỆN: Đúp chuột mở Dialog chi tiết
		tblBanAn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					BanAn ban = getBanDangChon();
					if (ban != null)
						moDialogChiTiet(ban);
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(tblBanAn);
		scrollPane.setBorder(null);

		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(lblHint, BorderLayout.SOUTH);
		return panel;
	}

	// LOAD TABLE
	private void loadTable() {
		model.setRowCount(0);
		dsBanAn = dao.getDanhSachBanDangSuDung();

		for (BanAn ban : dsBanAn) {
			model.addRow(new Object[] { ban.getMaBan(), ban.getTenBan(),
					ban.getLoaiBan() != null ? ban.getLoaiBan().getTenLB() : "", ban.getViTri(), ban.getSucChua(),
					ban.getTrangThai() });
		}
	}

	private BanAn getBanDangChon() {
		int row = tblBanAn.getSelectedRow();
		if (row < 0 || row >= dsBanAn.size())
			return null;
		return dsBanAn.get(row);
	}

	// DIALOG CHI TIẾT (Đúp chuột)
	private void moDialogChiTiet(BanAn ban) {
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;

		JDialog dialog = new JDialog(owner, "Chi tiết bàn ăn", true);
		dialog.setSize(550, 380); // Kích thước gọn gàng
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);

		JPanel root = new JPanel(new BorderLayout(16, 16));
		root.setBackground(Color.WHITE);
		root.setBorder(new EmptyBorder(20, 24, 20, 24));

		JLabel lblTitle = new JLabel("Chi tiết bàn ăn");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTitle.setBorder(new EmptyBorder(0, 0, 8, 0));


		JPanel infoPanel = new JPanel(new GridLayout(3, 2, 16, 12));
		infoPanel.setOpaque(false);

		infoPanel.add(createInfoItem("Mã bàn:", ban.getMaBan() != null ? ban.getMaBan() : "-"));
		infoPanel.add(createInfoItem("Loại bàn:", ban.getLoaiBan() != null ? ban.getLoaiBan().getTenLB() : "-"));

		infoPanel.add(createInfoItem("Trạng thái:", ban.getTrangThai() != null ? ban.getTrangThai() : "-"));
		infoPanel.add(createInfoItem("Tên bàn:", ban.getTenBan() != null ? ban.getTenBan() : "-"));

		infoPanel.add(createInfoItem("Vị trí:", ban.getViTri() != null ? ban.getViTri() : "-"));
		infoPanel.add(createInfoItem("Sức chứa:", String.valueOf(ban.getSucChua())));


		JPanel textPanel = new JPanel(new BorderLayout(0, 6));
		textPanel.setOpaque(false);
		textPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

		JLabel lblMoTa = new JLabel("Mô tả:");
		lblMoTa.setFont(new Font("Segoe UI", Font.BOLD, 13));

		JTextArea txtMoTa = new JTextArea(ban.getMoTa() != null ? ban.getMoTa() : "");
		txtMoTa.setEditable(false);
		txtMoTa.setLineWrap(true);
		txtMoTa.setWrapStyleWord(true);
		txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		txtMoTa.setFocusable(false);

		txtMoTa.setOpaque(false);

		JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
		scrollMoTa.setPreferredSize(new Dimension(100, 70));
		scrollMoTa.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollMoTa.setBorder(null);

		textPanel.add(lblMoTa, BorderLayout.NORTH);
		textPanel.add(scrollMoTa, BorderLayout.CENTER);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);
		centerPanel.add(infoPanel, BorderLayout.NORTH);
		centerPanel.add(textPanel, BorderLayout.CENTER);

		// Nút đóng
		JButton btnDong = new JButton("Đóng");
		btnDong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btnDong.addActionListener(e -> dialog.dispose());
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPanel.setOpaque(false);
		btnPanel.add(btnDong);

		root.add(lblTitle, BorderLayout.NORTH);
		root.add(centerPanel, BorderLayout.CENTER);
		root.add(btnPanel, BorderLayout.SOUTH);

		dialog.setContentPane(root);
		dialog.setVisible(true);
	}

	private JPanel createInfoItem(String title, String value) {
		JPanel p = new JPanel(new BorderLayout(4, 4));
		p.setOpaque(false);
		JLabel lb = new JLabel(title);
		lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
		JLabel lv = new JLabel(value);
		lv.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		p.add(lb, BorderLayout.NORTH);
		p.add(lv, BorderLayout.CENTER);
		return p;
	}

	private void moDialogThem() {
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;
		BanAnDialog dialog = new BanAnDialog(owner, dao, null);
		dialog.setVisible(true);
		if (dialog.isSucceeded())
			loadTable();
	}

	private void moDialogSua() {
		BanAn ban = getBanDangChon();
		if (ban == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần sửa.");
			return;
		}
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;
		BanAnDialog dialog = new BanAnDialog(owner, dao, ban);
		dialog.setVisible(true);
		if (dialog.isSucceeded())
			loadTable();
	}

	private void xoaMemBanAn() {
		BanAn ban = getBanDangChon();
		if (ban == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần xóa mềm.");
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận ngưng sử dụng bàn " + ban.getMaBan() + "?",
				"Xóa mềm bàn", JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION)
			return;

		if (dao.xoaMemBanAn(ban.getMaBan())) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái ngưng sử dụng.");
			loadTable();
		} else {
			JOptionPane.showMessageDialog(this, "Xóa mềm thất bại.");
		}
	}

	static class BanAnDialog extends JDialog {

		private final BanAnDAO dao;
		private final BanAn banSua;
		private boolean succeeded = false;

		private JTextField txtMaBan;
		private JTextField txtTenBan;
		private JComboBox<LoaiBan> cboLoaiBan;
		private JComboBox<String> cboTrangThai;

		private JComboBox<String> cboViTri;
		private JComboBox<String> cboSucChua;

		private JTextArea txtMoTa;

		public BanAnDialog(Frame owner, BanAnDAO dao, BanAn banSua) {
			super(owner, true);
			this.dao = dao;
			this.banSua = banSua;

			setTitle(banSua == null ? "Thêm bàn ăn" : "Sửa bàn ăn");
			initUI();
			if (banSua != null) {
				doDuLieuLenForm();
			} else {
				txtMaBan.setText(dao.taoMaBanTuDong());
			}

			pack();
			setSize(800, 420);
			setLocationRelativeTo(owner);
		}

		public boolean isSucceeded() {
			return succeeded;
		}

		private void initUI() {
			JPanel root = new JPanel(new BorderLayout(16, 16));
			root.setBorder(new EmptyBorder(20, 20, 20, 20));
			root.setBackground(Color.WHITE);

			JPanel fields = new JPanel(new GridLayout(3, 3, 12, 12));
			fields.setOpaque(false);

			txtMaBan = new JTextField();
			txtMaBan.setEditable(false);
			txtMaBan.setBackground(new Color(245, 245, 245));
			txtTenBan = new JTextField();
			cboLoaiBan = new JComboBox<>();
			loadLoaiBanToCombo();
			cboLoaiBan.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (value instanceof LoaiBan lb) {
						setText(lb.getMaLB() + " - " + lb.getTenLB() + " (" + lb.getSoGhe() + " ghế)");
					} else {
						setText("");
					}
					return this;
				}
			});

			cboTrangThai = new JComboBox<>(new String[] { "Trống", "Có khách", "Đã đặt", "Ngưng sử dụng" });
			if (banSua == null) {

				cboTrangThai.setSelectedItem("Trống");
				cboTrangThai.setEnabled(false);
			} else {

				String ttHienTai = banSua.getTrangThai();

				if (ttHienTai.equals("Có khách") || ttHienTai.equals("Đã đặt")) {

					cboTrangThai.removeAllItems();
					cboTrangThai.addItem(ttHienTai);
					cboTrangThai.setEnabled(false);
				} else {
					cboTrangThai.setSelectedItem(ttHienTai);
					cboTrangThai.setEnabled(true);
				}
			}
			cboViTri = new JComboBox<>(new String[] { "Tầng 1", "Tầng 2", "Phòng VIP" });
			cboViTri.setEditable(true);

			cboSucChua = new JComboBox<>(new String[] { "2", "4", "6", "8", "10", "12" });
			cboSucChua.setEditable(true);

			txtMoTa = new JTextArea(4, 20);
			txtMoTa.setLineWrap(true);
			txtMoTa.setWrapStyleWord(true);

			fields.add(createField("Mã bàn", txtMaBan));
			fields.add(createField("Loại bàn", cboLoaiBan));
			fields.add(createField("Trạng thái", cboTrangThai));

			fields.add(createField("Tên bàn", txtTenBan));
			fields.add(createField("Vị trí", cboViTri));
			fields.add(createField("Sức chứa", cboSucChua));

			JPanel moTaPanel = new JPanel(new BorderLayout(6, 6));
			moTaPanel.setOpaque(false);
			JLabel lbMoTa = new JLabel("Mô tả");
			lbMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));

			JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
			scrollMoTa.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

			moTaPanel.add(lbMoTa, BorderLayout.NORTH);
			moTaPanel.add(scrollMoTa, BorderLayout.CENTER);

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
			btnPanel.setOpaque(false);

			JButton btnHuy = new JButton("Hủy");
			JButton btnLuu = new JButton(banSua == null ? "Thêm" : "Lưu");

			btnHuy.addActionListener(e -> dispose());
			btnLuu.addActionListener(e -> luuBan());

			btnPanel.add(btnHuy);
			btnPanel.add(btnLuu);

			root.add(fields, BorderLayout.NORTH);
			root.add(moTaPanel, BorderLayout.CENTER);
			root.add(btnPanel, BorderLayout.SOUTH);

			setContentPane(root);
		}

		private JPanel createField(String label, JComponent comp) {
			JPanel p = new JPanel(new BorderLayout(6, 6));
			p.setOpaque(false);
			JLabel lb = new JLabel(label);
			lb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			p.add(lb, BorderLayout.NORTH);
			p.add(comp, BorderLayout.CENTER);
			return p;
		}

		private void loadLoaiBanToCombo() {
			List<LoaiBan> dsLoaiBan = dao.getAllLoaiBan();
			DefaultComboBoxModel<LoaiBan> model = new DefaultComboBoxModel<>();
			for (LoaiBan lb : dsLoaiBan)
				model.addElement(lb);
			cboLoaiBan.setModel(model);
		}

		private void doDuLieuLenForm() {
			txtMaBan.setText(banSua.getMaBan());
			txtMaBan.setEditable(false);

			txtTenBan.setText(banSua.getTenBan() != null ? banSua.getTenBan() : "");
			cboTrangThai.setSelectedItem(banSua.getTrangThai());

			if (banSua.getViTri() != null)
				cboViTri.setSelectedItem(banSua.getViTri());
			cboSucChua.setSelectedItem(String.valueOf(banSua.getSucChua()));

			txtMoTa.setText(banSua.getMoTa() != null ? banSua.getMoTa() : "");
			if (banSua.getLoaiBan() != null) {
				for (int i = 0; i < cboLoaiBan.getItemCount(); i++) {
					if (cboLoaiBan.getItemAt(i).getMaLB().equals(banSua.getLoaiBan().getMaLB())) {
						cboLoaiBan.setSelectedIndex(i);
						break;
					}
				}
			}
		}

		private void luuBan() {
			try {
				BanAn ban = layDuLieuForm();

				if (banSua == null) {
					if (dao.tonTaiMaBan(ban.getMaBan())) {
						JOptionPane.showMessageDialog(this, "Mã bàn đã tồn tại.");
						return;
					}

					if (dao.themBanAn(ban)) {
						JOptionPane.showMessageDialog(this, "Thêm bàn ăn thành công.");
						succeeded = true;
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Thêm bàn ăn thất bại.");
					}
				} else {
					if (dao.suaBanAn(ban)) {
						JOptionPane.showMessageDialog(this, "Cập nhật bàn ăn thành công.");
						succeeded = true;
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Cập nhật bàn ăn thất bại.");
					}
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}

		private BanAn layDuLieuForm() throws Exception {
			String maBan = txtMaBan.getText().trim();
			String tenBan = txtTenBan.getText().trim();
			LoaiBan loaiBan = (LoaiBan) cboLoaiBan.getSelectedItem();
			String trangThai = cboTrangThai.getSelectedItem().toString();

			String viTri = layGiaTriCombo(cboViTri);
			String sucChuaText = layGiaTriCombo(cboSucChua);

			if (maBan.isEmpty() || tenBan.isEmpty() || loaiBan == null || viTri.isEmpty() || sucChuaText.isEmpty()) {
				throw new Exception("Vui lòng nhập đầy đủ thông tin bắt buộc.");
			}

			int sucChua;
			try {
				sucChua = Integer.parseInt(sucChuaText);
			} catch (Exception e) {
				throw new Exception("Sức chứa phải là số nguyên.");
			}

			if (sucChua <= 0) {
				throw new Exception("Sức chứa phải lớn hơn 0.");
			}

			BanAn ban = new BanAn();
			ban.setMaBan(maBan);
			ban.setTenBan(tenBan);
			ban.setLoaiBan(loaiBan);
			ban.setTrangThai(trangThai);
			ban.setViTri(viTri);
			ban.setSucChua(sucChua);
			ban.setMoTa(txtMoTa.getText().trim());

			return ban;
		}

		private String layGiaTriCombo(JComboBox<String> cbo) {
			Object value = cbo.isEditable() ? cbo.getEditor().getItem() : cbo.getSelectedItem();
			return value != null ? value.toString().trim() : "";
		}
	}
}