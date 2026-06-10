package GUI;

import DAO.MonAnDAO;
import Entity.DanhMuc;
import Entity.MonAn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class FrmQLMonAn extends JPanel {

	private final MonAnDAO dao = new MonAnDAO();

	private JTable tblMonAn;
	private DefaultTableModel model;
	private List<MonAn> dsMonAn = new ArrayList<>();

	public FrmQLMonAn() {
		initUI();
		loadTable();
	}

	private void initUI() {
		setLayout(new BorderLayout());
		setBackground(UIConstants.BG_APP);

		JPanel root = new JPanel(new BorderLayout(16, 16));
		root.setBackground(UIConstants.BG_APP);
		root.setBorder(new EmptyBorder(20, 24, 24, 24));

		root.add(createTopBar(), BorderLayout.NORTH);
		root.add(createTablePanel(), BorderLayout.CENTER);

		add(root, BorderLayout.CENTER);
	}

	private JPanel createTopBar() {
		RoundedPanel panel = new RoundedPanel(12, true, UIConstants.CARD_BG);
		panel.setLayout(new BorderLayout(0, 10));
		panel.setBorder(new EmptyBorder(16, 24, 16, 24));

		// Hàng trên: nút
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		btnPanel.setOpaque(false);

		ModernButton btnThem = new ModernButton("+ Thêm món", ModernButton.Style.PRIMARY);
		btnThem.setPreferredSize(new Dimension(120, 36));
		ModernButton btnSua = new ModernButton("Sửa", ModernButton.Style.OUTLINE);
		btnSua.setPreferredSize(new Dimension(90, 36));
		ModernButton btnXoaMem = new ModernButton("Ngừng bán", ModernButton.Style.GHOST);
		btnXoaMem.setPreferredSize(new Dimension(110, 36));

		btnThem.addActionListener(e -> moDialogThem());
		btnSua.addActionListener(e -> moDialogSua());
		btnXoaMem.addActionListener(e -> xoaMemMonAn());

		btnPanel.add(btnXoaMem);
		btnPanel.add(btnSua);
		btnPanel.add(btnThem);

		// Hàng dưới: bộ lọc
		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		filterPanel.setOpaque(false);

		JLabel lblLoc = new JLabel("Lọc:");
		lblLoc.setFont(UIConstants.FONT_BOLD_13);

		JComboBox<String> cboLocDanhMuc = new JComboBox<>(
				new String[]{"Tất cả danh mục", "Món khai vị", "Món chính", "Đồ uống", "Tráng miệng"});
		cboLocDanhMuc.setPreferredSize(new Dimension(160, 30));

		JComboBox<String> cboLocTrangThai = new JComboBox<>(
				new String[]{"Tất cả trạng thái", "Đang bán", "Ngừng bán"});
		cboLocTrangThai.setPreferredSize(new Dimension(150, 30));

		filterPanel.add(lblLoc);
		filterPanel.add(new JLabel("Danh mục:"));
		filterPanel.add(cboLocDanhMuc);
		filterPanel.add(new JLabel("Trạng thái:"));
		filterPanel.add(cboLocTrangThai);

		java.awt.event.ActionListener locListener = e -> {
			String danhMuc = cboLocDanhMuc.getSelectedItem().toString();
			String trangThai = cboLocTrangThai.getSelectedItem().toString();

			model.setRowCount(0);
			for (MonAn mon : dsMonAn) {
				String tenDM = mon.getDanhMuc() != null ? mon.getDanhMuc().getTenDM() : "";
				String tt = mon.isTinhTrang() ? "Đang bán" : "Ngừng bán";

				boolean matchDM = danhMuc.equals("Tất cả danh mục") || tenDM.equals(danhMuc);
				boolean matchTT = trangThai.equals("Tất cả trạng thái") || tt.equals(trangThai);

				if (matchDM && matchTT) {
					model.addRow(new Object[]{
							mon.getMaMonAn(), mon.getTenMon(), tenDM,
							mon.getDonVi(), mon.getSoLuong(),
							String.format("%,.0f", mon.getGiaMon()).replace(",", "."),
							tt
					});
				}
			}
		};

		cboLocDanhMuc.addActionListener(locListener);
		cboLocTrangThai.addActionListener(locListener);

		panel.add(btnPanel, BorderLayout.NORTH);
		panel.add(filterPanel, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createTablePanel() {
		RoundedPanel panel = new RoundedPanel(12, true, UIConstants.CARD_BG);
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(16, 16, 16, 16));

		model = new DefaultTableModel(
				new String[] { "Mã món", "Tên món", "Danh Mục", "Đơn vị", "Tồn kho", "Giá bán", "Trạng thái" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblMonAn = new JTable(model);
		tblMonAn.setRowHeight(40);
		tblMonAn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblMonAn.setFont(UIConstants.FONT_PLAIN_14);
		tblMonAn.setForeground(UIConstants.TEXT_DARK);
		tblMonAn.setGridColor(UIConstants.BORDER);
		tblMonAn.setShowVerticalLines(false);
		tblMonAn.setIntercellSpacing(new Dimension(0, 0));

		JTableHeader header = tblMonAn.getTableHeader();
		header.setFont(UIConstants.FONT_BOLD_14);
		header.setBackground(new Color(249, 250, 251)); // Màu nền xám nhạt cho Header
		header.setForeground(UIConstants.TEXT_GRAY);
		header.setPreferredSize(new Dimension(100, 40));
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

		JLabel lblHint = new JLabel("Nhấn đúp chuột vào một món để xem chi tiết");
		lblHint.setFont(UIConstants.FONT_ITALIC_13);
		lblHint.setForeground(UIConstants.TEXT_PLACEHOLDER);
		lblHint.setBorder(new EmptyBorder(12, 4, 0, 0));

		tblMonAn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					MonAn mon = getMonAnDangChon();
					if (mon != null)
						moDialogChiTiet(mon);
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(tblMonAn);
		scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
		scrollPane.getViewport().setBackground(UIConstants.CARD_BG);

		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(lblHint, BorderLayout.SOUTH);
		return panel;
	}

	private void moDialogChiTiet(MonAn mon) {
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;

		JDialog dialog = new JDialog(owner, "Chi tiết món ăn", true);
		dialog.setSize(750, 520);
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);
		dialog.getContentPane().setBackground(UIConstants.BG_APP);

		RoundedPanel root = new RoundedPanel(16, false, UIConstants.CARD_BG);
		root.setLayout(new BorderLayout(24, 24));
		root.setBorder(new EmptyBorder(24, 30, 24, 30));

		JLabel lblTitle = new JLabel("Chi tiết món ăn");
		lblTitle.setFont(UIConstants.FONT_BOLD_20);
		lblTitle.setForeground(UIConstants.TEXT_DARK);
		lblTitle.setBorder(new EmptyBorder(0, 0, 8, 0));

		JPanel infoPanel = new JPanel(new GridLayout(5, 2, 16, 12));
		infoPanel.setOpaque(false);

		infoPanel.add(createInfoItem("Mã món:", mon.getMaMonAn() != null ? mon.getMaMonAn() : "-"));
		infoPanel.add(createInfoItem("Tên món:", mon.getTenMon() != null ? mon.getTenMon() : "-"));
		infoPanel.add(createInfoItem("Danh mục:", mon.getDanhMuc() != null ? mon.getDanhMuc().getTenDM() : "-"));
		infoPanel.add(createInfoItem("Đơn vị:", mon.getDonVi() != null ? mon.getDonVi() : "-"));
		infoPanel.add(createInfoItem("Tồn kho:", String.valueOf(mon.getSoLuong())));

		JLabel lblGia = new JLabel(String.format("%,.0f đ", mon.getGiaMon()).replace(",", "."));
		lblGia.setFont(UIConstants.FONT_BOLD_14);
		lblGia.setForeground(new Color(0x16A34A));
		JPanel pGia = new JPanel(new BorderLayout(4, 2)); pGia.setOpaque(false);
		JLabel titleGia = new JLabel("Giá bán:"); titleGia.setFont(UIConstants.FONT_BOLD_13); titleGia.setForeground(UIConstants.TEXT_GRAY);
		pGia.add(titleGia, BorderLayout.NORTH); pGia.add(lblGia, BorderLayout.CENTER);
		infoPanel.add(pGia);

		infoPanel.add(createInfoItem("Trạng thái:", mon.isTinhTrang() ? "Đang bán" : "Ngừng bán"));
		infoPanel.add(new JPanel() {{ setOpaque(false); }});

		JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 16));
		textPanel.setOpaque(false);

		JTextArea txtMoTa = new JTextArea(mon.getMoTa() != null ? mon.getMoTa() : "");
		txtMoTa.setEditable(false); txtMoTa.setLineWrap(true); txtMoTa.setWrapStyleWord(true);
		txtMoTa.setFont(UIConstants.FONT_PLAIN_14); txtMoTa.setBackground(new Color(249, 250, 251));
		textPanel.add(createTextAreaItem("Mô tả:", txtMoTa));

		JTextArea txtGhiChu = new JTextArea(mon.getGhiChu() != null ? mon.getGhiChu() : "");
		txtGhiChu.setEditable(false); txtGhiChu.setLineWrap(true); txtGhiChu.setWrapStyleWord(true);
		txtGhiChu.setFont(UIConstants.FONT_PLAIN_14); txtGhiChu.setBackground(new Color(249, 250, 251));
		textPanel.add(createTextAreaItem("Ghi chú:", txtGhiChu));

		JPanel leftPanel = new JPanel(new BorderLayout(0, 16));
		leftPanel.setOpaque(false);
		leftPanel.add(infoPanel, BorderLayout.NORTH);
		leftPanel.add(textPanel, BorderLayout.CENTER);

		JLabel lblAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
		lblAnh.setPreferredSize(new Dimension(220, 220));
		lblAnh.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_INPUT));
		lblAnh.setOpaque(true);
		lblAnh.setBackground(new Color(249, 250, 251));
		lblAnh.setFont(UIConstants.FONT_PLAIN_13);
		lblAnh.setForeground(UIConstants.TEXT_PLACEHOLDER);
		hienThiAnh(lblAnh, mon.getAnhMon(), 220, 220);

		ModernButton btnDong = new ModernButton("Đóng", ModernButton.Style.OUTLINE);
		btnDong.setPreferredSize(new Dimension(90, 36));
		btnDong.addActionListener(e -> dialog.dispose());
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPanel.setOpaque(false);
		btnPanel.add(btnDong);

		JPanel wrapAnhPanel = new JPanel(new GridBagLayout());
		wrapAnhPanel.setOpaque(false);
		wrapAnhPanel.add(lblAnh);

		root.add(lblTitle, BorderLayout.NORTH);
		root.add(leftPanel, BorderLayout.CENTER);
		root.add(wrapAnhPanel, BorderLayout.EAST);
		root.add(btnPanel, BorderLayout.SOUTH);

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(UIConstants.BG_APP);
		wrapper.setBorder(new EmptyBorder(16, 16, 16, 16));
		wrapper.add(root, BorderLayout.CENTER);

		dialog.setContentPane(wrapper);
		dialog.setVisible(true);
	}

	private JPanel createInfoItem(String title, String value) {
		JPanel p = new JPanel(new BorderLayout(4, 2));
		p.setOpaque(false);
		JLabel lb = new JLabel(title);
		lb.setFont(UIConstants.FONT_BOLD_13);
		lb.setForeground(UIConstants.TEXT_GRAY);
		JLabel lv = new JLabel(value);
		lv.setFont(UIConstants.FONT_PLAIN_14);
		lv.setForeground(UIConstants.TEXT_DARK);
		p.add(lb, BorderLayout.NORTH);
		p.add(lv, BorderLayout.CENTER);
		return p;
	}

	private JPanel createTextAreaItem(String title, JTextArea area) {
		JPanel p = new JPanel(new BorderLayout(4, 4));
		p.setOpaque(false);
		JLabel lb = new JLabel(title);
		lb.setFont(UIConstants.FONT_BOLD_13);
		lb.setForeground(UIConstants.TEXT_GRAY);
		JScrollPane sp = new JScrollPane(area);
		sp.setPreferredSize(new Dimension(100, 55));
		sp.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_INPUT));
		p.add(lb, BorderLayout.NORTH);
		p.add(sp, BorderLayout.CENTER);
		return p;
	}

	private void hienThiAnh(JLabel label, String imagePath, int width, int height) {
		label.setIcon(null);
		if (imagePath == null || imagePath.trim().isEmpty()) {
			label.setText("Chưa có ảnh");
			return;
		}
		File file = new File(imagePath);
		if (!file.exists()) {
			label.setText("Không tìm thấy ảnh");
			return;
		}
		ImageIcon icon = new ImageIcon(imagePath);
		Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		label.setText("");
		label.setIcon(new ImageIcon(img));
	}

	private void loadTable() {
		model.setRowCount(0);
		dsMonAn = dao.getAllMonAn();
		for (MonAn mon : dsMonAn) {
			model.addRow(new Object[] { mon.getMaMonAn(), mon.getTenMon(),
					mon.getDanhMuc() != null ? mon.getDanhMuc().getTenDM() : "", mon.getDonVi(), mon.getSoLuong(),
					String.format("%,.0f", mon.getGiaMon()).replace(",", "."),
					mon.isTinhTrang() ? "Đang bán" : "Ngừng bán" });
		}
	}

	public void lamMoiDuLieuMenu() {
		loadTable();
	}

	private MonAn getMonAnDangChon() {
		int row = tblMonAn.getSelectedRow();
		if (row < 0 || row >= dsMonAn.size())
			return null;
		return dsMonAn.get(row);
	}

	private void moDialogThem() {
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;
		MonAnDialog dialog = new MonAnDialog(owner, dao, null);
		dialog.setVisible(true);
		if (dialog.isSucceeded())
			loadTable();
	}

	private void moDialogSua() {
		MonAn mon = getMonAnDangChon();
		if (mon == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần sửa.");
			return;
		}
		Window window = SwingUtilities.getWindowAncestor(this);
		Frame owner = window instanceof Frame ? (Frame) window : null;
		MonAnDialog dialog = new MonAnDialog(owner, dao, mon);
		dialog.setVisible(true);
		if (dialog.isSucceeded())
			loadTable();
	}

	private void xoaMemMonAn() {
		MonAn mon = getMonAnDangChon();
		if (mon == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần cập nhật trạng thái.");
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(this,
				"Xác nhận cập nhật trạng thái NGỪNG BÁN cho món " + mon.getMaMonAn() + " - " + mon.getTenMon() + "?", "Cập nhật",
				JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION)
			return;
		if (dao.xoaMemMonAn(mon.getMaMonAn())) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái ngừng bán.");
			loadTable();
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật thất bại.");
		}
	}



	static class MonAnDialog extends JDialog {

		private final MonAnDAO dao;
		private final MonAn monAnSua;
		private boolean succeeded = false;

		private JTextField txtMaMon, txtTenMon, txtSoLuongTon, txtGiaBan, txtAnhMon;
		private JTextArea txtMoTa, txtGhiChu;
		private JComboBox<DanhMuc> cboDanhMuc;
		private JComboBox<String> cboDonVi;
		private JCheckBox chkTinhTrang;
		private JLabel lblPreviewAnh;

		public MonAnDialog(Frame owner, MonAnDAO dao, MonAn monAnSua) {
			super(owner, true);
			this.dao = dao;
			this.monAnSua = monAnSua;
			setTitle(monAnSua == null ? "Thêm món ăn" : "Sửa món ăn");
			initUI();
			if (monAnSua != null)
				doDuLieuLenForm();
			else
				txtMaMon.setText(dao.getMaMonTuDong());
			pack();
			setSize(920, 600);
			setLocationRelativeTo(owner);
		}

		public boolean isSucceeded() {
			return succeeded;
		}

		private void initUI() {
			getContentPane().setBackground(UIConstants.BG_APP);

			RoundedPanel root = new RoundedPanel(16, false, UIConstants.CARD_BG);
			root.setLayout(new BorderLayout(16, 16));
			root.setBorder(new EmptyBorder(24, 24, 24, 24));

			JPanel topFields = new JPanel(new GridLayout(3, 3, 16, 12));
			topFields.setOpaque(false);

			txtMaMon = new JTextField();
			txtMaMon.setEditable(false);
			txtMaMon.setBackground(new Color(245, 245, 245));
			txtTenMon = new JTextField();

			cboDanhMuc = new JComboBox<>();
			loadDanhMucToCombo();
			cboDanhMuc.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
															  boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (value instanceof DanhMuc dm)
						setText(dm.getTenDM());
					else
						setText("");
					return this;
				}
			});

			cboDonVi = new JComboBox<>(new String[] { "Phần", "Dĩa", "Tô", "Ly", "Chai", "Cái", "Suất","Lon" });
			cboDonVi.setEditable(true);

			txtSoLuongTon = new JTextField();
			txtGiaBan = new JTextField();
			txtAnhMon = new JTextField();

			chkTinhTrang = new JCheckBox("Đang bán");
			chkTinhTrang.setSelected(true);
			chkTinhTrang.setOpaque(false);
			chkTinhTrang.setFont(UIConstants.FONT_BOLD_14);

			topFields.add(createField("Mã món", txtMaMon));
			topFields.add(createField("Tên món", txtTenMon));
			topFields.add(createField("Danh mục", cboDanhMuc));
			topFields.add(createField("Đơn vị", cboDonVi));
			topFields.add(createField("Số lượng tồn", txtSoLuongTon));
			topFields.add(createField("Giá bán (VNĐ)", txtGiaBan));
			topFields.add(createField("Tình trạng", chkTinhTrang));
			topFields.add(new JLabel());
			topFields.add(new JLabel());

			JPanel textAreasPanel = new JPanel(new GridLayout(2, 1, 12, 12));
			textAreasPanel.setOpaque(false);

			txtMoTa = new JTextArea(3, 20);
			txtMoTa.setLineWrap(true); txtMoTa.setWrapStyleWord(true);
			txtMoTa.setFont(UIConstants.FONT_PLAIN_14);

			txtGhiChu = new JTextArea(3, 20);
			txtGhiChu.setLineWrap(true); txtGhiChu.setWrapStyleWord(true);
			txtGhiChu.setFont(UIConstants.FONT_PLAIN_14);

			textAreasPanel.add(createTextAreaField("Mô tả món ăn", txtMoTa));
			textAreasPanel.add(createTextAreaField("Ghi chú nội bộ", txtGhiChu));

			JPanel fieldsContainer = new JPanel(new BorderLayout(0, 16));
			fieldsContainer.setOpaque(false);
			fieldsContainer.add(topFields, BorderLayout.NORTH);
			fieldsContainer.add(textAreasPanel, BorderLayout.CENTER);

			// NÚT CHỌN ẢNH XỊN XÒ VÀ HIỂN THỊ ẢNH
			ModernButton btnChonAnh = new ModernButton("Tải ảnh lên", ModernButton.Style.OUTLINE);
			btnChonAnh.setPreferredSize(new Dimension(110, 36));
			btnChonAnh.addActionListener(e -> chonAnh());

			JPanel anhPanel = new JPanel(new BorderLayout(8, 8));
			anhPanel.setOpaque(false);
			anhPanel.add(createField("Đường dẫn ảnh", txtAnhMon), BorderLayout.CENTER);
			anhPanel.add(btnChonAnh, BorderLayout.EAST);

			lblPreviewAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
			lblPreviewAnh.setPreferredSize(new Dimension(200, 160));
			lblPreviewAnh.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_INPUT));
			lblPreviewAnh.setOpaque(true);
			lblPreviewAnh.setBackground(new Color(249, 250, 251));
			lblPreviewAnh.setForeground(UIConstants.TEXT_PLACEHOLDER);

			JPanel center = new JPanel(new BorderLayout(24, 24));
			center.setOpaque(false);
			center.add(fieldsContainer, BorderLayout.CENTER);
			center.add(anhPanel, BorderLayout.SOUTH);

			JPanel imageWrap = new JPanel(new BorderLayout());
			imageWrap.setOpaque(false);
			imageWrap.add(lblPreviewAnh, BorderLayout.NORTH);

			// Nút lưu / hủy
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
			btnPanel.setOpaque(false);
			btnPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
			ModernButton btnHuy = new ModernButton("Hủy bỏ", ModernButton.Style.GHOST);
			ModernButton btnLuu = new ModernButton(monAnSua == null ? "Lưu món mới" : "Cập nhật", ModernButton.Style.PRIMARY);
			btnHuy.setPreferredSize(new Dimension(90, 36));
			btnLuu.setPreferredSize(new Dimension(140, 36));

			btnHuy.addActionListener(e -> dispose());
			btnLuu.addActionListener(e -> luuMonAn());

			btnPanel.add(btnHuy);
			btnPanel.add(btnLuu);

			root.add(center, BorderLayout.CENTER);
			root.add(imageWrap, BorderLayout.EAST);
			root.add(btnPanel, BorderLayout.SOUTH);

			JPanel wrapper = new JPanel(new BorderLayout());
			wrapper.setBackground(UIConstants.BG_APP);
			wrapper.setBorder(new EmptyBorder(16, 16, 16, 16));
			wrapper.add(root, BorderLayout.CENTER);

			setContentPane(wrapper);
		}

		private JPanel createTextAreaField(String label, JTextArea area) {
			JPanel p = new JPanel(new BorderLayout(6, 6));
			p.setOpaque(false);
			JLabel lb = new JLabel(label);
			lb.setFont(UIConstants.FONT_BOLD_13);
			lb.setForeground(UIConstants.TEXT_GRAY);
			p.add(lb, BorderLayout.NORTH);
			JScrollPane sp = new JScrollPane(area);
			sp.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_INPUT));
			sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			p.add(sp, BorderLayout.CENTER);
			return p;
		}

		private JPanel createField(String label, JComponent comp) {
			JPanel p = new JPanel(new BorderLayout(6, 6));
			p.setOpaque(false);
			JLabel lb = new JLabel(label);
			lb.setFont(UIConstants.FONT_BOLD_13);
			lb.setForeground(UIConstants.TEXT_GRAY);

			if (comp instanceof JTextField) {
				((JTextField) comp).setPreferredSize(new Dimension(0, 32));
				((JTextField) comp).setFont(UIConstants.FONT_PLAIN_14);
				comp.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(UIConstants.BORDER_INPUT),
						new EmptyBorder(0, 8, 0, 8)
				));
			} else if (comp instanceof JComboBox) {
				((JComboBox<?>) comp).setPreferredSize(new Dimension(0, 32));
				comp.setFont(UIConstants.FONT_PLAIN_14);
			}

			p.add(lb, BorderLayout.NORTH);
			p.add(comp, BorderLayout.CENTER);
			return p;
		}

		// LOGIC CHỌN VÀ HIỂN THỊ ẢNH GIỮ NGUYÊN HOÀN TOÀN
		private void chonAnh() {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Chọn ảnh món ăn");
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				txtAnhMon.setText(file.getAbsolutePath());
				hienThiPreviewAnh(file.getAbsolutePath());
			}
		}

		private void hienThiPreviewAnh(String imagePath) {
			lblPreviewAnh.setIcon(null);
			if (imagePath == null || imagePath.trim().isEmpty()) {
				lblPreviewAnh.setText("Chưa có ảnh");
				return;
			}
			File file = new File(imagePath);
			if (!file.exists()) {
				lblPreviewAnh.setText("Không tìm thấy ảnh");
				return;
			}
			ImageIcon icon = new ImageIcon(imagePath);
			Image img = icon.getImage().getScaledInstance(200, 160, Image.SCALE_SMOOTH);
			lblPreviewAnh.setText("");
			lblPreviewAnh.setIcon(new ImageIcon(img));
		}

		private void loadDanhMucToCombo() {
			List<DanhMuc> dsDanhMuc = dao.getAllDanhMuc();
			DefaultComboBoxModel<DanhMuc> m = new DefaultComboBoxModel<>();
			for (DanhMuc dm : dsDanhMuc)
				m.addElement(dm);
			cboDanhMuc.setModel(m);
		}

		private void doDuLieuLenForm() {
			txtMaMon.setText(monAnSua.getMaMonAn());
			txtTenMon.setText(monAnSua.getTenMon());
			txtSoLuongTon.setText(String.valueOf(monAnSua.getSoLuong()));
			txtGiaBan.setText(String.valueOf((long) monAnSua.getGiaMon()));
			txtMoTa.setText(monAnSua.getMoTa() != null ? monAnSua.getMoTa() : "");
			txtGhiChu.setText(monAnSua.getGhiChu() != null ? monAnSua.getGhiChu() : "");
			txtAnhMon.setText(monAnSua.getAnhMon() != null ? monAnSua.getAnhMon() : "");
			chkTinhTrang.setSelected(monAnSua.isTinhTrang());
			if (monAnSua.getDonVi() != null)
				cboDonVi.setSelectedItem(monAnSua.getDonVi());
			if (monAnSua.getDanhMuc() != null) {
				for (int i = 0; i < cboDanhMuc.getItemCount(); i++) {
					if (cboDanhMuc.getItemAt(i).getMaDM().equals(monAnSua.getDanhMuc().getMaDM())) {
						cboDanhMuc.setSelectedIndex(i);
						break;
					}
				}
			}
			hienThiPreviewAnh(monAnSua.getAnhMon());
		}

		private void luuMonAn() {
			try {
				MonAn mon = layDuLieuForm();
				if (monAnSua == null) {
					if (dao.tonTaiMaMonAn(mon.getMaMonAn())) {
						JOptionPane.showMessageDialog(this, "Mã món ăn đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (dao.themMonAn(mon)) {
						JOptionPane.showMessageDialog(this, "Thêm món ăn thành công!");
						succeeded = true;
						dispose();
					} else
						JOptionPane.showMessageDialog(this, "Thêm món ăn thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
				} else {
					if (dao.suaMonAn(mon)) {
						JOptionPane.showMessageDialog(this, "Cập nhật món ăn thành công!");
						succeeded = true;
						dispose();
					} else
						JOptionPane.showMessageDialog(this, "Cập nhật món ăn thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE);
			}
		}

		private MonAn layDuLieuForm() throws Exception {
			String maMon = txtMaMon.getText().trim();
			String tenMon = txtTenMon.getText().trim();
			DanhMuc danhMuc = (DanhMuc) cboDanhMuc.getSelectedItem();
			String donVi = layGiaTriCombo(cboDonVi);
			String slText = txtSoLuongTon.getText().trim();
			String giaText = txtGiaBan.getText().trim();

			if (maMon.isEmpty() || tenMon.isEmpty() || danhMuc == null || donVi.isEmpty() || slText.isEmpty()
					|| giaText.isEmpty())
				throw new Exception("Vui lòng nhập đầy đủ các trường thông tin bắt buộc.");

			int soLuong;
			double giaBan;
			try {
				soLuong = Integer.parseInt(slText);
			} catch (Exception e) {
				throw new Exception("Số lượng tồn phải là số nguyên hợp lệ.");
			}
			try {
				giaBan = Double.parseDouble(giaText);
			} catch (Exception e) {
				throw new Exception("Giá bán phải là số hợp lệ.");
			}
			if (soLuong < 0)
				throw new Exception("Số lượng tồn không được âm.");
			if (giaBan <= 0)
				throw new Exception("Giá bán phải lớn hơn 0.");

			MonAn mon = new MonAn();
			mon.setMaMonAn(maMon);
			mon.setTenMon(tenMon);
			mon.setDanhMuc(danhMuc);
			mon.setDonVi(donVi);
			mon.setSoLuong(soLuong);
			mon.setGiaMon(giaBan);
			mon.setMoTa(txtMoTa.getText().trim());
			mon.setGhiChu(txtGhiChu.getText().trim());
			mon.setAnhMon(txtAnhMon.getText().trim());
			mon.setTinhTrang(chkTinhTrang.isSelected());
			return mon;
		}

		private String layGiaTriCombo(JComboBox<String> cbo) {
			Object value = cbo.isEditable() ? cbo.getEditor().getItem() : cbo.getSelectedItem();
			return value != null ? value.toString().trim() : "";
		}
	}
}