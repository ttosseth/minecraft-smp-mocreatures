import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

public final class ModLoader
{
	private static final List<av> animList = new LinkedList<av>();
	private static final Map<Integer, BaseMod> blockModels = new HashMap<Integer, BaseMod>();
	private static final Map<Integer, Boolean> blockSpecialInv = new HashMap<Integer, Boolean>();
	private static final File cfgdir = new File(Minecraft.b(), "/config/");
	private static final File cfgfile = new File(cfgdir, "ModLoader.cfg");

	public static Level cfgLoggingLevel = Level.FINER;
	private static Map<String, Class<? extends si>> classMap = null;
	private static long clock = 0L;
	public static final boolean DEBUG = false;
	private static Field field_animList = null;
	private static Field field_armorList = null;
	private static Field field_blockList = null;
	private static Field field_modifiers = null;
	private static Field field_TileEntityRenderers = null;
	private static boolean hasInit = false;
	private static int highestEntityId = 3000;
	private static final Map<BaseMod, Boolean> inGameHooks = new HashMap<BaseMod, Boolean>();
	private static final Map<BaseMod, Boolean> inGUIHooks = new HashMap<BaseMod, Boolean>();
	private static Minecraft instance = null;
	private static int itemSpriteIndex = 0;
	private static int itemSpritesLeft = 0;
	private static final Map<BaseMod, Map<px, boolean[]>> keyList = new HashMap<BaseMod, Map<px, boolean[]>>();
	private static final File logfile = new File(Minecraft.b(), "ModLoader.txt");
	private static final File modDir = new File(Minecraft.b(), "/mods/");
	private static final Logger logger = Logger.getLogger("ModLoader");
	private static FileHandler logHandler = null;
	private static Method method_RegisterEntityID = null;
	private static Method method_RegisterTileEntity = null;
	private static final Map<String, BaseMod> modList = new HashMap<String, BaseMod>();
	private static int nextBlockModelID = 1000;
	private static final Map<Integer, Map<String, Integer>> overrides = new HashMap<Integer, Map<String, Integer>>();

	public static final Properties props = new Properties();
	private static jz[] standardBiomes;
	private static int terrainSpriteIndex = 0;
	private static int terrainSpritesLeft = 0;
	private static String texPack = null;

	private static boolean texturesAdded = false;

	private static final boolean[] usedItemSprites = new boolean[256];

	private static final boolean[] usedTerrainSprites = new boolean[256];
	public static final String VERSION = "ModLoader Beta 1.6.6";

	public static void AddAchievementDesc(nu achievement, String name, String description)
	{
		try
		{
			if (achievement.f.contains(".")) {
				String key = achievement.f.split("\\.")[1];
				AddLocalization("achievement." + key, name);
				AddLocalization("achievement." + key + ".desc", description);
				setPrivateValue(vj.class, achievement, 1, dm.a("achievement." + key));
				setPrivateValue(nu.class, achievement, 3, dm.a("achievement." + key + ".desc"));
			} else {
				setPrivateValue(vj.class, achievement, 1, name);
				setPrivateValue(nu.class, achievement, 3, description);
			}
		} catch (IllegalArgumentException e) {
			logger.throwing("ModLoader", "AddAchievementDesc", e);
			ThrowException(e);
		} catch (SecurityException e) {
			logger.throwing("ModLoader", "AddAchievementDesc", e);
			ThrowException(e);
		} catch (NoSuchFieldException e) {
			logger.throwing("ModLoader", "AddAchievementDesc", e);
			ThrowException(e);
		}
	}

	public static int AddAllFuel(int id)
	{
		logger.finest("Finding fuel for " + id);
		int result = 0;
		for (BaseMod mod : modList.values())
		{
			if ((result = mod.AddFuel(id)) == 0)
			{
				logger.finest("Returned " + result);
				break;
			}
		}
		return result;
	}

	public static void AddAllRenderers(Map<Class<? extends si>, bu> o)
	{
		if (!hasInit) {
			init();
			logger.fine("Initialized");
		}

		for (BaseMod mod : modList.values())
			mod.AddRenderer(o);
	}

	public static void addAnimation(av anim)
	{
		logger.finest("Adding animation " + anim.toString());
		for (av oldAnim : animList)
		{
			if (oldAnim.b != anim.b)
				continue;
			animList.remove(anim);
			break;
		}

		animList.add(anim);
	}

	public static int AddArmor(String armor)
	{
		try
		{
			String[] existingArmor = (String[])field_armorList.get(null);
			List<String> existingArmorList = Arrays.asList(existingArmor);
			List<String> combinedList = new ArrayList<String>();
			combinedList.addAll(existingArmorList);
			if (!combinedList.contains(armor))
				combinedList.add(armor);
			int index = combinedList.indexOf(armor);
			field_armorList.set(null, combinedList.toArray(new String[0]));
			return index;
		} catch (IllegalArgumentException e) {
			logger.throwing("ModLoader", "AddArmor", e);
			ThrowException("An impossible error has occured!", e);
		} catch (IllegalAccessException e) {
			logger.throwing("ModLoader", "AddArmor", e);
			ThrowException("An impossible error has occured!", e);
		}
		return -1;
	}

