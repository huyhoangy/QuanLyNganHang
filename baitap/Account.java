package atm.app.baitap;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static atm.app.baitap.Bank.saveData;

public class Account extends Customer implements MuaVeXemPhim {
    private int soTK, pin;
    private Double soDu;
    private static int countSoTK = 10000;
    private Scanner scanner = new Scanner(System.in);
    DecimalFormat df = new DecimalFormat("#,###");
    private List<SavingsAccount> savingsAccounts = new ArrayList<>();
    protected List<Transaction> transactionDiary = new ArrayList<>();
    private static final String[] menu = {
            "Menu 3",
            "Giao dịch tại ATM Vietcombank",
            "Rút tiền",
            "Chuyển tiền",
            "Đổi pin",
            "Xem số dư",
            "Xem nhật ký giao dịch",
            "Mở sổ tiết kiệm",
            "Xem sổ tiết kiệm",
            "Gửi tiền",
            "Tính lãi",
            "Mua vé xem phim",
            "Thoát"
    };

    public Account() {
        super();
    }

    public Account(String ht, int cmnd, String ngS, double sd, int pin) {
        super(ht, cmnd, ngS);
        this.pin = pin;
        this.soDu = sd;
        this.soTK = countSoTK++;
    }

    public Account(Customer c, double sd, int pin) {
        super(c);
        this.soDu = sd;
        this.pin = pin;
        this.soTK = countSoTK++;
    }

    public Account(Customer c, int soTK, int pin, double sd) {
        super(c);
        this.soTK = soTK;
        this.soDu = sd;
        this.pin = pin;
    }

    public Account login(int soTK, int pin) {
        if (this.soTK == soTK && this.pin == pin) {
            return this;
        }
        return null;
    }

    @Override
    public void setMenu() {
        super.setMenu(menu);
    }

    public void start() {
        int option;
        do {
            System.out.println("-------- Giao dịch tại ATM Vietcombank --------");
            System.out.println("Chọn một tùy chọn:");

            // Hiển thị menu
            for (int i = 0; i < menu.length; i++) {
                System.out.println((i + 1) + ". " + menu[i]);
            }

            System.out.print("Lựa chọn của bạn: ");
            try {
                option = Integer.parseInt(scanner.nextLine());
                if (option < 1 || option > menu.length) {
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn từ 1 đến " + menu.length + ".");
                } else {
                    execute(option);
                }
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập một số hợp lệ.");
                option = -1;
            }

        } while (option != menu.length);

        System.out.println("\t\tVietcombank ATM hẹn gặp lại quý khách !");
    }

    public void withdraw() {
        System.out.println("\n\t\t------- Rút tiền -------");
        System.out.print("Số tiền cần rút: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            if (amount <= 0) {
                System.out.println("Lỗi: Số tiền cần rút phải lớn hơn 0!");
                return;
            }

            if (withdraw(amount, "Rút tiền mặt tại ATM")) {
                System.out.println(">>> Rút tiền thành công !");
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: nhập sai kiểu số !");
        }
    }

    public boolean withdraw(double amount, String description) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        double maxDebt = 10000000;

