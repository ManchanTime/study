import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class BaekJoon1037 {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ArrayList<Integer> divisorList = new ArrayList<>();
        int divisorNum = scanner.nextInt();
        for(int i=0; i<divisorNum; i++) {
                int divisor = scanner.nextInt();
                divisorList.add(divisor);
        }
        Collections.sort(divisorList);
        int answer = divisorList.get(0) * divisorList.get(divisorList.size()-1);
        System.out.println(answer);

    }


}
