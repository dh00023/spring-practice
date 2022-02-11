package dh0023.springtest.coverage;

public class Coverage {

    void statement(int x) {

        System.out.println("start line"); // (1)
        if (x > 0) { // (2)
            System.out.println("middle line"); // (3)
        }
        System.out.println("last line"); // (4)
    }

    void condition(int x, int y) {
        System.out.println("start line"); // (1)
        if (x > 0 && y < 0) { // (2)
            System.out.println("middle line"); // (3)
        }
        System.out.println("last line"); // (4)
    }
}
