/*******************************************************************************
 * Control Class Prototype
 *
 * Goal: Create a control system that attempts to reach a set depth, then
 * ascends. System should monitor descent and ascent rate to keep them on
 * target.
 *
 ******************************************************************************/
public class Control implements Runnable
{

    //Constants
    public final boolean DEBUG = true;
    public final int NUMBER_OF_ENGINES = 3;
    public final int ENGINE_UPDATE_INTERVAL = 500;

    public final double TARGET_DESCENT_RATE = 0.1; //meters per second
    public final double TARGET_ASCENT_RATE = 0.1; //meters per second

    //Instance Variables
    private BuoyancyEngine[] buoyancyEngines;
    private DepthSensor depthSensor;

    private Simulator simulator;

    private long lastNanoTime;
    private double lastDepth;

    private long targetDepth;
    private boolean depthReached;

    /***************************************************************************
     * Run Method
     *
     *
     **************************************************************************/
    public void run()
    {
        while(true)
        {

            //__________________________________________________________________
            //Section 1: Retrieving Information
            long elapsedTime = getElapsedTime();

            double elapsedTimeSeconds = elapsedTime / 1000.0;

            double currentDepth = this.depthSensor.getDepth(simulator);

            double currentSpeed = getSpeed(currentDepth, elapsedTimeSeconds);

            boolean sinking = this.isSinking(currentDepth);


            //__________________________________________________________________
            //Section 2: Do Logic

            if(currentDepth > targetDepth)
            {
                depthReached = true;
            }

            boolean overspeedDescent = (currentSpeed > TARGET_DESCENT_RATE
                    && sinking);

            boolean overspeedAscent = (currentSpeed > TARGET_ASCENT_RATE
                    && !sinking);


            if(elapsedTime > ENGINE_UPDATE_INTERVAL)
            {
                for(int i = 0; i < 1; i++)
                {
                    if(!depthReached && !overspeedDescent)
                    {
                        buoyancyEngines[i].sink();
                        System.out.println("Sink! | Speed: " + currentSpeed);
                    }
                    if(!depthReached && overspeedDescent)
                    {
                        buoyancyEngines[i].ascend();
                        System.out.println("Overspeed, Ascend! | Speed: " + currentSpeed);
                    }

                    if(depthReached && !overspeedAscent)
                    {
                        buoyancyEngines[i].ascend();
                        System.out.println("At depth, Ascend!");
                    }
                    if(depthReached && overspeedAscent)
                    {
                        buoyancyEngines[i].sink();
                        System.out.println("Rising too fast, Sink!");
                    }
                }
            }
            //__________________________________________________________________
            //Section 3: Sleep for a bit
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {}

        }
    }

    /***************************************************************************
     * getSpeed
     **************************************************************************/
    private double getSpeed(double currentDepth, double elapsedTime)
    {
        return Math.abs(currentDepth - lastDepth) * (elapsedTime);
    }

    /***************************************************************************
     * isSinking
     **************************************************************************/
    private boolean isSinking(double currentDepth)
    {
        return (currentDepth - lastDepth) > 0;
    }


    /***************************************************************************
     * getElapsed
     **************************************************************************/
    private long getElapsedTime()
    {
        long currentNanoTime = System.nanoTime();

        long deltaTime = currentNanoTime - this.lastNanoTime;

        deltaTime = deltaTime / 1000000;

        this.lastNanoTime = currentNanoTime;

        return deltaTime;
    }


    /***************************************************************************
     * Control Constructor
     **************************************************************************/
    public Control()
    {
        this.lastNanoTime = System.nanoTime();

        this.buoyancyEngines = new BuoyancyEngine[NUMBER_OF_ENGINES];

        for(int i = 0; i < buoyancyEngines.length; i++)
        {
            buoyancyEngines[i] = new BuoyancyEngine();
        }

        this.depthSensor = new DepthSensor();


        this.targetDepth = 60;
        this.depthReached = false;

    }

    /***************************************************************************
     * Control Constructor - Simulator Overload
     **************************************************************************/
    public Control(Simulator simulator)
    {
        this.lastNanoTime = System.nanoTime();

        this.buoyancyEngines = new BuoyancyEngine[NUMBER_OF_ENGINES];

        for(int i = 0; i < buoyancyEngines.length; i++)
        {
            buoyancyEngines[i] = new BuoyancyEngine();
        }

        this.depthSensor = new DepthSensor();


        this.targetDepth = 60;
        this.depthReached = false;


        this.simulator = simulator;
        this.simulator.setBuoyancyEngines(buoyancyEngines);
        this.simulator.setDepthSensor(depthSensor);
    }


}
