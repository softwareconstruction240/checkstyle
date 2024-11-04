import java.util.Scanner;

class should_FindNoErrors_when_TextBlock {
    public void notBananas() {
        String notABananaToken = "/*";
        String alsoNotABananaToken = "/**";
        String stillNotABananaToken = "//";
        String definitelyNotABananaToken = "///";
        String dontEvenThinkAboutThisBeingABananaToken = "////";

        String notABananaToken_withEndChar = "/*;";
        String alsoNotABananaToken_withEndChar = "/**;";
        String stillNotABananaToken_withEndChar = "//;";
        String definitelyNotABananaToken_withEndChar = "///;";
        String dontEvenThinkAboutThisBeingABananaToken_withEndChar = "////;";

        Scanner scanner = new Scanner(System.in);
        System.out.print("How many bananas? ");
        String input = scanner.next();
        int numBananas;
        try {
            numBananas = 0; // no bananas here...
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
