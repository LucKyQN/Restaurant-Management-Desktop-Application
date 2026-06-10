package Entity;

import java.util.Objects;

public class BanAn {
    private String maBan;
    private String tenBan;
    private String moTa;
    private String trangThai;
    private String viTri;
    private int sucChua;
    private LoaiBan loaiBan;

    public BanAn() {
    }

    public BanAn(String maBan, String tenBan, String moTa, String trangThai,
                 String viTri, int sucChua, LoaiBan loaiBan) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.viTri = viTri;
        this.sucChua = sucChua;
        this.loaiBan = loaiBan;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    public LoaiBan getLoaiBan() {
        return loaiBan;
    }

    public void setLoaiBan(LoaiBan loaiBan) {
        this.loaiBan = loaiBan;
    }

    @Override
    public String toString() {
        return "BanAn{" +
                "maBan='" + maBan + '\'' +
                ", tenBan='" + tenBan + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", viTri='" + viTri + '\'' +
                ", sucChua=" + sucChua +
                ", loaiBan=" + (loaiBan != null ? loaiBan.getMaLB() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BanAn)) return false;
        BanAn banAn = (BanAn) o;
        return Objects.equals(maBan, banAn.maBan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maBan);
    }
}