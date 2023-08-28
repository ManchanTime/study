import java.util.Scanner;

public class BaekJoon1110 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        int result=0;
        int count = 0;
        if(num == 0) {
            count = 1;
        }
        while(num != result) {
            if(0<=num && num<10) {
                num = num*10;
            }

            else if(count == 0){
                int frontNum = 0;
                int lastNum = 0;
               frontNum = num%10;
               lastNum = ((int)(num/10)+frontNum)%10;
               result = frontNum*10 + lastNum;
               count++;
            }
            else {
                int frontNum = 0;
                int lastNum = 0;
                frontNum = result%10;
                lastNum = ((int)(result/10) + frontNum)%10;
                result = frontNum*10 + lastNum;
                count++;
            }


        }
        System.out.println(count);
    }
}
