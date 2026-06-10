
package DAO;

import Entity.MonAn;
import Model.BanAnModel;
import Model.MonAnModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import connectDatabase.ConnectDB;
import javax.swing.JOptionPane;

public class PhucVuServiceDb implements PhucVuService {

	private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
	private final MonAnDAO monAnDAO = new MonAnDAO();

	public List<BanAnModel> getDanhSachBanChuaThanhToan() {
		List<BanAnModel> list = new ArrayList<>();

		String sql = "SELECT b.maBan, b.tenBan, b.sucChua, h.maHD, " + "       (SELECT ISNULL(SUM(thanhTien), 0) "
				+ "        FROM ChiTietHoaDon "
				+ "        WHERE maHD = h.maHD AND trangThaiPhucVu != N'Hủy') AS tamTinh " + "FROM BanAn b "
				+ "LEFT JOIN HoaDon h ON b.maBan = h.maBan AND h.trangThaiThanhToan != N'Đã thanh toán' "
				+ "WHERE b.trangThai IN (N'Có khách', N'Chờ thanh toán')";

		try {
			Connection con = ConnectDB.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				BanAnModel ban = new BanAnModel();
				ban.maBan = rs.getString("maBan");
				ban.tenBan = rs.getString("tenBan");
				ban.sucChua = rs.getInt("sucChua");
				ban.maHD = rs.getString("maHD");
				ban.tamTinh = (long) rs.getDouble("tamTinh");
				list.add(ban);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private boolean hoaDonDangChoThanhToan(String maHD) {
		String sql = "SELECT trangThaiThanhToan FROM HoaDon WHERE maHD = ?";
		try {
			Connection con = ConnectDB.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, maHD);
			ResultSet rs = ps.executeQuery();

			boolean ketQua = false;
			if (rs.next()) {
				String trangThai = rs.getString("trangThaiThanhToan");
				ketQua = "Chờ thanh toán".equalsIgnoreCase(trangThai);
			}

			rs.close();
			ps.close();
			return ketQua;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean themHoacTangMon(String maHD, String maMonAn, int sl, String ghiChu) {
		if (maHD == null || maHD.trim().isEmpty()) {
			System.err.println("LỖI: maHD truyền vào bị NULL!");
			return false;
		}

		if (hoaDonDangChoThanhToan(maHD)) {
			JOptionPane.showMessageDialog(null,
					"Hóa đơn này đang ở trạng thái chờ thanh toán,\nkhông thể thêm món nữa.");
			return false;
		}

		Connection con = null;
		try {
			con = ConnectDB.getInstance().getConnection();
			con.setAutoCommit(false);

			String sqlCheck = "SELECT ID_CTHD, soLuong, donGia " + "FROM ChiTietHoaDon "
					+ "WHERE maHD = ? AND maMonAn = ? " + "AND ISNULL(ghiChu, '') = ISNULL(?, '') "
					+ "AND ISNULL(trangThaiPhucVu, N'Chưa lên') != N'Hủy'";

			PreparedStatement psCheck = con.prepareStatement(sqlCheck);
			psCheck.setString(1, maHD);
			psCheck.setString(2, maMonAn);
			psCheck.setString(3, ghiChu == null ? "" : ghiChu);
			ResultSet rs = psCheck.executeQuery();

			boolean tonTai = false;
			int soLuongCu = 0;
			long donGia = 0;

			if (rs.next()) {
				tonTai = true;
				soLuongCu = rs.getInt("soLuong");
				donGia = (long) rs.getDouble("donGia");
			}

			rs.close();
			psCheck.close();

			if (tonTai) {
				String sqlUpdate = "UPDATE ChiTietHoaDon " + "SET soLuong = ?, thanhTien = ? "
						+ "WHERE maHD = ? AND maMonAn = ? AND ISNULL(ghiChu, '') = ISNULL(?, '') "
						+ "AND ISNULL(trangThaiPhucVu, N'Chưa lên') != N'Hủy'";

				int soLuongMoi = soLuongCu + sl;
				PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
				psUpdate.setInt(1, soLuongMoi);
				psUpdate.setLong(2, soLuongMoi * donGia);
				psUpdate.setString(3, maHD);
				psUpdate.setString(4, maMonAn);
				psUpdate.setString(5, ghiChu == null ? "" : ghiChu);

				if (psUpdate.executeUpdate() <= 0) {
					psUpdate.close();
					con.rollback();
					return false;
				}
				psUpdate.close();

			} else {
				String sqlInsert = "INSERT INTO ChiTietHoaDon "
						+ "(maHD, maMonAn, soLuong, donGia, thanhTien, trangThaiPhucVu, ghiChu) "
						+ "SELECT ?, ?, ?, giaBan, giaBan * ?, N'Chưa lên', ? " + "FROM MonAn WHERE maMonAn = ?";

				PreparedStatement psInsert = con.prepareStatement(sqlInsert);
				psInsert.setString(1, maHD);
				psInsert.setString(2, maMonAn);
				psInsert.setInt(3, sl);
				psInsert.setInt(4, sl);
				psInsert.setString(5, ghiChu == null ? "" : ghiChu);
				psInsert.setString(6, maMonAn);

				if (psInsert.executeUpdate() <= 0) {
					psInsert.close();
					con.rollback();
					return false;
				}
				psInsert.close();
			}

			PreparedStatement psTong = con.prepareStatement("UPDATE HoaDon "
					+ "SET tongTien = (SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietHoaDon WHERE maHD = ?) "
					+ "WHERE maHD = ?");
			psTong.setString(1, maHD);
			psTong.setString(2, maHD);
			psTong.executeUpdate();
			psTong.close();

			con.commit();
			return true;

		} catch (Exception e) {
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "Lỗi SQL: " + e.getMessage());
			return false;
		} finally {
			try {
				if (con != null) {
					con.setAutoCommit(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean themMonMoiTachDong(String maHD, String maMonAn, int soLuong, String ghiChu) {
		if (maHD == null || maHD.trim().isEmpty()) {
			System.err.println("LỖI: maHD truyền vào bị NULL!");
			return false;
		}

		if (hoaDonDangChoThanhToan(maHD)) {
			JOptionPane.showMessageDialog(null,
					"Hóa đơn này đang ở trạng thái chờ thanh toán,\nkhông thể thêm món nữa.");
			return false;
		}

		Connection con = null;
		try {
			con = ConnectDB.getInstance().getConnection();
			con.setAutoCommit(false);

			// Chỉ INSERT — không kiểm tra trùng lặp, không UPDATE dòng cũ
			String sqlInsert = "INSERT INTO ChiTietHoaDon (maHD, maMonAn, soLuong, donGia, thanhTien, ghiChu, trangThaiPhucVu) "
					+ "SELECT ?, ?, ?, giaBan, giaBan * ?, ?, N'Chưa lên' FROM MonAn WHERE maMonAn = ?";

			PreparedStatement psInsert = con.prepareStatement(sqlInsert);
			psInsert.setString(1, maHD);
			psInsert.setString(2, maMonAn);
			psInsert.setInt(3, soLuong);
			psInsert.setInt(4, soLuong);
			psInsert.setString(5, ghiChu == null ? "" : ghiChu);
			psInsert.setString(6, maMonAn);

			int affected = psInsert.executeUpdate();
			psInsert.close();

			if (affected <= 0) {
				con.rollback();
				return false;
			}

			// Cập nhật lại tổng tiền hóa đơn
			PreparedStatement psTong = con.prepareStatement(
					"UPDATE HoaDon SET tongTien = (SELECT ISNULL(SUM(thanhTien), 0) FROM ChiTietHoaDon WHERE maHD = ?) "
					+ "WHERE maHD = ?");
			psTong.setString(1, maHD);
			psTong.setString(2, maHD);
			psTong.executeUpdate();
			psTong.close();

			con.commit();
			return true;

		} catch (Exception e) {
			try {
				if (con != null) con.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "Lỗi SQL: " + e.getMessage());
			return false;
		} finally {
			try {
				if (con != null) con.setAutoCommit(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean capNhatSoLuongMon(String maHD, String maMonAn, int soLuongMoi) {
		return hoaDonDAO.capNhatSoLuongMon(maHD, maMonAn, soLuongMoi);
	}

	@Override
	public boolean xoaMonKhoiChiTiet(String maHD, String maMonAn) {
		return hoaDonDAO.xoaMonKhoiChiTiet(maHD, maMonAn);
	}

	@Override
	public List<MonAn> getMonAnDangPhucVu() {
		return monAnDAO.getAllMonAn().stream().filter(MonAn::isTinhTrang).collect(Collectors.toList());
	}

	@Override
	public List<MonAnModel> getMonAnTheoBan(String maBan, String trangThai) {
		List<MonAnModel> ds = new ArrayList<>();
		String sql = "";

		if (trangThai.equalsIgnoreCase("Có khách") || trangThai.equalsIgnoreCase("Chờ thanh toán")) {
			sql = "SELECT m.maMonAn, m.tenMonAn, " + "       c.soLuong, c.donGia, c.thanhTien, "
					+ "       ISNULL(c.trangThaiPhucVu, N'Chưa lên') AS trangThaiPhucVu, "
					+ "       c.ID_CTHD AS id_cthd " + "FROM ChiTietHoaDon c "
					+ "JOIN MonAn m ON c.maMonAn = m.maMonAn " + "JOIN HoaDon h ON c.maHD = h.maHD "
					+ "WHERE h.maBan = ? " + "  AND h.trangThaiThanhToan IN (N'Chưa thanh toán', N'Chờ thanh toán')";
		} else if (trangThai.equalsIgnoreCase("Đã đặt")) {
			sql = "SELECT m.maMonAn, m.tenMonAn, "
					+ "       ct.soLuong, ct.donGia, (ct.soLuong * ct.donGia) AS thanhTien, "
					+ "       N'Đã đặt trước' AS trangThaiPhucVu, " + "       0 AS id_cthd "
					+ "FROM ChiTietDonDatMon ct " + "JOIN MonAn m ON ct.maMonAn = m.maMonAn "
					+ "JOIN DonDatMon d ON ct.maDon = d.maDon " + "WHERE d.maBan = ? "
					+ "  AND d.trangThai = N'Chờ khách'";
		} else {
			return ds;
		}

		try {
			Connection con = ConnectDB.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, maBan);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				MonAnModel m = new MonAnModel();
				m.id_cthd = rs.getInt("id_cthd");
				m.maMonAn = rs.getString("maMonAn");
				m.tenMonAn = rs.getString("tenMonAn");
				m.soLuong = rs.getInt("soLuong");
				m.giaBan = rs.getLong("donGia");
				m.thanhTien = rs.getLong("thanhTien");
				m.trangThaiPhucVu = rs.getString("trangThaiPhucVu");
				ds.add(m);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ds;
	}

	public List<BanAnModel> getDanhSachBanCanPhucVu() {
		List<BanAnModel> list = new ArrayList<>();

		String sql = "SELECT hd.maHD, b.maBan, b.tenBan, b.viTri, hd.trangThaiThanhToan AS trangThaiHD, "
				+ "ISNULL((SELECT SUM(soLuong * donGia) FROM ChiTietHoaDon WHERE maHD = hd.maHD), 0) AS tamTinh "
				+ "FROM HoaDon hd JOIN BanAn b ON hd.maBan = b.maBan "
				+ "WHERE hd.trangThaiThanhToan IN (N'Chưa thanh toán', N'Chờ thanh toán') "
				+ "UNION "
				+ "SELECT hd.maHD, b.maBan, b.tenBan, b.viTri, hd.trangThaiThanhToan AS trangThaiHD, "
				+ "ISNULL((SELECT SUM(soLuong * donGia) FROM ChiTietHoaDon WHERE maHD = hd.maHD), 0) AS tamTinh "
				+ "FROM HoaDon hd JOIN ChiTietDatBan ct ON hd.maPhieuDatBan = ct.maPhieu "
				+ "JOIN BanAn b ON ct.maBan = b.maBan "
				+ "WHERE hd.trangThaiThanhToan IN (N'Chưa thanh toán', N'Chờ thanh toán')";


		try {
			connectDatabase.ConnectDB.getInstance().connect();

			java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
			java.sql.PreparedStatement ps = con.prepareStatement(sql);
			java.sql.ResultSet rs = ps.executeQuery();

			
			java.util.Map<String, BanAnModel> map = new java.util.LinkedHashMap<>();

			while (rs.next()) {
				String maHD = rs.getString("maHD");
				String maBan = rs.getString("maBan");
				String tenBan = rs.getString("tenBan");

				if (map.containsKey(maHD)) {
					BanAnModel ban = map.get(maHD);
					
					if (!ban.tenBan.contains(tenBan)) {
						ban.tenBan += ", " + tenBan;
					}
				} else {
					BanAnModel ban = new BanAnModel();
					ban.maHD = maHD;
					ban.maBan = maBan;
					ban.tenBan = tenBan;
					ban.viTri = rs.getString("viTri"); // thêm dòng này

					ban.trangThai = rs.getString("trangThaiHD");
					ban.tamTinh = rs.getLong("tamTinh");
					map.put(maHD, ban);
				}
			}
			list.addAll(map.values());
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<MonAnModel> getChiTietHoaDon(String maHD) {
		List<MonAnModel> list = new ArrayList<>();
		String sql = "SELECT ct.ID_CTHD, ct.maMonAn, m.tenMonAn, ct.soLuong, ct.donGia, ct.thanhTien, "
				+ "ISNULL(ct.trangThaiPhucVu, N'Chưa lên') as trangThaiPhucVu " + "FROM ChiTietHoaDon ct "
				+ "JOIN MonAn m ON ct.maMonAn = m.maMonAn " + "WHERE ct.maHD = ?";

		try {
			Connection con = ConnectDB.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, maHD);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				MonAnModel mon = new MonAnModel();
				mon.id_cthd = rs.getInt("ID_CTHD");
				mon.maMonAn = rs.getString("maMonAn");
				mon.tenMonAn = rs.getString("tenMonAn");
				mon.soLuong = rs.getInt("soLuong");
				mon.giaBan = (long) rs.getDouble("donGia");
				mon.thanhTien = (long) rs.getDouble("thanhTien");
				mon.trangThaiPhucVu = rs.getString("trangThaiPhucVu");
				list.add(mon);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean capNhatTrangThaiMon(int idCTHD, String trangThaiMoi) {
		String sql = "UPDATE ChiTietHoaDon SET trangThaiPhucVu = ? WHERE ID_CTHD = ?";
		try {
			Connection con = ConnectDB.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, trangThaiMoi);
			ps.setInt(2, idCTHD);
			boolean ok = ps.executeUpdate() > 0;
			ps.close();
			return ok;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean yeuCauThanhToan(String maHD, String maBan) {
		try {
			Connection con = ConnectDB.getInstance().getConnection();

			PreparedStatement ps1 = con
					.prepareStatement("UPDATE HoaDon SET trangThaiThanhToan = N'Chờ thanh toán' WHERE maHD = ?");
			ps1.setString(1, maHD);
			ps1.executeUpdate();
			ps1.close();

			PreparedStatement ps2 = con
					.prepareStatement("UPDATE BanAn SET trangThai = N'Chờ thanh toán' WHERE maBan = ?");
			ps2.setString(1, maBan);
			ps2.executeUpdate();
			ps2.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}