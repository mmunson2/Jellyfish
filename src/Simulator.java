/***************************************************************************
 * Simulator Class
 **************************************************************************/
public class Simulator implements Runnable
{
    //______________________________________________________________________
    // Various Constants

    private final int NUMBER_OF_CYLINDERS = 3;
    private final double WATER_DENSITY = 997;
    private final double GRAVITATIONAL_CONSTANT = 9.81;

    //______________________________________________________________________
    // Instrument Bay Parameters

    private final double INSTRUMENT_COMPARTMENT_MASS = 7; //kg
    private final double BUOYANCY_ENGINE_MASS_EMPTY = 9; //kg

    private final double INSTRUMENT_BAY_OD = 0.1443; //m
    private final double INSTRUMENT_BAY_LENGTH = 0.7; //m

    //______________________________________________________________________
    // Buoyancy Engine Parameters

    private final double BUOYANCY_ENGINE_OD = 0.1143; //m
    private final double BUOYANCY_ENGINE_ID = 0.0965; //m
    private final double BUOYANCY_ENGINE_LENGTH = 0.91; //m

    private final double BUOYANCY_ENGINE_PISTON_LENGTH =
            0.5 * BUOYANCY_ENGINE_LENGTH; //m - This is just a guess


    //______________________________________________________________________
    // Volume Calculations

    private final double INSTRUMENT_BAY_VOLUME =
            Math.PI * ((INSTRUMENT_BAY_OD / 2) * (INSTRUMENT_BAY_OD / 2))
                    * INSTRUMENT_BAY_LENGTH; //m^3

    private final double BUOYANCY_ENGINE_VOLUME =
            Math.PI * ((BUOYANCY_ENGINE_OD / 2) * (BUOYANCY_ENGINE_OD / 2))
            * BUOYANCY_ENGINE_LENGTH; //m^3

    private final double BUOYANCY_ENGINE_PISTON_VOLUME =
            Math.PI * ((BUOYANCY_ENGINE_ID / 2) * (BUOYANCY_ENGINE_ID / 2))
            * BUOYANCY_ENGINE_PISTON_LENGTH; //m^3

    //______________________________________________________________________
    // Instance Variables

    private double trueDepth;
    private double trueVelocity;
    private double trueAcceleration;

    private long lastNanoTime;
    private int counter = 0;

    private double extensionCoefficient;


    //______________________________________________________________________
    // Shared components with the Control class - be careful!`

    private DepthSensor depthSensor;
    private BuoyancyEngine[] buoyancyEngines;




