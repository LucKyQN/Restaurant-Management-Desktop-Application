package DAO;

import Entity.PhieuDatBan;
import connectDatabase.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatBanDAO {

    private Connection getConnection() throws Exception {
        ConnectDB.getInstance().connect();
        return ConnectDB.getInstance().getConnection();
    }

    // Hàm thêm mới phiếu đặt bàn
    public boolean taoPhieuDatBan(PhieuDatBan phieu) {
        String sql = "INSERT INTO PhieuDatBan (maPhieu, tenKhachHang, soDienThoai, thoiGianDen, soLuongKhach, ghiChu, maBan, trangThai, ngayTao, tienMonDatTruoc, tienCoc) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, N'Chờ khách', GETDATE(), ?, ?)";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, phieu.getMaPhieu());
            ps.setString(2, phieu.getTenKhachHang());
            ps.setString(3, phieu.getSoDienThoai());
            ps.setTimestamp(4, new Timestamp(phieu.getThoiGianDen().getTime()));
            ps.setInt(5, phieu.getSoLuongKhach());
            ps.setString(6, phieu.getGhiChu());
            ps.setString(7, phieu.getMaBan());
            ps.setDouble(8, phieu.getTienMonDatTruoc());
            ps.setDouble(9, phieu.getTienCoc());
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public PhieuDatBan getPhieuDatBanByMaBan(String maBan) {
        PhieuDatBan phieu = null;
        String sql = "SELECT TOP 1 p.* FROM PhieuDatBan p "
                + "LEFT JOIN ChiTietDatBan ct ON p.maPhieu = ct.maPhieu "
                + "WHERE (p.maBan = ? OR ct.maBan = ?) "
                + "AND p.trangThai = N'Chờ khách' "
                + "ORDER BY p.thoiGianDen ASC";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            ps.setString(2, maBan);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                phieu = new PhieuDatBan();
                phieu.setMaPhieu(rs.getString("maPhieu"));
                phieu.setTenKhachHang(rs.getString("tenKhachHang"));
                phieu.setSoDienThoai(rs.getString("soDienThoai"));
                phieu.setThoiGianDen(rs.getTimestamp("thoiGianDen"));
                phieu.setSoLuongKhach(rs.getInt("soLuongKhach"));
                phieu.setGhiChu(rs.getString("ghiChu"));
                phieu.setMaBan(rs.getString("maBan")); 
                phieu.setTienMonDatTruoc(rs.getDouble("tienMonDatTruoc"));
                phieu.setTienCoc(rs.getDouble("tienCoc"));
                phieu.setTrangThai(rs.getString("trangThai"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phieu;
    }

    public List<PhieuDatBan> getDanhSachDatChoChuaCheckIn() {
        List<PhieuDatBan> list = new ArrayList<>();
        String sql = "SELECT p.*, b.tenBan FROM PhieuDatBan p " + "JOIN BanAn b ON p.maBan = b.maBan "
                + "WHERE p.trangThai = N'Chờ khách' " + "ORDER BY p.thoiGianDen ASC";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhieuDatBan p = new PhieuDatBan();
                p.setMaPhieu(rs.getString("maPhieu"));
                p.setTenKhachHang(rs.getString("tenKhachHang"));
                p.setSoDienThoai(rs.getString("soDienThoai"));
                p.setThoiGianDen(rs.getTimestamp("thoiGianDen"));
                p.setSoLuongKhach(rs.getInt("soLuongKhach"));
                p.setGhiChu(rs.getString("ghiChu"));
                p.setMaBan(rs.getString("maBan"));
                p.setTrangThai(rs.getString("trangThai"));
                p.setTenBan(rs.getString("tenBan"));
                p.setTienMonDatTruoc(rs.getDouble("tienMonDatTruoc"));
                p.setTienCoc(rs.getDouble("tienCoc"));
                list.add(p);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean capNhatTrangThaiPhieu(String maPhieu, String trangThaiMoi) {
        String sql = "UPDATE PhieuDatBan SET trangThai = ? WHERE maPhieu = ?";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, trangThaiMoi);
            ps.setString(2, maPhieu);
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 1. HÀM MỚI: KIỂM TRA ĐỤNG GIỜ

    public boolean kiemTraTrungGioDatBan(String maBan, java.util.Date thoiGianKhachMuonDat) {
        boolean biTrung = false;
        String sql = "SELECT p.thoiGianDen FROM PhieuDatBan p "
                   + "LEFT JOIN ChiTietDatBan ct ON p.maPhieu = ct.maPhieu "
                   + "WHERE (p.maBan = ? OR ct.maBan = ?) "
                   + "AND p.trangThai = N'Chờ khách' "
                   + "AND CAST(p.thoiGianDen AS DATE) = CAST(? AS DATE)"; // Chỉ xét cùng 1 ngày
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            ps.setString(2, maBan);
            ps.setTimestamp(3, new Timestamp(thoiGianKhachMuonDat.getTime()));
            ResultSet rs = ps.executeQuery();
            
            long thoiGianMoi = thoiGianKhachMuonDat.getTime();
            
            while (rs.next()) {
                Timestamp tsCu = rs.getTimestamp("thoiGianDen");
                if(tsCu != null) {
                    long thoiGianCu = tsCu.getTime();
                    // Tính khoảng cách giữa 2 giờ đặt bằng Phút (Trị tuyệt đối)
                    long khoangCachPhut = Math.abs(thoiGianMoi - thoiGianCu) / (60 * 1000);
                    
                    // NẾU KHOẢNG CÁCH DƯỚI 150 PHÚT (2.5 TIẾNG) -> BỊ ĐỤNG GIỜ NHAU
                    if (khoangCachPhut < 150) {
                        biTrung = true;
                        break;
                    }
                }
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return biTrung;
    }


    // 2. HÀM MỚI: KIỂM TRA PHIẾU TƯƠNG LAI (Dùng lúc thanh toán khách vãng lai)

    public boolean kiemTraBanCoDatTruocHomNay(String maBan) {
        boolean coDatTruoc = false;
        String sql = "SELECT TOP 1 1 FROM PhieuDatBan p "
                + "LEFT JOIN ChiTietDatBan ct ON p.maPhieu = ct.maPhieu "
                + "WHERE (p.maBan = ? OR ct.maBan = ?) "
                + "AND p.trangThai = N'Chờ khách' "
                + "AND p.thoiGianDen > GETDATE()";

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            ps.setString(2, maBan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                coDatTruoc = true;
            }
            rs.close();
            ps.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return coDatTruoc;
    }


    // Đã đồng bộ tên cột theo bảng PhieuDatBan gốc để tránh lỗi SQL

    public boolean kiemTraBanAnToanChoKhachVangLai(String maBan, int thoiGianDuKienAn_Phut) {
        String sql = "SELECT thoiGianDen FROM PhieuDatBan p "
                   + "LEFT JOIN ChiTietDatBan ct ON p.maPhieu = ct.maPhieu "
                   + "WHERE (p.maBan = ? OR ct.maBan = ?) AND p.trangThai = N'Chờ khách' "
                   + "AND CAST(p.thoiGianDen AS DATE) = CAST(GETDATE() AS DATE)";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maBan);
            ps.setString(2, maBan);
            ResultSet rs = ps.executeQuery();

            long thoiGianAnXong = System.currentTimeMillis() + (thoiGianDuKienAn_Phut * 60 * 1000L);

            while (rs.next()) {
                long gioKhachDat = rs.getTimestamp("thoiGianDen").getTime();
                if (thoiGianAnXong > gioKhachDat) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đã đồng bộ bỏ JOIN KhachHang thừa vì bảng PhieuDatBan lưu trực tiếp tenKhachHang, soDienThoai
    public List<Object[]> getDanhSachDatBanChoLeTan(String boLocNgay, String tuKhoa) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.maPhieu, p.tenKhachHang, p.soDienThoai, b.tenBan, p.thoiGianDen, p.tienCoc " +
                "FROM PhieuDatBan p " +
                "LEFT JOIN BanAn b ON p.maBan = b.maBan " +
                "WHERE p.trangThai = N'Chờ khách' "
        );

        if ("Hôm nay".equals(boLocNgay)) {
            sql.append("AND CAST(p.thoiGianDen AS DATE) = CAST(GETDATE() AS DATE) ");
        } else if ("Ngày mai".equals(boLocNgay)) {
            sql.append("AND CAST(p.thoiGianDen AS DATE) = CAST(DATEADD(day, 1, GETDATE()) AS DATE) ");
        }

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            sql.append("AND (p.soDienThoai LIKE ? OR p.tenKhachHang LIKE ?) ");
        }

        sql.append("ORDER BY p.thoiGianDen ASC");

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql.toString());

            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                String searchPattern = "%" + tuKhoa.trim() + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
            }

            ResultSet rs = ps.executeQuery();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm dd/MM");

            while (rs.next()) {
                String gioDen = rs.getTimestamp("thoiGianDen") != null ? sdf.format(rs.getTimestamp("thoiGianDen")) : "";
                String tienCoc = String.format("%,.0f", rs.getDouble("tienCoc")).replace(",", ".") + "đ";

                list.add(new Object[]{
                        rs.getString("maPhieu"),
                        rs.getString("tenKhachHang"),
                        rs.getString("soDienThoai"),
                        rs.getString("tenBan"),
                        gioDen,
                        tienCoc
                });
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean doiThoiGianDat(String maPhieu, java.sql.Timestamp thoiGianMoi) {
        String sql = "UPDATE PhieuDatBan SET thoiGianDen = ? WHERE maPhieu = ?";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, thoiGianMoi);
            ps.setString(2, maPhieu);
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}