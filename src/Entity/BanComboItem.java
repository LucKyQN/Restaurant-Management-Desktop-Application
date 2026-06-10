package Entity;

public class BanComboItem {
    private String maBan;
    private String tenBan;

    public BanComboItem(String maBan, String tenBan) {
        this.maBan = maBan;
        this.tenBan = tenBan;
    }

    public String getMaBan() { return maBan; }
    public String getTenBan() { return tenBan; }

    @Override
    public String toString() {
        return tenBan; 
    }
}