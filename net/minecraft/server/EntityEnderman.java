package net.minecraft.server;

import java.util.ArrayList;
import net.minecraft.server.Block;
import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityDamageSourceIndirect;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTeleportEvent;

public class EntityEnderman extends EntityMonster {

   private static boolean[] b = new boolean[4096];
   public boolean a = false;
   private int g = 0;
   private int h = 0;


   static {
      b[Block.GRASS.id] = true;
      b[Block.DIRT.id] = true;
      b[Block.SAND.id] = true;
      b[Block.GRAVEL.id] = true;
      b[Block.YELLOW_FLOWER.id] = true;
      b[Block.RED_ROSE.id] = true;
      b[Block.BROWN_MUSHROOM.id] = true;
      b[Block.RED_MUSHROOM.id] = true;
      b[Block.TNT.id] = true;
      b[Block.CACTUS.id] = true;
      b[Block.CLAY.id] = true;
      b[Block.PUMPKIN.id] = true;
      b[Block.MELON.id] = true;
      b[Block.MYCEL.id] = true;
   }

   public EntityEnderman(World world) {
      super(world);
      this.texture = "/mob/enderman.png";
      this.bb = 0.2F;
      this.damage = 7;
      this.b(0.6F, 2.9F);
      this.bP = 1.0F;
   }

   public int getMaxHealth() {
      return 40;
   }

