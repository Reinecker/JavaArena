import java.net.*;
import java.io.*;
import java.util.*;

public class PlayerConnection implements Runnable
{
	Thread t;
	boolean shouldRun;
	boolean readyForBattle;
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	String name;
	public String screen;
	public int secondaryScreen;
	CombatClass playerClass;
	int battlePosition;
	boolean isTurn;
	int extraTurn;
	int healthPotions;
	int abilityPotions;
	boolean usedAbility;
	int wins;
	int losses;
	public int arena;
	PlayerConnection[] battlePlayers;

	public PlayerConnection(Socket pSocket)
	{
		battlePlayers = new PlayerConnection[2];
		playerClass = new Warrior();
		readyForBattle = false;
		isTurn = false;
		extraTurn = 0;
		healthPotions = 0;
		abilityPotions = 0;
		usedAbility = false;
		arena = -1;
		try
		{
			shouldRun = true;
			screen = "nameEnter";
			secondaryScreen = 0;
			clientSocket = pSocket;
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (Exception e)
		{
			// TODO: Better exception handling
			System.out.println(e);
			this.stop();
		}

		t = new Thread(this);
	}

	public void sendMessage(String s)
	{
		out.println(s);
	}

	public void run()
	{
		while(shouldRun)
		{
			String inputLine;
			try
			{
				processInput();
			} catch (IOException e)
			{
				System.out.println("Error reading input from connection");
				System.out.println("Attempting to close socket");
				// If they were in battle make sure it ends
				if (screen.equals("battle"))
				{
					GameServer.waitingForPlayer = true;
					reset();
					battlePlayers[battlePosition] = null;
					if (battlePosition == 0)
					{
						battlePlayers[1].reset();
						GameServer.players.add(battlePlayers[1]);
						battlePlayers[1] = null;
					}
					else
					{
						battlePlayers[0].reset();
						GameServer.players.add(battlePlayers[0]);
						battlePlayers[0] = null;
					}
				}
				else
				{
					GameServer.players.remove(this);
				}
				this.stop();
			} catch (NullPointerException e)
			{
				System.out.println("Unexpected disconnection. Null pointer exception thrown.");
				System.out.println("Attempting to close socket");
				// If they were in battle make sure it ends
				if (screen.equals("battle"))
				{
					GameServer.waitingForPlayer = true;
					reset();
					battlePlayers[battlePosition] = null;
					if (battlePosition == 0)
					{
						battlePlayers[1].reset();
						GameServer.players.add(battlePlayers[1]);
						battlePlayers[1] = null;
					}
					else
					{
						battlePlayers[0].reset();
						GameServer.players.add(battlePlayers[0]);
						battlePlayers[0] = null;
					}
				}
				else
				{
					GameServer.players.remove(this);
				}
				this.stop();
			}
		}
	}

	public void processInput() throws IOException
	{
		String pString;
		String message;

		if (screen.equals("nameEnter"))
		{
			sendMessage("Enter a name:");
			pString = in.readLine();
			name = pString;
			sendMessage("Your name is " + pString);
			screen = "queue";
		}

		if (screen.equals("queue"))
		{
			if (readyForBattle)
			{
				sendMessage("Waiting for battle...");
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException e)
				{
					System.out.println(e);
				}
				if (this == GameServer.battleQueue.peekFirst())
				{
					if (GameServer.battlePlayers[0] == null)
					{
						GameServer.battlePlayers[0] = GameServer.battleQueue.pollFirst();
						battlePosition = 0;
						isTurn = true;
						screen = "battle";
					}
					else if (GameServer.battlePlayers[1] == null)
					{
						GameServer.battlePlayers[0].setTurn(true);
						GameServer.battlePlayers[1] = GameServer.battleQueue.pollFirst();
						battlePosition = 1;
						isTurn = false;
						screen = "battle";
					}
				}
			}
			else if (secondaryScreen == 0)
			{
				MainScreen.mainScreenMenu(this, in, out);
			}
			else if (secondaryScreen == 1)
			{
				Shop.shopMenu(this, in, out);
			}
			else if (secondaryScreen == 2)
			{
				ClassSelection.classSelectionMenu(this, in, out);
			}
		}

		if (screen.equals("battle"))
		{
			if (arena >= 0)
			{
				battlePlayers = GameServer.arenas.get(arena);

				readyForBattle = false;
				if (playerClass.isDead())
				{
					arena = -1;
					sendMessage("You have lost the battle!");
					losses++;
					if (battlePosition == 0)
					{
						battlePlayers[1].incrementWins();
						battlePlayers[1].reset();
						GameServer.players.add(battlePlayers[1]);
						battlePlayers[1] = null;
					}
					else
					{
						battlePlayers[0].incrementWins();
						battlePlayers[0].reset();
						GameServer.players.add(battlePlayers[0]);
						battlePlayers[0] = null;
					}
					reset();
					GameServer.players.add(battlePlayers[battlePosition]);
					battlePlayers[battlePosition] = null;
					GameServer.waitingForPlayer = true;
				}
				else
				{
					String battleString = "You - " + playerClass.displayBattleInfo() + "\n";
					if (battlePosition == 0)
					{
						battleString += battlePlayers[1].getName() + " - " 
						+ battlePlayers[1].playerClass.displayBattleInfo() + "\n";
					}
					else
					{
						battleString += battlePlayers[0].getName() + " - " 
						+ battlePlayers[0].playerClass.displayBattleInfo() + "\n";
					}

					if (isTurn)
					{
						int damage = 0;
						battleString += "1. Attack\n2. Special Ability\n3. Use item";
						sendMessage(battleString);
						pString = in.readLine();

						if (pString.equals("1")) // Regular attack
						{
							damage = playerClass.calculateDamage();
							isTurn = false;
							if (battlePosition == 0)
							{
								battlePlayers[1].playerClass.takeDamage(damage);
								battlePlayers[1].setTurn(true);
							}
							else
							{
								battlePlayers[0].playerClass.takeDamage(damage);
								battlePlayers[0].setTurn(true);
							}
						}
						else if (pString.equals("2")) // Special ability
						{
							if (!usedAbility)
							{
								isTurn = false;
								usedAbility = true;
								if (playerClass.getAbility().equals("smash"))
								{
									damage = playerClass.calculateDamage() * 2;
								}
								else if (playerClass.getAbility().equals("doubleAttack"))
								{
									damage = playerClass.calculateDamage();
									damage += playerClass.calculateDamage();
								}
								else if (playerClass.getAbility().equals("freeze"))
								{
									isTurn = true;
									extraTurn = 2;
								}
								else if (playerClass.getAbility().equals("money"))
								{
									sendMessage("Merchants don't have a special combat ability!");
									isTurn = true;
								}
								else
								{
									System.err.println("Something went wrong!");
									sendMessage("Something went wrong!");
									isTurn = true;
								}

								if (battlePosition == 0 && !isTurn)
								{
									battlePlayers[1].playerClass.takeDamage(damage);
									battlePlayers[1].setTurn(true);
								}
								else if (!isTurn)
								{
									battlePlayers[0].playerClass.takeDamage(damage);
									battlePlayers[0].setTurn(true);
								}
							}
							else
							{
								sendMessage("You have already used your ability!");
							}
						}
						else if (pString.equals("3")) // Use item
						{
							if (battleItemMenu())
							{
								isTurn = false;
								if (battlePosition == 0)
								{
									battlePlayers[1].setTurn(true);
								}
								else
								{
									battlePlayers[0].setTurn(true);
								}
							}
						}
						else
						{
							sendMessage("Invalid selection");
						}

						if (extraTurn != 0)
						{
							extraTurn--;
							if (extraTurn <= 0)
							{
								isTurn = false;
							}
							else
							{
								isTurn = true;
								if (battlePosition == 0)
								{
									battlePlayers[1].setTurn(false);
								}
								else
								{
									battlePlayers[0].setTurn(false);
								}
							}
						}
					}
					else
					{
						sendMessage(battleString);
						sendMessage("Waiting for opponent to take their turn...");
						try
						{
							Thread.sleep(500);
						} catch (InterruptedException e)
						{
							System.out.println(e);
						}
					}
				}

				//GameServer.arenas.set(arena, battlePlayers);
			}
			else
			{
				sendMessage("Waiting for opponent");
				try
				{
					// Revives the player to make sure they are healed in case their last opponent
					// left in the middle of a match.
					playerClass.revive();
					usedAbility = false;
					Thread.sleep(500);
				} catch (InterruptedException e)
				{
					System.out.println("Interrupted");
					System.out.println(e);
				}
			}
		}
	}

