package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 


public class ModelLionK1 extends ModelQuadruped
{

    public ModelLionK1()
    {
        super(12, 0.0F);
        head = new ModelRenderer(20, 0);
        head.addBox(-7F, -8F, -2F, 14, 14, 8, 0.0F);
        head.setPosition(0.0F, 4F, -8F);
        body = new ModelRenderer(20, 0);
        body.addBox(-6F, -11F, -8F, 12, 10, 10, 0.0F);
        body.setPosition(0.0F, 5F, 2.0F);
    }
}