	public static void AddLocalization(String key, String value)
	{
		Properties props = null;
		try {
			props = (Properties)getPrivateValue(nd.class, nd.a(), 1);
		} catch (SecurityException e) {
			logger.throwing("ModLoader", "AddLocalization", e);
			ThrowException(e);
		} catch (NoSuchFieldException e) {
			logger.throwing("ModLoader", "AddLocalization", e);
			ThrowException(e);
		}
		if (props != null)
			props.put(key, value);
	}

	private static void addMod(ClassLoader classLoader, String className, String classFullName)
	{
		try
		{
			classFullName = classFullName.substring(0, classFullName.lastIndexOf('.'));
			if(!classFullName.contains("$") && (!props.containsKey(className) || (!props.getProperty(className).equalsIgnoreCase("no") && !props.getProperty(className).equalsIgnoreCase("off"))))
			{
				Class modClass = classLoader.loadClass(classFullName);
				if(BaseMod.class.isAssignableFrom(modClass))
				{
					setupProperties(modClass);
					BaseMod mod = (BaseMod)modClass.newInstance();
					String modName = mod.toString();
					if(mod != null && !modList.containsKey(modName))
					{
						modList.put(modName, mod);
						logger.fine((new StringBuilder("Mod Loaded: \"")).append(modName).append("\" from ").append(className).toString());
						System.out.println("Mod Loaded: " + modName);
					}
				}
			}
		}
		catch(Throwable e)
		{
			logger.fine((new StringBuilder("Failed to load mod from \"")).append(className).append("\"").toString());
			System.out.println((new StringBuilder("Failed to load mod from \"")).append(className).append("\"").toString());
			logger.throwing("ModLoader", "addMod", e);
			ThrowException(e);
		}
	}

	private static void setupProperties(Class<? extends BaseMod> mod) throws IllegalArgumentException, IllegalAccessException, IOException, SecurityException, NoSuchFieldException
	{
		Properties modprops = new Properties();

		File modcfgfile = new File(cfgdir, mod.getName() + ".cfg");
		if ((modcfgfile.exists()) && (modcfgfile.canRead())) {
			modprops.load(new FileInputStream(modcfgfile));
		}
		StringBuilder helptext = new StringBuilder();

		for (Field field : mod.getFields()) {
			if (((field.getModifiers() & 0x8) != 0) && (field.isAnnotationPresent(MLProp.class))) {
				Class type = field.getType();
				MLProp annotation = field.getAnnotation(MLProp.class);
				String key = annotation.name().length() == 0 ? field.getName() : annotation.name();
				Object currentvalue = field.get(null);

				StringBuilder range = new StringBuilder();
				if (annotation.min() != (-1.0D / 0.0D))
					range.append(String.format(",>=%.1f", new Object[] { Double.valueOf(annotation.min()) }));
				if (annotation.max() != (1.0D / 0.0D)) {
					range.append(String.format(",<=%.1f", new Object[] { Double.valueOf(annotation.max()) }));
				}
				StringBuilder info = new StringBuilder();
				if (annotation.info().length() > 0) {
					info.append(" -- ");
					info.append(annotation.info());
				}

				helptext.append(String.format("%s (%s:%s%s)%s\n", new Object[] { key, type.getName(), currentvalue, range, info }));

				if (modprops.containsKey(key)) {
					String strvalue = modprops.getProperty(key);

					Object value = null;
					if (type.isAssignableFrom(String.class)) value = strvalue;
					else if (type.isAssignableFrom(Integer.TYPE)) value = Integer.valueOf(Integer.parseInt(strvalue));
					else if (type.isAssignableFrom(Short.TYPE)) value = Short.valueOf(Short.parseShort(strvalue));
					else if (type.isAssignableFrom(Byte.TYPE)) value = Byte.valueOf(Byte.parseByte(strvalue));
					else if (type.isAssignableFrom(Boolean.TYPE)) value = Boolean.valueOf(Boolean.parseBoolean(strvalue));
					else if (type.isAssignableFrom(Float.TYPE)) value = Float.valueOf(Float.parseFloat(strvalue));
					else if (type.isAssignableFrom(Double.TYPE)) {
						value = Double.valueOf(Double.parseDouble(strvalue));
					}
					if (value != null) {
						if ((value instanceof Number)) {
							double num = ((Number)value).doubleValue();
							if ((annotation.min() != (-1.0D / 0.0D)) && (num < annotation.min()))
								continue;
							if ((annotation.max() != (1.0D / 0.0D)) && (num > annotation.max())) {
								continue;
							}
						}
						logger.finer(key + " set to " + value);
						if (!value.equals(currentvalue))
							field.set(null, value);
					}
				} else {
					logger.finer(key + " not in config, using default: " + currentvalue);
					modprops.setProperty(key, currentvalue.toString());
				}
			}
		}

		if ((!modprops.isEmpty()) && ((modcfgfile.exists()) || (modcfgfile.createNewFile())) && (modcfgfile.canWrite()))
			modprops.store(new FileOutputStream(modcfgfile), helptext.toString());
	}

