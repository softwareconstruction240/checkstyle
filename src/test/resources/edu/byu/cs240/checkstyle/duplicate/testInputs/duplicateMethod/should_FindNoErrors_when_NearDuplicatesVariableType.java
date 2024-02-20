class should_FindNoErrors_when_NearDuplicatesVariableType {
    private int numIFeelLikeReturning() {
        int i = 14;
        int j = 19;
        if(i > j) {
            return j;
        }
        else {
            return i;
        }
    }

    private int numIFeelLikeReturningDos() {
        long i = 14;
        int j = 19;
        if(i > j) {
            return j;
        }
        else {
            return i;
        }
    }
}