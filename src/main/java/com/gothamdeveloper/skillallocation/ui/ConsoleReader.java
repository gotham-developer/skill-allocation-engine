package com.gothamdeveloper.skillallocation.ui;

import java.util.Scanner;

public final class ConsoleReader {

    private final Scanner scanner;

    public ConsoleReader() {
        this.scanner = new Scanner(System.in);
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            }

            System.out.println("Please enter a valid number.");
            scanner.nextLine();
        }
    }

    public long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextLong()) {
                long value = scanner.nextLong();
                scanner.nextLine();
                return value;
            }

            System.out.println("Please enter a valid number.");
            scanner.nextLine();
        }
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public void close() {
        scanner.close();
    }

}