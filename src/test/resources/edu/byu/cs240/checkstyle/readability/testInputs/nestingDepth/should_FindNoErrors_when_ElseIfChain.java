import java.sql.Connection;

class should_FindNoErrors_when_ElseIfChain {
    public void hi(int i) {
        if(i < 0) {
            i++;
        }
        else if(i < 3) {
            i--;
        }
        else if(i < 5) {
            i /= 2;
        }
        else if(i < 7) {
            i /= 3;
        }
        else if(i < 12) System.out.println(i);
        else if(i < 15) i *= i;
        else if(i < 27) i += i;
        else if(i < 30) i /= i;
        else if(i < 35) i -= i;
    }
}