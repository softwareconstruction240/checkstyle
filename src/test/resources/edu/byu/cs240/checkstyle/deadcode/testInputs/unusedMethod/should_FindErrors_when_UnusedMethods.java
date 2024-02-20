class should_FindErrors_when_UnusedMethods {
    public void hi() {
        System.out.println("hi");
    }
    public void hello() {
        System.out.println("hello");
    }
    public should_FindErrors_when_UnusedMethods callMe() {
        return this;
    }
    static {
        new should_FindErrors_when_UnusedMethods().callMe();
    }
}