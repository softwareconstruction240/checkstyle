class should_FindNoErrors_when_UnusedAnnotated {
    @Override
    public void hi() {
        hello();
    }
    public void hello() {
        System.out.println("hello");
    }
}