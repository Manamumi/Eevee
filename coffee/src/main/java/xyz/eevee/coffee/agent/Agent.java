package xyz.eevee.coffee.agent;

import xyz.eevee.coffee.client.CoffeeRPCClient;

import java.util.Arrays;

public class Agent {
    public static void main(String[] args) {
        CoffeeRPCClient client = CoffeeRPCClient.builder()
                                                .coffeeHost("127.0.0.1")
                                                .coffeePort(7733)
                                                .insideAppToken("FOOBAR")
                                                .build();

        if (args.length < 2) {
            showUsage();
        }

        if (args[0].equalsIgnoreCase("get") && args.length == 3) {
            if (args[1].equalsIgnoreCase("string")) {
                System.out.println(client.getString(args[2]));
            } else if (args[1].equalsIgnoreCase("number")) {
                System.out.println(client.getNumber(args[2]));
            } else if (args[1].equalsIgnoreCase("boolean")) {
                System.out.println(client.getBoolean(args[2]));
            } else if (args[1].equalsIgnoreCase("stringlist")) {
                System.out.println(client.getStringList(args[2]));
            } else {
                showUsage();
            }
        } else if (args[0].equalsIgnoreCase("set") && args.length == 4) {
            if (args[1].equalsIgnoreCase("string")) {
                client.setString(args[2], args[3]);
            } else if (args[1].equalsIgnoreCase("number")) {
                client.setNumber(args[2], Double.parseDouble(args[3]));
            } else if (args[1].equalsIgnoreCase("boolean")) {
                client.setBoolean(args[2], Boolean.parseBoolean(args[3]));
            } else if (args[1].equalsIgnoreCase("stringlist")) {
                client.setStringList(args[2], Arrays.asList(Arrays.copyOf(args, 3)));
            } else {
                showUsage();
            }
        } else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
            client.delete(args[1]);
        } else {
            showUsage();
        }
    }

    private static void showUsage() {
        System.out.println("CLI tool for interacting with the local Coffee agent.");
        System.out.println("\nUsage:\n\tcoffee [get | set | delete] [string | number | bool | stringlist] <key> <value>");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("\t- get: Read from the local Coffee agent.");
        System.out.println("\t- set: Set a canary for the local Coffee agent.");
        System.out.println("\t- delete: Delete a canary from the local Coffee agent.");
    }
}
