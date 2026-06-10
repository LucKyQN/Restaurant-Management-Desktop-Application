package DAO;

import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhatKyDangNhapDAO {

	private Connection getConnection() throws Exception {
		ConnectDB.getInstance().connect();
		return ConnectDB.getInstance().getConnection();
	}

	public boolean ghiNhanDangNhap(String maNV, String hoTenNV, String vaiTro) {
		String sql = "INSERT INTO NhatKyDangNhap(maNV, hoTenNV, vaiTro, thoiGianDangNhap) VALUES (?, ?, ?, GETDATE())";
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, maNV);
			ps.setString(2, hoTenNV);
			ps.setString(3, vaiTro);

			int rows = ps.executeUpdate();
			ps.close();
			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<String[]> getDanhSachDangNhapHomNay() {
		List<String[]> list = new ArrayList<>();

		String sql = "SELECT maNV, hoTenNV, vaiTro, " + "CONVERT(VARCHAR(5), thoiGianDangNhap, 108) AS gioDangNhap "
				+ "FROM NhatKyDangNhap " + "WHERE CAST(thoiGianDangNhap AS DATE) = CAST(GETDATE() AS DATE) "
				+ "ORDER BY thoiGianDangNhap DESC";

		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(new String[] { rs.getString("maNV"), rs.getString("hoTenNV"), rs.getString("vaiTro"),
						rs.getString("gioDangNhap") });
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
}