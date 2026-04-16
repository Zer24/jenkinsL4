package org.example;

import java.util.Scanner;

public class Main {
    int a, b, c;
    public static void main(String[] args) {
        double a = 0, b = 0 , c = 0;
        String input;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Рассчёт квадратичной формулы ax^2 + bx + c = 0");
        System.out.println("Введите a");
        input = scanner.next();
        try {
            a = Double.parseDouble(input);
        }catch (NumberFormatException exception){
            System.err.println("Ввод не является числом!");
        }
        System.out.println("Введите b");
        input = scanner.next();
        try {
            b = Double.parseDouble(input);
        }catch (NumberFormatException exception){
            System.err.println("Ввод не является числом!");
        }System.out.println("Введите c");
        input = scanner.next();
        try {
            c = Double.parseDouble(input);
        }catch (NumberFormatException exception){
            System.err.println("Ввод не является числом!");
        }
        double d = b*b-4*a*c;
        if(d>0){
            System.out.println("Корень 1: "+(-b+Math.sqrt(d))/2);
            System.out.println("Корень 2: "+(-b-Math.sqrt(d))/2);
        }
        if(d==0){
            System.out.println("Корень: "+(-b)/2);
        }
        if(d<0){
            d = Math.abs(d);
            System.out.println("Корень 1: "+(-b/2)+" + "+(Math.sqrt(d)/2) + "i");
            System.out.println("Корень 2: "+(-b/2)+" - "+Math.abs((Math.sqrt(d)/2)) + "i");
        }
    }
    public static void recursionFunction(int a){
        System.out.println("hello! " + a);
        recursionFunction(a+1);
    }
}