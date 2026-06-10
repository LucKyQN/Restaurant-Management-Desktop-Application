package Entity;

import java.sql.Timestamp;

public class ThongBao {
    private int id;
    private String tieuDe;
    private String noiDung;
    private String loai;
    private String nguoiGui;
    private Timestamp thoiGian;
    private boolean daDoc;
    private String trangThai; // "Chưa đọc" / "Đã đọc"

    public ThongBao() {
    }

    public ThongBao(String tieuDe, String noiDung, String loai, String nguoiGui) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.loai = loai;
        this.nguoiGui = nguoiGui;
        this.daDoc = false;
        this.trangThai = "Chưa đọc";
    }



    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }

    public String getNguoiGui() { return nguoiGui; }
    public void setNguoiGui(String nguoiGui) { this.nguoiGui = nguoiGui; }

    public Timestamp getThoiGian() { return thoiGian; }
    public void setThoiGian(Timestamp thoiGian) { this.thoiGian = thoiGian; }

    public boolean isDaDoc() { return daDoc; }
    public void setDaDoc(boolean daDoc) { this.daDoc = daDoc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }


    public String getLoaiIcon() {
        if (loai == null) return "🔔";
        return switch (loai.toUpperCase()) {
            case "KHOA_MON"    -> "🔒";
            case "MO_KHOA_MON" -> "🔓";
            case "THANH_TOAN"  -> "💳";
            case "DAT_BAN"     -> "📋";
            case "HUY_DON"     -> "❌";
            case "CANH_BAO"    -> "⚠";
            default            -> "🔔";
        };
    }

    @Override
    public String toString() {
        return "ThongBao{id=" + id + ", tieuDe='" + tieuDe + "', loai='" + loai + "'}";
    }
}
