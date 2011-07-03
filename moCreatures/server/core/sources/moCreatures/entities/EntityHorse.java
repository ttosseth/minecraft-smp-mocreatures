package moCreatures.entities;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.List;

import moCreatures.mod_MoCreatures;
import moCreatures.helpers.AnimalChest;
import moCreatures.proxies.EntityPlayerProxy;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.StepSound;
import net.minecraft.src.World;

public class EntityHorse extends EntityAnimal
{
	private int nightmareInt;
	private boolean bred;
	public boolean eatenPumpkin;
	private int gestationTime;
	public boolean hasReproduced;
	private int maxHealth;
	private int temper;
	private double horseSpeed;
	private double horseJump;
	public int typeInt;
	public boolean typeChosen;
	public boolean tamed;
	public boolean horseBoolean;
	public boolean rideable;
	public boolean isJumping;
	public float wingB;
	public float wingC;
	public float wingD;
	public float wingE;
	public float wingH;
	public boolean chestedHorse;
	private IInventory localHorseChest;
	public ItemStack[] localStack;
	private boolean eatingHayStack;
	public float _b;
	public boolean adult;
	public static int counterEntity;

	public EntityHorse(World world)
	{
		super(world);
		horseBoolean = false;
		setSize(1.4F, 1.6F);
		health = 20;
		rideable = false;
		isJumping = false;
		tamed = false;
		texture = "/mob/horseb.png";
		typeInt = 0;
		horseSpeed = 0.8D;
		horseJump = 0.4D;
		temper = 100;
		typeChosen = false;
		wingB = 0.0F;
		wingC = 0.0F;
		wingH = 1.0F;
		localStack = new ItemStack[27];
		maxHealth = 20;
		hasReproduced = false;
		gestationTime = 0;
		eatenPumpkin = false;
		bred = false;
		nightmareInt = 0;
		isImmuneToFire = false;
		adult = true;
		_b = 0.35F;
	}

	public void setEntityDead()
	{
		if((tamed || bred) && health > 0)
			return;
		counterEntity--;
		super.setEntityDead();
	}

	public void setType(int i)
	{
		typeInt = i;
		typeChosen = false;
		chooseType();
	}

	public void chooseType()
	{
		int chance = mod_MoCreatures.pegasusChance;
		if(typeInt == 0)
		{
			if(rand.nextInt(5) == 0)
				adult = false;
			int j = rand.nextInt(100);
			if(j <= 51 - chance)
				typeInt = 1;
			else if(j <= 86 - chance)
				typeInt = 2;
			else if(j <= 95 - chance)
				typeInt = 3;
			else if(j <= 99 - chance)
				typeInt = 4;
			else
				typeInt = 5;
		}
		if(!typeChosen)
		{
			switch (typeInt)
			{
			case 1:
				horseSpeed = 0.9D;
				texture = "/mob/horseb.png";
				maxHealth = 25;
				break;
			case 2:
				horseSpeed = 1.0D;
				temper = 200;
				horseJump = 0.5D;
				texture = "/mob/horsebrownb.png";
				maxHealth = 30;
				break;
			case 3:
				horseSpeed = 1.1D;
				temper = 300;
				horseJump = 0.6D;
				texture = "/mob/horseblackb.png";
				maxHealth = 35;
				break;
			case 4:
				horseSpeed = 1.3D;
				horseJump = 0.6D;
				temper = 400;
				texture = "/mob/horsegoldb.png";
				maxHealth = 40;
				break;
			case 5:
				horseSpeed = 1.2D;
				temper = 500;
				texture = "/mob/horsewhiteb.png";
				maxHealth = 40;
				break;
			case 6:
				horseSpeed = 0.9D;
				temper = 600;
				texture = "/mob/horsepackb.png";
				maxHealth = 40;
				break;
			case 7:
				horseSpeed = 1.3D;
				temper = 700;
				horseJump = 0.6D;
				texture = "/mob/horsenightb.png";
				maxHealth = 50;
				isImmuneToFire = true;
				break;
			case 8:
				horseSpeed = 1.3D;
				temper = 800;
				texture = "/mob/horsebpb.png";
				maxHealth = 50;
				isImmuneToFire = true;
				break;
			}
		}
		typeChosen = true;
	}

