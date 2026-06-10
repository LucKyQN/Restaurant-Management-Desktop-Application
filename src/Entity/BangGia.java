package Entity;

import java.util.Date;
import java.util.Objects;

public class BangGia {
	private String maBangGia;
	private String tenBangGia;
	private Date ngayBatDau;
	private Date ngayKetThuc;
	private String trangThai;
	private String moTa;

	public BangGia() {
	}

	public BangGia(String maBangGia, String tenBangGia, Date ngayBatDau, Date ngayKetThuc, String moTa,
			String trangThai) {
		this.maBangGia = maBangGia;
		this.tenBangGia = tenBangGia;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
		this.moTa = moTa;
		this.trangThai = trangThai;
	}

	public String getMaBangGia() {
		return maBangGia;
	}

	public void setMaBangGia(String maBangGia) {
		this.maBangGia = maBangGia;
	}

	public String getTenBangGia() {
		return tenBangGia;
	}

	public void setTenBangGia(String tenBangGia) {
		this.tenBangGia = tenBangGia;
	}

	public Date getNgayBatDau() {
		return ngayBatDau;
	}

	public void setNgayBatDau(Date ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}

	public Date getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(Date ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	@Override
	public String toString() {
		return "BangGia{" + "maBangGia='" + maBangGia + '\'' + ", tenBangGia='" + tenBangGia + '\'' + ", trangThai='"
				+ trangThai + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BangGia))
			return false;
		BangGia bangGia = (BangGia) o;
		return Objects.equals(maBangGia, bangGia.maBangGia);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maBangGia);
	}
}
