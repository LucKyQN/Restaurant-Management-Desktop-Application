package Entity;

import java.util.Date;
import java.util.Objects;

public class KhachHang {
	private String maKH;
	private Date ngayThamGia;
	private String soDienThoai;
	private String ten;
	private int diemTichLuy;
	private boolean trangThai;

	public KhachHang() {
	}

	public KhachHang(String maKH, Date ngayThamGia, String soDienThoai, String ten, int diemTichLuy,
			boolean trangThai) {
		this.maKH = maKH;
		this.ngayThamGia = ngayThamGia;
		this.soDienThoai = soDienThoai;
		this.ten = ten;
		this.diemTichLuy = diemTichLuy;
		this.trangThai = trangThai;
	}

	public String getMaKH() {
		return maKH;
	}

	public void setMaKH(String maKH) {
		this.maKH = maKH;
	}

	public Date getNgayThamGia() {
		return ngayThamGia;
	}

	public void setNgayThamGia(Date ngayThamGia) {
		this.ngayThamGia = ngayThamGia;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public int getDiemTichLuy() {
		return diemTichLuy;
	}

	public void setDiemTichLuy(int diemTichLuy) {
		this.diemTichLuy = diemTichLuy;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	public void khachHang() {
	}

	public int tinhDiemTichLuy() {
		return diemTichLuy;
	}

	@Override
	public String toString() {
		return "KhachHang{" + "maKH='" + maKH + '\'' + ", ten='" + ten + '\'' + ", soDienThoai='" + soDienThoai + '\''
				+ ", diemTichLuy=" + diemTichLuy + ", trangThai=" + trangThai + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof KhachHang))
			return false;
		KhachHang khachHang = (KhachHang) o;
		return Objects.equals(maKH, khachHang.maKH);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maKH);
	}
}