package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.server.AchievementList;
import net.minecraft.server.Block;
import net.minecraft.server.CounterStatistic;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.CraftingStatistic;
import net.minecraft.server.FurnaceRecipes;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.LocaleI18n;
import net.minecraft.server.Statistic;

public class StatisticList {

   protected static Map a = new HashMap();
   public static List b = new ArrayList();
   public static List c = new ArrayList();
   public static List d = new ArrayList();
   public static List e = new ArrayList();
   public static Statistic f = (new CounterStatistic(1000, "stat.startGame")).e().d();
   public static Statistic g = (new CounterStatistic(1001, "stat.createWorld")).e().d();
   public static Statistic h = (new CounterStatistic(1002, "stat.loadWorld")).e().d();
   public static Statistic i = (new CounterStatistic(1003, "stat.joinMultiplayer")).e().d();
   public static Statistic j = (new CounterStatistic(1004, "stat.leaveGame")).e().d();
   public static Statistic k = (new CounterStatistic(1100, "stat.playOneMinute", Statistic.i)).e().d();
   public static Statistic l = (new CounterStatistic(2000, "stat.walkOneCm", Statistic.j)).e().d();
   public static Statistic m = (new CounterStatistic(2001, "stat.swimOneCm", Statistic.j)).e().d();
   public static Statistic n = (new CounterStatistic(2002, "stat.fallOneCm", Statistic.j)).e().d();
   public static Statistic o = (new CounterStatistic(2003, "stat.climbOneCm", Statistic.j)).e().d();
   public static Statistic p = (new CounterStatistic(2004, "stat.flyOneCm", Statistic.j)).e().d();
   public static Statistic q = (new CounterStatistic(2005, "stat.diveOneCm", Statistic.j)).e().d();
   public static Statistic r = (new CounterStatistic(2006, "stat.minecartOneCm", Statistic.j)).e().d();
   public static Statistic s = (new CounterStatistic(2007, "stat.boatOneCm", Statistic.j)).e().d();
   public static Statistic t = (new CounterStatistic(2008, "stat.pigOneCm", Statistic.j)).e().d();
   public static Statistic u = (new CounterStatistic(2010, "stat.jump")).e().d();
   public static Statistic v = (new CounterStatistic(2011, "stat.drop")).e().d();
   public static Statistic w = (new CounterStatistic(2020, "stat.damageDealt")).d();
   public static Statistic x = (new CounterStatistic(2021, "stat.damageTaken")).d();
   public static Statistic y = (new CounterStatistic(2022, "stat.deaths")).d();
   public static Statistic z = (new CounterStatistic(2023, "stat.mobKills")).d();
   public static Statistic A = (new CounterStatistic(2024, "stat.playerKills")).d();
   public static Statistic B = (new CounterStatistic(2025, "stat.fishCaught")).d();
   public static Statistic[] C = a("stat.mineBlock", 16777216);
   public static Statistic[] D;
   public static Statistic[] E;
   public static Statistic[] F;
   private static boolean G;
   private static boolean H;


   static {
      AchievementList.a();
      G = false;
      H = false;
   }

   public static void a() {}

   public static void b() {
      E = a(E, "stat.useItem", 16908288, 0, 256);
      F = b(F, "stat.breakItem", 16973824, 0, 256);
      G = true;
      d();
   }

   public static void c() {
      E = a(E, "stat.useItem", 16908288, 256, 32000);
      F = b(F, "stat.breakItem", 16973824, 256, 32000);
      H = true;
      d();
   }

   public static void d() {
      if(G && H) {
         HashSet hashset = new HashSet();
         Iterator itemstack = CraftingManager.getInstance().getRecipies().iterator();

         while(itemstack.hasNext()) {
            CraftingRecipe craftingrecipe = (CraftingRecipe)itemstack.next();
            hashset.add(Integer.valueOf(craftingrecipe.b().id));
         }

         Iterator iterator2 = FurnaceRecipes.getInstance().getRecipies().values().iterator();

         while(iterator2.hasNext()) {
            ItemStack itemstack1 = (ItemStack)iterator2.next();
            hashset.add(Integer.valueOf(itemstack1.id));
         }

         D = new Statistic[32000];
         iterator2 = hashset.iterator();

         while(iterator2.hasNext()) {
            Integer integer = (Integer)iterator2.next();
            if(Item.byId[integer.intValue()] != null) {
               String s = LocaleI18n.get("stat.craftItem", new Object[]{Item.byId[integer.intValue()].l()});
               D[integer.intValue()] = (new CraftingStatistic(16842752 + integer.intValue(), s, integer.intValue())).d();
            }
         }

         a(D);
      }

   }

