package Entity;

import java.util.Objects;

public class LoaiBan {
	private String maLB;
	private String tenLB;
	private int soGhe;

	public LoaiBan() {
	}

	public LoaiBan(String maLB, String tenLB, int soGhe) {
		this.maLB = maLB;
		this.tenLB = tenLB;
		this.soGhe = soGhe;
	}

	public String getMaLB() {
		return maLB;
	}

	public void setMaLB(String maLB) {
		this.maLB = maLB;
	}

	public String getTenLB() {
		return tenLB;
	}

	public void setTenLB(String tenLB) {
		this.tenLB = tenLB;
	}

	public int getSoGhe() {
		return soGhe;
	}

	public void setSoGhe(int soGhe) {
		this.soGhe = soGhe;
	}

	public void loaiBan() {
	}

	@Override
	public String toString() {
		return "LoaiBan{" + "maLB='" + maLB + '\'' + ", tenLB='" + tenLB + '\'' + ", soGhe=" + soGhe + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof LoaiBan))
			return false;
		LoaiBan loaiBan = (LoaiBan) o;
		return Objects.equals(maLB, loaiBan.maLB);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maLB);
	}
}
