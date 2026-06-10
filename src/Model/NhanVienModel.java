package Model;

public class NhanVienModel {
	public String maNV;
	public String tenNV;
	public String chucVu;
	public String taiKhoan;
	public String matKhau;

	public NhanVienModel() {
	}

	public NhanVienModel(String maNV, String tenNV, String chucVu) {
		this.maNV = maNV;
		this.tenNV = tenNV;
		this.chucVu = chucVu;
	}

	public String getMaNV() {
		return this.maNV;
	}

	public String getTenNV() {
		return this.tenNV;
	}

	public String getChucVu() {
		return this.chucVu;
	}

	public String getTaiKhoan() {
		return this.taiKhoan;
	}

	public String getMatKhau() {
		return this.matKhau;
	}

	// --- CÁC HÀM SETTER ĐỂ GÁN DỮ LIỆU (Nếu cần) ---
	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}

	public void setTenNV(String tenNV) {
		this.tenNV = tenNV;
	}

	public void setChucVu(String chucVu) {
		this.chucVu = chucVu;
	}

	public void setTaiKhoan(String taiKhoan) {
		this.taiKhoan = taiKhoan;
	}

	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}
}