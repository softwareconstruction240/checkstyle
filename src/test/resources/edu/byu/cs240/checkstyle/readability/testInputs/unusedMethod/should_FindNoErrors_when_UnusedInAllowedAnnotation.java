class should_FindNoErrors_when_UnusedInAllowedAnnotation {
    @foo.bar.FooBar("hello")
    public void hi() {
        System.out.println("hi");
    }
    @FooBar(value = "hi")
    public void hello() {
        System.out.println("hello");
    }
}