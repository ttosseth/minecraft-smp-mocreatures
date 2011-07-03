package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.List;

public class EntityBear extends EntityAnimals
{
	protected double attackRange;
	public boolean bearBoolean;
	protected int force;
	public static int counterEntity;

	public EntityBear(World world)
	{
		super(world);
		bearBoolean = false;
		texture = "/mob/bear.png";
		setSize(0.9F, 1.3F);
		health = 25;
		force = 5;
		attackRange = 16.0D;
	}

	public void onLivingUpdate()
	{
		super.onLivingUpdate();
	}

	protected Entity findPlayerToAttack()
	{
		if(worldObj.difficultySetting > 0)
		{
			if(getEntityBrightness(1.0F) < 0.0F)
			{
				EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(this, attackRange);
				if(entityplayer != null)
					return entityplayer;
			}
			if(rand.nextInt(80) == 0)
				return getClosestTarget(this, 10.0D);
		}
		return null;
	}

	public EntityLiving getClosestTarget(Entity entity, double d)
	{
		double d1 = -1.0D;
		EntityLiving entityliving = null;
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, d, d));
		for(int i = 0; i < list.size(); i++)
		{
			Entity entity1 = list.get(i);
			if(!(entity1 instanceof EntityLiving) || entity1 == entity || entity1 == entity.riddenByEntity || entity1 == entity.ridingEntity || (entity1 instanceof EntityPlayer) || (entity1 instanceof EntityMobs) || (entity1 instanceof EntityBear) || (entity1 instanceof EntityLionK) || (entity1 instanceof EntityHorse) && !mod_mocreatures.huntersAttackHorses)
				continue;
			double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
			if((d < 0.0D || d2 < d * d) && (d1 == -1.0D || d2 < d1) && ((EntityLiving)entity1).canEntityBeSeen(entity))
			{
				d1 = d2;
				entityliving = (EntityLiving)entity1;
			}
		}
		return entityliving;
	}

	public boolean attackEntityFrom(Entity entity, int i)
	{
		if(super.attackEntityFrom(entity, i))
		{
			if(riddenByEntity == entity || ridingEntity == entity)
				return true;
			if(entity != this && worldObj.difficultySetting > 0)
				playerToAttack = entity;
			return true;
		}
		return false;
	}

	protected void attackEntity(Entity entity, float f)
	{
		if((double)f < 2.5D && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
		{
			attackTime = 20;
			entity.attackEntityFrom(this, force);
			if(!(entity instanceof EntityPlayer))
				destroyDrops(this, 3.0D);
		}
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("BearBoolean", bearBoolean);
		nbttagcompound.setInteger("CounterEntity", counterEntity);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		bearBoolean = nbttagcompound.getBoolean("BearBoolean");
		counterEntity = nbttagcompound.getInteger("CounterEntity");
	}

	protected String getLivingSound()
	{
		return "beargrunt";
	}

	protected String getHurtSound()
	{
		return "bearhurt";
	}

	protected String getDeathSound()
	{
		return "beardying";
	}

	protected int getDropItemId()
	{
		return Item.fishRaw.shiftedIndex;
	}

	public int getMaxSpawnedInChunk()
	{
		return 2;
	}

	public void destroyDrops(Entity entity, double d)
	{
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(d, d, d));
		for(int i = 0; i < list.size(); i++)
		{
			Entity entity1 = list.get(i);
			if(!(entity1 instanceof EntityItem))
				continue;
			EntityItem entityitem = (EntityItem)entity1;
			if(entityitem != null && entityitem.age < 50 && mod_mocreatures.huntersDestroyDrops)
				entityitem.setEntityDead();
		}
	}

	public void setEntityDead()
	{
		counterEntity--;
		super.setEntityDead();
	}

	public boolean getCanSpawnHere()
	{
		if(super.getCanSpawnHere())
		{
			if(counterEntity >= mod_mocreatures.maxBears)
				return false;
			counterEntity++;
			return true;
		} 
		return false;
	}

	public boolean getCanSpawnHere2()
	{
		return super.getCanSpawnHere();
	}
}
