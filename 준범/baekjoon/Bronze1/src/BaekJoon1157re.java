
import java.util.Scanner;

public class BaekJoon1157re {

    public static void main(String[] args) {
        int[] alpabet = new int[26];
        Scanner scanner = new Scanner(System.in);
        String word = scanner.nextLine();
        word.toUpperCase();
        char answer ='?';
        int max = 0;
        for(int i=0;i<word.length();i++) {
            int wordLetter = word.charAt(i);
            alpabet[wordLetter-65]++;
        }
        for(int i=0;i<alpabet.length;i++) {
            max = alpabet[0];
            if(alpabet[i]>max) {
                max = alpabet[i];

            }
        }
        answer = (char) (max+65);
        System.out.println(answer);

    }
}
