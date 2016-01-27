public class HandleArenas implements Runnable
{
	Thread t;
	boolean shouldRun;

	public HandleArenas()
	{
		shouldRun = true;
		t = new Thread(this);
	}

	public void run()
	{
		int arenaNumber;
		while (shouldRun)
		{
			if (GameServer.battlePlayers[0] != null && GameServer.battlePlayers[1] != null)
			{
				arenaNumber = -1;
				for (int i = 0; i < GameServer.arenas.size(); i++)
				{
					if (GameServer.arenas.get(i) == null)
					{
						arenaNumber = i;
						i = GameServer.arenas.size() + 1;
					}
				}
				if (arenaNumber <= -1)
				{
					arenaNumber = GameServer.arenas.size();
				}

				GameServer.battlePlayers[0].setArena(arenaNumber);
				GameServer.battlePlayers[1].setArena(arenaNumber);
				GameServer.arenas.add(GameServer.battlePlayers);
				GameServer.battlePlayers = new PlayerConnection[2];
			}
		}
	}

	public synchronized void removeDeadArenas()
	{
		for (int i = 0; i < GameServer.arenas.size(); i++)
		{
			PlayerConnection[] battlePlayers = new PlayerConnection[2];
			battlePlayers = GameServer.arenas.get(i);
			if (battlePlayers[0] == null && battlePlayers[1] == null)
			{
				GameServer.arenas.set(i, null);
			}
			else if (battlePlayers[0] == null && battlePlayers[1] != null)
			{
				//GameServer.gameQueue.add(battlePlayers[1]);
				//GameServer.arenas.set(i, null);
			}
			else if (battlePlayers[1] == null && battlePlayers[0] != null)
			{
				//GameServer.gameQueue.add(battlePlayers[0]);
				//GameServer.arenas.set(i, null);
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