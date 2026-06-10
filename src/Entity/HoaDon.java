package Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HoaDon {
	private String maHD;
	private Date ngayGioLap;
	private Date ngayGioHuy;
	private String trangThaiThanhToan;
	private String ghiChu;
	private int soLuongKhach;
	private NhanVien nhanVien;
	private BanAn banAn;
	private KhachHang khachHang;
	private double chietKhau;
	private double VAT;
	private float tongTien;
	private List<DonDatMon> danhSachDon;

	public HoaDon() {
		danhSachDon = new ArrayList<>();
	}

	public HoaDon(String maHD, Date ngayGioLap, Date ngayGioHuy, String trangThaiThanhToan, String ghiChu,
			int soLuongKhach, NhanVien nhanVien, BanAn banAn, KhachHang khachHang, double chietKhau, double VAT,
			float tongTien, List<DonDatMon> danhSachDon) {
		this.maHD = maHD;
		this.ngayGioLap = ngayGioLap;
		this.ngayGioHuy = ngayGioHuy;
		this.trangThaiThanhToan = trangThaiThanhToan;
		this.ghiChu = ghiChu;
		this.soLuongKhach = soLuongKhach;
		this.nhanVien = nhanVien;
		this.banAn = banAn;
		this.khachHang = khachHang;
		this.chietKhau = chietKhau;
		this.VAT = VAT;
		this.tongTien = tongTien;
		this.danhSachDon = danhSachDon;
	}

	public String getMaHD() {
		return maHD;
	}

	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}

	public Date getNgayGioLap() {
		return ngayGioLap;
	}

	public void setNgayGioLap(Date ngayGioLap) {
		this.ngayGioLap = ngayGioLap;
	}

	public Date getNgayGioHuy() {
		return ngayGioHuy;
	}

	public void setNgayGioHuy(Date ngayGioHuy) {
		this.ngayGioHuy = ngayGioHuy;
	}

	public String getTrangThaiThanhToan() {
		return trangThaiThanhToan;
	}

	public void setTrangThaiThanhToan(String trangThaiThanhToan) {
		this.trangThaiThanhToan = trangThaiThanhToan;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public int getSoLuongKhach() {
		return soLuongKhach;
	}

	public void setSoLuongKhach(int soLuongKhach) {
		this.soLuongKhach = soLuongKhach;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	public BanAn getBanAn() {
		return banAn;
	}

	public void setBanAn(BanAn banAn) {
		this.banAn = banAn;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public double getChietKhau() {
		return chietKhau;
	}

	public void setChietKhau(double chietKhau) {
		this.chietKhau = chietKhau;
	}

	public double getVAT() {
		return VAT;
	}

	public void setVAT(double vAT) {
		VAT = vAT;
	}

	public float getTongTien() {
		return tongTien;
	}

	public void setTongTien(float tongTien) {
		this.tongTien = tongTien;
	}

	public List<DonDatMon> getDanhSachDon() {
		return danhSachDon;
	}

	public void setDanhSachDon(List<DonDatMon> danhSachDon) {
		this.danhSachDon = danhSachDon;
	}

	public void hoaDon() {
	}

	public double tinhVAT() {
		return tongTien * VAT / 100;
	}

	public double tinhChietKhau() {
		return tongTien * chietKhau / 100;
	}

	public double tinhTongTien() {
		double tienSauChietKhau = tongTien - tinhChietKhau();
		return tienSauChietKhau + tinhVAT();
	}

	@Override
	public String toString() {
		return "HoaDon{" + "maHD='" + maHD + '\'' + ", ngayGioLap=" + ngayGioLap + ", trangThaiThanhToan='"
				+ trangThaiThanhToan + '\'' + ", soLuongKhach=" + soLuongKhach + ", nhanVien="
				+ (nhanVien != null ? nhanVien.getMaNV() : null) + ", banAn="
				+ (banAn != null ? banAn.getMaBan() : null) + ", khachHang="
				+ (khachHang != null ? khachHang.getMaKH() : null) + ", chietKhau=" + chietKhau + ", VAT=" + VAT
				+ ", tongTien=" + tongTien + ", soDonDatMon=" + (danhSachDon != null ? danhSachDon.size() : 0) + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof HoaDon))
			return false;
		HoaDon hoaDon = (HoaDon) o;
		return Objects.equals(maHD, hoaDon.maHD);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maHD);
	}
}