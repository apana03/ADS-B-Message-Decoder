package ch.epfl.javions.aircraft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipFile;

/**
 * Represents an aircraft database
 *
 * @author David Fota 355816
 * @author Andrei Pana 361249
 */
public class AircraftDatabase {
    private String fileName, name;
    private String[] data;
    private AircraftData finalData;
    private String d;
    private char[] map = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * The constructor of the class
     *
     * @param fileName the string corresponding to the name of the file
     *                 in which the database is stored
     * @throws NullPointerException if the string is null or invalid
     */
    public AircraftDatabase(String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
    }

    /**
     * @param address corresponds to the ICAO address of the aircraft
     *                we are looking for
     *                This method iterates on every file present in the database
     *                in order to find and extract the required information
     * @returns the desired aircraft's data, which is
     * extracted from the database
     */
    public AircraftData get(IcaoAddress address) throws IOException {

        String crc = address.string();
        String fileAddress = crc.substring(crc.length() - 2);

        try (ZipFile fichierZip = new ZipFile(fileName);
             InputStream stream = fichierZip.getInputStream(
                     fichierZip.getEntry(fileAddress + ".csv"));
             Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String[] columns;
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith(address.string())) {
                    columns = line.split(",", -1);

                    return new AircraftData(new AircraftRegistration(columns[1]),
                            new AircraftTypeDesignator(columns[2]), columns[3],
                            new AircraftDescription(columns[4]),
                            WakeTurbulenceCategory.of(columns[5]));
                }
                if (line.compareTo(address.toString()) > 0) return null;
            }
        }
        return null;
    }
}
