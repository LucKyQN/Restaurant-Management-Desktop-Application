package DAO;

import Entity.ChiTietMonAn;
import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChiTietMonAnDAO {

    private Connection getConnection() throws Exception {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }

    public boolean themChiTiet(ChiTietMonAn ct) {
        String sql = "INSERT INTO ChiTietBangGia (maBangGia, maMonAn, giaBan) VALUES (?, ?, ?)";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, ct.getBangGia().getMaBangGia());
            stmt.setString(2, ct.getMonAn().getMaMonAn());
            stmt.setDouble(3, ct.getGiaBan());
            
            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getGiaMonTheoBangGia(String maMon, String maBangGia) {
        double gia = -1; // -1 biểu thị không tìm thấy giá trong bảng giá đặc biệt
        String sql = "SELECT giaBan FROM ChiTietBangGia WHERE maMonAn = ? AND maBangGia = ?";
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maMon);
            stmt.setString(2, maBangGia);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                gia = rs.getDouble("giaBan");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gia;
    }

    public List<Object[]> getDanhSachMonTheoBangGia(String maBangGia) {
        List<Object[]> list = new ArrayList<>();
        String sql;
        if ("BG001".equals(maBangGia)) {
            sql = "SELECT maMonAn, tenMonAn AS tenMon, giaBan AS giaGoc, giaBan AS giaMoi FROM MonAn";
        } else {
            sql = "SELECT m.maMonAn, m.tenMonAn AS tenMon, m.giaBan AS giaGoc, ct.giaBan AS giaMoi "
                + "FROM MonAn m "
                + "JOIN ChiTietBangGia ct ON m.maMonAn = ct.maMonAn "
                + "WHERE ct.maBangGia = ?";
        }
        
        try {
            Connection con = getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            if (!"BG001".equals(maBangGia)) {
                stmt.setString(1, maBangGia);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getString("maMonAn"),
                    rs.getString("tenMon"),
                    rs.getDouble("giaGoc"),
                    rs.getDouble("giaMoi")
                };
                list.add(row);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean apDungGiaHangLoat(String maBangGia, double phanTram) {
        String sqlDel = "DELETE FROM ChiTietBangGia WHERE maBangGia = ?";
        String sqlIns = "INSERT INTO ChiTietBangGia (maBangGia, maMonAn, giaBan) "
                      + "SELECT ?, maMonAn, giaBan + (giaBan * ? / 100.0) FROM MonAn";
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            
            PreparedStatement stmtDel = con.prepareStatement(sqlDel);
            stmtDel.setString(1, maBangGia);
            stmtDel.executeUpdate();
            stmtDel.close();
            
            PreparedStatement stmtIns = con.prepareStatement(sqlIns);
            stmtIns.setString(1, maBangGia);
            stmtIns.setDouble(2, phanTram);
            stmtIns.executeUpdate();
            stmtIns.close();
            
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