	public static void AddName(Object instance, String name)
	{
		String tag = null;
		if ((instance instanceof gk)) {
			gk item = (gk)instance;
			if (item.a() != null)
				tag = item.a() + ".name";
		} else if ((instance instanceof un)) {
			un block = (un)instance;
			if (block.k() != null)
				tag = block.k() + ".name";
		} else if ((instance instanceof iw)) {
			iw stack = (iw)instance;
			if (stack.l() != null)
				tag = stack.l() + ".name";
		} else {
			Exception e = new Exception(instance.getClass().getName() + " cannot have name attached to it!");
			logger.throwing("ModLoader", "AddName", e);
			ThrowException(e);
		}
		if (tag != null) { 
			AddLocalization(tag, name);
		} else {
			Exception e = new Exception(instance + " is missing name tag!");
			logger.throwing("ModLoader", "AddName", e);
			ThrowException(e);
		}
	}

	public static int addOverride(String fileToOverride, String fileToAdd)
	{
		try
		{
			int i = getUniqueSpriteIndex(fileToOverride);
			addOverride(fileToOverride, fileToAdd, i);
			return i;
		} catch (Throwable e) {
			logger.throwing("ModLoader", "addOverride", e);
			ThrowException(e);
			throw new RuntimeException(e);
		}
	}

	public static void addOverride(String path, String overlayPath, int index)
	{
		int dst = -1;
		int left = 0;
		if (path.equals("/terrain.png")) {
			dst = 0;
			left = terrainSpritesLeft;
		} else if (path.equals("/gui/items.png")) {
			dst = 1;
			left = itemSpritesLeft; } else {
				return;
			}System.out.println("Overriding " + path + " with " + overlayPath + " @ " + index + ". " + left + " left.");
			logger.finer("addOverride(" + path + "," + overlayPath + "," + index + "). " + left + " left.");
			Map<String, Integer> overlays = overrides.get(Integer.valueOf(dst));
			if (overlays == null) {
				overlays = new HashMap<String, Integer>();
				overrides.put(Integer.valueOf(dst), overlays);
			}
			overlays.put(overlayPath, Integer.valueOf(index));
	}

	public static void AddRecipe(iw output, Object[] params)
	{
		hi.a().a(output, params);
	}

	public static void AddShapelessRecipe(iw output, Object[] params)
	{
		hi.a().b(output, params);
	}

	public static void AddSmelting(int input, iw output)
	{
		ew.a().a(input, output);
	}

	public static void AddSpawn(Class<? extends lo> entityClass, int weightedProb, lg spawnList)
	{
		AddSpawn(entityClass, weightedProb, spawnList, null);
	}

	public static void AddSpawn(Class<? extends lo> entityClass, int weightedProb, lg spawnList, jz[] biomes)
	{
		if (entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		}
		if (spawnList == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		}
		if (biomes == null) {
			biomes = standardBiomes;
		}
		for (int i = 0; i < biomes.length; i++)
		{
			List<bi> list = biomes[i].a(spawnList);

			if (list != null) {
				boolean exists = false;
				for (bi entry : list) {
					if (entry.a == entityClass) {
						entry.b = weightedProb;
						exists = true;
						break;
					}
				}
				if (!exists)
					list.add(new bi(entityClass, weightedProb));
			}
		}
	}

	public static void AddSpawn(String entityName, int weightedProb, lg spawnList)
	{
		AddSpawn(entityName, weightedProb, spawnList, null);
	}

	public static void AddSpawn(String entityName, int weightedProb, lg spawnList, jz[] biomes)
	{
		Class entityClass = classMap.get(entityName);
		if ((entityClass != null) && (lo.class.isAssignableFrom(entityClass)))
			AddSpawn(entityClass, weightedProb, spawnList, biomes);
	}

	public static boolean DispenseEntity(fb world, double x, double y, double z, int xVel, int zVel, iw item)
	{
		for (BaseMod mod : modList.values())
			if (mod.DispenseEntity(world, x, y, z, xVel, zVel, item))
				return true;
		return false;
	}

	public static List<BaseMod> getLoadedMods()
	{
		return Collections.unmodifiableList(new LinkedList<BaseMod>(modList.values()));
	}

	public static Logger getLogger()
	{
		return logger;
	}

