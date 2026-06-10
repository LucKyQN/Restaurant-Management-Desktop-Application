package DAO;

import Entity.ThongBao;
import connectDatabase.ConnectDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class NotificationDAO {



    private Connection getConnection() throws SQLException {
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) {
            throw new SQLException("Không thể lấy kết nối database");
        }
        return con;
    }



    public boolean guiThongBao(ThongBao tb) {
        String sql = "INSERT INTO Notifications(tieuDe, noiDung, loai, nguoiGui, thoiGian, daDoc, trangThai) "
                   + "VALUES (?, ?, ?, ?, GETDATE(), 0, N'Chưa đọc')";
        Connection con = null;
        try {
             con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ps.setString(1, tb.getTieuDe());
            ps.setString(2, tb.getNoiDung());
            ps.setString(3, tb.getLoai());
            ps.setString(4, tb.getNguoiGui());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi gửi thông báo: " + e.getMessage());
            return false;
        }
    }


    public List<ThongBao> getThongBaoChuaDoc() {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT TOP 50 * FROM Notifications "
                   + "WHERE trangThai = N'Chưa đọc' "
                   + "ORDER BY thoiGian DESC";
        Connection con = null;
        try {
             con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapThongBao(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông báo chưa đọc: " + e.getMessage());
        }
        return list;
    }


    public List<ThongBao> getThongBaoMoi(int lastId) {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT * FROM Notifications "
                   + "WHERE id > ? AND trangThai = N'Chưa đọc' "
                   + "ORDER BY id ASC";
        Connection con = null;
        try {
             con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, lastId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapThongBao(rs));
                }
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông báo mới: " + e.getMessage());
        }
        return list;
    }


    public int getSoThongBaoChuaDoc() {
        String sql = "SELECT COUNT(*) FROM Notifications WHERE trangThai = N'Chưa đọc'";
        Connection con = null;
        try {
             con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                ps.close();
                return count;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm thông báo: " + e.getMessage());
        }
        return 0;
    }


    public void danhDauDaDoc(int id) {
        String sql = "UPDATE Notifications SET trangThai = N'Đã đọc', daDoc = 1 WHERE id = ?";
        Connection con = null;
        try {
             con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Lỗi đánh dấu đã đọc: " + e.getMessage());
        }
    }


    public void danhDauTatCaDaDoc() {
        String sql = "UPDATE Notifications SET trangThai = N'Đã đọc', daDoc = 1 "
                   + "WHERE trangThai = N'Chưa đọc'";
        Connection con = null;
        try {
             con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Lỗi đánh dấu tất cả đã đọc: " + e.getMessage());
        }
    }



    public boolean khoaMon(String maMonAn, boolean khoa, String maNV) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            // 1. Cập nhật tinhTrang: khóa → tinhTrang = false; mở → tinhTrang = true
            String sqlUpdate = "UPDATE MonAn SET tinhTrang = ? WHERE maMonAn = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setBoolean(1, !khoa); // khóa → false, mở → true
                ps.setString(2, maMonAn);
                ps.executeUpdate();
            }

            // 2. Lấy tên món
            String tenMon = maMonAn;
            String sqlTen = "SELECT tenMonAn FROM MonAn WHERE maMonAn = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlTen)) {
                ps.setString(1, maMonAn);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) tenMon = rs.getString("tenMonAn");
                }
            }

            // 3. Lấy tên nhân viên
            String tenNV = maNV;
            String sqlNV = "SELECT hoTenNV FROM NhanVien WHERE maNV = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlNV)) {
                ps.setString(1, maNV);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) tenNV = rs.getString("hoTenNV");
                }
            }

            // 4. Chèn thông báo
            String tieuDe = khoa ? "Món tạm ngừng" : "Món phục vụ trở lại";
            String noiDung = khoa
                    ? "Món \"" + tenMon + "\" đã bị tạm ngừng phục vụ bởi " + tenNV + "."
                    : "Món \"" + tenMon + "\" đã được phục vụ trở lại bởi " + tenNV + ".";

            String sqlNotif = "INSERT INTO Notifications(tieuDe, noiDung, loai, nguoiGui, thoiGian, daDoc, trangThai) "
                            + "VALUES (?, ?, ?, ?, GETDATE(), 0, N'Chưa đọc')";
            try (PreparedStatement ps = con.prepareStatement(sqlNotif)) {
                ps.setString(1, tieuDe);
                ps.setString(2, noiDung);
                ps.setString(3, khoa ? "KHOA_MON" : "MO_KHOA_MON");
                ps.setString(4, maNV);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi khi khóa/mở khóa món: " + e.getMessage());
            try { if (con != null) con.rollback(); } catch (SQLException ex) {
                System.err.println("Lỗi rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException e) {
                System.err.println("Lỗi phục hồi autoCommit: " + e.getMessage());
            }
        }
    }


    public List<ThongBao> getLichSuThongBao(int limit) {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " * FROM Notifications ORDER BY thoiGian DESC";
        Connection con = null;
        try {
             con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapThongBao(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử thông báo: " + e.getMessage());
        }
        return list;
    }


    private ThongBao mapThongBao(ResultSet rs) throws SQLException {
        ThongBao tb = new ThongBao();
        tb.setId(rs.getInt("id"));
        tb.setTieuDe(rs.getString("tieuDe"));
        tb.setNoiDung(rs.getString("noiDung"));
        tb.setLoai(rs.getString("loai"));
        tb.setNguoiGui(rs.getString("nguoiGui"));
        tb.setThoiGian(rs.getTimestamp("thoiGian"));
        tb.setDaDoc(rs.getBoolean("daDoc"));
        // cột trangThai có thể null nếu bảng cũ chưa có
        try {
            tb.setTrangThai(rs.getString("trangThai"));
        } catch (SQLException ignored) {
            tb.setTrangThai(tb.isDaDoc() ? "Đã đọc" : "Chưa đọc");
        }
        return tb;
    }
}
