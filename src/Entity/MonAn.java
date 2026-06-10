package Entity;

import java.util.Objects;

public class MonAn {
	private String maMonAn;
	private String tenMon;
	private double giaMon;
	private int soLuong;
	private String donVi;
	private String moTa;
	private String ghiChu;
	private String anhMon;
	private DanhMuc danhMuc;
	private boolean tinhTrang;

	public MonAn() {
	}

	public MonAn(String maMon, String tenMon, int soLuong, String donVi, String moTa, String ghiChu, String anhMon,
			DanhMuc danhMuc, boolean tinhTrang) {
		this.maMonAn = maMon;
		this.tenMon = tenMon;
		this.soLuong = soLuong;
		this.donVi = donVi;
		this.moTa = moTa;
		this.ghiChu = ghiChu;
		this.anhMon = anhMon;
		this.danhMuc = danhMuc;
		this.tinhTrang = tinhTrang;
	}

	public String getMaMonAn() {
		return maMonAn;
	}

	public void setMaMonAn(String maMonAn) {
		this.maMonAn = maMonAn;
	}

	public String getTenMon() {
		return tenMon;
	}

	public void setTenMon(String tenMon) {
		this.tenMon = tenMon;
	}

	public double getGiaMon() {
		return giaMon;
	}

	public void setGiaMon(double giaMon) {
		this.giaMon = giaMon;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public String getDonVi() {
		return donVi;
	}

	public void setDonVi(String donVi) {
		this.donVi = donVi;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public String getAnhMon() {
		return anhMon;
	}

	public void setAnhMon(String anhMon) {
		this.anhMon = anhMon;
	}

	public DanhMuc getDanhMuc() {
		return danhMuc;
	}

	public void setDanhMuc(DanhMuc danhMuc) {
		this.danhMuc = danhMuc;
	}

	public boolean isTinhTrang() {
		return tinhTrang;
	}

	public void setTinhTrang(boolean tinhTrang) {
		this.tinhTrang = tinhTrang;
	}

	@Override
	public String toString() {
		return "MonAn{" + "maMon='" + maMonAn + '\'' + ", tenMon='" + tenMon + '\'' + ", donVi='" + donVi + '\''
				+ ", soLuong=" + soLuong + ", tinhTrang=" + tinhTrang + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof MonAn))
			return false;
		MonAn monAn = (MonAn) o;
		return Objects.equals(maMonAn, monAn.maMonAn);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maMonAn);
	}
}
