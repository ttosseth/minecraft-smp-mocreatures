// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;


public enum EnumSkyBlock
{
    Sky("Sky", 0, 15),
    Block("Block", 1, 0);
/*
    public static EnumSkyBlock[] values()
    {
        return (EnumSkyBlock[])d.clone();
    }

    public static EnumSkyBlock valueOf(String s)
    {
        return (EnumSkyBlock)Enum.valueOf(net.minecraft.src.EnumSkyBlock.class, s);
    }
*/
    private EnumSkyBlock(String s, int i, int j)
    {
        //super(s, i);
        field_984_c = j;
    }
/*
    public static final EnumSkyBlock Sky;
    public static final EnumSkyBlock Block;
*/
    public final int field_984_c;
    //private static final EnumSkyBlock d[]; /* synthetic field */
/*
    static 
    {
        Sky = new EnumSkyBlock("Sky", 0, 15);
        Block = new EnumSkyBlock("Block", 1, 0);
        d = (new EnumSkyBlock[] {
            Sky, Block
        });
    }
*/
}
