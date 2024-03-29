// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ChunkProviderServer;
import net.minecraft.src.ConsoleCommandHandler;
import net.minecraft.src.ConsoleLogManager;
import net.minecraft.src.ConvertProgressUpdater;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.ISaveFormat;
import net.minecraft.src.IUpdatePlayerListBox;
import net.minecraft.src.NetworkListenThread;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.PropertyManager;
import net.minecraft.src.SaveConverterMcRegion;
import net.minecraft.src.SaveOldDir;
import net.minecraft.src.ServerCommand;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.ServerGUI;
import net.minecraft.src.StatList;
import net.minecraft.src.ThreadCommandReader;
import net.minecraft.src.ThreadServerApplication;
import net.minecraft.src.ThreadSleepForever;
import net.minecraft.src.Vec3D;
import net.minecraft.src.WorldManager;
import net.minecraft.src.WorldServer;

public class MinecraftServer
    implements Runnable, ICommandListener
{

    public MinecraftServer()
    {
        serverRunning = true;
        serverStopped = false;
        deathTime = 0;
        field_9010_p = new ArrayList();
        commands = Collections.synchronizedList(new ArrayList());
        new ThreadSleepForever(this);
    }

    private boolean startServer() throws UnknownHostException
    {
        commandHandler = new ConsoleCommandHandler(this);
        ThreadCommandReader threadcommandreader = new ThreadCommandReader(this);
        threadcommandreader.setDaemon(true);
        threadcommandreader.start();
        ConsoleLogManager.init();
        logger.info("Starting minecraft server version Beta 1.5_02");
        if(Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            logger.warning("**** NOT ENOUGH RAM!");
            logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        logger.info("Loading properties");
        propertyManagerObj = new PropertyManager(new File("server.properties"));
        String s = propertyManagerObj.getStringProperty("server-ip", "");
        onlineMode = propertyManagerObj.getBooleanProperty("online-mode", true);
        spawnPeacefulMobs = propertyManagerObj.getBooleanProperty("spawn-animals", true);
        pvpOn = propertyManagerObj.getBooleanProperty("pvp", true);
        allowFlight = propertyManagerObj.getBooleanProperty("allow-flight", false);
        InetAddress inetaddress = null;
        if(s.length() > 0)
        {
            inetaddress = InetAddress.getByName(s);
        }
        int i = propertyManagerObj.getIntProperty("server-port", 25565);
        logger.info((new StringBuilder()).append("Starting Minecraft server on ").append(s.length() != 0 ? s : "*").append(":").append(i).toString());
        try
        {
            networkServer = new NetworkListenThread(this, inetaddress, i);
        }
        catch(IOException ioexception)
        {
            logger.warning("**** FAILED TO BIND TO PORT!");
            logger.log(Level.WARNING, (new StringBuilder()).append("The exception was: ").append(ioexception.toString()).toString());
            logger.warning("Perhaps a server is already running on that port?");
            return false;
        }
        if(!onlineMode)
        {
            logger.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            logger.warning("The server will make no attempt to authenticate usernames. Beware.");
            logger.warning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            logger.warning("To change this, set \"online-mode\" to \"true\" in the server.settings file.");
        }
        configManager = new ServerConfigurationManager(this);
        entityTracker = new EntityTracker(this);
        long l = System.nanoTime();
        String s1 = propertyManagerObj.getStringProperty("level-name", "world");
        String s2 = propertyManagerObj.getStringProperty("level-seed", "");
        long l1 = (new Random()).nextLong();
        if(s2.length() > 0)
        {
            try
            {
                l1 = Long.parseLong(s2);
            }
            catch(NumberFormatException numberformatexception)
            {
                l1 = s2.hashCode();
            }
        }
        logger.info((new StringBuilder()).append("Preparing level \"").append(s1).append("\"").toString());
        initWorld(new SaveConverterMcRegion(new File(".")), s1, l1);
        logger.info((new StringBuilder()).append("Done (").append(System.nanoTime() - l).append("ns)! For help, type \"help\" or \"?\"").toString());
        return true;
    }

    private void initWorld(ISaveFormat isaveformat, String s, long l)
    {
        if(isaveformat.isOldSaveType(s))
        {
            logger.info("Converting map!");
            isaveformat.converMapToMCRegion(s, new ConvertProgressUpdater(this));
        }
        logger.info("Preparing start region");
        worldMngr = new WorldServer(this, new SaveOldDir(new File("."), s, true), s, propertyManagerObj.getBooleanProperty("hellworld", false) ? -1 : 0, l);
        worldMngr.addWorldAccess(new WorldManager(this));
        worldMngr.difficultySetting = propertyManagerObj.getBooleanProperty("spawn-monsters", true) ? 1 : 0;
        worldMngr.setAllowedSpawnTypes(propertyManagerObj.getBooleanProperty("spawn-monsters", true), spawnPeacefulMobs);
        configManager.setPlayerManager(worldMngr);
        char c = '\304';
        long l1 = System.currentTimeMillis();
        ChunkCoordinates chunkcoordinates = worldMngr.getSpawnPoint();
        for(int i = -c; i <= c && serverRunning; i += 16)
        {
            for(int j = -c; j <= c && serverRunning; j += 16)
            {
                long l2 = System.currentTimeMillis();
                if(l2 < l1)
                {
                    l1 = l2;
                }
                if(l2 > l1 + 1000L)
                {
                    int k = (c * 2 + 1) * (c * 2 + 1);
                    int i1 = (i + c) * (c * 2 + 1) + (j + 1);
                    outputPercentRemaining("Preparing spawn area", (i1 * 100) / k);
                    l1 = l2;
                }
                worldMngr.chunkProvider.loadChunk(chunkcoordinates.posX + i >> 4, chunkcoordinates.posZ + j >> 4);
                while(worldMngr.func_6156_d() && serverRunning) ;
            }

        }

        clearCurrentTask();
    }

    private void outputPercentRemaining(String s, int i)
    {
        currentTask = s;
        percentDone = i;
        logger.info((new StringBuilder()).append(s).append(": ").append(i).append("%").toString());
    }

    private void clearCurrentTask()
    {
        currentTask = null;
        percentDone = 0;
    }

    private void saveServerWorld()
    {
        logger.info("Saving chunks");
        worldMngr.saveWorld(true, null);
        worldMngr.func_27082_w();
    }

    private void stopServer()
    {
        logger.info("Stopping server");
        if(configManager != null)
        {
            configManager.savePlayerStates();
        }
        if(worldMngr != null)
        {
            saveServerWorld();
        }
    }

    public void initiateShutdown()
    {
        serverRunning = false;
    }

    public void run()
    {
        try
        {
            if(startServer())
            {
                long l = System.currentTimeMillis();
                long l1 = 0L;
                while(serverRunning) 
                {
                    long l2 = System.currentTimeMillis();
                    long l3 = l2 - l;
                    if(l3 > 2000L)
                    {
                        logger.warning("Can't keep up! Did the system time change, or is the server overloaded?");
                        l3 = 2000L;
                    }
                    if(l3 < 0L)
                    {
                        logger.warning("Time ran backwards! Did the system time change?");
                        l3 = 0L;
                    }
                    l1 += l3;
                    l = l2;
                    if(worldMngr.isAllPlayersFullyAsleep())
                    {
                        doTick();
                        l1 = 0L;
                    } else
                    {
                        while(l1 > 50L) 
                        {
                            l1 -= 50L;
                            doTick();
                        }
                    }
                    Thread.sleep(1L);
                }
            } else
            {
                while(serverRunning) 
                {
                    commandLineParser();
                    try
                    {
                        Thread.sleep(10L);
                    }
                    catch(InterruptedException interruptedexception)
                    {
                        interruptedexception.printStackTrace();
                    }
                }
            }
        }
        catch(Throwable throwable1)
        {
            throwable1.printStackTrace();
            logger.log(Level.SEVERE, "Unexpected exception", throwable1);
            while(serverRunning) 
            {
                commandLineParser();
                try
                {
                    Thread.sleep(10L);
                }
                catch(InterruptedException interruptedexception1)
                {
                    interruptedexception1.printStackTrace();
                }
            }
        }
        finally
        {
            try
            {
                stopServer();
                serverStopped = true;
            }
            catch(Throwable throwable2)
            {
                throwable2.printStackTrace();
            }
            finally
            {
                System.exit(0);
            }
        }
    }

    private void doTick()
    {
        ArrayList arraylist = new ArrayList();
        for(Iterator iterator = field_6037_b.keySet().iterator(); iterator.hasNext();)
        {
            String s = (String)iterator.next();
            int k = ((Integer)field_6037_b.get(s)).intValue();
            if(k > 0)
            {
                field_6037_b.put(s, Integer.valueOf(k - 1));
            } else
            {
                arraylist.add(s);
            }
        }

        for(int i = 0; i < arraylist.size(); i++)
        {
            field_6037_b.remove(arraylist.get(i));
        }

        AxisAlignedBB.clearBoundingBoxPool();
        Vec3D.initialize();
        deathTime++;
        if(deathTime % 20 == 0)
        {
            configManager.sendPacketToAllPlayers(new Packet4UpdateTime(worldMngr.getWorldTime()));
        }
        worldMngr.tick();
        while(worldMngr.func_6156_d()) ;
        worldMngr.updateEntities();
        networkServer.handleNetworkListenThread();
        configManager.onTick();
        entityTracker.updateTrackedEntities();
        for(int j = 0; j < field_9010_p.size(); j++)
        {
            ((IUpdatePlayerListBox)field_9010_p.get(j)).update();
        }

        try
        {
            commandLineParser();
        }
        catch(Exception exception)
        {
            logger.log(Level.WARNING, "Unexpected exception while parsing console command", exception);
        }
    }

    public void addCommand(String s, ICommandListener icommandlistener)
    {
        commands.add(new ServerCommand(s, icommandlistener));
    }

    public void commandLineParser()
    {
        ServerCommand servercommand;
        for(; commands.size() > 0; commandHandler.handleCommand(servercommand))
        {
            servercommand = (ServerCommand)commands.remove(0);
        }

    }

    public void func_6022_a(IUpdatePlayerListBox iupdateplayerlistbox)
    {
        field_9010_p.add(iupdateplayerlistbox);
    }

    public static void main(String args[])
    {
        StatList.func_27092_a();
        try
        {
            MinecraftServer minecraftserver = new MinecraftServer();
            if(!GraphicsEnvironment.isHeadless() && (args.length <= 0 || !args[0].equals("nogui")))
            {
                ServerGUI.initGui(minecraftserver);
            }
            (new ThreadServerApplication("Server thread", minecraftserver)).start();
        }
        catch(Exception exception)
        {
            logger.log(Level.SEVERE, "Failed to start the minecraft server", exception);
        }
    }

    public File getFile(String s)
    {
        return new File(s);
    }

    public void log(String s)
    {
        logger.info(s);
    }

    public void logWarning(String s)
    {
        logger.warning(s);
    }

    public String getUsername()
    {
        return "CONSOLE";
    }

    public static boolean isServerRunning(MinecraftServer minecraftserver)
    {
        return minecraftserver.serverRunning;
    }

    public static Logger logger = Logger.getLogger("Minecraft");
    public static HashMap field_6037_b = new HashMap();
    public NetworkListenThread networkServer;
    public PropertyManager propertyManagerObj;
    public WorldServer worldMngr;
    public ServerConfigurationManager configManager;
    private ConsoleCommandHandler commandHandler;
    private boolean serverRunning;
    public boolean serverStopped;
    int deathTime;
    public String currentTask;
    public int percentDone;
    private java.util.List field_9010_p;
    private java.util.List commands;
    public EntityTracker entityTracker;
    public boolean onlineMode;
    public boolean spawnPeacefulMobs;
    public boolean pvpOn;
    public boolean allowFlight;

}
