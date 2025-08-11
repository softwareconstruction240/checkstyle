class should_FindNoErrors_when_OtherComments {
    public void hi() {
// Hello test reader
// I hope you're having a really nice day!
// Sincerely,
// One who writes tests
        System.out.println("Hello World!");
        int i = 7;
        int j = 4;
        System.out.println(i + j);
    }

    /**
     * Lol idk seemed like a fun time;
     * Javadoc comments can be weird sometimes;
     * Documentation is great to have;
     * but seems like most devs don't like writing it;
     *
     * @param a yes (extra details)
     * @param b no (but is it actually?)
     * @param c sometimes, but semicolons;
     * @param d occasionally,
     * @param e often (
     * @param f almost always /
     * @param g literally never }
     * @param h the only one actually used lol {
     */
    public void hi(int a, int b, Object c, char d, boolean e, long f, Void g, String h) {
        System.out.println(h + " lol!");
    }
}