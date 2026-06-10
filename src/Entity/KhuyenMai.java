package Entity;

import java.util.Date;
import java.util.Objects;

public class KhuyenMai {
	private String maKM;
	private String tenKM;
	private String moTaKM;
	private Date ngayBatDau;
	private Date ngayKetThuc;
	private boolean trangThai;
	private double giaTriKM;
	private String loaiKM;
	private String dieuKienApDung;
	private int soLuongToiThieu;
	private NhanVien quanLy;

	public KhuyenMai() {
	}

	public KhuyenMai(String maKM, String tenKM, String moTaKM, Date ngayBatDau, Date ngayKetThuc, boolean trangThai,
			double giaTriKM, String loaiKM, String dieuKienApDung, int soLuongToiThieu, NhanVien quanLy) {
		this.maKM = maKM;
		this.tenKM = tenKM;
		this.moTaKM = moTaKM;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
		this.trangThai = trangThai;
		this.giaTriKM = giaTriKM;
		this.loaiKM = loaiKM;
		this.dieuKienApDung = dieuKienApDung;
		this.soLuongToiThieu = soLuongToiThieu;
		this.quanLy = quanLy;
	}

	public String getMaKM() {
		return maKM;
	}

	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}

	public String getTenKM() {
		return tenKM;
	}

	public void setTenKM(String tenKM) {
		this.tenKM = tenKM;
	}

	public String getMoTaKM() {
		return moTaKM;
	}

	public void setMoTaKM(String moTaKM) {
		this.moTaKM = moTaKM;
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

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	public double getGiaTriKM() {
		return giaTriKM;
	}

	public void setGiaTriKM(double giaTriKM) {
		this.giaTriKM = giaTriKM;
	}

	public String getLoaiKM() {
		return loaiKM;
	}

	public void setLoaiKM(String loaiKM) {
		this.loaiKM = loaiKM;
	}

	public String getDieuKienApDung() {
		return dieuKienApDung;
	}

	public void setDieuKienApDung(String dieuKienApDung) {
		this.dieuKienApDung = dieuKienApDung;
	}

	public int getSoLuongToiThieu() {
		return soLuongToiThieu;
	}

	public void setSoLuongToiThieu(int soLuongToiThieu) {
		this.soLuongToiThieu = soLuongToiThieu;
	}

	public NhanVien getQuanLy() {
		return quanLy;
	}

	public void setQuanLy(NhanVien quanLy) {
		this.quanLy = quanLy;
	}

	public void khuyenMai() {
	}

	@Override
	public String toString() {
		return "KhuyenMai{" + "maKM='" + maKM + '\'' + ", tenKM='" + tenKM + '\'' + ", loaiKM='" + loaiKM + '\''
				+ ", giaTriKM=" + giaTriKM + ", soLuongToiThieu=" + soLuongToiThieu + ", trangThai=" + trangThai + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof KhuyenMai))
			return false;
		KhuyenMai khuyenMai = (KhuyenMai) o;
		return Objects.equals(maKM, khuyenMai.maKM);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maKM);
	}
}