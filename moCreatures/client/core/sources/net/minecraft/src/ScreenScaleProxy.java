// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import net.minecraft.client.Minecraft;

// Referenced classes of package net.minecraft.src:
//            ScaledResolution, ModLoader

public class ScreenScaleProxy extends ScaledResolution
{

    public ScreenScaleProxy(int i, int j)
    {
        super(ModLoader.getMinecraftInstance().gameSettings, i, j);
    }
}