	public static Minecraft getMinecraftInstance()
	{
		if (instance == null) {
			try {
				ThreadGroup group = Thread.currentThread().getThreadGroup();
				int count = group.activeCount();
				Thread[] threads = new Thread[count];
				group.enumerate(threads);
				for (Thread thread : threads)
					if (thread.getName().equals("Minecraft main thread")) {
						instance = (Minecraft)getPrivateValue(Thread.class, thread, "target");
						break;
					}
			}
			catch (SecurityException e) {
				logger.throwing("ModLoader", "getMinecraftInstance", e);
				throw new RuntimeException(e);
			} catch (NoSuchFieldException e) {
				logger.throwing("ModLoader", "getMinecraftInstance", e);
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, int fieldindex)
	throws IllegalArgumentException, SecurityException, NoSuchFieldException
	{
		try
		{
			Field f = instanceclass.getDeclaredFields()[fieldindex];
			f.setAccessible(true);
			return (T)f.get(instance);
		} catch (IllegalAccessException e) {
			logger.throwing("ModLoader", "getPrivateValue", e);
			ThrowException("An impossible error has occured!", e);
		}
		return null;
	}

	public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, String field)
	throws IllegalArgumentException, SecurityException, NoSuchFieldException
	{
		try
		{
			Field f = instanceclass.getDeclaredField(field);
			f.setAccessible(true);
			return (T)f.get(instance);
		} catch (IllegalAccessException e) {
			logger.throwing("ModLoader", "getPrivateValue", e);
			ThrowException("An impossible error has occured!", e);
		}
		return null;
	}

	public static int getUniqueBlockModelID(BaseMod mod, boolean full3DItem)
	{
		int id = nextBlockModelID++;
		blockModels.put(Integer.valueOf(id), mod);
		blockSpecialInv.put(Integer.valueOf(id), Boolean.valueOf(full3DItem));
		return id;
	}

	public static int getUniqueEntityId()
	{
		return highestEntityId++;
	}

	private static int getUniqueItemSpriteIndex()
	{
		for (; itemSpriteIndex < usedItemSprites.length; itemSpriteIndex += 1)
			if (!usedItemSprites[itemSpriteIndex]) {
				usedItemSprites[itemSpriteIndex] = true;
				itemSpritesLeft -= 1;
				return itemSpriteIndex++;
			}
		Exception e = new Exception("No more empty item sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
		ThrowException(e);
		return 0;
	}

	public static int getUniqueSpriteIndex(String path)
	{
		if (path.equals("/gui/items.png")) return getUniqueItemSpriteIndex();
		if (path.equals("/terrain.png"))
			return getUniqueTerrainSpriteIndex();
		Exception e = new Exception("No registry for this texture: " + path);
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
		ThrowException(e);
		return 0;
	}

	private static int getUniqueTerrainSpriteIndex()
	{
		for (; terrainSpriteIndex < usedTerrainSprites.length; terrainSpriteIndex += 1)
			if (!usedTerrainSprites[terrainSpriteIndex]) {
				usedTerrainSprites[terrainSpriteIndex] = true;
				terrainSpritesLeft -= 1;
				return terrainSpriteIndex++;
			}
		Exception e = new Exception("No more empty terrain sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
		ThrowException(e);
		return 0;
	}

	private static void init()
	{
		hasInit = true;

		String usedItemSpritesString = 
			"1111111111111111111111111111111111111101111111011111111111111001111111111111111111111111111010111111100110000011111110000000001111111001100000110000000100000011000000010000001100000000000000110000000000000000000000000000000000000000000000001100000000000000";

		String usedTerrainSpritesString = 
			"1111111111111111111111111111110111111111111111111111110111111111111111111111000111111011111111111111001111000000111111111111100011111111000010001111011110000000111111000000000011111100000000001111000000000111111000000000001101000000000001111111111111000011";

		for (int i = 0; i < 256; i++) {
			usedItemSprites[i] = usedItemSpritesString.charAt(i) == '1';
			if (!usedItemSprites[i])
				itemSpritesLeft += 1;
			usedTerrainSprites[i] = usedTerrainSpritesString.charAt(i) == '1';
			if (!usedTerrainSprites[i]) {
				terrainSpritesLeft += 1;
			}

		}

		try
		{
			instance = (Minecraft)getPrivateValue(Minecraft.class, null, 1);

			instance.t = new EntityRendererProxy(instance);
			classMap = (Map<String, Class<? extends si>>)getPrivateValue(iz.class, null, 0);
			field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
			field_blockList = gp.class.getDeclaredFields()[0];
			field_blockList.setAccessible(true);
			field_TileEntityRenderers = lh.class.getDeclaredFields()[0];
			field_TileEntityRenderers.setAccessible(true);
			field_armorList = dq.class.getDeclaredFields()[3];
			field_modifiers.setInt(field_armorList, field_armorList.getModifiers() & 0xFFFFFFEF);
			field_armorList.setAccessible(true);
			field_animList = jf.class.getDeclaredFields()[5];
			field_animList.setAccessible(true);

			Field[] fieldArray = jz.class.getDeclaredFields();

			List<jz> biomes = new LinkedList<jz>();
			for (int i = 0; i < fieldArray.length; i++) {
				Class fieldType = fieldArray[i].getType();

				if (((fieldArray[i].getModifiers() & 0x8) != 0) && (fieldType.isAssignableFrom(jz.class))) {
					jz biome = (jz)fieldArray[i].get(null);
					if ((biome instanceof s))
						continue;
					biomes.add(biome);
				}
			}

			standardBiomes = biomes.toArray(new jz[0]);
			try
			{
				method_RegisterTileEntity = os.class.getDeclaredMethod("a", new Class[] { Class.class, String.class });
			} catch (NoSuchMethodException e) {
				method_RegisterTileEntity = os.class.getDeclaredMethod("addMapping", new Class[] { Class.class, String.class });
			}
			method_RegisterTileEntity.setAccessible(true);
			try
			{
				method_RegisterEntityID = iz.class.getDeclaredMethod("a", new Class[] { Class.class, String.class, Integer.TYPE });
			} catch (NoSuchMethodException e) {
				method_RegisterEntityID = iz.class.getDeclaredMethod("addMapping", new Class[] { Class.class, String.class, Integer.TYPE });
			}
			method_RegisterEntityID.setAccessible(true);
		}
		catch (SecurityException e) {
			logger.throwing("ModLoader", "init", e);
			ThrowException(e);
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			logger.throwing("ModLoader", "init", e);
			ThrowException(e);
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			logger.throwing("ModLoader", "init", e);
			ThrowException(e);
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			logger.throwing("ModLoader", "init", e);
			ThrowException(e);
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			logger.throwing("ModLoader", "init", e);
			ThrowException(e);
			throw new RuntimeException(e);
		}

		try
		{
			loadConfig();
			if (props.containsKey("loggingLevel"))
				cfgLoggingLevel = Level.parse(props.getProperty("loggingLevel"));
			if (props.containsKey("grassFix")) {
				ct.cfgGrassFix = Boolean.parseBoolean(props.getProperty("grassFix"));
			}
			logger.setLevel(cfgLoggingLevel);
			if (((logfile.exists()) || (logfile.createNewFile())) && (logfile.canWrite()) && (logHandler == null)) {
				logHandler = new FileHandler(logfile.getPath());
				logHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(logHandler);
			}
			logger.fine("ModLoader Beta 1.6.6 Initializing...");

			System.out.println("ModLoader Beta 1.6.6 Initializing...");
			File source = new File(ModLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			modDir.mkdirs();
			readFromModFolder(modDir);
			readFromClassPath(source);
			System.out.println("Done.");

			props.setProperty("loggingLevel", cfgLoggingLevel.getName());
			props.setProperty("grassFix", Boolean.toString(ct.cfgGrassFix));

			for (BaseMod mod : modList.values()) {
				mod.ModsLoaded();
				if (!props.containsKey(mod.getClass().getName())) {
					props.setProperty(mod.getClass().getName(), "on");
				}
			}
			initStats();

			saveConfig();
		}
		catch (Throwable e) {
			logger.throwing("ModLoader", "init", e);
			ThrowException("ModLoader has failed to initialize.", e);
			if (logHandler != null)
				logHandler.close();
			throw new RuntimeException(e);
		}
	}

	private static void initStats() {
		for (int id = 0; id < un.m.length; id++) {
			if ((!ji.a.containsKey(Integer.valueOf(16777216 + id))) && (un.m[id] != null) && (un.m[id].l())) {
				String str = dm.a("stat.mineBlock", new Object[] { un.m[id].j() });
				ji.C[id] = new tr(16777216 + id, str, id).g();
				ji.e.add(ji.C[id]);
			}
		}

		for (int id = 0; id < gk.c.length; id++) {
			if ((!ji.a.containsKey(Integer.valueOf(16908288 + id))) && (gk.c[id] != null)) {
				String str = dm.a("stat.useItem", new Object[] { gk.c[id].k() });
				ji.E[id] = new tr(16908288 + id, str, id).g();

				if (id >= un.m.length) {
					ji.d.add(ji.E[id]);
				}
			}

			if ((!ji.a.containsKey(Integer.valueOf(16973824 + id))) && (gk.c[id] != null) && (gk.c[id].g())) {
				String str = dm.a("stat.breakItem", new Object[] { gk.c[id].k() });
				ji.F[id] = new tr(16973824 + id, str, id).g();
			}
		}

		HashSet<Integer> idHashSet = new HashSet<Integer>();

		for (Object result : hi.a().b())
			idHashSet.add(Integer.valueOf(((dr)result).b().c));
		for (Object result : ew.a().b().values())
			idHashSet.add(Integer.valueOf(((iw)result).c));

		for (int id : idHashSet) {
			if ((!ji.a.containsKey(Integer.valueOf(16842752 + id))) && (gk.c[id] != null)) {
				String str = dm.a("stat.craftItem", new Object[] { gk.c[id].k() });
				ji.D[id] = new tr(16842752 + id, str, id).g();
			}
		}
	}

	public static boolean isGUIOpen(Class<? extends cy> gui)
	{
		Minecraft game = getMinecraftInstance();
		if (gui == null) {
			return game.r == null;
		}
		if ((game.r == null) && (gui != null)) {
			return false;
		}
		return gui.isInstance(game.r);
	}

	public static boolean isModLoaded(String modname)
	{
		Class chk = null;
		try {
			chk = Class.forName(modname);
		} catch (ClassNotFoundException e) {
			return false;
		}
		if (chk != null) {
			for (BaseMod mod : modList.values()) {
				if (chk.isInstance(mod))
					return true;
			}
		}
		return false;
	}

	public static void loadConfig()
	throws IOException
	{
		cfgdir.mkdir();

		if ((!cfgfile.exists()) && (!cfgfile.createNewFile())) {
			return;
		}
		if (cfgfile.canRead()) {
			InputStream in = new FileInputStream(cfgfile);
			props.load(in);
			in.close();
		}
	}

	public static BufferedImage loadImage(jf texCache, String path)
	throws Exception
	{
		ih pack = (ih)getPrivateValue(jf.class, texCache, 11);
		InputStream input = pack.a.a(path);
		if (input == null)
			throw new Exception("Image not found: " + path);
		BufferedImage image = ImageIO.read(input);
		if (image == null)
			throw new Exception("Image not found: " + path);
		return image;
	}

	public static void OnTick(Minecraft game)
	{
		if (!hasInit) {
			init();
			logger.fine("Initialized");
		}

		if ((texPack == null) || (game.z.l != texPack)) {
			texturesAdded = false;
			texPack = game.z.l;
		}

		if ((!texturesAdded) && (game.p != null))
		{
			RegisterAllTextureOverrides(game.p);
			texturesAdded = true;
		}

		long newclock = 0L;

		if (game.f != null)
		{
			newclock = game.f.t();
			for (Map.Entry<BaseMod, Boolean> modSet : inGameHooks.entrySet()) {
				if ((clock == newclock) && (modSet.getValue().booleanValue()))
					continue;
				modSet.getKey().OnTickInGame(game);
			}
		}

		if (game.r != null) {
			for (Map.Entry<BaseMod, Boolean> modSet : inGUIHooks.entrySet()) {
				if (clock == newclock) if ((modSet.getValue().booleanValue() & game.f != null))
					continue;
				modSet.getKey().OnTickInGUI(game, game.r);
			}
		}

		if (clock != newclock) {
			for (Map.Entry<BaseMod, Map<px, boolean[]>> modSet : keyList.entrySet()) {
				for (Map.Entry<px, boolean[]> keySet : modSet.getValue().entrySet()) {
					boolean state = Keyboard.isKeyDown(keySet.getKey().b);
					boolean[] keyInfo = keySet.getValue();
					boolean oldState = keyInfo[1];
					keyInfo[1] = state;
					if ((!state) || ((oldState) && (!keyInfo[0])))
						continue;
					modSet.getKey().KeyboardEvent(keySet.getKey());
				}
			}

		}

		clock = newclock;
	}

	public static void OpenGUI(gq player, cy gui)
	{
		if (!hasInit) {
			init();
			logger.fine("Initialized");
		}
		Minecraft game = getMinecraftInstance();
		if (game.h != player)
			return;
		if (gui != null)
			game.a(gui);
	}

	public static void PopulateChunk(cj generator, int chunkX, int chunkZ, fb world)
	{
		if (!hasInit) {
			init();
			logger.fine("Initialized");
		}
		for (BaseMod mod : modList.values())
		{
			if (generator.c().equals("RandomLevelSource")) mod.GenerateSurface(world, world.r, chunkX, chunkZ);
			else if (generator.c().equals("HellRandomLevelSource"))
				mod.GenerateNether(world, world.r, chunkX, chunkZ);
		}
	}

	private static void readFromModFolder(File folder) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
	{
		if (!folder.isDirectory()) {
			throw new IllegalArgumentException("folder must be a Directory.");
		}
		logger.finer((new StringBuilder("Adding mods from ")).append(folder.getCanonicalPath()).toString());
		System.out.println((new StringBuilder("Adding mods from ")).append(folder.getCanonicalPath()).toString());
		readFromFileRecursive(folder, folder);
	}

	private static void readFromClassPath(File file) throws FileNotFoundException, IOException
	{
		logger.finer((new StringBuilder("Adding mods from ")).append(file.getCanonicalPath()).toString());
		System.out.println((new StringBuilder("Adding mods from ")).append(file.getCanonicalPath()).toString());
		readFromFileRecursive(file, file.isDirectory() ? file : file.getParentFile());
	}

	private static void readFromFileRecursive(File file, File folder) throws FileNotFoundException, IOException
	{
		if(file.isDirectory())
			for (File subfFile : file.listFiles())
				readFromFileRecursive(subfFile, folder);
		else if(file.isFile())
		{
			String fileName = file.getName();
			if (fileName.endsWith(".jar") || fileName.endsWith(".zip"))
			{
				logger.finer("Archive found : " + fileName);
				ZipInputStream zipinputstream = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
				try
				{
					for (ZipEntry entry = zipinputstream.getNextEntry(); entry != null; entry = zipinputstream.getNextEntry())
					{
						String entryName = entry.getName();
						String className = entryName.substring(entryName.lastIndexOf('/') + 1, entryName.length());
						if(!entry.isDirectory() && className.startsWith("mod_") && className.endsWith(".class"))
							addMod(new URLClassLoader(new URL[] { folder.toURI().toURL() }), className, entryName.replace('/', '.'));
					}
				}
				finally
				{
					zipinputstream.close();
				}
			}
			else if(fileName.startsWith("mod_") && fileName.endsWith(".class"))
				addMod(new URLClassLoader(new URL[] { folder.toURI().toURL() }), fileName, file.getAbsolutePath().replace(folder.getAbsolutePath() + "\\", "").replace('\\', '.'));
		}
	}

	public static px[] RegisterAllKeys(px[] w)
	{
		List<px> existingKeyList = Arrays.asList(w);
		List<px> combinedList = new ArrayList<px>();
		combinedList.addAll(existingKeyList);
		for (Map<px, boolean[]> keyMap : keyList.values())
			combinedList.addAll(keyMap.keySet());
		return combinedList.toArray(new px[0]);
	}

	public static void RegisterAllTextureOverrides(jf texCache)
	{
		animList.clear();

		Minecraft game = getMinecraftInstance();
		for (BaseMod mod : modList.values()) {
			mod.RegisterAnimation(game);
		}
		for (av anim : animList)
		{
			texCache.a(anim);
		}

		for (Map.Entry<Integer, Map<String, Integer>> overlay : overrides.entrySet())
			for (Map.Entry<String, Integer> overlayEntry : overlay.getValue().entrySet()) {
				String overlayPath = overlayEntry.getKey();
				int index = overlayEntry.getValue().intValue();
				int dst = overlay.getKey().intValue();
				String dstPath = null;
				if (dst == 0) dstPath = "/terrain.png";
				else if (dst == 1) dstPath = "/gui/items.png"; else
					throw new ArrayIndexOutOfBoundsException(dst);
				try {
					BufferedImage im = loadImage(texCache, overlayPath);
					av anim = new ModTextureStatic(index, dst, im);

					texCache.a(anim);
				} catch (Exception e) {
					logger.throwing("ModLoader", "RegisterAllTextureOverrides", e);
					ThrowException(e);
					throw new RuntimeException(e);
				}
			}
	}

	public static void RegisterBlock(un block)
	{
		RegisterBlock(block, null);
	}

	public static void RegisterBlock(un block, Class<? extends ci> itemclass)
	{
		try
		{
			if (block == null) {
				throw new IllegalArgumentException("block parameter cannot be null.");
			}

			List<un> list = (List<un>)field_blockList.get(null);
			list.add(block);

			int id = block.bn;
			ci item = null;
			if (itemclass != null) item = itemclass.getConstructor(new Class[] { Integer.TYPE }).newInstance(new Object[] { Integer.valueOf(id - 256) }); else {
				item = new ci(id - 256);
			}
			if ((un.m[id] != null) && (gk.c[id] == null))
				gk.c[id] = item;
		} catch (IllegalArgumentException e) {
			logger.throwing("ModLoader", "RegisterBlock", e);
			ThrowException(e);
		} catch (IllegalAccessException e) {
			logger.throwing("ModLoader", "RegisterBlock", e);
			ThrowException(e);
		} catch (SecurityException e) {
			logger.throwing("ModLoader", "RegisterBlock", e);
			ThrowException(e);
		} catch (InstantiationException e) {
			logger.throwing("ModLoader", "RegisterBlock", e);
			ThrowException(e);
		} catch (InvocationTargetException e) {
			logger.throwing("ModLoader", "RegisterBlock", e);
			ThrowException(e);
		} catch (NoSuchMethodException e) {
			logger.throwing("ModLoader", "RegisterBlock", e);
			ThrowException(e);
		}
	}

	public static void RegisterEntityID(Class<? extends si> entityClass, String entityName, int id)
	{
		try
		{
			method_RegisterEntityID.invoke(null, new Object[] { entityClass, entityName, Integer.valueOf(id) });
		} catch (IllegalArgumentException e) {
			logger.throwing("ModLoader", "RegisterEntityID", e);
			ThrowException(e);
		} catch (IllegalAccessException e) {
			logger.throwing("ModLoader", "RegisterEntityID", e);
			ThrowException(e);
		} catch (InvocationTargetException e) {
			logger.throwing("ModLoader", "RegisterEntityID", e);
			ThrowException(e);
		}
	}

	public static void RegisterKey(BaseMod mod, px keyHandler, boolean allowRepeat)
	{
		Map<px, boolean[]> keyMap = keyList.get(mod);
		if (keyMap == null)
			keyMap = new HashMap<px, boolean[]>();
			keyMap.put(keyHandler, new boolean[] { allowRepeat });
			keyList.put(mod, keyMap);
	}

	public static void RegisterTileEntity(Class<? extends os> tileEntityClass, String id)
	{
		RegisterTileEntity(tileEntityClass, id, null);
	}

	public static void RegisterTileEntity(Class<? extends os> tileEntityClass, String id, jb renderer)
	{
		try
		{
			method_RegisterTileEntity.invoke(null, new Object[] { tileEntityClass, id });
			if (renderer != null) {
				lh ref = lh.a;

				Map<Class<? extends os>, jb> renderers = (Map<Class<? extends os>, jb>)field_TileEntityRenderers.get(ref);
				renderers.put(tileEntityClass, renderer);
				renderer.a(ref);
			}
		} catch (IllegalArgumentException e) {
			logger.throwing("ModLoader", "RegisterTileEntity", e);
			ThrowException(e);
		} catch (IllegalAccessException e) {
			logger.throwing("ModLoader", "RegisterTileEntity", e);
			ThrowException(e);
		} catch (InvocationTargetException e) {
			logger.throwing("ModLoader", "RegisterTileEntity", e);
			ThrowException(e);
		}
	}

	public static void RemoveSpawn(Class<? extends lo> entityClass, lg spawnList)
	{
		RemoveSpawn(entityClass, spawnList, null);
	}

	public static void RemoveSpawn(Class<? extends lo> entityClass, lg spawnList, jz[] biomes)
	{
		if (entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		}
		if (spawnList == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		}
		if (biomes == null) {
			biomes = standardBiomes;
		}
		for (int i = 0; i < biomes.length; i++) {
			List<bi> list = biomes[i].a(spawnList);

			if (list != null)
				for (bi entry : list)
					if (entry.a == entityClass) {
						list.remove(entry);
						break;
					}
		}
	}

	public static void RemoveSpawn(String entityName, lg spawnList)
	{
		RemoveSpawn(entityName, spawnList, null);
	}

	public static void RemoveSpawn(String entityName, lg spawnList, jz[] biomes)
	{
		Class entityClass = classMap.get(entityName);
		if ((entityClass != null) && (lo.class.isAssignableFrom(entityClass)))
			RemoveSpawn(entityClass, spawnList, biomes);
	}

	public static boolean RenderBlockIsItemFull3D(int modelID)
	{
		if (!blockSpecialInv.containsKey(Integer.valueOf(modelID)))
			return modelID == 11;
		return blockSpecialInv.get(Integer.valueOf(modelID)).booleanValue();
	}

	public static void RenderInvBlock(ct renderer, un block, int metadata, int modelID)
	{
		BaseMod mod = blockModels.get(Integer.valueOf(modelID));
		if (mod == null)
			return;
		mod.RenderInvBlock(renderer, block, metadata, modelID);
	}

	public static boolean RenderWorldBlock(ct renderer, xg world, int x, int y, int z, un block, int modelID)
	{
		BaseMod mod = blockModels.get(Integer.valueOf(modelID));
		if (mod == null)
			return false;
		return mod.RenderWorldBlock(renderer, world, x, y, z, block, modelID);
	}

	public static void saveConfig()
	throws IOException
	{
		cfgdir.mkdir();

		if ((!cfgfile.exists()) && (!cfgfile.createNewFile())) {
			return;
		}
		if (cfgfile.canWrite()) {
			OutputStream out = new FileOutputStream(cfgfile);
			props.store(out, "ModLoader Config");
			out.close();
		}
	}

	public static void SetInGameHook(BaseMod mod, boolean enable, boolean useClock)
	{
		if (enable) inGameHooks.put(mod, Boolean.valueOf(useClock)); else
			inGameHooks.remove(mod);
	}

	public static void SetInGUIHook(BaseMod mod, boolean enable, boolean useClock)
	{
		if (enable) inGUIHooks.put(mod, Boolean.valueOf(useClock)); else
			inGUIHooks.remove(mod);
	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, int fieldindex, E value)
	throws IllegalArgumentException, SecurityException, NoSuchFieldException
	{
		try
		{
			Field f = instanceclass.getDeclaredFields()[fieldindex];
			f.setAccessible(true);
			int modifiers = field_modifiers.getInt(f);
			if ((modifiers & 0x10) != 0)
				field_modifiers.setInt(f, modifiers & 0xFFFFFFEF);
			f.set(instance, value);
		} catch (IllegalAccessException e) {
			logger.throwing("ModLoader", "setPrivateValue", e);
			ThrowException("An impossible error has occured!", e);
		}
	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value)
	throws IllegalArgumentException, SecurityException, NoSuchFieldException
	{
		try
		{
			Field f = instanceclass.getDeclaredField(field);
			int modifiers = field_modifiers.getInt(f);
			if ((modifiers & 0x10) != 0)
				field_modifiers.setInt(f, modifiers & 0xFFFFFFEF);
			f.setAccessible(true);
			f.set(instance, value);
		} catch (IllegalAccessException e) {
			logger.throwing("ModLoader", "setPrivateValue", e);
			ThrowException("An impossible error has occured!", e);
		}
	}

	public static void TakenFromCrafting(gq player, iw item)
	{
		for (BaseMod mod : modList.values())
			mod.TakenFromCrafting(player, item);
	}

	public static void TakenFromFurnace(gq player, iw item)
	{
		for (BaseMod mod : modList.values())
			mod.TakenFromFurnace(player, item);
	}

	public static void OnItemPickup(gq player, iw item)
	{
		for (BaseMod mod : modList.values())
			mod.OnItemPickup(player, item);
	}

	public static void ThrowException(String message, Throwable e)
	{
		Minecraft game = getMinecraftInstance();
		if (game != null) game.a(new mc(message, e)); else
			throw new RuntimeException(e);
	}

	private static void ThrowException(Throwable e)
	{
		ThrowException("Exception occured in ModLoader", e);
	}
}