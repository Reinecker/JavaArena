import java.io.*;
import java.net.*;

public class GameClient
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.out.println("Usage: java GameClient <host name> <port number>");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		/* Tries to connect to host provided through commandline arguments.
		** If successful then it starts a new thread continiously listening
		** to the server while also accepting player input. */
		try (
			Socket clientSocket = new Socket(hostName, portNumber);
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			)
		{
			new RecieveMessages(clientSocket).start();
			String userInput;
			while ((userInput = stdIn.readLine()) != null)
			{
				out.println(userInput);
			}
		} catch (UnknownHostException e)
		{
			System.out.println("Unknown host " + hostName);
			System.exit(1);
		} catch (IOException e)
		{
			System.out.println("Could not connect to " + hostName);
			System.exit(1);
		}
	}
}