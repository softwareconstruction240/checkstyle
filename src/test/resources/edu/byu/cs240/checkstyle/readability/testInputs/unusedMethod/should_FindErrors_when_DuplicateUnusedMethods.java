class should_FindErrors_when_DuplicateUnusedMethods {
    public void hi() {
        System.out.println("hi");
    }

    public void hello() {
        System.out.println("hello");
    }

    public void hi(BananaToken myBananaToken) {
        System.out.println("hi " + myBananaToken);
    }

    public void hello(Set set) {
        System.out.println("hello " + set);
    }
}