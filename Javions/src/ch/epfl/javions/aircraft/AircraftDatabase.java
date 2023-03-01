package ch.epfl.javions.aircraft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipFile;

/**
 * Represents an aircraft database
 * @author David Fota
 * @author Andrei Pana
 */
public class AircraftDatabase
{
    String fileName, name;
    String[] data;
    AircraftData finalData;
    char[] hashMap = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    /**
     * @param fileName
     *        the string corresponding to the name of the file
     *        in which the database is stored
     * @throws NullPointerException if the string is null
     */
    public AircraftDatabase(String fileName)
    {
        this.fileName = Objects.requireNonNull(fileName);
    }
    /**
     *@returns the desired aircraft's data, which is
     *         extracted from the database
     *@param address
     *         corresponds to the ICAO address of the aircraft
     *         we are looking for
     * This method iterates on every file present in the database
     * in order to find and extract the required information
     */
    public AircraftData get(IcaoAddress address) throws IOException
    {
        String d = getClass().getResource(fileName).getFile();
        for(char c1 : hashMap)
            for(char c2 : hashMap)
            {
                name = "" + c1 + c2 + ".csv";
                try (ZipFile z = new ZipFile(d);
                     InputStream s = z.getInputStream(z.getEntry(name));
                     Reader r = new InputStreamReader(s, StandardCharsets.UTF_8);
                     BufferedReader b = new BufferedReader(r))
                {
                    String l = "";
                    while ((l = b.readLine()).compareTo(address.string()) <= 0 || l.startsWith(address.string()))
                    {
                        if (l.startsWith(address.string()))
                        {
                            data = l.split(",", -1);
                            finalData = new AircraftData(new AircraftRegistration(data[1]),
                                    new AircraftTypeDesignator(data[2]), data[3],
                                    new AircraftDescription(data[4]), WakeTurbulenceCategory.of(data[5]));
                            return finalData;
                        }
                    }
                }
            }
        return null;
    }
}
