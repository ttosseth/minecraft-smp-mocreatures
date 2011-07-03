package moCreatures.entities;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.List;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

import moCreatures.mod_mocreatures;

public class EntityFox extends EntityAnimal
{

    public EntityFox(World world)
    {
        super(world);
        foxboolean = false;
        texture = "/moCreatures/textures/fox.png";
        setSize(0.9F, 1.3F);
        health = 15;
        force = 2;
        attackRange = 4D;
    }

    public void onLivingUpdate()
    {
        super.onLivingUpdate();
    }

    protected Entity findPlayerToAttack()
    {
        if(rand.nextInt(80) == 0 && worldObj.difficultySetting > 0)
        {
            EntityLiving entityliving = getClosestTarget(this, 8D);
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
            if(!(entity1 instanceof EntityLiving) || entity1 == entity || entity1 == entity.riddenByEntity || entity1 == entity.ridingEntity || (entity1 instanceof EntityPlayer) || (entity1 instanceof EntityMob) || height <= entity1.height || width <= entity1.width)
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

    public boolean attackEntityFrom(Entity entity, int i)
    {
        if(super.attackEntityFrom(entity, i))
        {
            if(riddenByEntity == entity || ridingEntity == entity)
            {
                return true;
            }
            if(entity != this && worldObj.difficultySetting > 0)
            {
                playerToAttack = entity;
            }
            return true;
        } else
        {
            return false;
        }
    }

    protected void attackEntity(Entity entity, float f)
    {
        if((double)f < 2.5D && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
        {
            attackTime = 20;
            entity.attackEntityFrom(this, force);
            if(!(entity instanceof EntityPlayer))
            {
                destroyDrops(this, 3D);
            }
        }
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("FoxBoolean", foxboolean);
        nbttagcompound.setInteger("CounterEntity", counterEntity);
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        foxboolean = nbttagcompound.getBoolean("FoxBoolean");
        counterEntity = nbttagcompound.getInteger("CounterEntity");
    }

    protected float getSoundVolume()
    {
        return 0.3F;
    }

    protected String getLivingSound()
    {
        return "foxcall";
    }

    protected String getHurtSound()
    {
        return "foxhurt";
    }

    protected String getDeathSound()
    {
        return "foxdying";
    }

    protected int getDropItemId()
    {
        return Item.leather.shiftedIndex;
    }

    public int getMaxSpawnedInChunk()
    {
        return 1;
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
        if(super.getCanSpawnHere())
        {
            if(counterEntity >= mod_mocreatures.maxFoxesS.get())
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

    protected double attackRange;
    public boolean foxboolean;
    protected int force;
    public static int counterEntity;
}
