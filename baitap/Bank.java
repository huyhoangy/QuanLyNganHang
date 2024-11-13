package atm.app.baitap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
public class Bank extends Menu {
    protected static Vector<Account> aList = new Vector<Account>(20, 10);
    protected static Vector<Customer> cList = new Vector<Customer>(20, 10);
    static String[] mc = {"Menu 1", "Vietcombank banking counters welcome !", "Đăng ký thành viên", "Đăng nhập thành viên", "Nộp tiền vào tài khoản", "Xem danh sách thành viên", "Kết thúc"};
    Customer curenrCustomer;
    Account currentAccount;
    public Bank() {
        super(mc);
        loadData("account.txt");
    }
    public Bank(String[] mang) {
        super(mc);
    }

    @Override
    public void execute(int n) {
        switch (n) {
            case 1:
                customerRegistration();
                saveData("account.txt");
                break;
            case 2:
                try {
                    curenrCustomer = customerLogIn();
                    System.out.println(">>> Đăng nhập thành công, xin chào " + curenrCustomer.getTen() + " !");
                } catch (Exception e) {
                    System.out.println("Lỗi đăng nhập: " + e.getMessage());
                }
                try {
                    curenrCustomer.setMenu();
                    curenrCustomer.run();
                } catch (Exception e) {
                    System.out.println("Lỗi menu: " + e.toString());
                }
                break;
            case 3:
                doDepositCash();
                saveData("account.txt");
                break;
            case 4:
                viewCustomerList();
                break;
            case 5:
//                saveData("account.txt");
                System.out.println("\t\tVietcombank hẹn gặp lại quý khách !");
                System.exit(0);
        }
    }

    public void customerRegistration() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------- Đăng ký thành viên -------");

