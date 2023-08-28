import java.util.ArrayList;
import java.util.Scanner;

public class Baekjoon1032 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int fileNum;
        ArrayList<String> fileList = new ArrayList<>();
        fileNum = scanner.nextInt();
        for(int i = 0; i<fileNum; i++)
        {
            String fileName = scanner.next();
            fileList.add(fileName);
        }
        String answer = fileList.get(0);
        for(int i=0;i<fileList.size();i++)
        {
            String str = fileList.get(i);
            for(int j=0;j<str.length();j++)
            {
                if(answer.charAt(j)!= str.charAt(j)) {
                    StringBuilder stringBuilder = new StringBuilder(answer);
                    stringBuilder.setCharAt(j,'?');
                    answer = stringBuilder.toString();
                }
            }
        }
        System.out.println(answer);
    }



}
