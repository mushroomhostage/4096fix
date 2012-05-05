package net.minecraft.server;

import net.minecraft.server.Block;
import net.minecraft.server.Chunk;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ItemWorldMapBase;
import net.minecraft.server.MaterialMapColor;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet131ItemData;
import net.minecraft.server.World;
import net.minecraft.server.WorldMap;
import net.minecraft.server.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.event.server.MapInitializeEvent;

public class ItemWorldMap extends ItemWorldMapBase {

   protected ItemWorldMap(int i) {
      super(i);
      this.e(1);
   }

   public WorldMap getSavedMap(ItemStack itemstack, World world) {
      WorldMap worldmap = (WorldMap)world.a(WorldMap.class, "map_" + itemstack.getData());
      if(worldmap == null) {
         itemstack.setData(world.b("map"));
         String s = "map_" + itemstack.getData();
         worldmap = new WorldMap(s);
         worldmap.centerX = world.getWorldData().c();
         worldmap.centerZ = world.getWorldData().e();
         worldmap.scale = 3;
         worldmap.map = (byte)((WorldServer)world).dimension;
         worldmap.a();
         world.a(s, worldmap);
         MapInitializeEvent event = new MapInitializeEvent(worldmap.mapView);
         Bukkit.getServer().getPluginManager().callEvent(event);
      }

      return worldmap;
   }

