package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.List;

public class EntityDolphin extends EntityCustomWM
{
	public int gestationTime;
	public boolean bred;
	public float _b;
	public boolean adult;
	public boolean tamed;
	public int typeInt;
	private double dolphinSpeed;
	private int maxHealth;
	private int temper;
	private boolean eaten;
	public static int counterEntity;
	public boolean typeChosen;
	public boolean hungry;

	public EntityDolphin(World world)
	{
		super(world);
		texture = "/mob/dolphin.png";
		setSize(1.5F, 0.8F);
		_b = 0.8F + rand.nextFloat();
		adult = false;
		tamed = false;
		dolphinSpeed = 1.3D;
		maxHealth = 30;
		health = 30;
		temper = 50;
	}

	public void setTame()
	{
		tamed = true;
	}

	public double speed()
	{
		return dolphinSpeed;
	}

	public int tameTemper()
	{
		return temper;
	}

	public boolean isTamed()
	{
		return tamed;
	}

	public void setType(int i)
	{
		typeInt = i;
		typeChosen = false;
		chooseType();
	}

	public void chooseType()
	{
		if(typeInt == 0)
		{
			int i = rand.nextInt(100);
			if(i <= 35)
				typeInt = 1;
			else if(i <= 60)
				typeInt = 2;
			else if(i <= 85)
				typeInt = 3;
			else if(i <= 96)
				typeInt = 4;
			else if(i <= 98)
				typeInt = 5;
			else
				typeInt = 6;
		}
		if(!typeChosen)
		{
			switch (typeInt)
			{
			case 1:
				texture = "/mob/dolphin.png";
				dolphinSpeed = 1.5D;
				temper = 50;
				break;
			case 2:
				texture = "/mob/dolphin2.png";
				dolphinSpeed = 2.5D;
				temper = 100;
				break;
			case 3:
				texture = "/mob/dolphin3.png";
				dolphinSpeed = 3.5D;
				temper = 150;
				break;
			case 4:
				texture = "/mob/dolphin4.png";
				dolphinSpeed = 4.5D;
				temper = 200;
				break;
			case 5:
				texture = "/mob/dolphin5.png";
				dolphinSpeed = 5.5D;
				temper = 250;
				break;
			case 6:
				texture = "/mob/dolphin6.png";
				dolphinSpeed = 6.5D;
				temper = 300;
				break;
			}
		}
		typeChosen = true;
	}

	public boolean interact(EntityPlayer entityplayer)
	{
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		if(itemstack != null && itemstack.itemID == Item.fishRaw.shiftedIndex)
		{
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			if((temper -= 25) < 1)
				temper = 1;
			if((health += 15) > maxHealth)
				health = maxHealth;
			worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			if(!adult)
				_b += 0.01F;
			return true;
		}
		if(itemstack != null && itemstack.itemID == Item.fishCooked.shiftedIndex && tamed && adult)
		{
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			if((health += 25) > maxHealth)
				health = maxHealth;
			eaten = true;
			worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			return true;
		}
		if(adult)
		{
			rotationYaw = entityplayer.rotationYaw;
			rotationPitch = entityplayer.rotationPitch;
			entityplayer.mountEntity(this);
			return true;
		}
		return false;
	}

	public void onLivingUpdate()
	{
		health = 0;
		setEntityDead();
		super.onLivingUpdate();
		if(!adult && rand.nextInt(50) == 0)
		{
			_b += 0.01F;
			if(_b >= 1.5F)
				adult = true;
		}
		if(!hungry && rand.nextInt(100) == 0)
			hungry = true;
		if(deathTime == 0 && !tamed || hungry)
		{
			EntityItem entityitem = getClosestFish(this, 12.0D);
			if(entityitem != null)
			{
				moveToNextEntity(entityitem);
				EntityItem entityitem1 = getClosestFish(this, 2.0D);
				if(rand.nextInt(20) == 0 && entityitem1 != null && deathTime == 0)
				{
					entityitem1.setEntityDead();
					if((temper -= 25) < 1)
						temper = 1;
					health = maxHealth;
				}
			}
		}
		if(!readyforParenting(this))
			return;
		int i = 0;
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(8.0D, 2.0D, 8.0D));
		for(int j = 0; j < list.size(); j++)
		{
			Entity entity = list.get(j);
			if(entity instanceof EntityDolphin)
				i++;
		}

