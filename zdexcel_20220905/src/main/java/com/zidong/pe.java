package com.zidong;

import java.util.Scanner;

public class pe {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Double[] a = new Double[3];
        Double[] b = new Double[3];

        while (true) {
            for (int i = 0; i < 3; i++) {
                System.out.println("请依次输入第一个点的x、y或z坐标");
                a[i] = scanner.nextDouble();
            }
            for (int i = 0; i < 3; i++) {
                System.out.println("请依次输入第二个点的x、y或z坐标");
                b[i] = scanner.nextDouble();
            }

            Double[] ans = function(a, b);
            for (int i = 0; i < 3; i++) {
                System.out.println(ans[i]);
            }
            function2(a, ans, scanner);
        }


    }

    static Double[] function(Double[] a, Double[] b) {
        Double[] c = new Double[3];
        for (int i = 0; i < 3; i++) {
            c[i] = b[i] - a[i];
        }
        String[] ans = new String[3];
        for (int i = 0; i < 3; i++) {
            String op = a[i] > 0 ? "+" : "";
            if (i == 0) {
                ans[i] = "x=" + c[i] + "t" + op + a[i];
            } else if (i == 1) {
                ans[i] = "y=" + c[i] + "t" + op + a[i];
            } else {
                ans[i] = "z=" + c[i] + "t" + op + a[i];
            }
        }
        for (int i = 0; i < 3; i++) {
            System.out.println(ans[i]);
        }
        return c;
    }

    static void function2(Double[] a1, Double[] b2, Scanner scanner) {
        Double[] c1 = new Double[2];
        System.out.println("扣1输入z值，否则回到计算参数方程~");
        int j = scanner.nextInt();
        while (j == 1) {
            System.out.println("输入z值~");
            Double z = scanner.nextDouble();
            Double t = (z - a1[2]) / b2[2];
            for (int i = 0; i < 2; i++) {
                c1[i] = b2[i] * t + a1[i];
            }
            System.out.println("对应输入z的x坐标为：");
            System.out.println(c1[0]);
            System.out.println("对应输入z的y坐标为：");
            System.out.println(c1[1]);
            System.out.println("扣1输入z值，否则回到计算参数方程~");
            j = scanner.nextInt();
        }

    }

}


