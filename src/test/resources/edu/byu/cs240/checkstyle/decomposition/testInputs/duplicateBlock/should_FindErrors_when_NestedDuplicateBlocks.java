class should_FindErrors_when_DuplicateBlocks {
    public void hi() {
        int i = 7;
        int j = 4;
        if(i > j) {
            if (i <= j) {
                System.out.println(i + j);
                System.out.println(i - j);
                System.out.println(j - i);
            }
        }
        i = 2;
        j = 0;
        if(i > j) {
            if (i <= j) {
                System.out.println(i + j);
                System.out.println(i - j);
                System.out.println(j - i);
            }
        }
        i = 17;
        j = 17;
        if(i > j) {
            if (i <= j) {
                System.out.println(i + j);
                System.out.println(i - j);
                System.out.println(j - i);
            }
        }
    }
}