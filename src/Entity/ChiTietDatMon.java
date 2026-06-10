package Entity;

import java.util.Objects;

public class ChiTietDatMon {
	private String maDon;
	private String maMonAn;
	private int soLuong;

	public ChiTietDatMon() {
	}

	public ChiTietDatMon(String maDon, String maMonAn, int soLuong) {
		this.maDon = maDon;
		this.maMonAn = maMonAn;
		this.soLuong = soLuong;
	}

	public String getMaDon() {
		return maDon;
	}

	public void setMaDon(String maDon) {
		this.maDon = maDon;
	}

	public String getMaMonAn() {
		return maMonAn;
	}

	public void setMaMonAn(String maMonAn) {
		this.maMonAn = maMonAn;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	@Override
	public String toString() {
		return "ChiTietDatMon{" + "maDon='" + maDon + '\'' + ", maMonAn='" + maMonAn + '\'' + ", soLuong=" + soLuong
				+ '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ChiTietDatMon))
			return false;
		ChiTietDatMon that = (ChiTietDatMon) o;
		return Objects.equals(maDon, that.maDon) && Objects.equals(maMonAn, that.maMonAn);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maDon, maMonAn);
	}
}