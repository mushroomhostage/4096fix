package forge;

import forge.Property;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.server.Block;

public class Configuration {

   private boolean[] configBlocks = null;
   public static final String CATEGORY_GENERAL = "general";
   public static final String CATEGORY_BLOCK = "block";
   public static final String CATEGORY_ITEM = "item";
   File file;
   public Map categories = new TreeMap();
   public TreeMap blockProperties = new TreeMap();
   public TreeMap itemProperties = new TreeMap();
   public TreeMap generalProperties = new TreeMap();


   public Configuration(File file1) {
      this.file = file1;
      this.categories.put("general", this.generalProperties);
      this.categories.put("block", this.blockProperties);
      this.categories.put("item", this.itemProperties);
   }

   public Property getOrCreateBlockIdProperty(String s, int i) {
      if(this.configBlocks == null) {
         this.configBlocks = new boolean[4096];

         for(int map = 0; map < this.configBlocks.length; ++map) {
            this.configBlocks[map] = false;
         }
      }

      Map var6 = (Map)this.categories.get("block");
      Property property1;
      if(var6.containsKey(s)) {
         property1 = this.getOrCreateIntProperty(s, "block", i);
         this.configBlocks[Integer.parseInt(property1.value)] = true;
         return property1;
      } else {
         property1 = new Property();
         var6.put(s, property1);
         property1.name = s;
         if(Block.byId[i] == null && !this.configBlocks[i]) {
            property1.value = Integer.toString(i);
            this.configBlocks[i] = true;
            return property1;
         } else {
            for(int k = this.configBlocks.length - 1; k >= 0; --k) {
               if(Block.byId[k] == null && !this.configBlocks[k]) {
                  property1.value = Integer.toString(k);
                  this.configBlocks[k] = true;
                  return property1;
               }
            }

            throw new RuntimeException("No more block ids available for " + s);
         }
      }
   }

   public Property getOrCreateIntProperty(String s, String s1, int i) {
      Property property = this.getOrCreateProperty(s, s1, Integer.toString(i));

      try {
         Integer.parseInt(property.value);
         return property;
      } catch (NumberFormatException var6) {
         property.value = Integer.toString(i);
         return property;
      }
   }

   public Property getOrCreateBooleanProperty(String s, String s1, boolean flag) {
      Property property = this.getOrCreateProperty(s, s1, Boolean.toString(flag));
      if(!"true".equals(property.value.toLowerCase()) && !"false".equals(property.value.toLowerCase())) {
         property.value = Boolean.toString(flag);
         return property;
      } else {
         return property;
      }
   }

   public Property getOrCreateProperty(String s, String s1, String s2) {
      s1 = s1.toLowerCase();
      Object obj = (Map)this.categories.get(s1);
      if(obj == null) {
         obj = new TreeMap();
         this.categories.put(s1, obj);
      }

      if(((Map)obj).containsKey(s)) {
         return (Property)((Map)obj).get(s);
      } else if(s2 != null) {
         Property property = new Property();
         ((Map)obj).put(s, property);
         property.name = s;
         property.value = s2;
         return property;
      } else {
         return null;
      }
   }

   public void load() {
      try {
         if(this.file.getParentFile() != null) {
            this.file.getParentFile().mkdirs();
         }

         if(!this.file.exists() && !this.file.createNewFile()) {
            return;
         }

         if(this.file.canRead()) {
            FileInputStream ioexception = new FileInputStream(this.file);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(ioexception, "8859_1"));
            Object obj = null;

            while(true) {
               String s = bufferedreader.readLine();
               if(s == null) {
                  break;
               }

               int i = -1;
               int j = -1;
               boolean flag = false;

               for(int k = 0; k < s.length() && !flag; ++k) {
                  if(!Character.isLetterOrDigit(s.charAt(k)) && s.charAt(k) != 46) {
                     if(!Character.isWhitespace(s.charAt(k))) {
                        switch(s.charAt(k)) {
                        case 35:
                           flag = true;
                           break;
                        case 61:
                           String s2 = s.substring(i, j + 1);
                           if(obj == null) {
                              throw new RuntimeException("property " + s2 + " has no scope");
                           }

                           Property property = new Property();
                           property.name = s2;
                           property.value = s.substring(k + 1);
                           k = s.length();
                           ((Map)obj).put(s2, property);
                           break;
                        case 123:
                           String s1 = s.substring(i, j + 1);
                           obj = (Map)this.categories.get(s1);
                           if(obj == null) {
                              obj = new TreeMap();
                              this.categories.put(s1, obj);
                           }
                           break;
                        case 125:
                           obj = null;
                           break;
                        default:
                           throw new RuntimeException("unknown character " + s.charAt(k));
                        }
                     }
                  } else {
                     if(i == -1) {
                        i = k;
                     }

                     j = k;
                  }
               }
            }
         }
      } catch (IOException var12) {
         var12.printStackTrace();
      }

   }

   public void save() {
      try {
         if(this.file.getParentFile() != null) {
            this.file.getParentFile().mkdirs();
         }

         if(!this.file.exists() && !this.file.createNewFile()) {
            return;
         }

         if(this.file.canWrite()) {
            FileOutputStream ioexception = new FileOutputStream(this.file);
            BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(ioexception, "8859_1"));
            bufferedwriter.write("# Configuration file\r\n");
            bufferedwriter.write("# Generated on " + DateFormat.getInstance().format(new Date()) + "\r\n");
            bufferedwriter.write("\r\n");
            Iterator iterator = this.categories.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry entry = (Entry)iterator.next();
               bufferedwriter.write("####################\r\n");
               bufferedwriter.write("# " + (String)entry.getKey() + " \r\n");
               bufferedwriter.write("####################\r\n\r\n");
               bufferedwriter.write((String)entry.getKey() + " {\r\n");
               this.writeProperties(bufferedwriter, ((Map)entry.getValue()).values());
               bufferedwriter.write("}\r\n\r\n");
            }

            bufferedwriter.close();
            ioexception.close();
         }
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   private void writeProperties(BufferedWriter bufferedwriter, Collection collection) throws IOException {
      Iterator iterator = collection.iterator();

      while(iterator.hasNext()) {
         Property property = (Property)iterator.next();
         if(property.comment != null) {
            bufferedwriter.write("   # " + property.comment + "\r\n");
         }

         bufferedwriter.write("   " + property.name + "=" + property.value);
         bufferedwriter.write("\r\n");
      }

   }
}
