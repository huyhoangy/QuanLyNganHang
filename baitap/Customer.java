package atm.app.baitap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Customer extends Menu implements Comparable<Customer> {
    protected String maKH, ten, matKhau;
    protected int cmnd;
    protected Date ngaySinh;
    protected static int count = 1000;
    static String[] mc = {"Menu 2",
            "Vietcombank Internet Banking welcome !",
            "Đăng ký thêm tài khoản ngân hàng",
            "Xem danh sách tài khoản đang sở hữu",
            "Đăng nhập vào tài khoản ngân hàng",
            "Xóa tài khoản ngân hàng",
             "Thoát"};

    public Customer() {
        super();
    }

    public Customer(String ht, int cmnd, String ngS) {
        if (ht == null || cmnd < 0 || ngS == null) {
            throw new RuntimeException("Lỗi dữ liệu");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        dateFormat.setLenient(false);
        try {
            ngaySinh = dateFormat.parse(ngS);
            ten = ht;
            maKH = "C" + count++;
            matKhau = Integer.toString(cmnd);
            this.cmnd = cmnd;
        } catch (ParseException e) {
            throw new RuntimeException("Ngày sinh không hợp lệ !");
        }
    }

    public Customer(Customer c) {
        if (c == null) {
            throw new RuntimeException("Lỗi dữ liệu !");
        }
        this.maKH = c.maKH;
        this.cmnd = c.cmnd;
        this.matKhau = c.matKhau;
        this.ten = c.ten;
        this.ngaySinh = c.ngaySinh;
    }

    public Customer(String maKH, String ht, String matKhau, int cmnd, String ngS) {
        if (maKH == null || ht == null || matKhau == null || cmnd < 0 || ngS == null) {
            throw new RuntimeException("Lỗi dữ liệu");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            ngaySinh = dateFormat.parse(ngS);
            this.maKH = maKH;
            this.ten = ht;
            this.matKhau = matKhau;
            this.cmnd = cmnd;
        } catch (ParseException e) {
            throw new RuntimeException("Ngày sinh không hợp lệ !");
        }
    }

    public void setMenu() {
        super.setMenu(mc);
    }

    @Override
    public void execute(int n) {
        switch (n) {
            case 1:
                doCreateAcountOfCustomer();
                break;
            case 2:
                showAccountOfCustomer(this);
                break;
            case 3:
                try {
                    Account a = loginAccount();
                    a.setMenu();
                    a.run();
                } catch (Exception e) {
                    System.out.println("Lỗi đăng nhập tài khoản: " + e.getMessage());
                }
                break;
            case 4:
                deleteAccount();
                break;
            case 5:
                Bank b = new Bank(Bank.mc);
                b.run();
                break;
            case 6:
                System.out.println("\t\tVietcombank Internet Banking hẹn gặp lại quý khách !");
                System.exit(0);
        }
    }

    public void doCreateAcountOfCustomer() {
        double soDu = 0;
        int pin = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------- Thêm tài khoản ngân hàng -------");
        System.out.println("Khai báo số dư: ");
        try {
            soDu = Double.parseDouble(sc.nextLine());
            Random rd = new Random();
            pin = 100000 + rd.nextInt((900000 - 100000) + 1);
            Account a = CreateAcountOfCustomer(this, pin, soDu);
            System.out.println("\n>>> Thêm tài khoản thành công !");
            System.out.println("Số tài khoản: " + a.getSoTK() + "\nPin mặc định: " + a.getPin());
            System.out.println("\t------- *** -------");
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: số dư phải là kiểu số !");
        }
    }

    public Account CreateAcountOfCustomer(Customer c, int pin, double soDu) {
        Account a = new Account(c, soDu, pin);
        Bank.addAcountInaList(a);
        Bank.saveData("account.txt");
        return a;
    }

    public void deleteAccount() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------- Xóa tài khoản ngân hàng -------");
        System.out.println("Nhập số tài khoản cần xóa: ");
        int soTK = Integer.parseInt(sc.nextLine());

        Account accountToDelete = null;
        for (Account a : Bank.getaList()) {
            if (a.getSoTK() == soTK) {
                accountToDelete = a;
                break;
            }
        }

        if (accountToDelete != null) {
            Bank.getaList().remove(accountToDelete);
            Bank.saveData("account.txt");
            System.out.println(">>> Xóa tài khoản thành công !");
        } else {
            System.out.println("Tài khoản không tồn tại !");
        }
    }

    public static Account loginAccount() {
        int soTK = 0;
        int pin = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------- Đăng nhập vào số tài khoản ngân hàng -------");

        try {
            System.out.println("Số tài khoản: ");
            soTK = Integer.parseInt(sc.nextLine());
            System.out.println("Số Pin: ");
            pin = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: nhập sai kiểu số !");
        }

        for (Account a : Bank.getaList()) {
            if (a.getSoTK() == soTK && a.getPin() == pin) {
                System.out.println(">>> Đăng nhập thành công !");
                return a;
            }
        }
        throw new RuntimeException("Tài khoản và mật khẩu không đúng");
    }

    public void showAccountOfCustomer(Customer c) {
        boolean check = false;
        System.out.println("\n------- Các số tài khoản của \"" + c.ten + "\" -------");
        System.out.printf("%-7s|  %-16s| %-13s| %-10s", "Mã KH", "Họ tên", "Số tài khoản", "Pin");
        for (Account a : Bank.getaList()) {
            if (a.maKH.equals(c.maKH)) {
                System.out.printf("\n%-7s|  %-16s| %-13d| %-10d", a.maKH, a.ten, a.getSoTK(), a.getPin());
                check = true;
            }
        }
        if (!check) {
            System.out.println("Danh sách rỗng !");
        }
        System.out.println("\n\t --------- *** ---------");
    }

    @Override
    public String toString() {
        return String.format("\n%-7s| %-16s| %-15s", getMaKH(), getTen(), getMatKhau());
    }

    @Override
    public int compareTo(Customer o) {
        return this.ten.compareTo(o.ten);
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public int getCmnd() {
        return cmnd;
    }

    public void setCmnd(int cmnd) {
        this.cmnd = cmnd;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        Customer.count = count;
    }
}
