package util;

import java.text.DecimalFormat;

/**
 * @program: SEB-Framework
 * @description: Console Progress Bar
 * @creator: Li DX
 */
public class ConsoleProgressBarUtils {
    private int barLen;

    private long total;

    private char showChar;

    private String barName;

    private DecimalFormat formater = new DecimalFormat("#.##%");

    public ConsoleProgressBarUtils(int barLen,long total, String barName) {
        this.barLen = barLen;
        this.barName = barName;
        this.total=total;
    }

    public void show(int value) {
        reset();
        float rate = (float) (value*1.0 / total);
        draw(barLen, rate);
        if (value == total) {
            afterComplete();
        }
    }

    private void draw(int barLen, float rate) {
        int len = (int) (rate * barLen);
        System.out.print(barName+" Progress : |");
        for (int i = 0; i < len; i++) {
            System.out.print("#");
        }
        for (int i = 0; i < barLen-len; i++) {
            System.out.print(" ");
        }
        System.out.print(" | " + format(rate));
    }

    private void reset() {
        System.out.print('\r');
    }

    private void afterComplete() {
        System.out.print('\n');
    }

    private String format(float num) {
        return formater.format(num);
    }
}
