package Service;

import DAO.NotificationDAO;
import Entity.ThongBao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;


public class NotificationService {

    private static NotificationService instance;

    private final NotificationDAO dao = new NotificationDAO();
    private final List<NotificationListener> listeners = new CopyOnWriteArrayList<>();

    private Timer pollingTimer;
    private int lastKnownId = 0;
    private volatile boolean running = false;



    public interface NotificationListener {
        void onNotificationReceived(List<ThongBao> newNotifications);
        void onNotificationCountChanged(int count);
    }



    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    private NotificationService() {}




    public void startPolling(int intervalMs) {
        if (running) return;
        running = true;


        new SwingWorker<List<ThongBao>, Void>() {
            @Override
            protected List<ThongBao> doInBackground() {
                return dao.getThongBaoChuaDoc();
            }

            @Override
            protected void done() {
                try {
                    List<ThongBao> initial = get();
                    if (!initial.isEmpty()) {
                        lastKnownId = initial.get(0).getId();

                        for (ThongBao tb : initial) {
                            if (tb.getId() > lastKnownId) lastKnownId = tb.getId();
                        }
                    }
                    fireCountChanged(dao.getSoThongBaoChuaDoc());
                } catch (Exception ignored) {}
            }
        }.execute();

        pollingTimer = new Timer(intervalMs, e -> poll());
        pollingTimer.setCoalesce(true);
        pollingTimer.start();
    }


    public void stopPolling() {
        running = false;
        if (pollingTimer != null) {
            pollingTimer.stop();
            pollingTimer = null;
        }
    }

    private void poll() {
        new SwingWorker<List<ThongBao>, Void>() {
            @Override
            protected List<ThongBao> doInBackground() {
                return dao.getThongBaoMoi(lastKnownId);
            }

            @Override
            protected void done() {
                try {
                    List<ThongBao> newOnes = get();
                    if (!newOnes.isEmpty()) {
                        lastKnownId = newOnes.get(newOnes.size() - 1).getId();
                        notifyListeners(newOnes);
                    }
                    fireCountChanged(dao.getSoThongBaoChuaDoc());
                } catch (Exception e) {
                    System.err.println("Lỗi polling notification: " + e.getMessage());
                }
            }
        }.execute();
    }



    public void addListener(NotificationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(List<ThongBao> notifications) {
        for (NotificationListener l : listeners) {
            try { l.onNotificationReceived(notifications); }
            catch (Exception e) { System.err.println("Lỗi trong listener notification: " + e.getMessage()); }
        }
    }

    private void fireCountChanged(int count) {
        for (NotificationListener l : listeners) {
            try { l.onNotificationCountChanged(count); }
            catch (Exception e) { System.err.println("Lỗi fire count: " + e.getMessage()); }
        }
    }




    public void guiKhoaMon(String maMonAn, String tenMon, boolean khoa, String maNV) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return dao.khoaMon(maMonAn, khoa, maNV);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        String loai     = khoa ? "KHOA_MON" : "MO_KHOA_MON";
                        String tieuDe   = khoa ? "Món đã bị khóa" : "Món đã được mở khóa";
                        String noiDung  = khoa
                                ? "Món \"" + tenMon + "\" đã bị khóa bởi " + maNV + ". Không thể phục vụ."
                                : "Món \"" + tenMon + "\" đã được mở khóa bởi " + maNV + ". Có thể phục vụ trở lại.";
                        ThongBao tb = new ThongBao(tieuDe, noiDung, loai, maNV);
                        List<ThongBao> list = new ArrayList<>();
                        list.add(tb);
                        notifyListeners(list);
                        fireCountChanged(dao.getSoThongBaoChuaDoc());
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi gửi thông báo khóa món: " + e.getMessage());
                }
            }
        }.execute();
    }


    public void guiThongBao(String tieuDe, String noiDung, String loai, String maNV) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return dao.guiThongBao(new ThongBao(tieuDe, noiDung, loai, maNV));
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        List<ThongBao> list = new ArrayList<>();
                        list.add(new ThongBao(tieuDe, noiDung, loai, maNV));
                        notifyListeners(list);
                        fireCountChanged(dao.getSoThongBaoChuaDoc());
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi gửi thông báo: " + e.getMessage());
                }
            }
        }.execute();
    }

    public List<ThongBao> getTatCaThongBao() {
        return dao.getLichSuThongBao(100);
    }

    public void danhDauDaDoc(int id) {
        dao.danhDauDaDoc(id);
        SwingUtilities.invokeLater(() -> fireCountChanged(dao.getSoThongBaoChuaDoc()));
    }

    public void danhDauTatCaDaDoc() {
        dao.danhDauTatCaDaDoc();
        SwingUtilities.invokeLater(() -> fireCountChanged(dao.getSoThongBaoChuaDoc()));
    }

    public int getSoChuaDoc() {
        return dao.getSoThongBaoChuaDoc();
    }
}