    /***************************************************************************
     * run method
     *
     * Called by the Jellyfish main thread. Begins an infinite loop with 100
     * millisecond sleeps after every update. This runs 5 times faster than
     * the Control system to provide some realism
     **************************************************************************/
    public void run()
    {
        while(true)
        {
            update();

            try {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {}
        }
    }


    /***************************************************************************
     * Simulator NoArg constructor
     **************************************************************************/
    public Simulator()
    {}

    /***************************************************************************
     * update()
     *
     * The main logic of the Simulator. Updates the position of the
     * profiler which calls all physics methods. If the profiler is on
     * the surface its depth and velocity are set to zero.
     *
     * //TODO: Make a more robust output system. Implement file write
     **************************************************************************/
    public void update()
    {
        double elapsedTime = this.getElapsedTime();

        this.updatePosition(elapsedTime);


        if(trueDepth < 0)
        {
            trueDepth = 0;
            trueVelocity = 0;
        }

        if(counter % 100 == 0)
        {
            System.out.println("" +
                    "__________________________________________________" +
                    "______________________________");
            System.out.println("Depth: " + trueDepth);
            System.out.println("Velocity: " + trueVelocity);
            System.out.println("Acceleration: " + trueAcceleration);
            System.out.println();
            System.out.println("Density: " + getSystemDensity());

            System.out.println("" +
                    "__________________________________________________" +
                    "______________________________");
        }




        counter++;
    }

    /***************************************************************************
     * updatePosition
     *
     * The main physics call. Acceleration is calculated based on force
     * calculations. Velocity and Depth are then derived using the
     * elapsed time.
     *
     **************************************************************************/
    public void updatePosition(double elapsedTime)
    {
        elapsedTime /= 1000;

        this.trueAcceleration = getAcceleration();

        this.trueVelocity += this.trueAcceleration * elapsedTime;

        this.trueDepth += this.trueVelocity * elapsedTime;
    }



    /***************************************************************************
     * getElapsedTime
     *
     * helper method for determining exactly how much time has passed. Returns
     * time in milliseconds.
     *
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
     * Gets the acceleration on the system by dividing by its mass
     **************************************************************************/
    public double getAcceleration()
    {
        return getNetForce() / getSystemMass();
    }

    /***************************************************************************
     * Gets the net force on the system, currently only considers gravitational
     * and buoyant force
     *
     * //TODO: Implement drag calculation
     **************************************************************************/
    public double getNetForce()
    {
        return getGravitationalForce() - getBuoyantForce();
    }

    /***************************************************************************
     * Gets the buoyant force on the system
     *
     * Fb = density of liquid * displaced volume * g
     *
     **************************************************************************/
    public double getBuoyantForce()
    {
        return WATER_DENSITY * getSystemVolume() * GRAVITATIONAL_CONSTANT;
    }

    /***************************************************************************
     * Gets the gravitational force on the system
     *
     * Fg = Mg
     *
     **************************************************************************/
    public double getGravitationalForce()
    {
        return getSystemMass() * GRAVITATIONAL_CONSTANT;
    }


    /***************************************************************************
     * Gets the system's density, useful for comparing with water
     *
     * density = mass / volume
     *
     **************************************************************************/
    public double getSystemDensity()
    {
        return getSystemMass() / getSystemVolume();
    }

    /***************************************************************************
     * Gets the system's volume
     *
     **************************************************************************/
    public double getSystemVolume()
    {
        return INSTRUMENT_BAY_VOLUME +
                BUOYANCY_ENGINE_VOLUME * NUMBER_OF_CYLINDERS;
    }

    /***************************************************************************
     * Gets the system's mass. Sums the empty masses of the cylinders,
     * then uses the position of the buoyancy engine cylinders to determine
     * how much water is filling the pistons and adds its mass.
     **************************************************************************/
    public double getSystemMass()
    {
        double mass = INSTRUMENT_COMPARTMENT_MASS +
                (BUOYANCY_ENGINE_MASS_EMPTY * NUMBER_OF_CYLINDERS);

        if(buoyancyEngines == null)
        {
            System.out.println(
                    "Simulator Error: BuoyancyEngines not initialized");
        }
        else {

            for (int i = 0; i < NUMBER_OF_CYLINDERS
                    && i < buoyancyEngines.length; i++)
            {
                double extension = buoyancyEngines[i].getExtensionCoefficient();

                extension = 1 - extension; //1 now represents full of water

                mass += extension * BUOYANCY_ENGINE_PISTON_VOLUME
                        * WATER_DENSITY;
            }
        }

        return mass;
    }


    /***************************************************************************
     * Tells the Depth Sensor what depth it's at
     *
     * //Todo: Add variance
     **************************************************************************/
    public synchronized double getDepthReading()
    {
        double depthReading = trueDepth;

        return depthReading;
    }

    /***************************************************************************
     * Setter - gets a reference to the buoyancy engine array.
     *
     * Shared with the Control system, not good programming practice
     **************************************************************************/
    public synchronized void setBuoyancyEngines(BuoyancyEngine[] buoyancyEngines)
    {
        this.buoyancyEngines = buoyancyEngines;
    }

    /***************************************************************************
     * Setter - gets a reference to the depth sensor
     *
     * Shared with the Control system
     **************************************************************************/
    public synchronized void setDepthSensor(DepthSensor depthSensor)
    {
        this.depthSensor = depthSensor;
    }


}
