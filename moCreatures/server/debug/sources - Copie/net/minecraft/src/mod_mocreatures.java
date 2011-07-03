package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.MinecraftServer;

public class mod_mocreatures extends BaseMod
{
	public static String modName = "Finnithnel's SMP Mo'Creatures Mod";
	public static Item horseSaddle = (new HorseSaddle(3772)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/horsesaddle.png")).setItemName("HorseSaddle");
	public static Item hayStack = (new HayStack(3775)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/haystack.png")).setItemName("HayStack");
	public static Item sugarLump = (new SugarLump(3776)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/sugarlump.png")).setItemName("SugarLump");
	public static Item sharkTeeth = (new Item(3774)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/sharkteeth.png")).setItemName("sharkteeth");
	public static Item sharkEgg = (new ItemSharkEgg(3773)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/sharkegg.png")).setItemName("sharkegg");
	public static int worldDifficulty;
	public static int maxMobs;
	public static int maxAnimals;
	public static int maxWaterMobs;
	public static int maxLions;
	public static int maxBears;
	public static int maxPolarBears;
	public static int maxBoars;
	public static int maxFoxes;
	public static int maxBirds;
	public static int maxDucks;
	public static int maxBunnies;
	public static int maxHorses;
	public static int maxSharks;
	public static int maxSquids;
	public static int maxDolphins;
	public static int maxOgres;
	public static int maxFireOgres;
	public static int maxCaveOgres;
	public static int maxWraiths;
	public static int maxFireWraiths;
	public static int maxWerewolves;
	public static int maxWolves;
	public static boolean spawnLions;
	public static boolean spawnBears;
	public static boolean spawnPolarBears;
	public static boolean spawnWolves;
	public static boolean spawnDucks;
	public static boolean spawnBoars;
	public static boolean spawnBunnies;
	public static boolean spawnWraiths;
	public static boolean spawnFireWraiths;
	public static boolean spawnOgres;
	public static boolean spawnFireOgres;
	public static boolean spawnCaveOgres;
	public static boolean spawnHorses;
	public static boolean spawnBirds;
	public static boolean spawnWerewolves;
	public static boolean spawnFoxes;
	public static boolean spawnSharks;
	public static boolean spawnSquids;
	public static boolean spawnDolphins;
	public static boolean dolphinsAttackSharks;
	public static boolean huntersAttackHorses;
	public static boolean huntersDestroyDrops;
	public static boolean easyBreeding;
	public static int pegasusChance;
	public static float ogresStrength;
	public static float fireOgresStrength;
	public static float caveOgresStrength;
	public static int ogresSpawnDifficulty;
	public static int caveOgresSpawnDifficulty;
	public static int fireOgresSpawnDifficulty;
	public static int werewolvesSpawnDifficulty;
	public static int wraithsSpawnDifficulty;
	public static int fireWraithsSpawnDifficulty;
	public static int sharksSpawnDifficulty;
	public static PropertyManager propertyManagerObj;
	private static Map<String, Integer> difficulties = new HashMap<String, Integer>();

	public mod_mocreatures()
	{
		ModLoader.zzz(EntityHorse.class, "Horse", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityOgre.class, "Ogre", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityFireOgre.class, "FireOgre", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityCaveOgre.class, "CaveOgre", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityBoar.class, "Boar", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityBear.class, "Bear", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityDuck.class, "Duck", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityLionK.class, "LionK", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityWolf.class, "Wolf", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityPolarBear.class, "PolarBear", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityWraith.class, "Wraith", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityFlameWraith.class, "FlameWraith", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityBunny.class, "Bunny", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityBird.class, "Bird", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityFox.class, "Fox", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityWerewolf.class, "Werewolf", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityShark.class, "Shark", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntitySharkEgg.class, "SharkEgg", ModLoader.getUniqueEntityId());
		ModLoader.zzz(EntityDolphin.class, "Dolphin", ModLoader.getUniqueEntityId());

		difficulties.put("Peaceful", Integer.valueOf(0));
		difficulties.put("Easy", Integer.valueOf(1));
		difficulties.put("Normal", Integer.valueOf(2));
		difficulties.put("Hard", Integer.valueOf(3));

		propertyManagerObj = new PropertyManager(new File("server.properties"));
		worldDifficulty = getDifficulty(propertyManagerObj.getStringProperty("World-Difficulty", "Peaceful"));
		
		maxMobs = propertyManagerObj.getIntProperty("SpawnLimit-Mobs", 70);
		maxAnimals = propertyManagerObj.getIntProperty("SpawnLimit-Animals", 30);
		maxWaterMobs = propertyManagerObj.getIntProperty("SpawnLimit-WaterMobs", 20);

		spawnHorses = propertyManagerObj.getBooleanProperty("Horses-Spawn", true);
		maxHorses = propertyManagerObj.getIntProperty("Horses-MaxNumber", 15);
		easyBreeding = propertyManagerObj.getBooleanProperty("Horses-EasyBreeding", false);
		pegasusChance = propertyManagerObj.getIntProperty("Horses-PegasusSpawningChance", 1);
		spawnBirds = propertyManagerObj.getBooleanProperty("Birds-Spawn", true);
		maxBirds = propertyManagerObj.getIntProperty("Birds-MaxNumber", 6);
		spawnBunnies = propertyManagerObj.getBooleanProperty("Bunnies-Spawn", true);
		maxBunnies = propertyManagerObj.getIntProperty("Bunnies-MaxNumber", 10);
		spawnDucks = propertyManagerObj.getBooleanProperty("Ducks-Spawn", true);
		maxDucks = propertyManagerObj.getIntProperty("Ducks-MaxNumber", 10);

		huntersAttackHorses = propertyManagerObj.getBooleanProperty("Hunters-AttackHorses", true);
		huntersDestroyDrops = propertyManagerObj.getBooleanProperty("Hunters-DestroyDrops", true);
		spawnLions = propertyManagerObj.getBooleanProperty("Lions-Spawn", true);
		maxLions = propertyManagerObj.getIntProperty("Lions-MaxNumber", 4);
		spawnBears = propertyManagerObj.getBooleanProperty("Bears-Spawn", true);
		maxBears = propertyManagerObj.getIntProperty("Bears-MaxNumber", 4);
		spawnPolarBears = propertyManagerObj.getBooleanProperty("PolarBears-Spawn", true);
		maxPolarBears = propertyManagerObj.getIntProperty("PolarBears-MaxNumber", 4);
		spawnBoars = propertyManagerObj.getBooleanProperty("Boars-Spawn", true);
		maxBoars = propertyManagerObj.getIntProperty("Boars-MaxNumber", 4);
		spawnFoxes = propertyManagerObj.getBooleanProperty("Foxes-Spawn", true);
		maxFoxes = propertyManagerObj.getIntProperty("Foxes-MaxNumber", 4);

		/*spawnOgres = propertyManagerObj.getBooleanProperty("Ogres-Spawn", true);
		maxOgres = propertyManagerObj.getIntProperty("Ogres-MaxNumber", 8);
		ogresSpawnDifficulty = getDifficulty(propertyManagerObj.getStringProperty("Ogres-SpawnDifficulty", "Easy"));
		ogresStrength = getFloatProperty("Ogres-Strength", 2.5F);
		spawnFireOgres = propertyManagerObj.getBooleanProperty("FireOgres-Spawn", true);
		maxFireOgres = propertyManagerObj.getIntProperty("FireOgres-MaxNumber", 6);
		fireOgresSpawnDifficulty = getDifficulty(propertyManagerObj.getStringProperty("FireOgres-SpawnDifficulty", "Normal"));
		fireOgresStrength = getFloatProperty("FireOgres-Strength", 2.0F);
		spawnCaveOgres = propertyManagerObj.getBooleanProperty("CaveOgres-Spawn", true);
		maxCaveOgres = propertyManagerObj.getIntProperty("CaveOgres-MaxNumber", 6);
		caveOgresSpawnDifficulty = getDifficulty(propertyManagerObj.getStringProperty("CaveOgres-SpawnDifficulty", "Easy"));
		caveOgresStrength = getFloatProperty("CaveOgres-Strength", 3F);
		spawnWerewolves = propertyManagerObj.getBooleanProperty("Werewolves-Spawn", true);
		maxWerewolves = propertyManagerObj.getIntProperty("Werewolves-MaxNumber", 8);
		werewolvesSpawnDifficulty = getDifficulty(propertyManagerObj.getStringProperty("Werewolves-SpawnDifficulty", "Easy"));
		spawnWraiths = propertyManagerObj.getBooleanProperty("Wraiths-Spawn", true);
		maxWraiths = propertyManagerObj.getIntProperty("Wraiths-MaxNumber", 10);
		wraithsSpawnDifficulty = getDifficulty(propertyManagerObj.getStringProperty("Wraiths-SpawnDifficulty", "Easy"));
		spawnFireWraiths = propertyManagerObj.getBooleanProperty("FlameWraiths-Spawn", true);
		maxFireWraiths = propertyManagerObj.getIntProperty("FlameWraiths-MaxNumber", 6);
		fireWraithsSpawnDifficulty = getDifficulty(propertyManagerObj.getStringProperty("FlameWraiths-SpawnDifficulty", "Normal"));*/
		spawnWolves = propertyManagerObj.getBooleanProperty("Wolves-Spawn", true);
		maxWolves = propertyManagerObj.getIntProperty("Wolves-MaxNumber", 10);

		/*spawnSharks = propertyManagerObj.getBooleanProperty("Sharks-Spawn", true);
		maxSharks = propertyManagerObj.getIntProperty("Sharks-MaxNumber", 7);
		sharksSpawnDifficulty = getDifficulty(propertyManagerObj.getStringProperty("Sharks-SpawnDifficulty", "Easy"));*/
		spawnSquids = propertyManagerObj.getBooleanProperty("Squids-Spawn", true);
		maxSquids = propertyManagerObj.getIntProperty("Sharks-MaxNumber", 6);
		/*spawnDolphins = propertyManagerObj.getBooleanProperty("Dolphins-Spawn", true);
		maxDolphins = propertyManagerObj.getIntProperty("Dolphins-MaxNumber", 7);
		dolphinsAttackSharks = propertyManagerObj.getBooleanProperty("Dolphins-AttackSharks", true);*/
		
		spawnWraiths = false;
		spawnFireWraiths = false;
		spawnOgres = false;
		spawnFireOgres = false;
		spawnCaveOgres = false;
		spawnSharks = false;
		spawnDolphins = false;

		try
		{
			ModLoader.zzy(EnumCreatureType.class, EnumCreatureType.monster, "e", Integer.valueOf(maxMobs));
			ModLoader.zzy(EnumCreatureType.class, EnumCreatureType.creature, "e", Integer.valueOf(maxAnimals));
			ModLoader.zzy(EnumCreatureType.class, EnumCreatureType.waterCreature, "e", Integer.valueOf(maxWaterMobs));
		}
		catch(NoSuchFieldException e)
		{
			try
			{
				ModLoader.zzy(EnumCreatureType.class, EnumCreatureType.monster, "maxNumberOfCreature", Integer.valueOf(maxMobs));
				ModLoader.zzy(EnumCreatureType.class, EnumCreatureType.creature, "maxNumberOfCreature", Integer.valueOf(maxAnimals));
				ModLoader.zzy(EnumCreatureType.class, EnumCreatureType.waterCreature, "maxNumberOfCreature", Integer.valueOf(maxWaterMobs));
			}
			catch(Exception exception) {}
		}
		catch(Exception e) { }
		if(spawnHorses)
			spawnlist.add("Surface biomes", spawnlist.CREATURE, new Object[] { EntityHorse.class });
		if(spawnOgres)
			spawnlist.add("Surface biomes", spawnlist.MONSTER, new Object[] { EntityOgre.class });
		if(spawnFireOgres)
		{
			spawnlist.add("Hell", spawnlist.MONSTER, new Object[] { EntityFireOgre.class });
			spawnlist.add("Surface biomes", spawnlist.MONSTER, new Object[] { EntityFireOgre.class });
		}
		if(spawnCaveOgres)
			spawnlist.add("Surface biomes", spawnlist.MONSTER, new Object[] { EntityCaveOgre.class });
		if(spawnBoars)
			spawnlist.add("Surface biomes", spawnlist.CREATURE, new Object[] { EntityBoar.class });
		if(spawnBears)
			spawnlist.add("Surface biomes", spawnlist.CREATURE, new Object[] { EntityBear.class });
		if(spawnDucks)
			spawnlist.add("Surface biomes", spawnlist.CREATURE, new Object[] { EntityDuck.class });
		if(spawnLions)
			spawnlist.add("Surface biomes", spawnlist.CREATURE, new Object[] { EntityLionK.class });
		if(spawnWolves)
			spawnlist.add("Surface biomes", spawnlist.MONSTER, new Object[] { EntityWolf.class });
		if(spawnPolarBears)
			spawnlist.add("Surface biomes", spawnlist.CREATURE, new Object[] { EntityPolarBear.class });
		if(spawnWraiths)
			spawnlist.add("Surface biomes", spawnlist.MONSTER, new Object[] { EntityWraith.class });
		if(spawnFireWraiths) 
			spawnlist.add("Hell", spawnlist.MONSTER, new Object[] { EntityFlameWraith.class });
		spawnlist.add("Surface biomes", spawnlist.MONSTER, new Object[] { EntityFlameWraith.class });
		if(spawnBunnies) 
			spawnlist.add("Surface biomes", spawnlist.CREATURE, new Object[] { EntityBunny.class }); 
		if(spawnBirds) 
			spawnlist.add("Surface biomes", spawnlist.CREATURE, new Object[] { EntityBird.class }); 
		if(spawnFoxes) 
			spawnlist.add("Surface biomes", spawnlist.CREATURE, new Object[] { EntityFox.class }); 
		if(spawnWerewolves) 
			spawnlist.add("Surface biomes", spawnlist.MONSTER, new Object[] { EntityWerewolf.class }); 
		if(spawnSharks) 
			spawnlist.add("Surface biomes", spawnlist.WATERCREATURE, new Object[] { EntityShark.class }); 
		if(spawnDolphins) 
			spawnlist.add("Surface biomes", spawnlist.WATERCREATURE, new Object[] { EntityDolphin.class }); 
		if(!spawnSquids) 
			spawnlist.remove("Surface biomes", spawnlist.WATERCREATURE, new Object[] { "Squid" }); 
	}

	public String version()
	{
		return "v1.1.0 (MoCr 2.8.1) (MC 1.3_01)";
	}

	public void addRecipes(CraftingManager craftingmanager)
	{
		craftingmanager.addRecipe(new ItemStack(horseSaddle, 1), new Object[] {
			"XXX", "X#X", "# #", Character.valueOf('#'), Item.ingotIron, Character.valueOf('X'), Item.leather
		});
		craftingmanager.addRecipe(new ItemStack(hayStack, 1), new Object[] {
			"XXX", "XXX", Character.valueOf('X'), Item.wheat
		});
		craftingmanager.addRecipe(new ItemStack(Item.wheat, 6), new Object[] {
			"X", Character.valueOf('X'), hayStack
		});
		craftingmanager.addRecipe(new ItemStack(sugarLump, 1), new Object[] {
			"XX", "##", Character.valueOf('X'), Item.sugar, Character.valueOf('#'), Item.sugar
		});
		craftingmanager.addRecipe(new ItemStack(horseSaddle, 1), new Object[] {
			"X", "#", Character.valueOf('X'), Item.saddle, Character.valueOf('#'), Item.leather
		});
	}

	private int getDifficulty(String difficulty)
	{
		if (difficulties.containsKey(difficulty))
			return difficulties.get(difficulty);
		return 1;
	}

	private float getFloatProperty(String s, float i)
	{
		try
		{
			return Float.parseFloat(propertyManagerObj.getStringProperty(s, ""+i));
		}
		catch(NumberFormatException exception)
		{
			MinecraftServer.logger.warning(new StringBuilder("Property [").append(s).append("] must be a float value. Default value(").append(i).append(") is used.").toString());
		}
		return i;
	}
}
