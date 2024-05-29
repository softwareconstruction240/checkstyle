class should_FindErrors_when_NearDuplicatesVariableName {
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
        int x = 14;
        int j = 19;
        if(x > j) {
            return j;
        }
        else {
            return x;
        }
    }
}