package Entity;

import java.util.Objects;

public class HoaDonKhuyenMai {
	private double phanTramKM;
	private HoaDon hoaDon;
	private KhuyenMai khuyenMai;

	public HoaDonKhuyenMai() {
	}

	public HoaDonKhuyenMai(double phanTramKM, HoaDon hoaDon, KhuyenMai khuyenMai) {
		this.phanTramKM = phanTramKM;
		this.hoaDon = hoaDon;
		this.khuyenMai = khuyenMai;
	}

	public double getPhanTramKM() {
		return phanTramKM;
	}

	public void setPhanTramKM(double phanTramKM) {
		this.phanTramKM = phanTramKM;
	}

	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public void setHoaDon(HoaDon hoaDon) {
		this.hoaDon = hoaDon;
	}

	public KhuyenMai getKhuyenMai() {
		return khuyenMai;
	}

	public void setKhuyenMai(KhuyenMai khuyenMai) {
		this.khuyenMai = khuyenMai;
	}

	public void hoaDonKM() {
	}

	@Override
	public String toString() {
		return "HoaDonKhuyenMai{" + "phanTramKM=" + phanTramKM + ", hoaDon="
				+ (hoaDon != null ? hoaDon.getMaHD() : null) + ", khuyenMai="
				+ (khuyenMai != null ? khuyenMai.getMaKM() : null) + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof HoaDonKhuyenMai))
			return false;
		HoaDonKhuyenMai that = (HoaDonKhuyenMai) o;
		return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(khuyenMai, that.khuyenMai);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hoaDon, khuyenMai);
	}
}