package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

public class EntityFireOgre extends EntityOgre
{
	public static int counterEntity;

	public EntityFireOgre(World world)
	{
		super(world);
		attackStrength = 3;
		attackRange = 16.0D;
		ogreBoolean = false;
		texture = "/mob/fireogre.png";
		setSize(1.5F, 4.0F);
		health = 35;
		fireOgre = true;
		destroyForce = 2.0F;
		isImmuneToFire = true;
		frequencyA = 35;
	}

	protected int getDropItemId()
	{
		return Block.fire.blockID;
	}

	public void onLivingUpdate()
	{
		findPlayerToAttack();
		if(ogreHasEnemy && rand.nextInt(frequencyA) == 0)
		{
			ogreAttack = true;
			attackTime = 15;
		}
		if(worldObj.isDaytime())
		{
			float f = getEntityBrightness(1.0F);
			if(f > 0.5F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F)
				health -= 5;
		}
		super.onLivingUpdate();
	}

	public void setEntityDead()
	{
		counterEntity--;
		super.setEntityDead();
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("CounterEntity", counterEntity);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		counterEntity = nbttagcompound.getInteger("CounterEntity");
	}

	public boolean getCanSpawnHere()
	{
		if(worldObj.difficultySetting >= mod_mocreatures.fireOgresSpawnDifficulty && super.getCanSpawnHere2())
		{
			if(counterEntity >= mod_mocreatures.maxFireOgres)
				return false;
			counterEntity++;
			return true;
		}
		return false;
	}

	public int getMaxSpawnedInChunk()
	{
		return 2;
	}
}
