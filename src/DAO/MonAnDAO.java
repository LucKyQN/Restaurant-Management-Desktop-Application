package DAO;

import Entity.DanhMuc;
import Entity.MonAn;
import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MonAnDAO {

	private Connection getConnection() throws Exception {
		ConnectDB.getInstance().connect();
		return ConnectDB.getInstance().getConnection();
	}

	public List<DanhMuc> getAllDanhMuc() {
		List<DanhMuc> list = new ArrayList<>();
		String sql = "SELECT maDM, tenDM FROM DanhMucMonAn ORDER BY maDM";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				DanhMuc dm = new DanhMuc();
				dm.setMaDM(rs.getString("maDM"));
				dm.setTenDM(rs.getString("tenDM"));
				list.add(dm);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<MonAn> getAllMonAn() {
		List<MonAn> list = new ArrayList<>();

		String sql = "SELECT m.maMonAn, m.maDM, d.tenDM, m.tenMonAn, m.donVi, m.soLuongTon, "
				+ "       ISNULL(ct.giaBan, m.giaBan) AS giaBan, "
				+ "       m.moTa, m.ghiChu, m.anhMon, m.tinhTrang "
				+ "FROM MonAn m "
				+ "LEFT JOIN DanhMucMonAn d ON m.maDM = d.maDM "
				+ "LEFT JOIN ChiTietBangGia ct ON m.maMonAn = ct.maMonAn "
				+ "    AND ct.maBangGia = (SELECT TOP 1 maBangGia FROM BangGia WHERE trangThai = 1) "
				+ "WHERE m.tinhTrang = 1 "
				+ "ORDER BY m.maMonAn";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(mapMonAn(rs));
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public boolean themMonAn(MonAn mon) {
		String sql = "INSERT INTO MonAn (maMonAn, maDM, tenMonAn, donVi, soLuongTon, giaBan, moTa, ghiChu, anhMon, tinhTrang) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);

			stmt.setString(1, mon.getMaMonAn());

			if (mon.getDanhMuc() != null && mon.getDanhMuc().getMaDM() != null
					&& !mon.getDanhMuc().getMaDM().trim().isEmpty()) {
				stmt.setString(2, mon.getDanhMuc().getMaDM());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}

			stmt.setString(3, mon.getTenMon());
			stmt.setString(4, mon.getDonVi());
			stmt.setInt(5, mon.getSoLuong());
			stmt.setDouble(6, mon.getGiaMon());
			stmt.setString(7, mon.getMoTa());
			stmt.setString(8, mon.getGhiChu());
			stmt.setString(9, mon.getAnhMon());
			stmt.setBoolean(10, mon.isTinhTrang());

			int rows = stmt.executeUpdate();
			stmt.close();

			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean suaMonAn(MonAn mon) {
		String sql = "UPDATE MonAn SET maDM = ?, tenMonAn = ?, donVi = ?, soLuongTon = ?, giaBan = ?, "
				+ "moTa = ?, ghiChu = ?, anhMon = ?, tinhTrang = ? " + "WHERE maMonAn = ?";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);

			if (mon.getDanhMuc() != null && mon.getDanhMuc().getMaDM() != null
					&& !mon.getDanhMuc().getMaDM().trim().isEmpty()) {
				stmt.setString(1, mon.getDanhMuc().getMaDM());
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}

			stmt.setString(2, mon.getTenMon());
			stmt.setString(3, mon.getDonVi());
			stmt.setInt(4, mon.getSoLuong());
			stmt.setDouble(5, mon.getGiaMon());
			stmt.setString(6, mon.getMoTa());
			stmt.setString(7, mon.getGhiChu());
			stmt.setString(8, mon.getAnhMon());
			stmt.setBoolean(9, mon.isTinhTrang());
			stmt.setString(10, mon.getMaMonAn());

			int rows = stmt.executeUpdate();
			stmt.close();

			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean xoaMemMonAn(String maMonAn) {
		String sql = "UPDATE MonAn SET tinhTrang = 0 WHERE maMonAn = ?";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maMonAn);

			int rows = stmt.executeUpdate();
			stmt.close();

			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean tonTaiMaMonAn(String maMonAn) {
		String sql = "SELECT COUNT(*) FROM MonAn WHERE maMonAn = ?";

		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maMonAn);

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

	private MonAn mapMonAn(ResultSet rs) throws Exception {
		MonAn mon = new MonAn();
		mon.setMaMonAn(rs.getString("maMonAn"));
		mon.setTenMon(rs.getString("tenMonAn"));
		mon.setDonVi(rs.getString("donVi"));
		mon.setSoLuong(rs.getInt("soLuongTon"));
		mon.setGiaMon(rs.getDouble("giaBan"));
		mon.setMoTa(rs.getString("moTa"));
		mon.setGhiChu(rs.getString("ghiChu"));
		mon.setAnhMon(rs.getString("anhMon"));
		mon.setTinhTrang(rs.getBoolean("tinhTrang"));

		String maDM = rs.getString("maDM");
		String tenDM = rs.getString("tenDM");
		if (maDM != null) {
			DanhMuc dm = new DanhMuc();
			dm.setMaDM(maDM);
			dm.setTenDM(tenDM);
			mon.setDanhMuc(dm);
		}

		return mon;
	}

	public double getGiaBanHienTai(String maMonAn) {
		BangGiaDAO bangGiaDAO = new BangGiaDAO();
		Entity.BangGia bgHienHanh = bangGiaDAO.getBangGiaHienHanh();
		
		if (bgHienHanh != null) {
			ChiTietMonAnDAO ctDAO = new ChiTietMonAnDAO();
			double giaMoi = ctDAO.getGiaMonTheoBangGia(maMonAn, bgHienHanh.getMaBangGia());
			if (giaMoi != -1) {
				return giaMoi; // Trả về giá trong bảng giá đặc biệt
			}
		}
		
		// Fallback: Lấy giá gốc trong bảng MonAn nếu không có bảng giá hiện hành hoặc không tìm thấy giá
		double giaGoc = 0;
		String sql = "SELECT giaBan FROM MonAn WHERE maMonAn = ?";
		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maMonAn);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				giaGoc = rs.getDouble("giaBan");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return giaGoc;
	}

	public String getMaMonTuDong() {
		String maMoi = "MA001"; 
		String sql = "SELECT TOP 1 maMonAn FROM MonAn ORDER BY maMonAn DESC";
		try {
			Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String maCu = rs.getString("maMonAn"); 
				int so = Integer.parseInt(maCu.substring(2)) + 1;
				maMoi = String.format("MA%03d", so);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return maMoi;
	}
}
