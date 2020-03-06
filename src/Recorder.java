import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/***************************************************************************
 * Recorder Class
 **************************************************************************/
public class Recorder
{
    private final String RECORDER_VERSION = "0.01";
    private final String OUTPUT_FILE_NAME = "OutputData.csv";

    private final int BUFFER_SIZE = 50;

    private FileWriter fstream;
    private String[] buffer;
    private int bufferIndex;

    private ArrayList<String> columnNames;
    private ArrayList<String> columnValues;

    private StringBuilder writeString;

    /***************************************************************************
     * Recorder Constructor
     **************************************************************************/
    public Recorder()
    {
        this.fstream = null;
        this.buffer = new String[50];
        this.bufferIndex = 0;

        this.writeString = new StringBuilder();

        this.columnValues = new ArrayList<>();
        this.columnNames = new ArrayList<>();

        this.createFile();
    }

    /***************************************************************************
     * updateVariable - double overload
     **************************************************************************/
    public void updateVariable(String name, double value)
    {
        updateVariable(name, value + "");
    }

    /***************************************************************************
     * updateVariable - String overload
     **************************************************************************/
    public void updateVariable(String name, String value)
    {
        int columnIndex = -1;

        for(int i = 0; i < this.columnNames.size(); i++)
        {
            if(name.equalsIgnoreCase(this.columnNames.get(i)))
            {
                columnIndex = i;
            }
        }

        if(columnIndex == -1)
        {
            this.columnNames.add(name);
            this.columnValues.add(value);
        }
        else
        {
            this.columnValues.add(columnIndex, value);
        }
    }

    /***************************************************************************
     * writeVariables()
     **************************************************************************/
    public void writeVariables()
    {
        this.writeString.setLength(0);

        for(int i = 0; i < this.columnValues.size() - 1; i++)
        {
            writeString.append(columnValues.get(i));
            writeString.append( ", ");
        }

        writeString.append(columnValues.get(columnValues.size() - 1));

        this.writeLine(this.writeString.toString());
    }

    /***************************************************************************
     * writeVariableNames()
     **************************************************************************/
    public void writeVariableNames()
    {
        this.writeString.setLength(0);

        for(int i = 0; i < this.columnNames.size() - 1; i++)
        {
            writeString.append(columnNames.get(i));
            writeString.append( ", ");
        }

        writeString.append(columnNames.get(columnNames.size() - 1));

        this.writeLine(this.writeString.toString());
    }


    /***************************************************************************
     * WriteLine
     **************************************************************************/
    public void writeLine(String line)
    {
        this.buffer[bufferIndex] = line;
        this.bufferIndex++;


        if(this.bufferIndex >= buffer.length)
        {
            this.writeToFile(buffer.length);
            this.bufferIndex = 0;
        }
    }

    /***************************************************************************
     * stopRecording
     **************************************************************************/
    public void stopRecording()
    {
        this.writeToFile(this.bufferIndex);

        try
        {
            this.fstream.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    /***************************************************************************
     * CreateFile
     **************************************************************************/
    private void createFile()
    {
        try
        {
            this.fstream = new FileWriter(OUTPUT_FILE_NAME, false);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        //File Header
        try
        {
            fstream.write("Jellyfish Simulator File Output:" + "\n");
            fstream.write("Current Recorder Version:, " + RECORDER_VERSION + "\n");
            fstream.write("Start Time:, " + System.currentTimeMillis() + "\n");
            fstream.write("\n");
            fstream.flush();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    /***************************************************************************
     * writeToFile
     **************************************************************************/
    private void writeToFile(int stopIndex)
    {
        try
        {
            for(int i = 0 ; i < stopIndex; i++)
            {
                fstream.write(buffer[i] + "\n");
            }

            fstream.flush();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    /***************************************************************************
     * Testing main
     **************************************************************************/
    public static void main(String[] args)
    {
        Recorder test = new Recorder();

        test.updateVariable("Depth", 100);
        test.updateVariable("Temperature", 54);
        test.updateVariable("Mood", "Good!");

        test.writeVariableNames();
        test.writeVariables();
        test.writeVariables();
        test.writeVariables();
        test.writeVariables();

        test.stopRecording();
    }
}
