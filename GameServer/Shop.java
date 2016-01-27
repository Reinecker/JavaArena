import java.io.*;

public class Shop
{
	public static void shopMenu(PlayerConnection player, BufferedReader in, PrintWriter out) throws IOException
	{
		String message = "Current Gold: " + player.getPlayerClass().getGold() + "\n";
		message += "1. Health Potion - Heals 10 HP - 25 Gold\n";
		message += "2. Ability Potion - Allows you to use ability again - 50 Gold\n";
		message += "3. Light Armor - +1 ARM - 30 Gold\n";
		message += "4. Medium Armor - +2 ARM - 60 Gold\n";
		message += "5. Heavy Armor - +3 ARM -1 SPD - 75 Gold\n";
		message += "6. Dagger - +1 STR - 30 Gold\n";
		message += "7. Sword - +2 STR - 60 Gold\n";
		message += "8. Stimulant - +1 SPD - 30 Gold\n";
		message += "9. Thingamajig - +1 STR +1 SPD +1 ARM +10 MAX HP - 100 Gold\n";
		message += "0. Leave Shop";
		player.sendMessage(message);
		String pString = in.readLine();

		if (pString.equals("0"))
		{
			player.returnToMenu();
		}
		else if (pString.equals("1") && player.getPlayerClass().getGold() >= 25)
		{
			player.getPlayerClass().changeGold(-25);
			player.addHealthPotions(1);
		}
		else if (pString.equals("2") && player.getPlayerClass().getGold() >= 50)
		{
			player.getPlayerClass().changeGold(-50);
			player.addAbilityPotions(1);
		}
		else if (pString.equals("3") && player.getPlayerClass().getGold() >= 30)
		{
			player.getPlayerClass().changeGold(-30);
			player.getPlayerClass().changeArmor(1);
		}
		else if (pString.equals("4") && player.getPlayerClass().getGold() >= 60)
		{
			player.getPlayerClass().changeGold(-60);
			player.getPlayerClass().changeArmor(2);
		}
		else if (pString.equals("5") && player.getPlayerClass().getGold() >= 75)
		{
			player.getPlayerClass().changeGold(-75);
			player.getPlayerClass().changeArmor(3);
			player.getPlayerClass().changeSpeed(-1);
		}
		else if (pString.equals("6") && player.getPlayerClass().getGold() >= 30)
		{
			player.getPlayerClass().changeGold(-30);
			player.getPlayerClass().changeStrength(1);
		}
		else if (pString.equals("7") && player.getPlayerClass().getGold() >= 60)
		{
			player.getPlayerClass().changeGold(-60);
			player.getPlayerClass().changeStrength(2);
		}
		else if (pString.equals("8") && player.getPlayerClass().getGold() >= 30)
		{
			player.getPlayerClass().changeGold(-30);
			player.getPlayerClass().changeSpeed(1);

		}
		else if (pString.equals("9") && player.getPlayerClass().getGold() >= 100)
		{
			player.getPlayerClass().changeGold(-100);
			player.getPlayerClass().changeStrength(1);
			player.getPlayerClass().changeSpeed(1);
			player.getPlayerClass().changeArmor(1);
			player.getPlayerClass().changeMaxHealth(10);
		}
		else
		{
			player.sendMessage("Invalid selection");
		}
	}
}