        if (amount > soDu) {
            double debt = amount - soDu;
            if (debt > maxDebt) {
                System.out.println("Lỗi: Số tiền ghi nợ không được vượt quá " + formatter.format(maxDebt) + " VNĐ!");
                return false;
            }
            System.out.println("Số dư không đủ, ghi nợ số tiền: - " + formatter.format(debt));
            transactionDiary.add(new Transaction(this, amount, "Rút Tiền", description));
            soDu -= amount;
            return true;
        } else {
            transactionDiary.add(new Transaction(this, amount, "Rút Tiền", description));
            soDu -= amount;
            return true;
        }
    }

    public void transferMoney() {
        System.out.println("\n\t\t------- Chuyển tiền -------");
        try {
            System.out.print("Số tài khoản nhận tiền: ");
            int targetAccountNumber = Integer.parseInt(scanner.nextLine());
            System.out.print("Số tiền cần chuyển: ");
            double amount = Double.parseDouble(scanner.nextLine());
            System.out.print("Nội dung chuyển tiền: ");
            String description = scanner.nextLine();

            Account targetAccount = Bank.getAccount(targetAccountNumber);
            transfer(targetAccount, amount, description);
            System.out.println(">>> Chuyển khoản thành công !");
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: nhập sai kiểu số !");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void transfer(Account targetAccount, double amount, String description) {
        if (targetAccount == null) {
            throw new RuntimeException("Không tìm thấy tài khoản nhận tiền !");
        }
        if (targetAccount == this) {
            throw new RuntimeException("Tài khoản nhận tiền phải khác tài khoản chuyển tiền !");
        }
        if (amount > soDu) {
            throw new RuntimeException("Số dư không đủ để chuyển tiền !");
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        transactionDiary.add(new Transaction(this, amount, "Chuyển Khoản", description));
        transactionDiary.add(new Transaction(targetAccount, amount, "Nhận Chuyển Khoản", description));
        soDu -= amount;
        targetAccount.soDu += amount;
        System.out.println("Số tiền đã chuyển: " + formatter.format(amount));
    }

    public void changePin() {
        try {
            System.out.println("\n\t\t------- Đổi Pin -------");
            System.out.print("Nhập pin cũ: ");
            int oldPin = Integer.parseInt(scanner.nextLine());
            if (oldPin != this.pin) {
                System.out.println("Pin cũ không chính xác !");
                return;
            }
            System.out.print("Nhập pin mới: ");
            int newPin = Integer.parseInt(scanner.nextLine());
            System.out.print("Nhập lại pin mới: ");
            int confirmPin = Integer.parseInt(scanner.nextLine());
            if (newPin < 100000) {
                System.out.println("Pin phải có 6 chữ số !");
                return;
            }
            if (newPin != confirmPin) {
                System.out.println("Pin nhập lại không chính xác !");
                return;
            }
            updatePin(newPin, "Đổi pin tại ATM");
            System.out.println(">>> Đổi pin thành công !");
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: Nhập sai kiểu số !");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }


    }

    public void updatePin(int newPin, String description) {
        if (newPin == this.pin) {
            throw new RuntimeException("Pin mới phải khác Pin cũ !");
        }
        transactionDiary.add(new Transaction(this, 0, "Đổi Pin", description));
        this.pin = newPin;
        saveData("account.txt");
    }

    public void checkBalance() {
        DecimalFormat df = new DecimalFormat("#,###");
        System.out.println("\n\n\t\t------- Kiểm tra số dư -------");
        System.out.printf("%-10s| %-10s%n", "Tài khoản", "Số dư");
        System.out.printf("%-10d| %-10s VNĐ%n", this.soTK, df.format(this.soDu));
    }

    public void viewTransactionDiary() {
        System.out.println("\n------- Nhật ký GD của tài khoản \"" + this.soTK + "\" -------");
        System.out.printf("%-5s| %-28s| %-8s| %-15s| %-15s| %-25s%n", "ID", "Thời gian", "Số TK", "Số tiền", "Loại GD", "Mô tả");
        for (Transaction transaction : transactionDiary) {
            if (transaction.getAcc().getSoTK() == this.getSoTK()) {
                System.out.print(transaction.toString());
            }
        }
        if (transactionDiary.isEmpty()) {
            System.out.println("\nBạn chưa có giao dịch nào trên hệ thống ATM !");
        }
    }

    public void openSavingsAccount() {
        System.out.println("\n\t\t------- Mở sổ tiết kiệm -------");
        try {
            System.out.print("Số tiền gửi ban đầu: ");
            double initialDeposit = Double.parseDouble(scanner.nextLine());
            System.out.print("Lãi suất (%): ");
            double interestRate = Double.parseDouble(scanner.nextLine());

            SavingsAccount savingsAccount = new SavingsAccount(initialDeposit, interestRate);
            savingsAccounts.add(savingsAccount);
            System.out.println(">>> Mở sổ tiết kiệm thành công !");
            System.out.println(savingsAccount);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: nhập sai kiểu số !");
        }
    }

    public void viewSavingsAccounts() {
        System.out.println("\n\t\t------- Danh sách sổ tiết kiệm -------");
        if (savingsAccounts.isEmpty()) {
            System.out.println("Bạn chưa có sổ tiết kiệm nào.");
        } else {
            for (SavingsAccount sa : savingsAccounts) {
                System.out.println(sa);
            }
            System.out.print("Nhập ID sổ tiết kiệm để rút tiền (hoặc 0 để quay lại): ");
            int id = Integer.parseInt(scanner.nextLine());

            if (id != 0) {
                SavingsAccount savingsAccount = findSavingsAccountById(id);
                if (savingsAccount != null) {
                    System.out.print("Nhập số tiền cần rút: ");
                    double amount = Double.parseDouble(scanner.nextLine());
                    if (amount <= 0) {
                        System.out.println("Lỗi: Số tiền cần rút phải lớn hơn 0!");
                    } else {
                        if (savingsAccount.withdraw(amount)) {
                            System.out.println(">>> Rút tiền từ sổ tiết kiệm thành công!");
                        } else {
                            System.out.println(">>> Rút tiền không thành công. Vui lòng kiểm tra số dư.");
                        }
                    }
                } else {
                    System.out.println("Lỗi: Không tìm thấy sổ tiết kiệm với ID đã nhập!");
                }
            }
        }
    }

    private SavingsAccount findSavingsAccountById(int savingsAccountId) {
        for (SavingsAccount account : savingsAccounts) {
            if (account.getId() == savingsAccountId) {
                return account;
            }
        }
        return null;
    }

    public void calculateInterestForSavingsAccounts() {
        try {
            System.out.println("\n\t\t------- Tính lãi suất sổ tiết kiệm -------");
            System.out.print("Nhập số tháng: ");

            int months = Integer.parseInt(scanner.nextLine());
            if (months <= 0) {
                System.out.println("Số tháng phải lớn hơn 0 !");
                return;
            }

            if (savingsAccounts.isEmpty()) {
                System.out.println("Bạn chưa có sổ tiết kiệm nào để tính lãi suất.");
                return;
            }

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

            for (SavingsAccount sa : savingsAccounts) {
                double interest = sa.calculateInterest(months);
                System.out.printf("Sổ tiết kiệm ID: %d, Lãi suất trong %d tháng: %s%n",
                        sa.getId(), months, formatter.format(interest));
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: nhập sai kiểu số !");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void deposit() {
        System.out.println("\n\t\t------- Gửi tiền -------");
        System.out.print("Số tiền cần gửi: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            System.out.print("Chọn loại tài khoản (1: Tài khoản hiện tại, 2: Sổ tiết kiệm): ");
            int accountType = Integer.parseInt(scanner.nextLine());
            String description = "Gửi tiền vào tài khoản tại ATM";

            if (accountType == 1) {
                deposit(amount, description);
                System.out.println(">>> Gửi tiền vào tài khoản hiện tại thành công !");
            } else if (accountType == 2) {
                System.out.print("Nhập ID sổ tiết kiệm: ");
                String savingsAccountId = scanner.nextLine().trim();
                deposit(amount, description, savingsAccountId);
            } else {
                System.out.println("Lỗi: Loại tài khoản không hợp lệ!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: nhập sai kiểu số !");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public void deposit(double amount, String description) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        transactionDiary.add(new Transaction(this, amount, "Gửi Tiền", description));
        System.out.println("Số tiền gửi: " + formatter.format(amount));
        this.soDu += amount;
    }

    public void deposit(double amount, String description, String savingsAccountId) {
        SavingsAccount savingsAccount = findSavingsAccountById(savingsAccountId);
        if (savingsAccount != null) {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            savingsAccount.deposit(amount, description);
            System.out.println("Số tiền gửi vào sổ tiết kiệm: " + formatter.format(amount));
        } else {
            System.out.println("Lỗi: Không tìm thấy sổ tiết kiệm với ID đã nhập!");
        }
    }

    private SavingsAccount findSavingsAccountById(String savingsAccountId) {
        // Chuyển đổi savingsAccountId sang int nếu ID là kiểu số
        int idToFind = Integer.parseInt(savingsAccountId); // Giả định savingsAccountId là một chuỗi

        for (SavingsAccount account : savingsAccounts) {
            if (account.getId() == idToFind) { // So sánh trực tiếp với giá trị int
                return account; // Trả về tài khoản nếu tìm thấy
            }
        }
        return null;
    }

    @Override
    public void execute(int option) {
        switch (option) {
            case 1:
                withdraw();
                break;
            case 2:
                transferMoney();
                break;
            case 3:
                changePin();
                break;
            case 4:
                checkBalance();
                break;
            case 5:
                viewTransactionDiary();
                break;
            case 6:
                openSavingsAccount();
                break;
            case 7:
                viewSavingsAccounts();
                break;
            case 8:
                deposit();
                break;
            case 9:
                calculateInterestForSavingsAccounts();
                break;
            case 10:
                manageMovieTickets();
                break;
            case 11:
                System.out.println("\t\tVietcombank ATM hẹn gặp lại quý khách !");
                System.exit(0);
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    public int getSoTK() {
        return soTK;
    }

    public void setSoTK(int soTK) {
        this.soTK = soTK;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public Double getSoDu() {
        return soDu;
    }

    public void setSoDu(Double soDu) {
        this.soDu = soDu;
    }

    public static int getCountSoTK() {
        return countSoTK;
    }

    public static void setCountSoTK(int countSoTK) {
        Account.countSoTK = countSoTK;
    }

    public List<Transaction> getTransactionDiary() {
        return transactionDiary;
    }

    @Override
    public String toString() {
        return "Account{" + "soTK=" + soTK + ", pin=" + pin + ", soDu=" + soDu + '}';
    }

    @Override
    public String datVe() {
        try {
            System.out.println("\n\t\t------- Đặt Vé Xem Phim -------");

            System.out.print("Nhập tên phim: ");
            String tenPhim = scanner.nextLine();

            System.out.print("Nhập số lượng vé cần đặt: ");
            int soLuongVe = Integer.parseInt(scanner.nextLine());

            System.out.print("Nhập vị trí ghế (ví dụ: A1, B2): ");
            String viTriGhe = scanner.nextLine();

            System.out.print("Nhập thời gian xem phim (ví dụ: 20:00): ");
            String thoiGian = scanner.nextLine();

            double giaVe = 100000;
            double tongTien = giaVe * soLuongVe;

            if (tongTien > this.soDu) {
                System.out.println("Số dư không đủ để đặt vé. Cần thêm " + (tongTien - this.soDu) + " VNĐ.");
                return "Đặt vé không thành công!";
            }
            this.soDu -= tongTien;

            String description = "Đặt vé cho phim \"" + tenPhim + "\", số ghế: " + viTriGhe + ", số lượng: " + soLuongVe + ", thời gian: " + thoiGian;
            Transaction transaction = new Transaction(this, tongTien, "Mua Vé", description);
            transactionDiary.add(transaction);

            System.out.println(">>> Đặt vé xem phim thành công! Mã vé của bạn là: " + transaction.getLogID());
            return "Vé đã được đặt!";
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: Nhập sai kiểu số !");
            return "Đặt vé không thành công!";
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
            return "Đặt vé không thành công!";
        }
    }

    @Override
    public String huyVe() {
        System.out.println("\n\t\t------- Hủy Vé Xem Phim -------");
        System.out.print("Nhập mã vé (hoặc mô tả) cần hủy: ");
        String maVe = scanner.nextLine();
        for (Transaction transaction : transactionDiary) {
            if (transaction.getLogID() == Integer.parseInt(maVe) && transaction.getLoaiGD().equals("Mua Vé")) {
                transactionDiary.remove(transaction);
                this.soDu += transaction.getSoTien();
                System.out.println(">>> Hủy vé thành công!");
                return "Vé đã được hủy!";
            }
        }
        System.out.println("Lỗi: Không tìm thấy vé với mã đã nhập!");
        return "Hủy vé không thành công!";
    }

    @Override
    public String kiemTraPhim() {
        return "Thông tin phim: [Tên phim, Thời gian, Địa điểm...]";
    }

    @Override
    public String xemThongTinVe() {
        return "Thông tin vé: [Mã vé, Tên phim, Ghế ngồi...]";
    }

    public void manageMovieTickets() {
        int option;
        do {
            System.out.println("\n\t\t------- Quản lý Vé Xem Phim -------");
            System.out.println("Chọn một tùy chọn:");
            System.out.println("1. Đặt vé xem phim");
            System.out.println("2. Hủy vé xem phim");
            System.out.println("3. Kiểm tra thông tin phim");
            System.out.println("4. Xem thông tin vé");
            System.out.println("5. Quay lại");

            System.out.print("Lựa chọn của bạn: ");
            try {
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1:
                        datVe();
                        break;
                    case 2:
                        huyVe();
                        break;
                    case 3:
                        String movieInfo = kiemTraPhim();
                        System.out.println(movieInfo);
                        break;
                    case 4:
                        String ticketInfo = xemThongTinVe();
                        System.out.println(ticketInfo);
                        break;
                    case 5:
                        System.out.println("Quay lại menu chính.");
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Nhập sai kiểu số !");
                option = -1;
            }
        } while (option != 5); 
    }
}