   private static Statistic[] a(String paramString, int paramInt) {
      Statistic[] arrayOfStatistic = new Statistic[4096];

      for(int i1 = 0; i1 < 4096; ++i1) {
         if(Block.byId[i1] != null && Block.byId[i1].r()) {
            String str = LocaleI18n.get(paramString, new Object[]{Block.byId[i1].getName()});
            arrayOfStatistic[i1] = (new CraftingStatistic(paramInt + i1, str, i1)).d();
            e.add((CraftingStatistic)arrayOfStatistic[i1]);
         }
      }

      a(arrayOfStatistic);
      return arrayOfStatistic;
   }

   private static Statistic[] a(Statistic[] paramArrayOfStatistic, String paramString, int paramInt1, int paramInt2, int paramInt3) {
      if(paramArrayOfStatistic == null) {
         paramArrayOfStatistic = new Statistic[32000];
      }

      for(int i1 = paramInt2; i1 < paramInt3; ++i1) {
         if(Item.byId[i1] != null) {
            String str = LocaleI18n.get(paramString, new Object[]{Item.byId[i1].l()});
            paramArrayOfStatistic[i1] = (new CraftingStatistic(paramInt1 + i1, str, i1)).d();
            if(i1 >= 256) {
               d.add((CraftingStatistic)paramArrayOfStatistic[i1]);
            }
         }
      }

      a(paramArrayOfStatistic);
      return paramArrayOfStatistic;
   }

   private static Statistic[] b(Statistic[] paramArrayOfStatistic, String paramString, int paramInt1, int paramInt2, int paramInt3) {
      if(paramArrayOfStatistic == null) {
         paramArrayOfStatistic = new Statistic[32000];
      }

      for(int i1 = paramInt2; i1 < paramInt3; ++i1) {
         if(Item.byId[i1] != null && Item.byId[i1].g()) {
            String str = LocaleI18n.get(paramString, new Object[]{Item.byId[i1].l()});
            paramArrayOfStatistic[i1] = (new CraftingStatistic(paramInt1 + i1, str, i1)).d();
         }
      }

      a(paramArrayOfStatistic);
      return paramArrayOfStatistic;
   }

   private static void a(Statistic[] paramArrayOfStatistic) {
      a(paramArrayOfStatistic, Block.STATIONARY_WATER.id, Block.WATER.id);
      a(paramArrayOfStatistic, Block.STATIONARY_LAVA.id, Block.STATIONARY_LAVA.id);
      a(paramArrayOfStatistic, Block.JACK_O_LANTERN.id, Block.PUMPKIN.id);
      a(paramArrayOfStatistic, Block.BURNING_FURNACE.id, Block.FURNACE.id);
      a(paramArrayOfStatistic, Block.GLOWING_REDSTONE_ORE.id, Block.REDSTONE_ORE.id);
      a(paramArrayOfStatistic, Block.DIODE_ON.id, Block.DIODE_OFF.id);
      a(paramArrayOfStatistic, Block.REDSTONE_TORCH_ON.id, Block.REDSTONE_TORCH_OFF.id);
      a(paramArrayOfStatistic, Block.RED_MUSHROOM.id, Block.BROWN_MUSHROOM.id);
      a(paramArrayOfStatistic, Block.DOUBLE_STEP.id, Block.STEP.id);
      a(paramArrayOfStatistic, Block.GRASS.id, Block.DIRT.id);
      a(paramArrayOfStatistic, Block.SOIL.id, Block.DIRT.id);
   }

   private static void a(Statistic[] paramArrayOfStatistic, int paramInt1, int paramInt2) {
      if(paramArrayOfStatistic[paramInt1] != null && paramArrayOfStatistic[paramInt2] == null) {
         paramArrayOfStatistic[paramInt2] = paramArrayOfStatistic[paramInt1];
      } else {
         b.remove(paramArrayOfStatistic[paramInt1]);
         e.remove(paramArrayOfStatistic[paramInt1]);
         c.remove(paramArrayOfStatistic[paramInt1]);
         paramArrayOfStatistic[paramInt1] = paramArrayOfStatistic[paramInt2];
      }
   }
}