        String ten;
        int cmnd = 0;
        String ngaySinh = null;
        try {
            System.out.println("Nhập tên");
            ten = sc.nextLine();
            System.out.println("Nhập cmnd");
            cmnd = Integer.parseInt(sc.nextLine());
            System.out.println("Nhập ngày sinh dd/mm/yyyy");
            ngaySinh = sc.nextLine();
            Customer c = new Customer(ten, cmnd, ngaySinh);
            cList.add(c);

            //Random rand = new Random();
            //int randomNum = minimum + rand.nextInt((maximum - minimum) + 1);
            Random rd = new Random();
            int pin = 100000 + rd.nextInt((900000 - 100000) + 1);//pin = [100000, 900000]
            double soDu = 10000;
            Account a = new Account(c, soDu, pin);
            aList.add(a);
            System.out.println("\n>>> Đăng ký tài khoản thành công, xin chào " + ten + " !");
            System.out.println("\t------- *** -------");
            System.out.println("Mã KH của bạn: " + c.getMaKH() + "\nMật khẩu mặc định: " + c.getMatKhau());
            DecimalFormat df = new DecimalFormat("#,##0"); // Định dạng số dư
            System.out.println("Bạn được cấp số tài khoản: " + a.getSoTK()+
                    "\nPin mặc định: " + a.getPin() +
                    "\nSố dư mặc định: " + df.format(a.getSoDu()) + " VNĐ");
        } catch (Exception e) {
            System.out.println("Lỗi nhập sai dữ liệu: " + e.getMessage());
        }
    }

    public Customer customerLogIn() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------- Đăng nhập thành viên -------");
        System.out.println("Mã khách hàng: ");
        String maKH = sc.nextLine();
        System.out.println("Mật khẩu: ");
        String matKhau = sc.nextLine();
        for (Customer c : cList) {
            if (maKH.equals(c.maKH) && matKhau.equals(c.matKhau)) {
                return c;
            }
        }
        throw new RuntimeException("Tài khoản và mật khẩu không đúng !");
    }

    public void doDepositCash() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------- Nộp tiền vào tài khoản -------");
        System.out.println("Nhập số tài khoản: ");
        int stk = sc.nextInt();
        Account a = getAccount(stk);
        double tienNop = 0;
        try {
            tienNop = depositCash(a);
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
            return;
        }
        double soDuCu = a.getSoDu();
        a.setSoDu(tienNop + soDuCu);
        System.out.println(">>> Nộp tiền vào tài khoản " + a.getSoTK() + " thành công !");
    }

    public double depositCash(Account a) {
        if (a == null) {
            throw new RuntimeException("Số tài khoản không tồn tại !");
        }
        Scanner sc = new Scanner(System.in);
        double tienNop = 0;
        System.out.println("Số tiền gửi vào tài khoản: ");
        try {
            tienNop = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Tiền phải là kiểu số !");
        }
        String mota = "Nộp tiền vào Tài khoản";
        if (tienNop <= 0) {
            throw new RuntimeException("Tiền nộp vào tài khoản phải > 0");
        }
        a.transactionDiary.add(new Transaction(a, tienNop, "Nộp Tiền", mota));
        return tienNop;
    }

    public void viewCustomerList() {
        System.out.println("\n------- Danh sách thành viên -------");
        if (cList.size() == 0) {
            System.out.println("Danh sách rỗng !");
        } else {
            Collections.sort(cList);
            System.out.printf("%-7s| %-16s| %-15s", "Mã KH", "Họ tên", "Mật khẩu");
            for (int i = 0; i < cList.size(); i++) {
                if (i < cList.size() - 1) {
                    if ((cList.get(i).getMaKH()).equals(cList.get(i + 1).getMaKH())) {
                        continue;
                    }
                }
                System.out.print(cList.get(i));
            }
        }
        System.out.println("\n\t ------- *** -------");
    }

    public static void saveData(String path) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        File fileName = new File(path);
        try {
            fileName.createNewFile();
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            Collections.sort(aList);
            for (Account a : aList) {
                String dateOfBirth = dateFormat.format(a.getNgaySinh());
                bw.write(a.getMaKH() + "::" + a.getTen() + "::" + a.getMatKhau() + "::" + a.getCmnd() + "::" + dateOfBirth
                        + "::" + a.getSoTK() + "::" + a.getPin() + "::" + a.getSoDu() + "\n");
            }
            bw.close();
            fw.close();
        } catch (IOException ex) {
            System.out.println("Lỗi lưu file !" + ex.toString());
        }
    }

    public void loadData(String path) {
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] array = line.trim().split("::");
                Customer c = createCustomer(array);
                cList.add(c);
                Account a = createAccount(c, array);
                aList.add(a);
            }
            br.close();
            fr.close();
        } catch (Exception ex) {
            System.out.println("Lỗi load file: " + ex.toString());
        }
    }

    public Customer createCustomer(String[] array) {
        String maKH = array[0];
        String ht = array[1];
        String matKhau = array[2];
        int cmnd = Integer.parseInt(array[3]);
        String ngaySinh = array[4];
        int count = Integer.parseInt(array[0].trim().substring(1, 5));
        Customer.setCount(++count);
        return new Customer(maKH, ht, matKhau, cmnd, ngaySinh);
    }

    public Account createAccount(Customer c, String[] array) {
        int soTK = Integer.parseInt(array[5]);
        int pin = Integer.parseInt(array[6]);
        double soDu = Double.parseDouble(array[7]);
        int count = soTK;
        //Đảm bảo CountSoTK cuối cùng khi load xong file là lớn nhất để không bị trùng, vì file sắp xếp theo tên Khách hàng
        if (count >= Account.getCountSoTK()) {
            Account.setCountSoTK(++count);
        }
        return new Account(c, soTK, pin, soDu);
    }

    public static Account getAccount(int soTK) {
        for (Account a : aList) {
            if (soTK == a.getSoTK()) {
                return a;
            }
        }
        return null;
    }

    public static void addAcountInaList(Account a) {
        aList.add(a);
    }

    public static Vector<Account> getaList() {
        return aList;
    }

    public static void setaList(Vector<Account> aList) {
        Bank.aList = aList;
    }

    public static Vector<Customer> getcList() {
        return cList;
    }

    public static void setcList(Vector<Customer> cList) {
        Bank.cList = cList;
    }

}