   protected void b() {
      super.b();
      this.datawatcher.a(16, new Byte((byte)0));
      this.datawatcher.a(17, new Byte((byte)0));
   }

   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setShort("carried", (short)this.getCarriedId());
      nbttagcompound.setShort("carriedData", (short)this.getCarriedData());
   }

   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.setCarriedId(nbttagcompound.getShort("carried"));
      this.setCarriedData(nbttagcompound.getShort("carriedData"));
   }

   protected Entity findTarget() {
      EntityHuman entityhuman = this.world.findNearbyVulnerablePlayer(this, 64.0D);
      if(entityhuman != null) {
         if(this.c(entityhuman)) {
            if(this.h++ == 5) {
               this.h = 0;
               return entityhuman;
            }
         } else {
            this.h = 0;
         }
      }

      return null;
   }

   public float b(float f) {
      return super.b(f);
   }

   private boolean c(EntityHuman entityhuman) {
      ItemStack itemstack = entityhuman.inventory.armor[3];
      if(itemstack != null && itemstack.id == Block.PUMPKIN.id) {
         return false;
      } else {
         Vec3D vec3d = entityhuman.f(1.0F).b();
         Vec3D vec3d1 = Vec3D.create(this.locX - entityhuman.locX, this.boundingBox.b + (double)(this.length / 2.0F) - (entityhuman.locY + (double)entityhuman.getHeadHeight()), this.locZ - entityhuman.locZ);
         double d0 = vec3d1.c();
         vec3d1 = vec3d1.b();
         double d1 = vec3d.a(vec3d1);
         return d1 > 1.0D - 0.025D / d0?entityhuman.h(this):false;
      }
   }

   public void e() {
      if(this.aT()) {
         this.damageEntity(DamageSource.DROWN, 1);
      }

      this.a = this.target != null;
      this.bb = this.target != null?6.5F:0.3F;
      int i;
      if(!this.world.isStatic) {
         int f;
         int k;
         int l;
         if(this.getCarriedId() == 0) {
            if(this.random.nextInt(20) == 0) {
               i = MathHelper.floor(this.locX - 2.0D + this.random.nextDouble() * 4.0D);
               f = MathHelper.floor(this.locY + this.random.nextDouble() * 3.0D);
               k = MathHelper.floor(this.locZ - 2.0D + this.random.nextDouble() * 4.0D);
               l = this.world.getTypeId(i, f, k);
               if(b[l] && !CraftEventFactory.callEntityChangeBlockEvent(this, this.world.getWorld().getBlockAt(i, f, k), Material.AIR).isCancelled()) {
                  this.setCarriedId(this.world.getTypeId(i, f, k));
                  this.setCarriedData(this.world.getData(i, f, k));
                  this.world.setTypeId(i, f, k, 0);
               }
            }
         } else if(this.random.nextInt(2000) == 0) {
            i = MathHelper.floor(this.locX - 1.0D + this.random.nextDouble() * 2.0D);
            f = MathHelper.floor(this.locY + this.random.nextDouble() * 2.0D);
            k = MathHelper.floor(this.locZ - 1.0D + this.random.nextDouble() * 2.0D);
            l = this.world.getTypeId(i, f, k);
            int i1 = this.world.getTypeId(i, f - 1, k);
            if(l == 0 && i1 > 0 && Block.byId[i1].b()) {
               org.bukkit.block.Block bblock = this.world.getWorld().getBlockAt(i, f, k);
               if(!CraftEventFactory.callEntityChangeBlockEvent(this, bblock, bblock.getType()).isCancelled()) {
                  this.world.setTypeIdAndData(i, f, k, this.getCarriedId(), this.getCarriedData());
                  this.setCarriedId(0);
               }
            }
         }
      }

      for(i = 0; i < 2; ++i) {
         this.world.a("portal", this.locX + (this.random.nextDouble() - 0.5D) * (double)this.width, this.locY + this.random.nextDouble() * (double)this.length - 0.25D, this.locZ + (this.random.nextDouble() - 0.5D) * (double)this.width, (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
      }

      if(this.world.e() && !this.world.isStatic) {
         float var7 = this.b(1.0F);
         if(var7 > 0.5F && this.world.isChunkLoaded(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) && this.random.nextFloat() * 30.0F < (var7 - 0.4F) * 2.0F) {
            this.target = null;
            this.x();
         }
      }

      if(this.aT()) {
         this.target = null;
         this.x();
      }

      this.aZ = false;
      if(this.target != null) {
         this.a(this.target, 100.0F, 100.0F);
      }

      if(!this.world.isStatic && this.isAlive()) {
         if(this.target != null) {
            if(this.target instanceof EntityHuman && this.c((EntityHuman)this.target)) {
               this.aW = this.aX = 0.0F;
               this.bb = 0.0F;
               if(this.target.j(this) < 16.0D) {
                  this.x();
               }

               this.g = 0;
            } else if(this.target.j(this) > 256.0D && this.g++ >= 30 && this.e(this.target)) {
               this.g = 0;
            }
         } else {
            this.g = 0;
         }
      }

      super.e();
   }

   protected boolean x() {
      double d0 = this.locX + (this.random.nextDouble() - 0.5D) * 64.0D;
      double d1 = this.locY + (double)(this.random.nextInt(64) - 32);
      double d2 = this.locZ + (this.random.nextDouble() - 0.5D) * 64.0D;
      return this.b(d0, d1, d2);
   }

   protected boolean e(Entity entity) {
      Vec3D vec3d = Vec3D.create(this.locX - entity.locX, this.boundingBox.b + (double)(this.length / 2.0F) - entity.locY + (double)entity.getHeadHeight(), this.locZ - entity.locZ);
      vec3d = vec3d.b();
      double d0 = 16.0D;
      double d1 = this.locX + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.a * d0;
      double d2 = this.locY + (double)(this.random.nextInt(16) - 8) - vec3d.b * d0;
      double d3 = this.locZ + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.c * d0;
      return this.b(d1, d2, d3);
   }

   protected boolean b(double d0, double d1, double d2) {
      double d3 = this.locX;
      double d4 = this.locY;
      double d5 = this.locZ;
      this.locX = d0;
      this.locY = d1;
      this.locZ = d2;
      boolean flag = false;
      int i = MathHelper.floor(this.locX);
      int j = MathHelper.floor(this.locY);
      int k = MathHelper.floor(this.locZ);
      int l;
      if(this.world.isLoaded(i, j, k)) {
         boolean short1 = false;

         while(!short1 && j > 0) {
            l = this.world.getTypeId(i, j - 1, k);
            if(l != 0 && Block.byId[l].material.isSolid()) {
               short1 = true;
            } else {
               --this.locY;
               --j;
            }
         }

         if(short1) {
            EntityTeleportEvent d6 = new EntityTeleportEvent(this.getBukkitEntity(), new Location(this.world.getWorld(), d3, d4, d5), new Location(this.world.getWorld(), this.locX, this.locY, this.locZ));
            this.world.getServer().getPluginManager().callEvent(d6);
            if(d6.isCancelled()) {
               return false;
            }

            Location to = d6.getTo();
            this.setPosition(to.getX(), to.getY(), to.getZ());
            if(this.world.getCubes(this, this.boundingBox).size() == 0 && !this.world.containsLiquid(this.boundingBox)) {
               flag = true;
            }
         }
      }

      if(!flag) {
         this.setPosition(d3, d4, d5);
         return false;
      } else {
         short var31 = 128;

         for(l = 0; l < var31; ++l) {
            double var30 = (double)l / ((double)var31 - 1.0D);
            float f = (this.random.nextFloat() - 0.5F) * 0.2F;
            float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
            float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
            double d7 = d3 + (this.locX - d3) * var30 + (this.random.nextDouble() - 0.5D) * (double)this.width * 2.0D;
            double d8 = d4 + (this.locY - d4) * var30 + this.random.nextDouble() * (double)this.length;
            double d9 = d5 + (this.locZ - d5) * var30 + (this.random.nextDouble() - 0.5D) * (double)this.width * 2.0D;
            this.world.a("portal", d7, d8, d9, (double)f, (double)f1, (double)f2);
         }

         this.world.makeSound(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
         this.world.makeSound(this, "mob.endermen.portal", 1.0F, 1.0F);
         return true;
      }
   }

   protected String i() {
      return "mob.endermen.idle";
   }

   protected String j() {
      return "mob.endermen.hit";
   }

   protected String k() {
      return "mob.endermen.death";
   }

   protected int getLootId() {
      return Item.ENDER_PEARL.id;
   }

   protected void dropDeathLoot(boolean flag, int i) {
      int j = this.getLootId();
      if(j > 0) {
         ArrayList loot = new ArrayList();
         int count = this.random.nextInt(2 + i);
         if(j > 0 && count > 0) {
            loot.add(new org.bukkit.inventory.ItemStack(j, count));
         }

         CraftEventFactory.callEntityDeathEvent(this, loot);
      }

   }

   public void setCarriedId(int i) {
      this.datawatcher.watch(16, Byte.valueOf((byte)(i & 255)));
   }

   public int getCarriedId() {
      return this.datawatcher.getByte(16);
   }

   public void setCarriedData(int i) {
      this.datawatcher.watch(17, Byte.valueOf((byte)(i & 255)));
   }

   public int getCarriedData() {
      return this.datawatcher.getByte(17);
   }

   public boolean damageEntity(DamageSource damagesource, int i) {
      if(!(damagesource instanceof EntityDamageSourceIndirect)) {
         return super.damageEntity(damagesource, i);
      } else {
         for(int j = 0; j < 64; ++j) {
            if(this.x()) {
               return true;
            }
         }

         return false;
      }
   }
}
