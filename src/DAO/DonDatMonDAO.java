package DAO;

import connectDatabase.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DonDatMonDAO {

	private Connection getConnection() throws Exception {
		ConnectDB.getInstance().connect();
		return ConnectDB.getInstance().getConnection();
	}

	public boolean taoDonDatMon(String maDon, String maNV, String maBan, String ghiChu) {
		String sql = "INSERT INTO DonDatMon (maDon, maNV, maBan, thoiGianTao, trangThai, ghiChu) "
				+ "VALUES (?, ?, ?, GETDATE(), N'Chờ khách', ?)";
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, maDon);
			ps.setString(2, maNV);
			ps.setString(3, maBan);
			ps.setString(4, ghiChu);
			int rows = ps.executeUpdate();
			ps.close();
			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean themChiTietDonDatMon(String maDon, String maMonAn, int soLuong, long donGia, String ghiChu) {
		String sql = "INSERT INTO ChiTietDonDatMon (maDon, maMonAn, soLuong, donGia, ghiChu) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, maDon);
			ps.setString(2, maMonAn);
			ps.setInt(3, soLuong);
			ps.setLong(4, donGia);
			ps.setString(5, ghiChu == null ? "" : ghiChu);
			int rows = ps.executeUpdate();
			ps.close();
			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}