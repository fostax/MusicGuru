import java.io.IOException;
import java.net.*;

public class HealthCheck {
	public static void main(String[] args){
		if(args.length != 1){
			System.exit(1);
		}
		int port = Integer.parseInt(args[0]);

		try (ServerSocket sock = new ServerSocket(port)){
			while(true){
				Socket s = sock.accept();
				s.close();
			}
		} catch (IOException e) {
            System.exit(1);
        }
    }
}