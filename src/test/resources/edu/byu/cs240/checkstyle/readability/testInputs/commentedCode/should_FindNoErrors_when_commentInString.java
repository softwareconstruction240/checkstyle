import java.util.Scanner;

class should_FindNoErrors_when_TextBlock {
    public void notBananas() {
        String notABananaToken = "/*";
        String alsoNotABananaToken = "/**";
        String stillNotABananaToken = "//";
        String definitelyNotABananaToken = "///";
        String dontEvenThinkAboutThisBeingABananaToken = "////";

        String notABananaToken_twice = "/*" + "/*";
        String alsoNotABananaToken_twice = "/*" + "/*";
        String stillNotABananaToken_twice = "//" + "//";
        String definitelyNotABananaToken_twice = "///" + "///";
        String dontEvenThinkAboutThisBeingABananaToken_twice = "////";

        String notABananaToken_withEndChar = "/*;";
        String alsoNotABananaToken_withEndChar = "/**;";
        String stillNotABananaToken_withEndChar = "//;";
        String definitelyNotABananaToken_withEndChar = "///;";
        String dontEvenThinkAboutThisBeingABananaToken_withEndChar = "////;";

        String notABananaToken_twice_withEndChar = "/*;" + "/*;";
        String alsoNotABananaToken_twice_withEndChar = "/**;" + "/**;";
        String stillNotABananaToken_twice_withEndChar = "//;" + "//;";
        String definitelyNotABananaToken_twice_withEndChar = "///;" + "///;";
        String dontEvenThinkAboutThisBeingABananaToken_twice_withEndChar = "////;" + "////;";

        Scanner scanner = new Scanner(System.in);
        System.out.print("How many bananas? ");
        String input = scanner.next();
        int numBananas;
        try {
            numBananas = 0; // aint no bananas here...
        }
        catch (NumberFormatException e) {
            System.out.println("Could not parse " + input + " as a number. No bananas.");
            numBananas = 0;
        }
        for(int i = 0; i < numBananas; i++) {
            System.out.println("Banana " + (i + 1));
            System.out.println(myBananaToken);
        }
    }


    public static void main(String[] args) {
        new should_FindNoErrors_when_TextBlock().notBananas();
    }
}
