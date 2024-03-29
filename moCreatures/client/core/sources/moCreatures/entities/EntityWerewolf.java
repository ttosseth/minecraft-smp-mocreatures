package moCreatures.entities;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import moCreatures.mod_mocreatures;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

public class EntityWerewolf extends EntityMob
{

    public EntityWerewolf(World world)
    {
        super(world);
        wereboolean = false;
        texture = "/moCreatures/textures/werehuman.png";
        setSize(0.9F, 1.3F);
        humanform = true;
        health = 15;
        transforming = false;
        tcounter = 0;
        hunched = false;
        isUndead = true;
    }

    protected Entity findPlayerToAttack()
    {
        if(humanform)
        {
            return null;
        }
        EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(this, 16D);
        if(entityplayer != null && canEntityBeSeen(entityplayer))
        {
            return entityplayer;
        } else
        {
            return null;
        }
    }

    protected void updatePlayerActionState()
    {
        if(!transforming)
        {
            super.updatePlayerActionState();
        }
    }

    protected void attackEntity(Entity entity, float f)
    {
        if(humanform)
        {
            playerToAttack = null;
            return;
        }
        if(f > 2.0F && f < 6F && rand.nextInt(15) == 0)
        {
            if(onGround)
            {
                hunched = true;
                double d = entity.posX - posX;
                double d1 = entity.posZ - posZ;
                float f1 = MathHelper.sqrt_double(d * d + d1 * d1);
                motionX = (d / (double)f1) * 0.5D * 0.80000001192092896D + motionX * 0.20000000298023221D;
                motionZ = (d1 / (double)f1) * 0.5D * 0.80000001192092896D + motionZ * 0.20000000298023221D;
                motionY = 0.40000000596046448D;
            }
        } else
        {
            super.attackEntity(entity, f);
        }
    }

    public boolean attackEntityFrom(Entity entity, int i)
    {
        if(!humanform && entity != null && (entity instanceof EntityPlayer))
        {
            EntityPlayer entityplayer = (EntityPlayer)entity;
            ItemStack itemstack = entityplayer.getCurrentEquippedItem();
            if(itemstack != null)
            {
                i = 1;
                if(itemstack.itemID == Item.hoeGold.shiftedIndex)
                {
                    i = 6;
                }
                if(itemstack.itemID == Item.shovelGold.shiftedIndex)
                {
                    i = 7;
                }
                if(itemstack.itemID == Item.pickaxeGold.shiftedIndex)
                {
                    i = 8;
                }
                if(itemstack.itemID == Item.axeGold.shiftedIndex)
                {
                    i = 9;
                }
                if(itemstack.itemID == Item.swordGold.shiftedIndex)
                {
                    i = 10;
                }
            }
        }
        return super.attackEntityFrom(entity, i);
    }

    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(IsNight() && humanform || !IsNight() && !humanform && rand.nextInt(250) == 0)
        {
            transforming = true;
        }
        if(humanform && playerToAttack != null)
        {
            playerToAttack = null;
        }
        if(playerToAttack != null && !humanform && playerToAttack.posX - posX > 3D && playerToAttack.posZ - posZ > 3D)
        {
            hunched = true;
        }
        if(hunched && rand.nextInt(50) == 0)
        {
            hunched = false;
        }
        if(transforming && rand.nextInt(3) == 0)
        {
            tcounter++;
            if(tcounter % 2 == 0)
            {
                posX += 0.29999999999999999D;
                posY += tcounter / 30;
                attackEntityFrom(this, 1);
            }
            if(tcounter % 2 != 0)
            {
                posX -= 0.29999999999999999D;
            }
            if(tcounter == 10)
            {
                worldObj.playSoundAtEntity(this, "weretransform", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
            }
            if(tcounter > 30)
            {
                Transform();
                tcounter = 0;
                transforming = false;
            }
        }
        if(rand.nextInt(300) == 0)
        {
            entityAge -= 100 * worldObj.difficultySetting;
            if(entityAge < 0)
            {
            	entityAge = 0;
            }
        }
    }

    public boolean IsNight()
    {
        return !worldObj.isDaytime();
    }

    public void moveEntityWithHeading(float f, float f1)
    {
        if(!humanform && onGround)
        {
            motionX *= 1.2D;
            motionZ *= 1.2D;
        }
        super.moveEntityWithHeading(f, f1);
    }

