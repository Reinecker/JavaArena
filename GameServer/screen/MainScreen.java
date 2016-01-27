package gameserver.screen;

import java.io.*;
import gameserver.*;

public class MainScreen
{
	public static void mainScreenMenu(PlayerConnection player, BufferedReader in, PrintWriter out) throws IOException
	{
		String message = "Gold: " + player.getPlayerClass().getGold() + " Class: " + player.getPlayerClass().getClassName() + "\n";
		message += "Stats: " + player.getPlayerClass().getStats() + "\n";
		message += "Wins: " + player.getWins() + " Losses: " + player.getLosses() + "\n";
		message += "1. Shop\n2. Choose Class\n3. Ready For Battle";
		player.sendMessage(message);
		String pString = in.readLine();

		if (pString.equals("1"))
		{
			player.goToShop();
		}
		else if (pString.equals("2"))
		{
			player.goToClassSelection();
		}
		else if (pString.equals("3"))
		{
			player.setReadyForBattle(true);
			GameServer.battleQueue.add(player);
			GameServer.players.remove(player);
		}
		else
		{
			player.sendMessage("Invalid selection");
		}
	}
}