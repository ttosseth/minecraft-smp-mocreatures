package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 


public class SugarLump extends Item
{
	private int _a;
	
	public SugarLump(int i)
	{
		super(i);
		maxStackSize = 32;
		maxDamage = 64;
		_a = 3;
	}

	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{
		itemstack.stackSize--;
		entityplayer.heal(_a);
		return itemstack;
	}
}
