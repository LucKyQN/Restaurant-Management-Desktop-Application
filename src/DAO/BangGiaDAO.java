package DAO;

import Entity.BangGia;
import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BangGiaDAO {

    private Connection getConnection() throws Exception {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }

    public List<BangGia> getAllBangGia() {
        List<BangGia> list = new ArrayList<>();
        String sql = "SELECT * FROM BangGia"; 
        
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BangGia bg = new BangGia();
                bg.setMaBangGia(rs.getString("maBangGia"));
                bg.setTenBangGia(rs.getString("tenBangGia"));
                bg.setNgayBatDau(rs.getDate("ngayBatDau"));
                bg.setNgayKetThuc(rs.getDate("ngayKetThuc"));
                bg.setMoTa(rs.getString("moTa"));
                bg.setTrangThai(rs.getBoolean("trangThai") ? "Đang hoạt động" : "Ngừng hoạt động");
                list.add(bg);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean themBangGia(BangGia bg) {
        String sql = "INSERT INTO BangGia (maBangGia, tenBangGia, ngayBatDau, ngayKetThuc, moTa, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, bg.getMaBangGia());
            stmt.setString(2, bg.getTenBangGia());
            stmt.setDate(3, new java.sql.Date(bg.getNgayBatDau().getTime()));
            stmt.setDate(4, new java.sql.Date(bg.getNgayKetThuc().getTime()));
            stmt.setString(5, bg.getMoTa());
            boolean isHoatDong = bg.getTrangThai() != null && bg.getTrangThai().equalsIgnoreCase("Đang hoạt động");
            stmt.setBoolean(6, isHoatDong);
            
            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public BangGia getBangGiaHienHanh() {
        BangGia bg = null;
        // Dựa vào điều kiện ngayBatDau <= GETDATE() và ngayKetThuc >= GETDATE()
        // Ưu tiên lấy bảng giá tạo sau cùng (ORDER BY maBangGia DESC)
        String sql = "SELECT TOP 1 * FROM BangGia WHERE ngayBatDau <= GETDATE() AND ngayKetThuc >= GETDATE() ORDER BY maBangGia DESC";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                bg = new BangGia();
                bg.setMaBangGia(rs.getString("maBangGia"));
                bg.setTenBangGia(rs.getString("tenBangGia"));
                bg.setNgayBatDau(rs.getDate("ngayBatDau"));
                bg.setNgayKetThuc(rs.getDate("ngayKetThuc"));
                bg.setMoTa(rs.getString("moTa"));
                bg.setTrangThai(rs.getBoolean("trangThai") ? "Đang hoạt động" : "Ngừng hoạt động");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bg;
    }

    public String phatSinhMaBangGia() {
        String maMoi = "BG001";
        String sql = "SELECT TOP 1 maBangGia FROM BangGia ORDER BY maBangGia DESC";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String maCu = rs.getString("maBangGia");
                if (maCu != null && maCu.length() > 2) {
                    try {
                        int so = Integer.parseInt(maCu.substring(2)) + 1;
                        maMoi = String.format("BG%03d", so);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maMoi;
    }

    public boolean doiTrangThaiBangGia(String maBangGia, int trangThaiMoi) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            
            if (trangThaiMoi == 1) {
                // Tắt tất cả bảng giá
                String sql1 = "UPDATE BangGia SET trangThai = 0";
                PreparedStatement stmt1 = con.prepareStatement(sql1);
                stmt1.executeUpdate();
                stmt1.close();
                
                // Bật bảng giá được chọn
                String sql2 = "UPDATE BangGia SET trangThai = 1 WHERE maBangGia = ?";
                PreparedStatement stmt2 = con.prepareStatement(sql2);
                stmt2.setString(1, maBangGia);
                stmt2.executeUpdate();
                stmt2.close();
            } else {
                // Tắt bảng giá hiện tại
                String sql1 = "UPDATE BangGia SET trangThai = 0 WHERE maBangGia = ?";
                PreparedStatement stmt1 = con.prepareStatement(sql1);
                stmt1.setString(1, maBangGia);
                stmt1.executeUpdate();
                stmt1.close();
                
                // Bật bảng giá mặc định BG001
                String sql2 = "UPDATE BangGia SET trangThai = 1 WHERE maBangGia = 'BG001'";
                PreparedStatement stmt2 = con.prepareStatement(sql2);
                stmt2.executeUpdate();
                stmt2.close();
            }
            
            con.commit();
            return true;
        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ex) {}
        }
    }

    public boolean suaBangGia(BangGia bg) {
        String sql = "UPDATE BangGia SET tenBangGia = ?, ngayBatDau = ?, ngayKetThuc = ? WHERE maBangGia = ?";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, bg.getTenBangGia());
            stmt.setDate(2, new java.sql.Date(bg.getNgayBatDau().getTime()));
            stmt.setDate(3, new java.sql.Date(bg.getNgayKetThuc().getTime()));
            stmt.setString(4, bg.getMaBangGia());
            
            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoaBangGia(String maBangGia) {
        Connection con = null;
        try {
            con = getConnection();
            
            // Bước 1: Kiểm tra xem bảng giá này có đang hoạt động không
            boolean dangHoatDong = false;
            String sqlCheck = "SELECT trangThai FROM BangGia WHERE maBangGia = ?";
            PreparedStatement stmtCheck = con.prepareStatement(sqlCheck);
            stmtCheck.setString(1, maBangGia);
            ResultSet rsCheck = stmtCheck.executeQuery();
            if (rsCheck.next()) {
                dangHoatDong = rsCheck.getBoolean("trangThai");
            }
            rsCheck.close();
            stmtCheck.close();
            
            con.setAutoCommit(false);
            
            // Bước 2: Xóa chi tiết bảng giá
            String sqlDelCT = "DELETE FROM ChiTietBangGia WHERE maBangGia = ?";
            PreparedStatement stmtDelCT = con.prepareStatement(sqlDelCT);
            stmtDelCT.setString(1, maBangGia);
            stmtDelCT.executeUpdate();
            stmtDelCT.close();
            
            // Bước 3: Xóa bảng giá
            String sqlDelBG = "DELETE FROM BangGia WHERE maBangGia = ?";
            PreparedStatement stmtDelBG = con.prepareStatement(sqlDelBG);
            stmtDelBG.setString(1, maBangGia);
            stmtDelBG.executeUpdate();
            stmtDelBG.close();
            
            // Bước 4: Fallback về mặc định nếu bảng giá bị xóa đang hoạt động
            if (dangHoatDong) {
                String sqlFallback = "UPDATE BangGia SET trangThai = 1 WHERE maBangGia = 'BG001'";
                PreparedStatement stmtFallback = con.prepareStatement(sqlFallback);
                stmtFallback.executeUpdate();
                stmtFallback.close();
            }
            
            con.commit();
            return true;
        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ex) {}
        }
    }
}
