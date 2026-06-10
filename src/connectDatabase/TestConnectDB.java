package connectDatabase;

public class TestConnectDB {
    public static void main(String[] args) {
        try {
            ConnectDB.getInstance().connect();
            System.out.println("Test kết nối thành công.");
        } catch (Exception e) {
            System.out.println("Kết nối thất bại.");
            e.printStackTrace();
        }
    }
}