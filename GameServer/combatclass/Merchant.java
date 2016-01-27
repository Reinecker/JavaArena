package gameserver.combatclass;

public class Merchant extends CombatClass
{
	public Merchant()
	{
		super();
		strength = 8;
		maxHealth = 30;
		specialAbility = "money";
		speed = 5;
		className = "Merchant";
		gold = gold * 2;
	}
}