package moCreatures.entities;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityCow;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

import moCreatures.mod_mocreatures;

public class EntityWildWolf extends EntityMob
{

    public EntityWildWolf(World world)
    {
        super(world);
        wildwolfboolean = false;
        texture = "/moCreatures/textures/wildwolf.png";
        setSize(0.9F, 1.3F);
        attackStrength = 1;
        newRotationPitch = 0.80000001192092896D;
    }

    public void onLivingUpdate()
    {
        if(worldObj.difficultySetting == 1)
        {
            attackStrength = 3;
        } else
        if(worldObj.difficultySetting > 1)
        {
            attackStrength = 5;
        }
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
            double d = 16D;
            return worldObj.getClosestPlayerToEntity(this, d);
        }
        if(rand.nextInt(80) == 0)
        {
            EntityLiving entityliving = getClosestTarget(this, 10D);
            return entityliving;
        } else
        {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
	public EntityLiving getClosestTarget(Entity entity, double d)
    {
        double d1 = -1D;
        EntityLiving entityliving = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, d, d));
        for(int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity)list.get(i);
            if(!(entity1 instanceof EntityLiving) || entity1 == entity || entity1 == entity.riddenByEntity || entity1 == entity.ridingEntity || (entity1 instanceof EntityPlayer) || (entity1 instanceof EntityMob) || (entity1 instanceof EntityLionK) || (entity1 instanceof EntityBear) || (entity1 instanceof EntityCow) || (entity1 instanceof EntityHorse) && !mod_mocreatures.attackhorses.get())
            {
                continue;
            }
            double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if((d < 0.0D || d2 < d * d) && (d1 == -1D || d2 < d1) && ((EntityLiving)entity1).canEntityBeSeen(entity))
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
            {
                destroyDrops(this, 3D);
            }
        }
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("WildWolfBoolean", wildwolfboolean);
        nbttagcompound.setInteger("CounterEntity", counterEntity);
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        wildwolfboolean = nbttagcompound.getBoolean("WildWolfBoolean");
        counterEntity = nbttagcompound.getInteger("CounterEntity");
    }

    protected String getLivingSound()
    {
        return "wildwolfgrunt";
    }

    protected String getHurtSound()
    {
        return "wildwolfhurt";
    }

    protected String getDeathSound()
    {
        return "wildwolfdeath";
    }

    protected int getDropItemId()
    {
        return Item.leather.shiftedIndex;
    }

    @SuppressWarnings("rawtypes")
	public void destroyDrops(Entity entity, double d)
    {
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(d, d, d));
        for(int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity)list.get(i);
            if(!(entity1 instanceof EntityItem))
            {
                continue;
            }
            EntityItem entityitem = (EntityItem)entity1;
            if(entityitem != null && entityitem.age < 50 && mod_mocreatures.destroyitems.get())
            {
                entityitem.setEntityDead();
            }
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
            if(counterEntity >= mod_mocreatures.maxWildWolfS.get())
            {
                return false;
            } else
            {
                counterEntity++;
                return true;
            }
        } else
        {
            return false;
        }
    }

    public boolean wildwolfboolean;
    public static int counterEntity;
}
