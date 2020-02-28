/***************************************************************************
 * Jellyfish Class
 **************************************************************************/
public class Jellyfish implements Runnable
{

    public final boolean DEBUG = true;

    /***************************************************************************
     * run
     **************************************************************************/
    public void run()
    {
        Simulator simulator = new Simulator();
        Thread simThread = new Thread(simulator);

        Control control = new Control(simulator);
        Thread controlThread = new Thread(control);

        Input input = new Input();
        Thread inputThread = new Thread(input);

        simThread.start();
        controlThread.start();
        inputThread.start();
    }

    /***************************************************************************
     * System main
     **************************************************************************/
    public static void main(String[] args)
    {
        Jellyfish jellyfish = new Jellyfish();

        jellyfish.run();
    }
}
