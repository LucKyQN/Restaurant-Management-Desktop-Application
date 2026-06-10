package Entity;

import java.util.Date;
import java.util.Objects;

public class DonDatMon {
	private String maDon;
	private Date thoiGianTao;
	private NhanVien nhanVien;
	private String danhSachMon;
	private BanAn ban;
	private String trangThai;

	public DonDatMon() {
	}

	public DonDatMon(String maDon, Date thoiGianTao, NhanVien nhanVien, String danhSachMon, BanAn ban,
			String trangThai) {
		this.maDon = maDon;
		this.thoiGianTao = thoiGianTao;
		this.nhanVien = nhanVien;
		this.danhSachMon = danhSachMon;
		this.ban = ban;
		this.trangThai = trangThai;
	}

	public String getMaDon() {
		return maDon;
	}

	public void setMaDon(String maDon) {
		this.maDon = maDon;
	}

	public Date getThoiGianTao() {
		return thoiGianTao;
	}

	public void setThoiGianTao(Date thoiGianTao) {
		this.thoiGianTao = thoiGianTao;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	public String getDanhSachMon() {
		return danhSachMon;
	}

	public void setDanhSachMon(String danhSachMon) {
		this.danhSachMon = danhSachMon;
	}

	public BanAn getBan() {
		return ban;
	}

	public void setBan(BanAn ban) {
		this.ban = ban;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return "DonDatMon{" + "maDon='" + maDon + '\'' + ", thoiGianTao=" + thoiGianTao + ", nhanVien="
				+ (nhanVien != null ? nhanVien.getMaNV() : null) + ", ban=" + (ban != null ? ban.getMaBan() : null)
				+ ", trangThai='" + trangThai + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DonDatMon))
			return false;
		DonDatMon donDatMon = (DonDatMon) o;
		return Objects.equals(maDon, donDatMon.maDon);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maDon);
	}
}