		if(i > 1)
			return;
		List<Entity> list1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(4.0D, 2.0D, 4.0D));
		for(int k = 0; k < list.size(); k++)
		{
			Entity entity1 = list1.get(k);
			if(!(entity1 instanceof EntityDolphin) || entity1 == this)
				continue;
			EntityDolphin entitydolphin = (EntityDolphin)entity1;
			if(!readyforParenting(this) || !readyforParenting(entitydolphin))
				continue;
			if(rand.nextInt(100) == 0)
				gestationTime++;
			if(gestationTime <= 50)
				continue;
			EntityDolphin entitydolphin1 = new EntityDolphin(worldObj);
			entitydolphin1.setPosition(posX, posY, posZ);
			worldObj.entityJoinedWorld(entitydolphin1);
			worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
			eaten = false;
			entitydolphin.eaten = false;
			gestationTime = 0;
			entitydolphin.gestationTime = 0;
			int l = genetics(this, entitydolphin);
			entitydolphin1.bred = true;
			entitydolphin1._b = 0.35F;
			entitydolphin1.adult = false;
			entitydolphin1.setType(l);
			break;
		}

	}

	public boolean readyforParenting(EntityDolphin entitydolphin)
	{
		return entitydolphin.riddenByEntity == null && entitydolphin.ridingEntity == null && entitydolphin.tamed && entitydolphin.eaten && entitydolphin.adult;
	}

	private int genetics(EntityDolphin entitydolphin, EntityDolphin entitydolphin1)
	{
		if(entitydolphin.typeInt == entitydolphin1.typeInt)
			return entitydolphin.typeInt;
		int i = entitydolphin.typeInt + entitydolphin1.typeInt;
		boolean flag = rand.nextInt(3) == 0;
		if(i < 5 && flag)
			return i;
		boolean flag1 = rand.nextInt(10) == 0;
		if((i == 5 || i == 6) && flag1)
			return i;
		return 0;
	}

	private boolean moveToNextEntity(Entity entity)
	{
		if(entity != null)
		{
			int i = MathHelper.floor_double(entity.posX);
			int j = MathHelper.floor_double(entity.posY);
			int k = MathHelper.floor_double(entity.posZ);
			faceItem(i, j, k, 30.0F);
			if(posX < i)
			{
				double d = entity.posX - posX;
				if(d > 0.5D)
					motionX += 0.05D;
			} else
			{
				double d1 = posX - entity.posX;
				if(d1 > 0.5D)
					motionX -= 0.05D;
			}
			if(posZ < k)
			{
				double d2 = entity.posZ - posZ;
				if(d2 > 0.5D)
					motionZ += 0.05D;
			} else
			{
				double d3 = posZ - entity.posZ;
				if(d3 > 0.5D)
					motionZ -= 0.05D;
			}
			return true;
		}
		return false;
	}

	public void faceItem(int i, int j, int k, float f)
	{
		double d = i - posX;
		double d1 = k - posZ;
		double d2 = j - posY;
		double d3 = MathHelper.sqrt_double(d * d + d1 * d1);
		float f1 = (float)((Math.atan2(d1, d) * 180.0D) / 3.141592741012573D) - 90.0F;
		float f2 = (float)((Math.atan2(d2, d3) * 180.0D) / 3.141592741012573D);
		rotationPitch = -updateRotation(rotationPitch, f2, f);
		rotationYaw = updateRotation(rotationYaw, f1, f);
	}

	private float updateRotation(float f, float f1, float f2)
	{
		float f3 = f1 - f;
		while (f3 < -180.0F)
			f3 += 360.0F;
		while (f3 >= 180.0F)
			f3 -= 360.0F;
		if(f3 > f2)
			f3 = f2;
		if(f3 < -f2)
			f3 = -f2;
		return f + f3;
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Tamed", tamed);
		nbttagcompound.setInteger("TypeInt", typeInt);
		nbttagcompound.setBoolean("Adult", adult);
		nbttagcompound.setBoolean("Bred", bred);
		nbttagcompound.setFloat("Age", _b);
		nbttagcompound.setInteger("counterEntity", counterEntity);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		tamed = nbttagcompound.getBoolean("Tamed");
		typeInt = nbttagcompound.getInteger("TypeInt");
		adult = nbttagcompound.getBoolean("Adult");
		bred = nbttagcompound.getBoolean("Bred");
		_b = nbttagcompound.getFloat("Age");
		counterEntity = nbttagcompound.getInteger("counterEntity");
	}

	public EntityLiving getClosestTarget(Entity entity, double d)
	{
		double d1 = -1.0D;
		EntityLiving entityliving = null;
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, d, d));
		for(int i = 0; i < list.size(); i++)
		{
			Entity entity1 = list.get(i);
			if(!(entity1 instanceof EntityShark))
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

	protected Entity findPlayerToAttack()
	{
		if(worldObj.difficultySetting > 0 && _b >= 1.0F && mod_mocreatures.dolphinsAttackSharks && rand.nextInt(50) == 0)
		{
			EntityLiving entityliving = getClosestTarget(this, 12.0D);
			if(entityliving != null && entityliving.inWater)
				return entityliving;
		}
		return null;
	}

	public boolean attackEntityFrom(Entity entity, int i)
	{
		if(super.attackEntityFrom(entity, i) && worldObj.difficultySetting > 0)
		{
			if(riddenByEntity == entity || ridingEntity == entity)
				return true;
			if(entity != this)
				playerToAttack = entity;
			return true;
		} 
		return false;
	}

	protected void attackEntity(Entity entity, float f)
	{
		if(f < 3.5D && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY && _b >= 1.0F)
		{
			attackTime = 20;
			entity.attackEntityFrom(this, 5);
		}
	}

	protected int getDropItemId()
	{
		return Item.fishRaw.shiftedIndex;
	}

	public boolean getCanSpawnHere()
	{
		if(super.getCanSpawnHere())
		{
			if(counterEntity >= mod_mocreatures.maxDolphins)
				return false;
			counterEntity++;
			return true;
		} 
		return false;
	}

	public void setEntityDead()
	{
		if((tamed || bred) && health > 0)
			return;
		counterEntity--;
		super.setEntityDead();
	}

	protected String getLivingSound()
	{
		return "dolphin";
	}

	protected String getHurtSound()
	{
		return "dolphinhurt";
	}

	protected String getDeathSound()
	{
		return "dolphindying";
	}

	protected float getSoundVolume()
	{
		return 0.4F;
	}

	protected String getUpsetSound()
	{
		return "dolphinupset";
	}
}
