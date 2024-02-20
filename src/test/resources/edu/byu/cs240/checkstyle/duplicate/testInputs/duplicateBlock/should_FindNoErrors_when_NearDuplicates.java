class should_FindNoErrors_when_NearDuplicates {
    public void hi() {
        System.out.println("Hello World!");
        int i = 7;
        int j = 4;
        if(i > j) {
            System.out.println(i + j);
            System.out.println(i - j);
            System.out.println(j - i);
        }
        if(i > j) {
            System.out.println(i * j);
            System.out.println(i - j);
            System.out.println(j - i);
        }
        if(i > j) {
            System.out.println(i + j);
            System.out.println(i + i);
            System.out.println(j - i);
        }
        if(i > j) {
            System.out.println(i + j);
            System.out.println(i - j);
            System.out.println(j / i);
        }
        if(i > j) {
            System.out.println(i + j);
            System.out.println(i - j);
            System.out.println(j - i);
            System.out.println(j -= i);
        }
    }
}