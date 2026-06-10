package DAO;

import Entity.MonAn;
import Model.BanAnModel;
import Model.MonAnModel;

import java.util.List;

public interface PhucVuService {

	List<BanAnModel> getDanhSachBanChuaThanhToan();

	List<MonAnModel> getChiTietHoaDon(String maHD);

	List<BanAnModel> getDanhSachBanCanPhucVu();

	List<MonAnModel> getMonAnTheoBan(String maBan, String trangThai);


	boolean capNhatSoLuongMon(String maHD, String maMonAn, int soLuongMoi);

	boolean xoaMonKhoiChiTiet(String maHD, String maMonAn);

	boolean capNhatTrangThaiMon(int idCTHD, String trangThaiMoi);

	boolean yeuCauThanhToan(String maHD, String maBan);

	boolean themHoacTangMon(String maHD, String maMonAn, int sl, String ghiChu);

	boolean themMonMoiTachDong(String maHD, String maMonAn, int soLuong, String ghiChu);

	List<MonAn> getMonAnDangPhucVu();
}
