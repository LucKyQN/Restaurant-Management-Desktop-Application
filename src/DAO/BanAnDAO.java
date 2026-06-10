package DAO;

import Entity.BanAn;
import Entity.LoaiBan;
import connectDatabase.ConnectDB;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BanAnDAO {

	private Connection getConnection() throws Exception {
		ConnectDB.getInstance().connect();
		return ConnectDB.getInstance().getConnection();
	}

	public List<LoaiBan> getAllLoaiBan() {
		List<LoaiBan> list = new ArrayList<>();
		String sql = "SELECT maLB, tenLB, soGhe FROM LoaiBan ORDER BY maLB";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				LoaiBan lb = new LoaiBan();
				lb.setMaLB(rs.getString("maLB"));
				lb.setTenLB(rs.getString("tenLB"));
				lb.setSoGhe(rs.getInt("soGhe"));
				list.add(lb);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<BanAn> getAllBanAn() {
		List<BanAn> list = new ArrayList<>();

		String sql = "SELECT b.maBan, b.maLB, l.tenLB, l.soGhe, b.tenBan, b.viTri, b.sucChua, b.trangThai, b.moTa "
				+ "FROM BanAn b " + "LEFT JOIN LoaiBan l ON b.maLB = l.maLB " + "ORDER BY b.maBan";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(mapBanAn(rs));
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public boolean themBanAn(BanAn ban) {
		String sql = "INSERT INTO BanAn (maBan, maLB, tenBan, viTri, sucChua, trangThai, moTa) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);

			stmt.setString(1, ban.getMaBan());
			stmt.setString(2, ban.getLoaiBan() != null ? ban.getLoaiBan().getMaLB() : null);
			stmt.setString(3, ban.getTenBan());
			stmt.setString(4, ban.getViTri());
			stmt.setInt(5, ban.getSucChua());
			stmt.setString(6, ban.getTrangThai());
			stmt.setString(7, ban.getMoTa());

			int rows = stmt.executeUpdate();
			stmt.close();

			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean suaBanAn(BanAn ban) {
		String sql = "UPDATE BanAn SET maLB = ?, tenBan = ?, viTri = ?, sucChua = ?, trangThai = ?, moTa = ? "
				+ "WHERE maBan = ?";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);

			stmt.setString(1, ban.getLoaiBan() != null ? ban.getLoaiBan().getMaLB() : null);
			stmt.setString(2, ban.getTenBan());
			stmt.setString(3, ban.getViTri());
			stmt.setInt(4, ban.getSucChua());
			stmt.setString(5, ban.getTrangThai());
			stmt.setString(6, ban.getMoTa());
			stmt.setString(7, ban.getMaBan());

			int rows = stmt.executeUpdate();
			stmt.close();

			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean capNhatTrangThai(String maBan, String trangThaiMoi) {
		String sql = "UPDATE BanAn SET trangThai = ? WHERE maBan = ?";
		try {
			Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, trangThaiMoi);
			stmt.setString(2, maBan);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean chuyenHoacGopBan(String banCu, String banMoi) {
		Connection con = null;
		try {
			con = getConnection();
			
			con.setAutoCommit(false);

			String sql1 = "UPDATE BanAn SET trangThai = N'Trống' WHERE tenBan = ?";
			PreparedStatement ps1 = con.prepareStatement(sql1);
			ps1.setString(1, banCu);
			ps1.executeUpdate();
			ps1.close();

			String sql2 = "UPDATE BanAn SET trangThai = N'Có khách' WHERE tenBan = ?";
			PreparedStatement ps2 = con.prepareStatement(sql2);
			ps2.setString(1, banMoi);
			ps2.executeUpdate();
			ps2.close();

			con.commit();
			return true;

		} catch (Exception e) {
			try {
				if (con != null)
					con.rollback();
			} catch (Exception ex) {
			}
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (con != null)
					con.setAutoCommit(true);
			} catch (Exception ex) {
			}
		}
	}

	public boolean xoaMemBanAn(String maBan) {
		String sql = "UPDATE BanAn SET trangThai = N'Ngưng sử dụng' WHERE maBan = ?";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maBan);

			int rows = stmt.executeUpdate();
			stmt.close();

			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean tonTaiMaBan(String maBan) {
		String sql = "SELECT COUNT(*) FROM BanAn WHERE maBan = ?";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maBan);

			ResultSet rs = stmt.executeQuery();
			boolean exists = false;
			if (rs.next()) {
				exists = rs.getInt(1) > 0;
			}

			rs.close();
			stmt.close();
			return exists;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String taoMaBanTuDong() {
		String sql = "SELECT TOP 1 maBan FROM BanAn WHERE maBan LIKE 'BAN%' ORDER BY maBan DESC";

		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String maCuoi = rs.getString("maBan"); 
				if (maCuoi != null && maCuoi.matches("BAN\\d+")) {
					int so = Integer.parseInt(maCuoi.substring(3));
					rs.close();
					ps.close();
					return String.format("BAN%02d", so + 1);
				}
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "BAN01";
	}

	public List<BanAn> getDanhSachBanDangSuDung() {
		List<BanAn> list = new ArrayList<>();

		String sql = "SELECT b.maBan, b.maLB, l.tenLB, l.soGhe, b.tenBan, b.viTri, b.sucChua, b.trangThai, b.moTa "
				+ "FROM BanAn b " + "LEFT JOIN LoaiBan l ON b.maLB = l.maLB "
				+ "WHERE b.trangThai IN (N'Trống', N'Có khách', N'Đã đặt') " + "ORDER BY b.maBan";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(mapBanAn(rs));
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	private BanAn mapBanAn(ResultSet rs) throws Exception {
		BanAn ban = new BanAn();
		ban.setMaBan(rs.getString("maBan"));
		ban.setTenBan(rs.getString("tenBan"));
		ban.setViTri(rs.getString("viTri"));
		ban.setSucChua(rs.getInt("sucChua"));
		ban.setTrangThai(rs.getString("trangThai"));
		ban.setMoTa(rs.getString("moTa"));

		String maLB = rs.getString("maLB");
		if (maLB != null) {
			LoaiBan lb = new LoaiBan();
			lb.setMaLB(maLB);
			lb.setTenLB(rs.getString("tenLB"));
			lb.setSoGhe(rs.getInt("soGhe"));
			ban.setLoaiBan(lb);
		}

		return ban;
	}

	public String getTenBanGhepChung(String maBanGhep) {
		String sql = "SELECT trangThai FROM BanAn WHERE maBan = ?";
		try {
			Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, maBanGhep);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				String trangThai = rs.getString("trangThai");
				if (trangThai != null && trangThai.toLowerCase().startsWith("đang ghép")) {
					// Ví dụ trangThai: "Đang ghép với Bàn A3" -> cắt lấy "Bàn A3"
					String tenBanChinh = trangThai.substring("Đang ghép".length()).trim();
					if (tenBanChinh.toLowerCase().startsWith("với")) {
						tenBanChinh = tenBanChinh.substring(3).trim();
					}
					if (!tenBanChinh.isEmpty()) {
						return tenBanChinh;
					}
				}
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "bàn khác";
	}

	public List<String> getDanhSachMaBanTheoHoaDon(String maHD) {
		List<String> list = new ArrayList<>();
		try {
			Connection con = connectDatabase.ConnectDB.getInstance().getConnection();
			// 1. Lấy mã bàn chính của hóa đơn
			String sqlMain = "SELECT maBan, tenBan FROM BanAn WHERE maBan = (SELECT maBan FROM HoaDon WHERE maHD = ?)";
			PreparedStatement psMain = con.prepareStatement(sqlMain);
			psMain.setString(1, maHD);
			ResultSet rsMain = psMain.executeQuery();
			String tenBanChinh = "";
			if (rsMain.next()) {
				list.add(rsMain.getString("maBan"));
				tenBanChinh = rsMain.getString("tenBan");
			}
			rsMain.close();
			psMain.close();
			
			// 2. Lấy các bàn phụ (bàn đang ghép với bàn chính)
			if (!tenBanChinh.isEmpty()) {
				String sqlPhu = "SELECT maBan FROM BanAn WHERE trangThai = ?";
				PreparedStatement psPhu = con.prepareStatement(sqlPhu);
				psPhu.setString(1, "Đang ghép với " + tenBanChinh);
				ResultSet rsPhu = psPhu.executeQuery();
				while (rsPhu.next()) {
					list.add(rsPhu.getString("maBan"));
				}
				rsPhu.close();
				psPhu.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}