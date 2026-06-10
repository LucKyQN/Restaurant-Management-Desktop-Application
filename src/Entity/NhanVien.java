package Entity;

import java.util.Date;
import java.util.Objects;

public class NhanVien {
    private String maNV;
    private double heSoLuong;
    private String soDienThoai;
    private String hoTenNV;
    private Date ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private boolean trangThai;
    private String caLam;
    private String khuVucQuanLy;
    private String khuVucPhucVu;
    private String khuVucTiepTan;

    // Thông tin tài khoản đăng nhập
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;

    public NhanVien() {
    }

    public NhanVien(String maNV, double heSoLuong, String soDienThoai, String hoTenNV,
                    Date ngaySinh, String gioiTinh, String diaChi, boolean trangThai,
                    String caLam, String khuVucQuanLy, String khuVucPhucVu,
                    String khuVucTiepTan, String tenDangNhap, String matKhau, String vaiTro) {
        this.maNV = maNV;
        this.heSoLuong = heSoLuong;
        this.soDienThoai = soDienThoai;
        this.hoTenNV = hoTenNV;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.trangThai = trangThai;
        this.caLam = caLam;
        this.khuVucQuanLy = khuVucQuanLy;
        this.khuVucPhucVu = khuVucPhucVu;
        this.khuVucTiepTan = khuVucTiepTan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public double getHeSoLuong() {
        return heSoLuong;
    }

    public void setHeSoLuong(double heSoLuong) {
        this.heSoLuong = heSoLuong;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getHoTenNV() {
        return hoTenNV;
    }

    public void setHoTenNV(String hoTenNV) {
        this.hoTenNV = hoTenNV;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public String getCaLam() {
        return caLam;
    }

    public void setCaLam(String caLam) {
        this.caLam = caLam;
    }

    public String getKhuVucQuanLy() {
        return khuVucQuanLy;
    }

    public void setKhuVucQuanLy(String khuVucQuanLy) {
        this.khuVucQuanLy = khuVucQuanLy;
    }

    public String getKhuVucPhucVu() {
        return khuVucPhucVu;
    }

    public void setKhuVucPhucVu(String khuVucPhucVu) {
        this.khuVucPhucVu = khuVucPhucVu;
    }

    public String getKhuVucTiepTan() {
        return khuVucTiepTan;
    }

    public void setKhuVucTiepTan(String khuVucTiepTan) {
        this.khuVucTiepTan = khuVucTiepTan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public void suaNhanVien() {
    }

    public void suaMonAn() {
    }

    public void taoKhuyenMai() {
    }

    public void capNhatGia() {
    }

    public void xemBaoCaoDoanhThu() {
    }

    public void datBan() {
    }

    public void xepBan() {
    }

    public void huyDatBan() {
    }

    public void capNhatDatBan() {
    }

    public void goiMon() {
    }

    public void guiBep() {
    }

    public void themMon() {
    }

    public void huyMon() {
    }

    public void chuyenBan() {
    }

    public void gopBan() {
    }

    public void taoHoaDon() {
    }

    public void apDungKhuyenMai() {
    }

    public void inHoaDon() {
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNV='" + maNV + '\'' +
                ", hoTenNV='" + hoTenNV + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", caLam='" + caLam + '\'' +
                ", trangThai=" + trangThai +
                ", tenDangNhap='" + tenDangNhap + '\'' +
                ", vaiTro='" + vaiTro + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NhanVien)) return false;
        NhanVien nhanVien = (NhanVien) o;
        return Objects.equals(maNV, nhanVien.maNV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNV);
    }
}