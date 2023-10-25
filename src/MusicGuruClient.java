import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * This class is the client for the MusicGuru application.
 */
public class MusicGuruClient {
    private static int year;

    /**
     * Main method for the client.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        int serverPort;
        int userYear;
        try {      //Open connection
            if (args.length != 3) {
                throw new IOException("Must input hostname, port, year");
            }
            String serverHostname = args[0];

            try {
                serverPort = Integer.parseInt(args[1]);
                userYear = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid parameters");
            }

            if (serverPort < 0 || serverPort > 65535) {
                throw new IOException("Invalid Port number, must be between 0 and 65535");
            }

            try (Socket socket = new Socket(serverHostname, serverPort)) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                System.out.println("Connected to server " + serverHostname + ":" + serverPort);

                //get date range from server (m1)
                String dateRange = in.readLine();
                System.out.println("Server response: " + dateRange);

                //Check date range against user input, send to server (m2)
                year = getYear(dateRange, userYear);
                out.println(year);

                //Receive song from Server (m3)
                String song = in.readLine();
                String songOutput = processSong(song);
                System.out.println(songOutput);

            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * This method checks the response from the server
     * if int is within the range, will return the int,
     * otherwise returns a random number within the range
     *
     * @param range String
     * @param year  int
     * @return int
     */
    private static int getYear(String range, int year) {
        int startDate = Integer.parseInt(range.split("-")[0]);
        int endDate = Integer.parseInt(range.split("-")[1]);

        if (year >= startDate && year <= endDate) {
            return year;
        } else {
            //return random number between start and end dates
            int randYear = new Random().nextInt((endDate - startDate) + 1) + startDate;
            System.out.println("Specified year out of range (" + range + "), using random date instead: " + randYear);
            return randYear;
        }
    }

    /**
     * This method processes the song string from the server
     *
     * @param song String
     * @return String
     */
    private static String processSong(String song) {
        StringBuilder sb = new StringBuilder();
        String[] songParts = song.split("\\D+");
        if (songParts.length > 0) {
            String num = songParts[0];
            sb.append("In ")
                    .append(year)
                    .append(" the number ")
                    .append(num)
                    .append(" song was ")
                    .append(song.replaceFirst("^\\d+\\.\\s*", ""));
        } else {
            sb.append("Error within processSong()");
        }
        return sb.toString();
    }
}
