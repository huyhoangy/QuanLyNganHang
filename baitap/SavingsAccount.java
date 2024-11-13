package atm.app.baitap;

import java.text.NumberFormat;
import java.util.Locale;

public class SavingsAccount  {
    private static int count = 0;
    private int id;
    private double balance;
    private double interestRate;
    private String startDate;

    public SavingsAccount(double initialDeposit, double interestRate) {
        this.id = ++count;
        this.balance = initialDeposit;
        this.interestRate = interestRate;
        this.startDate = java.time.LocalDate.now().toString();
    }


    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void deposit(double amount, String description) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            throw new RuntimeException("Số dư không đủ để rút tiền từ sổ tiết kiệm!");
        }
        this.balance -= amount;
    }

    public double calculateInterest(int months) {
        return balance * (interestRate / 100) * (months / 12.0);
    }

    @Override
    public String toString() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return String.format("Sổ tiết kiệm ID: %d, Số dư: %s VNĐ, Lãi suất: %.2f%%, Ngày mở: %s",
                id, formatter.format(balance), interestRate, startDate);
    }
}