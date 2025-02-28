package atm.app.baitap;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class Menu {
    protected ArrayList<String> luaChon = new ArrayList<>(10);

    public Menu() {
    }

    public Menu(String[] inLuaChon) {
        setMenu(inLuaChon);
    }

    public void setMenu(String[] inLuaChon) {
        luaChon.clear();
        for (String lc : inLuaChon) {
            luaChon.add(lc);
        }
    }

    public void display() {
        System.out.println("\n\n\t\t\t" + luaChon.get(1) + "\n");
        System.out.println("-------- " + luaChon.get(0) + " --------");
        for (int i = 2; i < luaChon.size(); i++) {
            System.out.println(i - 1 + "." + luaChon.get(i));
        }
        System.out.println("---------- *** ----------");
    }

    public int getSelected() {
        display();
        Scanner sc = new Scanner(System.in);
        String lc;
        int numberChoose = 0;
        System.out.println("Mời chọn mục: ");
        lc = sc.nextLine();
        try {
            numberChoose = Integer.parseInt(lc);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: Phải nhập vào kiểu số nguyên !");
        }
        return numberChoose;
    }

    public abstract void execute(int n);

    public void run() {
        int lc = -1;
        do {
            lc = getSelected();
            if (lc > luaChon.size() - 2 || lc <= 0) {
                System.out.println("Lựa chọn không đúng !\n\t\tVietcombank hẹn gặp lại quý khách !");
                System.exit(0);
            }
            execute(lc);
        } while (lc <= luaChon.size() - 2 && lc > 0);
    }

    public ArrayList<String> getLuaChon() {
        return luaChon;
    }

    public void setLuaChon(ArrayList<String> luaChon) {
        this.luaChon = luaChon;
    }
}