   public void a(World world, Entity entity, WorldMap worldmap) {
      if(((WorldServer)world).dimension == worldmap.map) {
         short short1 = 128;
         short short2 = 128;
         int i = 1 << worldmap.scale;
         int j = worldmap.centerX;
         int k = worldmap.centerZ;
         int l = MathHelper.floor(entity.locX - (double)j) / i + short1 / 2;
         int i1 = MathHelper.floor(entity.locZ - (double)k) / i + short2 / 2;
         int j1 = 128 / i;
         if(world.worldProvider.e) {
            j1 /= 2;
         }

         ++worldmap.g;

         for(int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
            if((k1 & 15) == (worldmap.g & 15)) {
               int l1 = 255;
               int i2 = 0;
               double d0 = 0.0D;

               for(int j2 = i1 - j1 - 1; j2 < i1 + j1; ++j2) {
                  if(k1 >= 0 && j2 >= -1 && k1 < short1 && j2 < short2) {
                     int k2 = k1 - l;
                     int l2 = j2 - i1;
                     boolean flag = k2 * k2 + l2 * l2 > (j1 - 2) * (j1 - 2);
                     int i3 = (j / i + k1 - short1 / 2) * i;
                     int j3 = (k / i + j2 - short2 / 2) * i;
                     byte b0 = 0;
                     byte b1 = 0;
                     byte b2 = 0;
                     int[] aint = new int[256];
                     Chunk chunk = world.getChunkAtWorldCoords(i3, j3);
                     if(!chunk.isEmpty()) {
                        int k3 = i3 & 15;
                        int l3 = j3 & 15;
                        int i4 = 0;
                        double d1 = 0.0D;
                        int k4;
                        int l4;
                        int j4;
                        int i5;
                        if(world.worldProvider.e) {
                           l4 = i3 + j3 * 231871;
                           l4 = l4 * l4 * 31287121 + l4 * 11;
                           if((l4 >> 20 & 1) == 0) {
                              aint[Block.DIRT.id] += 10;
                           } else {
                              aint[Block.STONE.id] += 10;
                           }

                           d1 = 100.0D;
                        } else {
                           for(l4 = 0; l4 < i; ++l4) {
                              for(j4 = 0; j4 < i; ++j4) {
                                 k4 = chunk.b(l4 + k3, j4 + l3) + 1;
                                 int l5 = 0;
                                 if(k4 > 1) {
                                    boolean d2 = false;

                                    do {
                                       d2 = true;
                                       l5 = chunk.getTypeId(l4 + k3, k4 - 1, j4 + l3);
                                       if(l5 == 0) {
                                          d2 = false;
                                       } else if(k4 > 0 && l5 > 0 && Block.byId[l5].material.F == MaterialMapColor.b) {
                                          d2 = false;
                                       }

                                       if(!d2) {
                                          --k4;
                                          if(k4 <= 0) {
                                             break;
                                          }

                                          l5 = chunk.getTypeId(l4 + k3, k4 - 1, j4 + l3);
                                       }
                                    } while(k4 > 0 && !d2);

                                    if(k4 > 0 && l5 != 0 && Block.byId[l5].material.isLiquid()) {
                                       i5 = k4 - 1;
                                       boolean flag2 = false;

                                       int b3;
                                       do {
                                          b3 = chunk.getTypeId(l4 + k3, i5--, j4 + l3);
                                          ++i4;
                                       } while(i5 > 0 && b3 != 0 && Block.byId[b3].material.isLiquid());
                                    }
                                 }

                                 d1 += (double)k4 / (double)(i * i);
                                 ++aint[l5];
                              }
                           }
                        }

                        i4 /= i * i;
                        int var10000 = b0 / (i * i);
                        var10000 = b1 / (i * i);
                        var10000 = b2 / (i * i);
                        l4 = 0;
                        j4 = 0;

                        for(k4 = 0; k4 < 256; ++k4) {
                           if(aint[k4] > l4) {
                              j4 = k4;
                              l4 = aint[k4];
                           }
                        }

                        double var45 = (d1 - d0) * 4.0D / (double)(i + 4) + ((double)(k1 + j2 & 1) - 0.5D) * 0.4D;
                        byte var44 = 1;
                        if(var45 > 0.6D) {
                           var44 = 2;
                        }

                        if(var45 < -0.6D) {
                           var44 = 0;
                        }

                        i5 = 0;
                        if(j4 > 0) {
                           MaterialMapColor b4 = Block.byId[j4].material.F;
                           if(b4 == MaterialMapColor.n) {
                              var45 = (double)i4 * 0.1D + (double)(k1 + j2 & 1) * 0.2D;
                              var44 = 1;
                              if(var45 < 0.5D) {
                                 var44 = 2;
                              }

                              if(var45 > 0.9D) {
                                 var44 = 0;
                              }
                           }

                           i5 = b4.q;
                        }

                        d0 = d1;
                        if(j2 >= 0 && k2 * k2 + l2 * l2 < j1 * j1 && (!flag || (k1 + j2 & 1) != 0)) {
                           byte var43 = worldmap.colors[k1 + j2 * short1];
                           byte b5 = (byte)(i5 * 4 + var44);
                           if(var43 != b5) {
                              if(l1 > j2) {
                                 l1 = j2;
                              }

                              if(i2 < j2) {
                                 i2 = j2;
                              }

                              worldmap.colors[k1 + j2 * short1] = b5;
                           }
                        }
                     }
                  }
               }

               if(l1 <= i2) {
                  worldmap.flagDirty(k1, l1, i2);
               }
            }
         }
      }

   }

   public void a(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
      if(!world.isStatic) {
         WorldMap worldmap = this.getSavedMap(itemstack, world);
         if(entity instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman)entity;
            worldmap.a(entityhuman, itemstack);
         }

         if(flag) {
            this.a(world, entity, worldmap);
         }
      }

   }

   public void d(ItemStack itemstack, World world, EntityHuman entityhuman) {
      itemstack.setData(world.b("map"));
      String s = "map_" + itemstack.getData();
      WorldMap worldmap = new WorldMap(s);
      world.a(s, worldmap);
      worldmap.centerX = MathHelper.floor(entityhuman.locX);
      worldmap.centerZ = MathHelper.floor(entityhuman.locZ);
      worldmap.scale = 3;
      worldmap.map = (byte)((WorldServer)world).dimension;
      worldmap.a();
      MapInitializeEvent event = new MapInitializeEvent(worldmap.mapView);
      Bukkit.getServer().getPluginManager().callEvent(event);
   }

   public Packet c(ItemStack itemstack, World world, EntityHuman entityhuman) {
      byte[] abyte = this.getSavedMap(itemstack, world).getUpdatePacket(itemstack, world, entityhuman);
      return abyte == null?null:new Packet131ItemData((short)Item.MAP.id, (short)itemstack.getData(), abyte);
   }
}
