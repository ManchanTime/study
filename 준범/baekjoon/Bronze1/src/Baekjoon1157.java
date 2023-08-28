import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class Baekjoon1157 {
    public static void main(String[] args) {
        Scanner scnnner = new Scanner(System.in);
        String word = scnnner.nextLine();
        ArrayList<Wordandcount> alpabet = new ArrayList<>();
        String answer;
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            if (alpabet.contains(Character.toUpperCase(letter))||alpabet.contains(Character.toLowerCase(letter))) {
                continue;
            } else {
                int count = 0;
                Wordandcount wordandcount = null;
                for (int j = i; j < word.length(); j++) {
                    if (letter == Character.toUpperCase(word.charAt(j))||letter == Character.toLowerCase(word.charAt(j))) {
                        count++;
                    }
                    wordandcount = new Wordandcount(letter, count);

                }
                alpabet.add(wordandcount);
            }
        }
        Collections.sort(alpabet, Collections.reverseOrder());

        if (alpabet.size()!=1&&(alpabet.get(0).getCount() == alpabet.get(1).getCount())) {
            answer = "?";
        } else {
            answer = alpabet.get(0).getLetter()+"";
            answer = answer.toUpperCase();

        }

        System.out.println(answer);

    }
}


    class Wordandcount implements Comparable<Wordandcount> {
        private char letter;
        private int count;

        public Wordandcount(char letter, int count) {
            this.letter = letter;
            this.count = count;
        }

        @Override
        public int compareTo(Wordandcount wordandcount) {
            if (wordandcount.count < count) {
                return 1;
            } else if (wordandcount.count > count) {
                return -1;
            }
            return 0;
        }

        public int getCount() {
            return this.count;
        }

        public char getLetter() {
            return this.letter;
        }

    }

