package gameserver.combatclass;

import java.net.*;
import java.io.*;
import java.util.*;

public class CombatClass
{
	int maxHealth;
	int currentHealth;
	int strength;
	int speed;
	int armor;
	int gold;
	boolean dead;
	String specialAbility;
	String className;

	public CombatClass()
	{
		maxHealth = 25;
		currentHealth = maxHealth;
		strength = 10;
		speed = 7;
		armor = 0;
		gold = 100;
		dead = false;
		specialAbility = "";
		className = "";
	}

	public int calculateDamage()
	{
		Random random = new Random();
		int damage = random.nextInt(strength);
		int chanceToHit = random.nextInt(10) + 1;
		if (chanceToHit > speed)
		{
			damage = 0;
		}
		return damage;
	}

	public int takeDamage(int damage)
	{
		int fDamage = damage;
		if (damage < 0) // If damage is negative, then it actually heals the player
		{
			currentHealth -= damage;
			if (currentHealth > maxHealth)
			{
				currentHealth = maxHealth;
			}
		}
		else
		{
			fDamage = damage - armor;
			if (fDamage <= 0)
			{
				fDamage = 1;
			}
			currentHealth -= fDamage;
			if (currentHealth <= 0)
			{
				dead = true;
			}
		}
		return fDamage;
	}

	public String displayBattleInfo()
	{
		String rString = "Health: " + currentHealth + "/" + maxHealth;
		return rString;
	}

	// Resets the player's stats after a battle without removing their items or resetting their gold.
	public void revive()
	{
		dead = false;
		currentHealth = maxHealth;
	}

	public boolean isDead()
	{
		return dead;
	}

	public void changeStrength(int amount)
	{
		strength += amount;
	}
	public void changeSpeed(int amount)
	{
		speed += amount;
	}
	public void changeArmor(int amount)
	{
		armor += amount;
	}
	public void changeMaxHealth(int amount)
	{
		maxHealth += amount;
		currentHealth = maxHealth;
	}

	public int getGold()
	{
		return gold;
	}

	public void changeGold(int amount)
	{
		gold += amount;
	}

	public String getClassName()
	{
		return className;
	}

	public String getAbility()
	{
		return specialAbility;
	}

	public String getStats()
	{
		String stats = "HP: " + maxHealth + " STR: " + strength + " SPD: " + speed + " ARM: " + armor;
		return stats; 
	}
}