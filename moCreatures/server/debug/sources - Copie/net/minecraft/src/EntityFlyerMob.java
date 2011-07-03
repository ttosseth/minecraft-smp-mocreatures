package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

public class EntityFlyerMob extends EntityMobs
{
	protected int attackStrength;
	private PathEntity entityPath;
	public double speedModifier;

	public EntityFlyerMob(World world)
	{
		super(world);
		isCollidedVertically = false;
		speedModifier = 0.03D;
		setSize(1.5F, 1.5F);
		entityWalks = false;
		attackStrength = 3;
		health = 10;
	}

	protected void fall(float f)
	{
	}

	public void moveEntityWithHeading(float f, float f1)
	{
		if(handleWaterMovement())
		{
			moveFlying(f, f1, 0.02F);
			moveEntity(motionX, motionY, motionZ);
			motionX *= 0.8D;
			motionY *= 0.8D;
			motionZ *= 0.8D;
		} else if(handleLavaMovement())
		{
			moveFlying(f, f1, 0.02F);
			moveEntity(motionX, motionY, motionZ);
			motionX *= 0.5D;
			motionY *= 0.5D;
			motionZ *= 0.5D;
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
			moveEntity(motionX, motionY, motionZ);
			motionX *= f2;
			motionY *= f2;
			motionZ *= f2;
			if(isCollidedHorizontally)
				motionY = 0.2D;
			if(rand.nextInt(30) == 0)
				motionY = -0.25D;
		}
		field_9142_bc = field_9141_bd;
		double d2 = posX - prevPosX;
		double d3 = posZ - prevPosZ;
		float f4 = MathHelper.sqrt_double(d2 * d2 + d3 * d3) * 4.0F;
		if(f4 > 1.0F)
			f4 = 1.0F;
		field_9141_bd += (f4 - field_9141_bd) * 0.4F;
		field_386_ba += field_9141_bd;
	}

	public boolean isOnLadder()
	{
		return false;
	}

	protected Entity findPlayerToAttack()
	{
		EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(this, 20.0D);
		if(entityplayer != null && canEntityBeSeen(entityplayer))
			return entityplayer;
		return null;
	}

	protected void updatePlayerActionState()
	{
		hasAttacked = false;
		float f = 16.0F;
		if(playerToAttack == null)
		{
			playerToAttack = findPlayerToAttack();
			if(playerToAttack != null)
				entityPath = worldObj.getPathToEntity(this, playerToAttack, f);
		} else if(!playerToAttack.isEntityAlive())
			playerToAttack = null;
		else
		{
			float f1 = playerToAttack.getDistanceToEntity(this);
			if(canEntityBeSeen(playerToAttack))
				attackEntity(playerToAttack, f1);
		}
		if(!hasAttacked && playerToAttack != null && (entityPath == null || rand.nextInt(10) == 0))
			entityPath = worldObj.getPathToEntity(this, playerToAttack, f);
		else if(entityPath == null && rand.nextInt(80) == 0 || rand.nextInt(80) == 0)
		{
			boolean flag = false;
			int j = -1;
			int k = -1;
			int l = -1;
			float f2 = -99999F;
			for(int i1 = 0; i1 < 10; i1++)
			{
				int j1 = MathHelper.floor_double(posX + rand.nextInt(13) - 6.0D);
				int k1 = MathHelper.floor_double(posY + rand.nextInt(7) - 3.0D);
				int l1 = MathHelper.floor_double(posZ + rand.nextInt(13) - 6.0D);
				float f3 = getBlockPathWeight(j1, k1, l1);
				if(f3 > f2)
				{
					f2 = f3;
					j = j1;
					k = k1;
					l = l1;
					flag = true;
				}
			}

			if(flag)
				entityPath = worldObj.getEntityPathToXYZ(this, j, k, l, 10.0F);
		}
		int i = MathHelper.floor_double(boundingBox.minY);
		boolean flag1 = handleWaterMovement();
		boolean flag2 = handleLavaMovement();
		rotationPitch = 0.0F;
		if(entityPath == null || rand.nextInt(100) == 0)
		{
			super.updatePlayerActionState();
			entityPath = null;
			return;
		}
		Vec3D vec3d = entityPath.getPosition(this);
		for(double d = width * 2.0F; vec3d != null && vec3d.squareDistanceTo(posX, vec3d.yCoord, posZ) < d * d;)
		{
			entityPath.incrementPathIndex();
			if(entityPath.isFinished())
			{
				vec3d = null;
				entityPath = null;
			} else
				vec3d = entityPath.getPosition(this);
		}

		isJumping = false;
		if(vec3d != null)
		{
			double d1 = vec3d.xCoord - posX;
			double d2 = vec3d.zCoord - posZ;
			double d3 = vec3d.yCoord - i;
			float f4 = (float)(Math.atan2(d2, d1) * 180.0D / Math.PI) - 90.0F;
			float f5 = f4 - rotationYaw;
			moveForward = moveSpeed;
			while (f5 < -180.0F) f5 += 360.0F;
			while (f5 >= 180.0F) f5 -= 360.0F;
			if(f5 > 30.0F)
				f5 = 30.0F;
			if(f5 < -30.0F)
				f5 = -30.0F;
			rotationYaw += f5;
			if(hasAttacked && playerToAttack != null)
			{
				double d4 = playerToAttack.posX - posX;
				double d5 = playerToAttack.posZ - posZ;
				float f6 = rotationYaw;
				rotationYaw = (float)(Math.atan2(d5, d4) * 180.0D / Math.PI) - 90.0F;
				float f7 = (f6 - rotationYaw + 90.0F) * (float)Math.PI / 180.0F;
				moveStrafing = -MathHelper.sin(f7) * moveForward * 1.0F;
				moveForward = MathHelper.cos(f7) * moveForward * 1.0F;
			}
			if(d3 > 0.0D)
				isJumping = true;
		}
		if(playerToAttack != null)
			faceEntity(playerToAttack, 30.0F);
		if(isCollidedHorizontally)
			isJumping = true;
		if(rand.nextFloat() < 0.8F && (flag1 || flag2))
			isJumping = true;
	}

	protected void attackEntity(Entity entity, float f)
	{
		if(f < 2.5D && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
		{
			attackTime = 20;
			entity.attackEntityFrom(this, attackStrength);
		}
	}

	public boolean getCanSpawnHere()
	{
		return super.getCanSpawnHere();
	}
}
