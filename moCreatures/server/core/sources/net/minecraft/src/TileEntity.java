// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package net.minecraft.src:
//            TileEntityDispenser, TileEntityMobSpawner, World, TileEntityChest, 
//            NBTTagCompound, TileEntityFurnace, TileEntityNote, TileEntitySign, 
//            Packet

public class TileEntity
{

    public TileEntity()
    {
    }

    private static void addMapping(Class class1, String s)
    {
        if(classToNameMap.containsKey(s))
        {
            throw new IllegalArgumentException((new StringBuilder()).append("Duplicate id: ").append(s).toString());
        } else
        {
            nameToClassMap.put(s, class1);
            classToNameMap.put(class1, s);
            return;
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        xCoord = nbttagcompound.getInteger("x");
        yCoord = nbttagcompound.getInteger("y");
        zCoord = nbttagcompound.getInteger("z");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        String s = (String)classToNameMap.get(getClass());
        if(s == null)
        {
            throw new RuntimeException((new StringBuilder()).append(getClass()).append(" is missing a mapping! This is a bug!").toString());
        } else
        {
            nbttagcompound.setString("id", s);
            nbttagcompound.setInteger("x", xCoord);
            nbttagcompound.setInteger("y", yCoord);
            nbttagcompound.setInteger("z", zCoord);
            return;
        }
    }

    public void updateEntity()
    {
    }

    public static TileEntity createAndLoadEntity(NBTTagCompound nbttagcompound)
    {
        TileEntity tileentity = null;
        try
        {
            Class class1 = (Class)nameToClassMap.get(nbttagcompound.getString("id"));
            if(class1 != null)
            {
                tileentity = (TileEntity)class1.newInstance();
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        if(tileentity != null)
        {
            tileentity.readFromNBT(nbttagcompound);
        } else
        {
            System.out.println((new StringBuilder()).append("Skipping TileEntity with id ").append(nbttagcompound.getString("id")).toString());
        }
        return tileentity;
    }

    public void onInventoryChanged()
    {
        if(worldObj != null)
        {
            worldObj.func_515_b(xCoord, yCoord, zCoord, this);
        }
    }

    public Packet getDescriptionPacket()
    {
        return null;
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw new NoClassDefFoundError(classnotfoundexception.getMessage());
        }
    }

    private static Map nameToClassMap = new HashMap();
    private static Map classToNameMap = new HashMap();
    public World worldObj;
    public int xCoord;
    public int yCoord;
    public int zCoord;

    static 
    {
        addMapping(net.minecraft.src.TileEntityFurnace.class, "Furnace");
        addMapping(net.minecraft.src.TileEntityChest.class, "Chest");
        addMapping(net.minecraft.src.TileEntityDispenser.class, "Trap");
        addMapping(net.minecraft.src.TileEntitySign.class, "Sign");
        addMapping(net.minecraft.src.TileEntityMobSpawner.class, "MobSpawner");
        addMapping(net.minecraft.src.TileEntityNote.class, "Music");
    }
}
