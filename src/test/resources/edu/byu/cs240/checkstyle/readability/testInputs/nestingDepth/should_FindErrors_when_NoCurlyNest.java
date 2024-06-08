class should_FindErrors_when_NoCurlyNest {
    public void hi() {
        if(true) while(false) if(false) for(int i : new int[2]) System.out.println(i);
    }
}