package gameserver.screen;

import java.io.*;
import gameserver.combatclass.*;
import gameserver.*;

public class ClassSelection
{
	public static void classSelectionMenu(PlayerConnection player, BufferedReader in, PrintWriter out) throws IOException
	{
		String message = "Choose your class:\n";
		message += "1. Warrior\n2. Rogue\n3. Wizard\n4. Merchant\n5. Back";
		player.sendMessage(message);
		String pString = in.readLine();

		if (pString.equals("1"))
		{
			player.setPlayerClass(new Warrior());
			player.returnToMenu();
		}
		else if (pString.equals("2"))
		{
			player.setPlayerClass(new Rogue());
			player.returnToMenu();
		}
		else if (pString.equals("3"))
		{
			player.setPlayerClass(new Wizard());
			player.returnToMenu();
		}
		else if (pString.equals("4"))
		{
			player.setPlayerClass(new Merchant());
			player.returnToMenu();
		}
		else if (pString.equals("5"))
		{
			player.returnToMenu();
		}
		else
		{
			player.sendMessage("Invalid selection");
		}
	}
}