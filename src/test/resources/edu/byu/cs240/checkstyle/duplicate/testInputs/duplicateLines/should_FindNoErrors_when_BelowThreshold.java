class should_FindNoErrors_when_BelowThreshold {
    public boolean hi() {
        System.out.println("Hello World!");
        int i = 7;
        int j = 4;
        if(i > j) {
            return true;
        }
        i = 3;
        if(i > j) {
            return true;
        }
        j = 1;
        if(i > j) {
            return true;
        }
        return false;
    }
}