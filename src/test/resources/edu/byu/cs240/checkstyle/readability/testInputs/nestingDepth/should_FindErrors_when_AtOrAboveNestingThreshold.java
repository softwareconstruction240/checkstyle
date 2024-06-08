import java.sql.Connection;

class should_FindErrors_when_AtOrAboveNestingThreshold {
    public void hi() {
        if(true) {
            for(int i = 0; i < 10; i++) {
                try {                                           //1
                    System.out.println("hi");
                    System.out.println("hello");
                } catch (Exception e) {}
            }
        } else {
            int i = 0;
            while(i < 10) {
                if(i % 2 == 0) {                                //2
                    System.out.println("hi " + i);
                }
                i++;
            }
            try(Connection c = null) {
                System.out.println("hi " + i);
                switch (i) {                                    //3
                    case 1:
                        i++;
                        break;
                    case 7:
                        i--;
                        break;
                    default:
                        if(i % 2 == 0) i--;                     //4
                }
            } catch (Exception e) {
                i--;
                if(i == 2) {                                    //5
                    i += 3;
                }
            }
        }
        try {
            if(false) {
                while (false) {                                 //6
                    for(int i = 0; i < 10; i++) {               //7
                        i *= 2;
                        i -= switch (i) {                       //8
                            case 1 -> 1;
                            case 2 -> 8;
                            case 3 -> 17;
                            default -> {
                                if(i % 2 == 0) yield i - 1;     //9
                                yield i;
                            }
                        };
                    }
                    System.out.println("hi");
                }
                System.out.println("hi");
            }
        } catch (Exception e) {
            System.out.println("hi");
        }
    }
}