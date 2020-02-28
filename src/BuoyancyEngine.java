/***************************************************************************
 * Buoyancy Engine Class
 *
 *
 **************************************************************************/
public class BuoyancyEngine
{

    //Constants
    public final boolean DEBUG = true;
    public final double EXTENSION_DELTA = 0.01;

    //Instance Variables
    double extensionCoefficient;


    /***************************************************************************
     * No Argument Constructor
     *
     *
     **************************************************************************/
    BuoyancyEngine()
    {
        this.extensionCoefficient = 0.5;
    }

    /***************************************************************************
     * Sink
     *
     *
     **************************************************************************/
    public void sink()
    {
        if(this.extensionCoefficient >= EXTENSION_DELTA)
            this.extensionCoefficient -= EXTENSION_DELTA;
    }

    /***************************************************************************
     * Ascend
     *
     *
     **************************************************************************/
    public void ascend()
    {
        if(this.extensionCoefficient <= 1.0)
        {
            this.extensionCoefficient += EXTENSION_DELTA;
        }
    }

    /***************************************************************************
     * Getter - Get Extension Coefficient
     *
     *
     **************************************************************************/
    public synchronized double getExtensionCoefficient()
    {
        return this.extensionCoefficient;
    }



}
