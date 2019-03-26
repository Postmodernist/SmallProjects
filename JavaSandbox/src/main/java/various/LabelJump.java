package various;

public class LabelJump {
    public static void main(String[] args) {
        int i = 0;
        int j = 0;
        loop:
        for (i = 0; i < 10; i++) {
            for (j = 0; j < 10; j++) {
                if (i == 3 && j == 5) break loop;
            }
        }
        System.out.println(i + " " + j);
    }
}
