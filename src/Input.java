import java.util.Scanner;

/***************************************************************************
 * Input Class
 **************************************************************************/
public class Input implements Runnable {

    public final boolean DEBUG = true;

    Scanner keyboard;


    public void run()
    {}

    public Input()
    {
        keyboard = new Scanner(System.in);
    }


    private String getUserInput()
    {
        return keyboard.nextLine();
    }

}
