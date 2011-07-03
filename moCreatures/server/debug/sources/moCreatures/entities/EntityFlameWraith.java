package moCreatures.entities;

import moCreatures.mod_MoCreatures;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class EntityFlameWraith extends EntityWraith
{
	protected int burningTime;
	public static int counterEntity;

	public EntityFlameWraith(World world)
	{
		super(world);
		texture = "/mob/flamewraith.png";
		setSize(1.5F, 1.5F);
		isImmuneToFire = true;
		burningTime = 30;
		health = 15;
		moveSpeed = 1.1F;
	}

	protected int getDropItemId()
	{
		return Item.redstone.shiftedIndex;
	}

	public void onLivingUpdate()
	{
		if(rand.nextInt(40) == 0)
			fire = 2;
		if(worldObj.isDaytime())
		{
			float f = getEntityBrightness(1.0F);
			if(f > 0.5F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F)
				health -= 2;
		}
		super.onLivingUpdate();
	}

	protected void attackEntity(Entity entity, float f)
	{
		if((double)f < 2.5D && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
		{
			attackTime = 20;
			entity.attackEntityFrom(this, 2);
			entity.fire = burningTime;
		}
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
		if(worldObj.difficultySetting >= mod_MoCreatures.fireWraithsSpawnDifficulty && rand.nextInt(2) == 0 && super.getCanSpawnHere2())
		{
			if(counterEntity >= mod_MoCreatures.maxFireWraiths)
				return false;
			counterEntity++;
			return true;
		}
		return false;
	}
}