    private void Transform()
    {
        if(deathTime > 0)
        {
            return;
        }
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY) + 1;
        int k = MathHelper.floor_double(posZ);
        float f = 0.1F;
        for(int l = 0; l < 30; l++)
        {
            double d = (float)i + worldObj.rand.nextFloat();
            double d1 = (float)j + worldObj.rand.nextFloat();
            double d2 = (float)k + worldObj.rand.nextFloat();
            double d3 = d - (double)i;
            double d4 = d1 - (double)j;
            double d5 = d2 - (double)k;
            double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5D / (d6 / (double)f + 0.10000000000000001D);
            d7 *= worldObj.rand.nextFloat() * worldObj.rand.nextFloat() + 0.3F;
            d3 *= d7;
            d4 *= d7;
            d5 *= d7;
            worldObj.spawnParticle("explode", (d + (double)i * 1.0D) / 2D, (d1 + (double)j * 1.0D) / 2D, (d2 + (double)k * 1.0D) / 2D, d3, d4, d5);
        }

        if(humanform)
        {
            humanform = false;
            health = 40;
            transforming = false;
        } else
        {
            humanform = true;
            health = 15;
            transforming = false;
        }
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("HumanForm", humanform);
        nbttagcompound.setInteger("CounterEntity", counterEntity);
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        humanform = nbttagcompound.getBoolean("HumanForm");
        counterEntity = nbttagcompound.getInteger("CounterEntity");
    }

    protected String getLivingSound()
    {
        if(humanform)
        {
            return "werehumangrunt";
        } else
        {
            return "werewolfgrunt";
        }
    }

    protected String getHurtSound()
    {
        if(humanform)
        {
            return "werehumanhurt";
        } else
        {
            return "werewolfhurt";
        }
    }

    protected String getDeathSound()
    {
        if(humanform)
        {
            return "werehumandying";
        } else
        {
            return "werewolfdying";
        }
    }

    protected int getDropItemId()
    {
        int i = rand.nextInt(12);
        if(humanform)
        {
            switch(i)
            {
            case 0: // '\0'
                return Item.shovelWood.shiftedIndex;

            case 1: // '\001'
                return Item.axeWood.shiftedIndex;

            case 2: // '\002'
                return Item.swordWood.shiftedIndex;

            case 3: // '\003'
                return Item.hoeWood.shiftedIndex;

            case 4: // '\004'
                return Item.pickaxeWood.shiftedIndex;
            }
            return Item.stick.shiftedIndex;
        }
        switch(i)
        {
        case 0: // '\0'
            return Item.hoeSteel.shiftedIndex;

        case 1: // '\001'
            return Item.shovelSteel.shiftedIndex;

        case 2: // '\002'
            return Item.axeSteel.shiftedIndex;

        case 3: // '\003'
            return Item.pickaxeSteel.shiftedIndex;

        case 4: // '\004'
            return Item.swordSteel.shiftedIndex;

        case 5: // '\005'
            return Item.hoeStone.shiftedIndex;

        case 6: // '\006'
            return Item.shovelStone.shiftedIndex;

        case 7: // '\007'
            return Item.axeStone.shiftedIndex;

        case 8: // '\b'
            return Item.pickaxeStone.shiftedIndex;

        case 9: // '\t'
            return Item.swordStone.shiftedIndex;
        }
        return Item.appleGold.shiftedIndex;
    }

    public void onDeath(Entity entity)
    {
        if(scoreValue > 0 && entity != null)
        {
            entity.addToPlayerScore(this, scoreValue);
        }
        unused_flag = true;
        if(!worldObj.multiplayerWorld)
        {
            for(int i = 0; i < 2; i++)
            {
                int j = getDropItemId();
                if(j > 0)
                {
                    dropItem(j, 1);
                }
            }

        }
        worldObj.func_4079_a(this, 3F);
    }

    public int getMaxSpawnedInChunk()
    {
        return 1;
    }

    public void setEntityDead()
    {
        counterEntity--;
        super.setEntityDead();
    }

    public boolean getCanSpawnHere()
    {
        if(worldObj.difficultySetting >= mod_mocreatures.wereSpawnDifficulty.get() + 1 && rand.nextInt(2) == 0 && super.getCanSpawnHere())
        {
            if(counterEntity >= mod_mocreatures.maxWerewolfS.get())
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

    public boolean wereboolean;
    public boolean humanform;
    private boolean transforming;
    private int tcounter;
    public boolean hunched;
    public boolean isUndead;
    public static int counterEntity;
}
