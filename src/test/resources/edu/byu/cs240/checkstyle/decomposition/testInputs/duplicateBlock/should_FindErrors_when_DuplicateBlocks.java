class should_FindErrors_when_DuplicateBlocks {
    public void hi() {
        System.out.println("Hello World!");
        int i = 7;
        int j = 4;
        if(i > j) {
            System.out.println(i + j);
            System.out.println(i - j);
            System.out.println(j - i);
        }
        i = 2;
        if(i > j) {
            System.out.println(i + j);
            System.out.println(i - j);
            System.out.println(j - i);
        }
        j = 0;
        if(i > j) {
            System.out.println(i + j);
            System.out.println(i - j);
            System.out.println(j - i);
        }
        i = 17;
        if(i > j) {
            System.out.println(i + j);
            System.out.println(i - j);
            System.out.println(j - i);
        }
    }
}