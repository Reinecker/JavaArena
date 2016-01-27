import java.net.*;
import java.io.*;
import java.util.*;

public class GameServer
{
	public static ArrayList<PlayerConnection> players;
	public static ArrayList<PlayerConnection[]> arenas;
	public static Deque<PlayerConnection> gameQueue;
	public static Deque<PlayerConnection> battleQueue;
	public volatile static PlayerConnection[] battlePlayers;
	public static boolean waitingForPlayer;

	public static void main(String[] args) throws IOException
	{
		players = new ArrayList<PlayerConnection>();
		arenas = new ArrayList<PlayerConnection[]>();
		gameQueue = new ArrayDeque<PlayerConnection>();
		battleQueue = new ArrayDeque<PlayerConnection>();
		battlePlayers = new PlayerConnection[2];
		waitingForPlayer = true;

		if (args.length != 1)
		{
			System.out.println("Usage: java GameServer <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		boolean running = true;
		HandleArenas arenaHandler = new HandleArenas();
		arenaHandler.start();

		// Listens for player connections, then starts a new thread for them.
		while(running)
		{
			try (
				ServerSocket serverSocket = new ServerSocket(portNumber);
				)
			{
				//gameQueue.add(new PlayerConnection(serverSocket.accept()));
				players.add(new PlayerConnection(serverSocket.accept()));
				players.get(players.size() - 1).start();
				//gameQueue.peekLast().start();
			} catch (IOException e)
			{
				System.out.println("Exception caught when trying to listen");
				System.out.println(e.getMessage());
			}
		}
	}
}