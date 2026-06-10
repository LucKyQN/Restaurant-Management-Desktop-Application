package Entity;

import java.util.Objects;

public class ChiTietMonAn {
	private String maCTBangGia;
	private double giaBan;
	private MonAn monAn;
	private BangGia bangGia;

	public ChiTietMonAn() {
	}

	public ChiTietMonAn(double giaBan, MonAn monAn, BangGia bangGia) {
		this.giaBan = giaBan;
		this.monAn = monAn;
		this.bangGia = bangGia;
	}

	public String getMaCTBangGia() {
		return maCTBangGia;
	}

	public void setMaCTBangGia(String maCTBangGia) {
		this.maCTBangGia = maCTBangGia;
	}

	public double getGiaBan() {
		return giaBan;
	}

	public void setGiaBan(double giaBan) {
		this.giaBan = giaBan;
	}

	public MonAn getMonAn() {
		return monAn;
	}

	public void setMonAn(MonAn monAn) {
		this.monAn = monAn;
	}

	public BangGia getBangGia() {
		return bangGia;
	}

	public void setBangGia(BangGia bangGia) {
		this.bangGia = bangGia;
	}

	@Override
	public String toString() {
		return "ChiTietMonAn{" + "monAn=" + (monAn != null ? monAn.getMaMonAn() : null) + ", bangGia="
				+ (bangGia != null ? bangGia.getMaBangGia() : null) + ", giaBan=" + giaBan + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ChiTietMonAn))
			return false;
		ChiTietMonAn that = (ChiTietMonAn) o;
		return Objects.equals(monAn, that.monAn) && Objects.equals(bangGia, that.bangGia);
	}

	@Override
	public int hashCode() {
		return Objects.hash(monAn, bangGia);
	}
}