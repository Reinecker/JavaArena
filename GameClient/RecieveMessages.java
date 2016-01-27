import java.net.*;
import java.io.*;

public class RecieveMessages implements Runnable
{
	Thread t;
	boolean shouldRun;
	BufferedReader in;

	public RecieveMessages(Socket pSocket)
	{
		try
		{
			shouldRun = true;
			in = new BufferedReader(new InputStreamReader(pSocket.getInputStream()));
		} catch (Exception e)
		{
			System.out.println(e);
			this.stop();
		}

		t = new Thread(this);
	}

	public void run()
	{
		/* Continuously checks for messages from server and prints them.
		** If a null message is recieved then it typically means a disconnection
		** from the server. */
		while (shouldRun)
		{
			try
			{
				String serverMessage;
				while ((serverMessage = in.readLine()) != null)
				{
					System.out.println(serverMessage);
				}
				this.stop();
			} catch (IOException e)
			{
				System.out.println("Error recieving message from server");
				this.stop();
				throw new RuntimeException(e);
			}
		}
	}

	public void stop()
	{
		shouldRun = false;
	}

	public void start()
	{
		t.start();
	}
}