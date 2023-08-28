import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class BaekJoon1145 {
    public static void main(String[] args)  {
        Scanner scanner = new Scanner(System.in);
            ArrayList<Integer> integers = new ArrayList<>();
            ArrayList<Integer> lcms = new ArrayList<>();
            int n1 = scanner.nextInt();
            int n2 = scanner.nextInt();
            int n3 = scanner.nextInt();
            int n4 = scanner.nextInt();
            int n5 = scanner.nextInt();
            integers.add(n1);
            integers.add(n2);
            integers.add(n3);
            integers.add(n4);
            integers.add(n5);

            int max = 0;

            for(int i=0;i<5;i++) {
                for(int j=i+1;j<5;j++) {
                    for(int k=j+1;k<5;k++) {

                                    int[] intArray = {integers.get(i),integers.get(j),integers.get(k)};
                                    int answer = getLCM(intArray);
                                    lcms.add(answer);

                        }
                    }

                }
                System.out.println(Collections.min(lcms));
            }
    public static int getLCM(int[] arr) {
        if (arr.length == 1) {
            return arr[0];
        }

        int gcd = getGCD(arr[0], arr[1]);
        int lcm = (arr[0] * arr[1]) / gcd;

        for (int i = 2; i < arr.length; i++) {
            gcd = getGCD(lcm, arr[i]);
            lcm = (lcm * arr[i]) / gcd;
        }

        //  System.out.println("the greatest common demoniator : " + gcd);

        return lcm;
    }

    public static int getGCD(int num1, int num2) {
        if (num1 % num2 == 0) {
            return num2;
        }
        return getGCD(num2, num1 % num2);
    }
}






