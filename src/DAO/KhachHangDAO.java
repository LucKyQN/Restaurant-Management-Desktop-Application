package DAO;

import Entity.KhachHang;
import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    private Connection getConnection() throws Exception {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }


    private String phatSinhMaKH(Connection con) {
        String sql = "SELECT TOP 1 maKH FROM KhachHang ORDER BY maKH DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String maCuoi = rs.getString("maKH");
                if (maCuoi != null && maCuoi.length() >= 5) {
                    int so = Integer.parseInt(maCuoi.substring(2));
                    return String.format("KH%03d", so + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "KH001";
    }


    public void luuHoacCapNhatKhachHang(Connection con, String sdt, String tenKH, long tienThanhToan) {
        if (sdt == null || sdt.trim().isEmpty() || sdt.equalsIgnoreCase("Trống")) {
            return;
        }

        int diemCongThem = (int) (tienThanhToan / 100000);
        String sqlCheck = "SELECT maKH FROM KhachHang WHERE soDienThoai = ?";

        try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {

            psCheck.setString(1, sdt);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                String sqlUpdate = "UPDATE KhachHang SET tenKH = ?, diemTichLuy = diemTichLuy + ? WHERE soDienThoai = ?";
                try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, tenKH);
                    psUpdate.setInt(2, diemCongThem);
                    psUpdate.setString(3, sdt);
                    psUpdate.executeUpdate();
                }
            } else {
                String maKHMoi = phatSinhMaKH(con); // Truyền con vào đây
                String sqlInsert = "INSERT INTO KhachHang (maKH, tenKH, soDienThoai, email, ngayThamGia, diemTichLuy, trangThai) "
                        + "VALUES (?, ?, ?, '', GETDATE(), ?, 1)";
                try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                    psInsert.setString(1, maKHMoi);
                    psInsert.setString(2, tenKH);
                    psInsert.setString(3, sdt);
                    psInsert.setInt(4, diemCongThem);
                    psInsert.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY ngayThamGia ASC";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getDate("ngayThamGia"),
                        rs.getString("soDienThoai"),
                        rs.getString("tenKH"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("trangThai")
                );
                ds.add(kh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }


    public List<KhachHang> timKiemKhachHang(String keyword) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE tenKH LIKE ? OR soDienThoai LIKE ?";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ds.add(new KhachHang(
                        rs.getString("maKH"), rs.getDate("ngayThamGia"),
                        rs.getString("soDienThoai"), rs.getString("tenKH"),
                        rs.getInt("diemTichLuy"), rs.getBoolean("trangThai")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
    public KhachHang timKhachHangTheoSDT(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE soDienThoai = ? AND trangThai = 1";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();

            KhachHang kh = null;
            if (rs.next()) {
                kh = new KhachHang(
                        rs.getString("maKH"),
                        rs.getDate("ngayThamGia"),
                        rs.getString("soDienThoai"),
                        rs.getString("tenKH"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("trangThai")
                );
            }

            rs.close();
            ps.close();
            return kh;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String timTenKhachTheoSDT(String sdt) {
        KhachHang kh = timKhachHangTheoSDT(sdt);
        return kh != null ? kh.getTen() : null;
    }
}