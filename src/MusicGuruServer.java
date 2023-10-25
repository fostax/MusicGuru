import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is the server for the MusicGuru application.
 */
public class MusicGuruServer {
    private static final String songDatabase = "top10.txt";

    /**
     * Main method for the server.
     *
     * @param args String[]
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        int port;
        int year;

        try {
            if (args.length > 1) {
                throw new IOException("Must only enter one port number parameter");
            }

            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException n) {
                throw new IOException("Invalid input, must be an integer");
            }

            if (port < 0 || port > 65535) {
                throw new IOException("Invalid Port number, must be between 0 and 65535");
            }

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server waiting for client connections");

                while (true) {
                    //Accept Connection
                    Socket clientSocket = serverSocket.accept();

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    System.out.println("Client connected");

                    //Send range (m1)
                    if (dateRange() != null) {
                        out.println(dateRange());
                    } else {
                        throw new IOException("File read/Date Range error");
                    }

                    //Receive year from Client (m2)
                    try {
                        year = Integer.parseInt(in.readLine());
                    } catch (NumberFormatException n) {
                        throw new IOException("Invalid year parse");
                    }
                    System.out.println("Client input: " + year);

                    //Select random song from year, format, and send to Client (m3)
                    StringBuilder songOutput = new StringBuilder();
                    String song = selectSong(year), hostAddress = InetAddress.getLocalHost().getHostAddress();
                    songOutput.append(song)
                            .append(" (")
                            .append(hostAddress)
                            .append(")");

                    out.println(songOutput);
                    System.out.println("Output: " + songOutput);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * This method reads the file and returns the date range.
     *
     * @return String date range
     */
    private static String dateRange() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader file = new BufferedReader(new FileReader(songDatabase))) {
            String line;
            String startDate = null;
            String endDate = null;

            while ((line = file.readLine()) != null) {
                if (line.matches("^\\d{4}$")) {
                    if (startDate == null) {
                        startDate = line;
                    } else {
                        endDate = line;
                    }
                }
            }
            if (startDate != null && endDate != null) {
                sb.append(startDate).append("-").append(endDate);
                return sb.toString();
            } else {
                System.out.println("Error: Could not find date range");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * This method reads the file and returns a random song for the year.
     *
     * @param year int
     * @return String song
     */
    private static String selectSong(int year) {
        List<String> lines = readFile();
        if (lines != null) {
            int startIndex = -1;
            int endIndex = -1;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).matches("^\\d{4}$")) {
                    int currentYear = Integer.parseInt(lines.get(i));
                    if (currentYear == year) {
                        startIndex = i + 1; //increment index by 1 to ignore year line, start at No. 1 song

                        if (!lines.get(startIndex + 9).matches("^\\d{4}$")) {
                            endIndex = startIndex + 9; //set endIndex to 9 more than startIndex (No.10 song)
                        }
                        break;
                    }
                }
            }

            if (startIndex != -1 && endIndex != -1) {
                if (startIndex < endIndex) {
                    int randomIndex = new Random().nextInt((endIndex - startIndex) + 1) + startIndex; //Select random song within Top 10 of given year
                    return lines.get(randomIndex);
                }
            }
        }
        return null;
    }

    /**
     * This method reads the file and returns a list of lines.
     *
     * @return List<String> lines
     */
    private static List<String> readFile() {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(songDatabase))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        if (!lines.isEmpty()) {
            return lines;
        }

        return null;
    }
}
