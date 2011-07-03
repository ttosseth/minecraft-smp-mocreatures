package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.List;
import java.util.Random;

public class EntityBunny extends EntityAnimal
{

    public EntityBunny(World world)
    {
        super(world);
        a = false;
        b = 0.0F;
        c = 0.0F;
        f = 1.0F;
        moveSpeed = 1.5F;
        texture = "/mob/bunny.png";
        yOffset = -0.16F;
        setSize(0.4F, 0.4F);
        health = 4;
        j = rand.nextInt(64);
        i = 0;
    }

    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        e = b;
        d = c;
        c = (float)((double)c + (double)(onGround ? -1 : 4) * 0.29999999999999999D);
        if(c < 0.0F)
        {
            c = 0.0F;
        }
        if(c > 1.0F)
        {
            c = 1.0F;
        }
        if(!onGround && f < 1.0F)
        {
            f = 1.0F;
        }
        f = (float)((double)f * 0.90000000000000002D);
        b += f * 2.0F;
    }

    public void onUpdate()
    {
        if(j < 1023)
        {
            j++;
        } else
        if(i < 127)
        {
            i++;
        } else
        {
            int k = 0;
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 16D, 16D));
            for(int l = 0; l < list.size(); l++)
            {
                Entity entity = (Entity)list.get(l);
                if(entity instanceof EntityBunny)
                {
                    k++;
                }
            }

            if(k > 12)
            {
                proceed();
                return;
            }
            List list1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(1.0D, 1.0D, 1.0D));
            boolean flag = false;
            for(int i1 = 0; i1 < list.size(); i1++)
            {
                Entity entity1 = (Entity)list1.get(i1);
                if(!(entity1 instanceof EntityBunny) || entity1 == this)
                {
                    continue;
                }
                EntityBunny entitybunny = (EntityBunny)entity1;
                if(entitybunny.worldObj != null || entitybunny.j < 1023)
                {
                    continue;
                }
                EntityBunny entitybunny1 = new EntityBunny(worldObj);
                entitybunny1.setPosition(posX, posY, posZ);
                worldObj.entityJoinedWorld(entitybunny1);
                worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
                proceed();
                entitybunny.proceed();
                flag = true;
                break;
            }

            if(!flag)
            {
                k = rand.nextInt(16);
            }
        }
        super.onUpdate();
    }

    protected void fall(float f1)
    {
    }

    protected void updatePlayerActionState()
    {
        if(onGround && (motionX > 0.050000000745058101D || motionZ > 0.050000000745058101D || motionX < -0.050000000745058101D || motionZ < -0.050000000745058101D))
        {
            motionY = 0.44999998807907099D;
        }
        if(!h)
        {
            super.updatePlayerActionState();
        } else
        if(onGround)
        {
            h = false;
            worldObj.playSoundAtEntity(this, "rabbitland", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(12D, 12D, 12D));
            for(int k = 0; k < list.size(); k++)
            {
                Entity entity = (Entity)list.get(k);
                if(entity instanceof EntityMob)
                {
                    EntityMob entitymobs = (EntityMob)entity;
                    entitymobs.playerToAttack = this;
                }
            }

        }
    }

    public boolean interact(EntityPlayer entityplayer)
    {
        rotationYaw = entityplayer.rotationYaw;
        mountEntity(entityplayer);
        if(ridingEntity == null)
        {
            h = true;
        } else
        {
            worldObj.playSoundAtEntity(this, "rabbitlift", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
        }
        motionX = entityplayer.motionX * 5D;
        motionY = entityplayer.motionY / 2D + 0.5D;
        motionZ = entityplayer.motionZ * 5D;
        return true;
    }

    public double getYOffset()
    {
        if(ridingEntity instanceof EntityPlayer)
        {
            return (double)(yOffset - 1.15F);
        } else
        {
            return (double)yOffset;
        }
    }

    protected String getLivingSound()
    {
        return null;
    }

    public void proceed()
    {
        i = 0;
        j = rand.nextInt(64);
    }

    protected String getHurtSound()
    {
        return "rabbithurt";
    }

    public void knockBack(Entity entity, int k, double d2, double d3)
    {
        super.knockBack(entity, k, d2, d3);
    }

    protected String getDeathSound()
    {
        return "rabbitdeath";
    }

    public boolean maxNumberReached()
    {
        int k = 0;
        for(int l = 0; l < worldObj.loadedEntityList.size(); l++)
        {
            Entity entity = (Entity)worldObj.loadedEntityList.get(l);
            if(entity instanceof EntityBunny)
            {
                k++;
            }
        }

        return false;
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
        if(super.getCanSpawnHere())
        {
            if(counterEntity >= mod_mocreatures.maxBunnyS.get())
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

    public boolean a;
    public float b;
    public float c;
    public float d;
    public float d1;
    public float e;
    public float f;
    public boolean h;
    public int j;
    public int i;
    public static int counterEntity;
}
