package Entity;

import java.util.Objects;

public class DanhMuc {
	private String maDM;
	private String tenDM;

	public DanhMuc() {
	}

	public DanhMuc(String maDM, String tenDM) {
		this.maDM = maDM;
		this.tenDM = tenDM;
	}

	public String getMaDM() {
		return maDM;
	}

	public void setMaDM(String maDM) {
		this.maDM = maDM;
	}

	public String getTenDM() {
		return tenDM;
	}

	public void setTenDM(String tenDM) {
		this.tenDM = tenDM;
	}

	@Override
	public String toString() {
		return "DanhMuc{" + "maDM='" + maDM + '\'' + ", tenDM='" + tenDM + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DanhMuc))
			return false;
		DanhMuc danhMuc = (DanhMuc) o;
		return Objects.equals(maDM, danhMuc.maDM);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maDM);
	}
}