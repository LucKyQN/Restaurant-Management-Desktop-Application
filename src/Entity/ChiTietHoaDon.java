package Entity;

import java.util.Objects;

public class ChiTietHoaDon {
	private int soLuongMonAn;
	private double thanhTien;
	private HoaDon hoaDon;
	private MonAn monAn;
	private double donGia;

	public ChiTietHoaDon() {
	}

	public ChiTietHoaDon(int soLuongMonAn, double thanhTien, HoaDon hoaDon, MonAn monAn, double donGia) {
		this.soLuongMonAn = soLuongMonAn;
		this.thanhTien = thanhTien;
		this.hoaDon = hoaDon;
		this.monAn = monAn;
		this.donGia = donGia;
	}

	public int getSoLuongMonAn() {
		return soLuongMonAn;
	}

	public void setSoLuongMonAn(int soLuongMonAn) {
		this.soLuongMonAn = soLuongMonAn;
	}

	public double getThanhTien() {
		return thanhTien;
	}

	public void setThanhTien(double thanhTien) {
		this.thanhTien = thanhTien;
	}

	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public void setHoaDon(HoaDon hoaDon) {
		this.hoaDon = hoaDon;
	}

	public MonAn getMonAn() {
		return monAn;
	}

	public void setMonAn(MonAn monAn) {
		this.monAn = monAn;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	public void chiTietHoaDon() {
	}

	public double tinhThanhTien() {
		thanhTien = soLuongMonAn * donGia;
		return thanhTien;
	}

	@Override
	public String toString() {
		return "ChiTietHoaDon{" + "hoaDon=" + (hoaDon != null ? hoaDon.getMaHD() : null) + ", monAn="
				+ (monAn != null ? monAn.getMaMonAn() : null) + ", soLuongMonAn=" + soLuongMonAn + ", donGia=" + donGia
				+ ", thanhTien=" + thanhTien + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ChiTietHoaDon))
			return false;
		ChiTietHoaDon that = (ChiTietHoaDon) o;
		return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(monAn, that.monAn);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hoaDon, monAn);
	}
}