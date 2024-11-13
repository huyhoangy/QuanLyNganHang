package atm.app.baitap;

public interface MuaVeXemPhim {

    /**
     * Đặt vé xem phim.
     * @return Thông tin đặt vé.
     */
    String datVe();

    /**
     * Hủy vé xem phim.
     * @return Thông báo kết quả hủy vé.
     */
    String huyVe();

    /**
     * Kiểm tra thông tin phim.
     * @return Thông tin phim.
     */
    String kiemTraPhim();

    /**
     * Xem thông tin vé.
     * @return Thông tin chi tiết vé.
     */
    String xemThongTinVe();
}