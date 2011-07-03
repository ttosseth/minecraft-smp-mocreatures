package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.List;

public class EntityWolf extends EntityMobs
{
	public boolean wolfBoolean;
	public static int counterEntity;

	public EntityWolf(World world)
	{
		super(world);
		wolfBoolean = false;
		texture = "/mob/wolf.png";
		setSize(0.9F, 1.3F);
		attackStrength = 1;
		field_9135_bk = 0.8D;
	}

	public void onLivingUpdate()
	{
		if(worldObj.difficultySetting == 1)
			attackStrength = 3;
		else if(worldObj.difficultySetting > 1)
			attackStrength = 5;
		super.onLivingUpdate();
	}

	public int getMaxSpawnedInChunk()
	{
		return 6;
	}

	protected Entity findPlayerToAttack()
	{
		float f = getEntityBrightness(1.0F);
		if(f < 0.5F)
		{
			double d = 16.0D;
			return worldObj.getClosestPlayerToEntity(this, d);
		}
		if(rand.nextInt(80) == 0)
		{
			EntityLiving entityliving = getClosestTarget(this, 10.0D);
			return entityliving;
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
			if(!(entity1 instanceof EntityLiving) || entity1 == entity || entity1 == entity.riddenByEntity || entity1 == entity.ridingEntity || (entity1 instanceof EntityPlayer) || (entity1 instanceof EntityMobs) || (entity1 instanceof EntityLionK) || (entity1 instanceof EntityBear) || (entity1 instanceof EntityCow) || (entity1 instanceof EntityHorse) && !mod_mocreatures.huntersAttackHorses)
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

	protected void attackEntity(Entity entity, float f)
	{
		if((double)f < 2.5D && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
		{
			attackTime = 20;
			entity.attackEntityFrom(this, attackStrength);
			if(!(entity instanceof EntityPlayer))
				destroyDrops(this, 3.0D);
		}
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("WolfBoolean", wolfBoolean);
		nbttagcompound.setInteger("CounterEntity", counterEntity);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		wolfBoolean = nbttagcompound.getBoolean("WolfBoolean");
		counterEntity = nbttagcompound.getInteger("CounterEntity");
	}

	protected String getLivingSound()
	{
		return "wolfgrunt";
	}

	protected String getHurtSound()
	{
		return "wolfhurt";
	}

	protected String getDeathSound()
	{
		return "wolfdeath";
	}

	protected int getDropItemId()
	{
		return Item.leather.shiftedIndex;
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
		if(worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && super.getCanSpawnHere())
		{
			if(counterEntity >= mod_mocreatures.maxWolves)
				return false;
			counterEntity++;
			return true;
		}
		return false;
	}
}