	@SuppressWarnings("unchecked")
	public void onLivingUpdate()
	{
		if(rand.nextInt(300) == 0 && health <= maxHealth && deathTime < 0)
			health += 5;
		wingE = wingB;
		wingD = wingC;
		wingC = (float)(wingC + (onGround ? -1 : 4) * 0.3D);
		if(wingC < 0.0F)
			wingC = 0.0F;
		if(wingC > 1.0F)
			wingC = 1.0F;
		if(!onGround && wingH < 1.0F)
			wingH = 0.3F;
		wingH = (float)(wingH * 0.9D);
		if(!onGround && motionY < 0.0D && (typeInt == 5 || typeInt == 8))
			motionY *= 0.6D;
		wingB += wingH * 2.0F;
		super.onLivingUpdate();
		if(typeInt == 7 && riddenByEntity != null && nightmareInt > 0 && rand.nextInt(2) == 0)
		{
			nightmareEffect();
		}
		if(!adult && rand.nextInt(200) == 0)
		{
			_b += 0.01F;
			if(_b >= 1.0F)
				adult = true;
		}
		if(!readyforParenting(this))
			return;
		int i = 0;
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(8.0D, 3.0D, 8.0D));
		for(int j = 0; j < list.size(); j++)
		{
			Entity entity = list.get(j);
			if(entity instanceof EntityHorse)
				i++;
		}