	public boolean battleItemMenu()
	{
		String playerInput;
		while(true)
		{
			sendMessage("Items: " + healthPotions + " Health Potions - " +
				abilityPotions + " Ability Potions\n1. Use Health Potion\n2. Use Ability Potion\n3. Don't use item");
			try
			{
				if ((playerInput = in.readLine()) != null)
				{
					if (playerInput.equals("1"))
					{
						if (healthPotions > 0)
						{
							healthPotions--;
							playerClass.changeGold(25);
							playerClass.takeDamage(-10);
							return true;
						}
						else
						{
							sendMessage("You don't have any health potions!");
						}
					}
					else if (playerInput.equals("2"))
					{
						if (abilityPotions > 0)
						{
							abilityPotions--;
							playerClass.changeGold(50);
							usedAbility = false;
							return true;
						}
						else
						{
							sendMessage("You don't have any ability potions!");
						}
					}
					else if (playerInput.equals("3"))
					{
						return false;
					}
					else
					{
						sendMessage("Invalid selection");
					}
				}
				else
				{
					return false;
				}
			} catch (Exception e)
			{
				System.out.println("Error reading input from connection");
				System.out.println("Attempting to close socket");
				System.out.println(e);
				// If they were in battle make sure it ends
				if (screen.equals("battle"))
				{
					reset();
					GameServer.waitingForPlayer = true;
					battlePlayers[battlePosition] = null;
				}
				else
				{
					GameServer.players.remove(this);
				}
				this.stop();
			}
		}
	}

	public void stop()
	{
		shouldRun = false;
	}

	public void start()
	{	
		System.out.println("New connection made");
		t.start();
	}

	public String getName()
	{
		return name;
	}

	public int getWins()
	{
		return wins;
	}
	public int getLosses()
	{
		return losses;
	}

	public void setTurn(boolean pTurn)
	{
		isTurn = pTurn;
	}

	public void setReadyForBattle(boolean ready)
	{
		readyForBattle = ready;
	}

	public void incrementWins()
	{
		wins++;
	}

	public void addHealthPotions(int amount)
	{
		healthPotions += amount;
	}
	public void addAbilityPotions(int amount)
	{
		abilityPotions += amount;
	}

	public void setArena(int pA)
	{
		arena = pA;
	}

	public CombatClass getPlayerClass()
	{
		return playerClass;
	}
	public void setPlayerClass(CombatClass pCombatClass)
	{
		playerClass = pCombatClass;
	}

	public void returnToMenu()
	{
		secondaryScreen = 0;
	}
	public void goToShop()
	{
		secondaryScreen = 1;
	}
	public void goToClassSelection()
	{
		secondaryScreen = 2;
	}

	public void reset()
	{
		playerClass.revive();
		screen = "queue";
		secondaryScreen = 0;
		arena = -1;
		usedAbility = false;
		extraTurn = 0;
		readyForBattle = false;
	}
}