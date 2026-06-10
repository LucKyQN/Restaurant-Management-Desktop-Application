package DAO;

import Entity.KhuyenMai;
import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

	private Connection getConnection() throws Exception {
		ConnectDB.getInstance().connect();
		return ConnectDB.getInstance().getConnection();
	}

	public List<KhuyenMai> getAllKhuyenMai() {
		List<KhuyenMai> list = new ArrayList<>();

		String sql = "SELECT maKM, tenKM, moTaKM, loaiKM, giaTriKM, ngayBatDau, ngayKetThuc, "
				+ "dieuKienApDung, soLuongToiThieu, trangThai " + "FROM KhuyenMai ORDER BY maKM";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				KhuyenMai km = new KhuyenMai();
				km.setMaKM(rs.getString("maKM"));
				km.setTenKM(rs.getString("tenKM"));
				km.setMoTaKM(rs.getString("moTaKM"));
				km.setLoaiKM(rs.getString("loaiKM"));
				km.setGiaTriKM(rs.getDouble("giaTriKM"));
				km.setNgayBatDau(rs.getDate("ngayBatDau"));
				km.setNgayKetThuc(rs.getDate("ngayKetThuc"));
				km.setDieuKienApDung(rs.getString("dieuKienApDung"));
				km.setSoLuongToiThieu(rs.getInt("soLuongToiThieu"));
				km.setTrangThai(rs.getBoolean("trangThai"));
				list.add(km);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int getSoDangChay() {
		String sql = "SELECT COUNT(*) " + "FROM KhuyenMai " + "WHERE trangThai = 1 "
				+ "AND GETDATE() BETWEEN ngayBatDau AND ngayKetThuc";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int kq = rs.getInt(1);
				rs.close();
				stmt.close();
				return kq;
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
	// Thêm hàm này vào KhuyenMaiDAO.java
	public int getLuotSuDungCuaKhuyenMai(String maKM) {
		String sql = "SELECT COUNT(*) FROM HoaDonKhuyenMai WHERE maKM = ?";
		try {
			java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
			java.sql.PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, maKM);
			java.sql.ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public int getSoSapToi() {
		String sql = "SELECT COUNT(*) " + "FROM KhuyenMai " + "WHERE trangThai = 1 AND ngayBatDau > GETDATE()";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int kq = rs.getInt(1);
				rs.close();
				stmt.close();
				return kq;
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public int getLuotSuDungHomNay() {
		String sql = "SELECT COUNT(*) FROM HoaDonKhuyenMai hk "
				+ "JOIN HoaDon h ON hk.maHD = h.maHD "
				+ "WHERE CAST(h.ngayGioThanhToan AS DATE) = CAST(GETDATE() AS DATE) "
				+ "AND h.trangThaiThanhToan = N'Đã thanh toán'";

		try {
			java.sql.Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
			java.sql.PreparedStatement ps = con.prepareStatement(sql);
			java.sql.ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1); // Trả về con số đếm được
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getMaKMTuDong() {
		String sql = "SELECT TOP 1 maKM FROM KhuyenMai ORDER BY maKM DESC";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String maCuoi = rs.getString("maKM");
				rs.close();
				stmt.close();

				if (maCuoi != null && maCuoi.matches("KM\\d+")) {
					int so = Integer.parseInt(maCuoi.substring(2)) + 1;
					return String.format("KM%03d", so);
				}
			} else {
				rs.close();
				stmt.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "KM001";
	}

	public boolean themKhuyenMai(KhuyenMai km) {
		String sql = "INSERT INTO KhuyenMai "
				+ "(maKM, tenKM, moTaKM, loaiKM, giaTriKM, ngayBatDau, ngayKetThuc, dieuKienApDung, soLuongToiThieu, trangThai) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);

			stmt.setString(1, km.getMaKM());
			stmt.setString(2, km.getTenKM());
			stmt.setString(3, km.getMoTaKM());
			stmt.setString(4, km.getLoaiKM());
			stmt.setDouble(5, km.getGiaTriKM());
			stmt.setDate(6, km.getNgayBatDau() != null ? new Date(km.getNgayBatDau().getTime()) : null);
			stmt.setDate(7, km.getNgayKetThuc() != null ? new Date(km.getNgayKetThuc().getTime()) : null);
			stmt.setString(8, km.getDieuKienApDung());
			stmt.setInt(9, km.getSoLuongToiThieu());
			stmt.setBoolean(10, km.isTrangThai());

			int rows = stmt.executeUpdate();
			stmt.close();

			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean suaKhuyenMai(KhuyenMai km) {
		String sql = "UPDATE KhuyenMai SET "
				+ "tenKM = ?, moTaKM = ?, loaiKM = ?, giaTriKM = ?, ngayBatDau = ?, ngayKetThuc = ?, "
				+ "dieuKienApDung = ?, soLuongToiThieu = ?, trangThai = ? " + "WHERE maKM = ?";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);

			stmt.setString(1, km.getTenKM());
			stmt.setString(2, km.getMoTaKM());
			stmt.setString(3, km.getLoaiKM());
			stmt.setDouble(4, km.getGiaTriKM());
			stmt.setDate(5, km.getNgayBatDau() != null ? new Date(km.getNgayBatDau().getTime()) : null);
			stmt.setDate(6, km.getNgayKetThuc() != null ? new Date(km.getNgayKetThuc().getTime()) : null);
			stmt.setString(7, km.getDieuKienApDung());
			stmt.setInt(8, km.getSoLuongToiThieu());
			stmt.setBoolean(9, km.isTrangThai());
			stmt.setString(10, km.getMaKM());

			int rows = stmt.executeUpdate();
			stmt.close();

			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Form đang gọi xoaKhuyenMai(), nên ta cập nhật trạng thái = false
	public boolean xoaKhuyenMai(String maKM) {
		String sql = "UPDATE KhuyenMai SET trangThai = 0 WHERE maKM = ?";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maKM);

			int rows = stmt.executeUpdate();
			stmt.close();

			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}