		if(i > 1)
			return;
		List<Entity> list1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(4.0D, 2.0D, 4.0D));
		for(int k = 0; k < list.size(); k++)
		{
			Entity entity1 = list1.get(k);
			if(!(entity1 instanceof EntityHorse) || entity1 == this)
				continue;
			EntityHorse entityhorse = (EntityHorse)entity1;
			if(!readyforParenting(this) || !readyforParenting(entityhorse))
				continue;
			if(rand.nextInt(100) == 0)
				gestationTime++;
			if(gestationTime <= 50)
				continue;
			EntityHorse entityhorse1 = new EntityHorse(worldObj);
			entityhorse1.setPosition(posX, posY, posZ);
			worldObj.entityJoinedWorld(entityhorse1);
			worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
			if(!mod_MoCreatures.easyBreeding)
				hasReproduced = true;
			eatenPumpkin = false;
			entityhorse.eatenPumpkin = false;
			gestationTime = 0;
			entityhorse.gestationTime = 0;
			int l = horseGenetics(this, entityhorse);
			entityhorse1.bred = true;
			entityhorse1.adult = false;
			entityhorse1.setType(l);
			break;
		}

	}

	protected void updatePlayerActionState()
	{
		if(!field_9112_aN && riddenByEntity == null && !eatingHayStack)
			super.updatePlayerActionState();
	}

	private void nightmareEffect()
	{
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(boundingBox.minY);
		int k = MathHelper.floor_double(posZ);
		worldObj.setBlockWithNotify(i - 1, j, k - 1, Block.fire.blockID);
		nightmareInt--;
	}

	public boolean readyforParenting(EntityHorse entityhorse)
	{
		return entityhorse.riddenByEntity == null && entityhorse.ridingEntity == null && entityhorse.tamed && entityhorse.eatenPumpkin && !entityhorse.hasReproduced && entityhorse.adult;
	}

	private int horseGenetics(EntityHorse entityhorse, EntityHorse entityhorse1)
	{
		int geneticvalue = entityhorse.typeInt + entityhorse1.typeInt;
		boolean flag = mod_MoCreatures.easyBreeding;
		boolean flag1 = rand.nextInt(3) == 0;
		if(geneticvalue == 7 && (flag || flag1))
			return 6;
		if(geneticvalue == 9 && (flag || flag1))
			return 7;
		if(geneticvalue == 10 && (flag || flag1))
			return 5;
		if(geneticvalue == 12 && entityhorse.typeInt != 6 && (flag || flag1))
			return 8;
		return 0;
	}

	public boolean attackEntityFrom(Entity entity, int i)
	{
		if(riddenByEntity != null && entity == riddenByEntity)
			return false;
		return super.attackEntityFrom(entity, i);
	}

	public void moveEntityWithHeading(float f, float f1)
	{
		if(handleWaterMovement())
		{
			if(riddenByEntity != null)
			{
				motionX += riddenByEntity.motionX * horseSpeed / 2.0D;
				motionZ += riddenByEntity.motionZ * horseSpeed / 2.0D;
				EntityPlayer entityplayer = (EntityPlayer)riddenByEntity;
				if(((EntityPlayerProxy)entityplayer).isJumping() && !isJumping)
				{
					motionY += 0.5D;
					isJumping = true;
				}
				moveEntity(motionX, motionY, motionZ);
				if(onGround)
					isJumping = false;
				rotationPitch = riddenByEntity.rotationPitch * 0.5F;
				if(rand.nextInt(20) == 0)
					rotationYaw = riddenByEntity.rotationYaw;
				setRotation(rotationYaw, rotationPitch);
				if(!tamed)
					riddenByEntity = null;
			}
			double d = posY;
			moveFlying(f, f1, 0.02F);
			moveEntity(motionX, motionY, motionZ);
			motionX *= 0.8D;
			motionY *= 0.8D;
			motionZ *= 0.8D;
			motionY -= 0.02D;
			if(isCollidedHorizontally && isOffsetPositionInLiquid(motionX, motionY + 0.6D - posY + d, motionZ))
				motionY = 0.3D;
		} else if(handleLavaMovement())
		{
			if(riddenByEntity != null)
			{
				motionX += riddenByEntity.motionX * horseSpeed / 2.0D;
				motionZ += riddenByEntity.motionZ * horseSpeed / 2.0D;
				EntityPlayer entityplayer1 = (EntityPlayer)riddenByEntity;
				if(((EntityPlayerProxy)entityplayer1).isJumping() && !isJumping)
				{
					motionY += 0.5D;
					isJumping = true;
				}
				moveEntity(motionX, motionY, motionZ);
				if(onGround)
					isJumping = false;
				rotationPitch = riddenByEntity.rotationPitch * 0.5F;
				if(rand.nextInt(20) == 0)
					rotationYaw = riddenByEntity.rotationYaw;
				setRotation(rotationYaw, rotationPitch);
				if(!tamed)
					riddenByEntity = null;
			}
			double d1 = posY;
			moveFlying(f, f1, 0.02F);
			moveEntity(motionX, motionY, motionZ);
			motionX *= 0.5D;
			motionY *= 0.5D;
			motionZ *= 0.5D;
			motionY -= 0.02D;
			if(isCollidedHorizontally && isOffsetPositionInLiquid(motionX, motionY + 0.6D - posY + d1, motionZ))
				motionY = 0.3D;
		} else
		{
			float f2 = 0.91F;
			if(onGround)
			{
				f2 = 0.55F;
				int i = worldObj.getBlockId(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ));
				if(i > 0)
					f2 = Block.blocksList[i].slipperiness * 0.91F;
			}
			float f3 = 0.16F / (f2 * f2 * f2);
			moveFlying(f, f1, onGround ? 0.1F * f3 : 0.02F);
			f2 = 0.91F;
			if(onGround)
			{
				f2 = 0.55F;
				int j = worldObj.getBlockId(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ));
				if(j > 0)
					f2 = Block.blocksList[j].slipperiness * 0.91F;
			}
			if(isOnLadder())
			{
				fallDistance = 0.0F;
				if(motionY < -0.15D)
					motionY = -0.15D;
			}
			if(riddenByEntity != null && !tamed)
			{
				if(rand.nextInt(5) == 0 && !isJumping)
				{
					motionY += 0.4D;
					isJumping = true;
				}
				if(rand.nextInt(10) == 0)
				{
					motionX += rand.nextDouble() / 30.0D;
					motionZ += rand.nextDouble() / 10.0D;
				}
				moveEntity(motionX, motionY, motionZ);
				if(rand.nextInt(50) == 0)
				{
					worldObj.playSoundAtEntity(this, "horsemad", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
					riddenByEntity.motionY += 0.9D;
					riddenByEntity.motionZ -= 0.3D;
					riddenByEntity = null;
				}
				if(onGround)
					isJumping = false;
				if(rand.nextInt(temper * 8) == 0)
					tamed = true;
			}
			if(riddenByEntity != null && tamed)
			{
				boundingBox.maxY = riddenByEntity.boundingBox.maxY;
				motionX += riddenByEntity.motionX * horseSpeed;
				motionZ += riddenByEntity.motionZ * horseSpeed;
				EntityPlayer entityplayer2 = (EntityPlayer)riddenByEntity;
				if(((EntityPlayerProxy)entityplayer2).isJumping() && !isJumping)
				{
					motionY += horseJump;
					isJumping = true;
				}
				if(((EntityPlayerProxy)entityplayer2).isJumping() && (typeInt == 5 || typeInt == 8))
					motionY += 0.1D;
				moveEntity(motionX, motionY, motionZ);
				if(onGround)
				{
					isJumping = false;
				}
				rotationPitch = riddenByEntity.rotationPitch * 0.5F;
				if(rotationYaw > riddenByEntity.rotationYaw)
				{
					float f5 = rotationYaw - riddenByEntity.rotationYaw;
					if(f5 > 25.0F)
						rotationYaw -= f5 / 10.0F;
				} else
				{
					float f6 = riddenByEntity.rotationYaw - rotationYaw;
					if(f6 > 25.0F)
						rotationYaw += f6 / 10.0F;
				}
				setRotation(rotationYaw, rotationPitch);
			}
			moveEntity(motionX, motionY, motionZ);
			if(isCollidedHorizontally && isOnLadder())
			{
				motionY = 0.2D;
			}
			if((typeInt == 5 || typeInt == 8) && riddenByEntity != null && tamed)
			{
				motionY -= 0.08D;
				motionY *= 0.6D;
			} else
			{
				motionY -= 0.08D;
				motionY *= 0.98D;
			}
			motionX *= f2;
			motionZ *= f2;
		}
		field_9142_bc = field_9141_bd;
		double d2 = posX - prevPosX;
		double d3 = posZ - prevPosZ;
		float f7 = MathHelper.sqrt_double(d2 * d2 + d3 * d3) * 4.0F;
		if(f7 > 1.0F)
			f7 = 1.0F;
		field_9141_bd += (f7 - field_9141_bd) * 0.4F;
		field_386_ba += field_9141_bd;
	}

	protected void fall(float f)
	{
		int i = (int)Math.ceil(f - 3.0F);
		if(i > 0 && typeInt != 5 && typeInt != 8)
		{
			if(typeInt >= 3)
				i /= 3;
			if(i > 0)
				attackEntityFrom(this, i);
			if(riddenByEntity != null && i > 0)
				riddenByEntity.attackEntityFrom(this, i);
			if(typeInt == 5 || typeInt == 8)
				return;
			int j = worldObj.getBlockId(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.2D - (double)prevRotationPitch), MathHelper.floor_double(posZ));
			if(j > 0)
			{
				StepSound stepsound = Block.blocksList[j].stepSound;
				worldObj.playSoundAtEntity(this, stepsound.func_737_c(), stepsound.func_738_a() * 0.5F, stepsound.func_739_b() * 0.75F);
			}
		}
	}

	protected float getSoundVolume()
	{
		return 0.4F;
	}

	public boolean interact(EntityPlayer entityplayer)
	{
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		if(itemstack != null && itemstack.itemID == Item.wheat.shiftedIndex)
		{
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			if((temper -= 25) < 25)
				temper = 25;
			if((health += 5) > maxHealth)
				health = maxHealth;
			worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			if(!adult)
				_b += 0.01F;
			return true;
		}
		if(itemstack != null && itemstack.itemID == mod_MoCreatures.sugarLump.shiftedIndex)
		{
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			if((temper -= 50) < 25)
				temper = 25;
			if((health += 10) > maxHealth)
				health = maxHealth;
			worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			if(!adult)
				_b += 0.02F;
			return true;
		}
		if(itemstack != null && itemstack.itemID == Item.bread.shiftedIndex)
		{
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			if((temper -= 100) < 25)
				temper = 25;
			if((health += 20) > maxHealth)
				health = maxHealth;
			worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			if(!adult)
				_b += 0.03F;
			return true;
		}
		if(itemstack != null && itemstack.itemID == Item.appleRed.shiftedIndex)
		{
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			tamed = true;
			health = maxHealth;
			worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			if(!adult)
				_b += 0.05F;
			return true;
		}
		if(itemstack != null && tamed && itemstack.itemID == Block.crate.blockID && (typeInt == 6 || typeInt == 8))
		{
			if(chestedHorse)
				return false;
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			chestedHorse = true;
			worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
			return true;
		}
		if(itemstack != null && tamed && itemstack.itemID == mod_MoCreatures.hayStack.shiftedIndex)
		{
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			eatingHayStack = true;
			worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			health = maxHealth;
			return true;
		}
		if(itemstack != null && (itemstack.itemID == Item.shovelStone.shiftedIndex || itemstack.itemID == Block.torchWood.blockID) && chestedHorse)
		{
			localHorseChest = new AnimalChest(localStack, "HorseChest");
			entityplayer.displayGUIChest(localHorseChest);
			return true;
		}
		if(itemstack != null && (itemstack.itemID == Block.pumpkin.blockID || itemstack.itemID == Item.bowlSoup.shiftedIndex))
		{
			if(hasReproduced || !adult)
				return false;
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			eatenPumpkin = true;
			health = maxHealth;
			worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			return true;
		}
		if(itemstack != null && tamed && itemstack.itemID == Item.redstone.shiftedIndex && typeInt == 7)
		{
			if(nightmareInt > 500)
				return false;
			if(--itemstack.stackSize == 0)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			nightmareInt = 500;
			worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
			return true;
		}
		if(rideable && adult)
		{
			rotationYaw = entityplayer.rotationYaw;
			rotationPitch = entityplayer.rotationPitch;
			eatingHayStack = false;
			entityplayer.mountEntity(this);
			gestationTime = 0;
			return true;
		}
		return false;
	}

	public void onDeath(Entity entity)
	{
		if(scoreValue > 0 && entity != null)
			entity.addToPlayerScore(this, scoreValue);
        unused_flag = true;
		if(!worldObj.singleplayerWorld)
			dropFewItems();
		//worldObj.getAnaglyphs(this, 3.0F);
		if(chestedHorse && (typeInt == 6 || typeInt == 8))
		{
			int i = MathHelper.floor_double(posX);
			int j = MathHelper.floor_double(boundingBox.minY);
			int k = MathHelper.floor_double(posZ);
			horseRemoval(worldObj, i, j, k);
		}
	}

	public void horseRemoval(World world, int i, int j, int k)
	{
		if(localStack == null)
			return;
		localHorseChest = new AnimalChest(localStack, "HorseChest");
		for(int l = 0; l < localHorseChest.getSizeInventory(); l++)
		{
			ItemStack itemstack = localHorseChest.getStackInSlot(l);
			if(itemstack == null)
				continue;
			float f = rand.nextFloat() * 0.8F + 0.1F;
			float f1 = rand.nextFloat() * 0.8F + 0.1F;
			float f2 = rand.nextFloat() * 0.8F + 0.1F;
			while (itemstack.stackSize > 0)
			{
				int i1 = rand.nextInt(21) + 10;
				if(i1 > itemstack.stackSize)
					i1 = itemstack.stackSize;
				itemstack.stackSize -= i1;
				EntityItem entityitem = new EntityItem(worldObj, i + f, j + f1, k + f2, new ItemStack(itemstack.itemID, i1, itemstack.getItemDamage()));
				float f3 = 0.05F;
				entityitem.motionX = (float)rand.nextGaussian() * f3;
				entityitem.motionY = (float)rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float)rand.nextGaussian() * f3;
				worldObj.entityJoinedWorld(entityitem);
			}
		}

	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Saddle", rideable);
		nbttagcompound.setBoolean("Tamed", tamed);
		nbttagcompound.setBoolean("HorseBoolean", horseBoolean);
		nbttagcompound.setInteger("TypeInt", typeInt);
		nbttagcompound.setBoolean("ChestedHorse", chestedHorse);
		nbttagcompound.setBoolean("HasReproduced", hasReproduced);
		nbttagcompound.setBoolean("Bred", bred);
		nbttagcompound.setBoolean("Adult", adult);
		nbttagcompound.setFloat("Age", _b);
		nbttagcompound.setInteger("CounterEntity", counterEntity);
		if(typeInt == 6 || typeInt == 8)
		{
			NBTTagList nbttaglist = new NBTTagList();
			for(int i = 0; i < localStack.length; i++)
			{
				if(localStack[i] != null)
				{
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Slot", (byte)i);
					localStack[i].writeToNBT(nbttagcompound1);
					nbttaglist.setTag(nbttagcompound1);
				}
			}

			nbttagcompound.setTag("Items", nbttaglist);
		}
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		rideable = nbttagcompound.getBoolean("Saddle");
		tamed = nbttagcompound.getBoolean("Tamed");
		bred = nbttagcompound.getBoolean("Bred");
		adult = nbttagcompound.getBoolean("Adult");
		horseBoolean = nbttagcompound.getBoolean("HorseBoolean");
		chestedHorse = nbttagcompound.getBoolean("ChestedHorse");
		hasReproduced = nbttagcompound.getBoolean("HasReproduced");
		typeInt = nbttagcompound.getInteger("TypeInt");
		_b = nbttagcompound.getFloat("Age");
		counterEntity = nbttagcompound.getInteger("CounterEntity");
		if(typeInt == 6 || typeInt == 8)
		{
			NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
			localStack = new ItemStack[27];
			for(int i = 0; i < nbttaglist.tagCount(); i++)
			{
				NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 0xff;
				if(j >= 0 && j < localStack.length)
					localStack[j] = new ItemStack(nbttagcompound1);
			}

		}
	}

	protected String getLivingSound()
	{
		return "horsegrunt";
	}

	protected String getHurtSound()
	{
		return "horsehurt";
	}

	protected String getDeathSound()
	{
		return "horsedying";
	}

	protected int getDropItemId()
	{
		return Item.leather.shiftedIndex;
	}

	public int getMaxSpawnedInChunk()
	{
		return 6;
	}

	public boolean getCanSpawnHere()
	{
		if(super.getCanSpawnHere())
		{
			if(counterEntity >= mod_MoCreatures.maxHorses)
				return false;
			counterEntity++;
			return true;
		}
		return false;
	}
}
