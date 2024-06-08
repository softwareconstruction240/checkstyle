import java.sql.Connection;

class should_FindNoErrors_when_BelowNestingThreshold {
    public void hi() {
        if(true) {
            int j = 4;
            for(int i = 0; i < 10; i++) {
                System.out.println("hi " + i);
                j *= (i + 3);
            }
            int x = switch (j) {
                case 0 -> 0;
                case 1 -> 7;
                case 2 -> 9;
                default -> -1;
            };
        } else {
            int i = 0;
            while(i < 10) {
                System.out.println("hi " + i);
                i++;
            }
            try(Connection c = null) {
                System.out.println("hi " + i);
            }
        }
        try {
            int i = 2;
            switch (i) {
                case 1:
                    i -= 7;
                    break;
                case 3:
                    i += 4;
                    break;
                default:
                    i++;
            }
        } catch (Exception e) {
            System.out.println("hi");
        }
    }
}