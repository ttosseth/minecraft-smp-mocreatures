package moCreatures.entities;

import moCreatures.mod_MoCreatures;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class EntitySharkEgg extends EntityLiving
{
	private int tCounter;
	private int lCounter;

	public EntitySharkEgg(World world)
	{
		super(world);
		setSize(0.25F, 0.25F);
		tCounter = 0;
		lCounter = 0;
		texture = "/mob/sharkeggt.png";
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
	}

	protected void entityInit()
	{
	}

	public void onLivingUpdate()
	{
		health = 0;
		setEntityDead();
		moveStrafing = 0.0F;
		moveForward = 0.0F;
		randomYawVelocity = 0.0F;
		moveEntityWithHeading(moveStrafing, moveForward);
	}

	public void onCollideWithPlayer(EntityPlayer entityplayer)
	{
		if(worldObj.singleplayerWorld)
			return;
		if(lCounter > 10 && entityplayer.inventory.addItemStackToInventory(new ItemStack(mod_MoCreatures.sharkEgg, 1)))
		{
			worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			entityplayer.onItemPickup(this, 1);
			setEntityDead();
		}
	}

	public void onUpdate()
	{
		super.onUpdate();
		if(rand.nextInt(20) == 0)
			lCounter++;
		if(inWater && rand.nextInt(20) == 0)
		{
			tCounter++;
			if(tCounter >= 50)
			{
				EntityShark entityshark = new EntityShark(worldObj);
				entityshark._b = 0.3F;
				entityshark.tamed = true;
				entityshark.setPosition(posX, posY, posZ);
				worldObj.entityJoinedWorld(entityshark);
				worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
				setEntityDead();
			}
		}
	}

	public boolean canBreatheUnderwater()
	{
		return true;
	}

	public boolean handleWaterMovement()
	{
		return worldObj.handleMaterialAcceleration(boundingBox, Material.water, this);
	}

	protected String getLivingSound()
	{
		return null;
	}

	protected String getHurtSound()
	{
		return null;
	}

	protected String getDeathSound()
	{
		return null;
	}

	protected float getSoundVolume()
	{
		return 0.4F;
	}
}
