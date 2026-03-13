package com.crisafe;

public class Main {

    public static void main(String[] args) {
        CryptoService crypto = new CryptoService();

        System.out.print("Enter password to encode: ");
        String password = System.console() != null
                ? new String(System.console().readPassword())
                : new java.util.Scanner(System.in).nextLine();

        String hash = crypto.encode(password);
        System.out.println("Encoded hash: " + hash);

        System.out.print("Re-enter password to verify: ");
        String verify = System.console() != null
                ? new String(System.console().readPassword())
                : new java.util.Scanner(System.in).nextLine();

        boolean match = crypto.verify(verify, hash);
        System.out.println(match ? "Password match!" : "Password does NOT match.");

    }

}
