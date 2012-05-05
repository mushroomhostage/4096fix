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


   public Configuration(File file) {
      this.file = file;
      this.categories.put("general", this.generalProperties);
      this.categories.put("block", this.blockProperties);
      this.categories.put("item", this.itemProperties);
   }

   public Property getOrCreateBlockIdProperty(String key, int defaultId) {
      if(this.configBlocks == null) {
         this.configBlocks = new boolean[256];

         for(int properties = 0; properties < this.configBlocks.length; ++properties) {
            this.configBlocks[properties] = false;
         }
      }

      Map var6 = (Map)this.categories.get("block");
      Property property;
      if(var6.containsKey(key)) {
         property = this.getOrCreateIntProperty(key, "block", defaultId);
         this.configBlocks[Integer.parseInt(property.value)] = true;
         return property;
      } else {
         property = new Property();
         var6.put(key, property);
         property.name = key;
         if(Block.byId[defaultId] == null && !this.configBlocks[defaultId]) {
            property.value = Integer.toString(defaultId);
            this.configBlocks[defaultId] = true;
            return property;
         } else {
            for(int j = this.configBlocks.length - 1; j >= 0; --j) {
               if(Block.byId[j] == null && !this.configBlocks[j]) {
                  property.value = Integer.toString(j);
                  this.configBlocks[j] = true;
                  return property;
               }
            }

            throw new RuntimeException("No more block ids available for " + key);
         }
      }
   }

   public Property getOrCreateIntProperty(String key, String category, int defaultValue) {
      Property prop = this.getOrCreateProperty(key, category, Integer.toString(defaultValue));

      try {
         Integer.parseInt(prop.value);
         return prop;
      } catch (NumberFormatException var6) {
         prop.value = Integer.toString(defaultValue);
         return prop;
      }
   }

   public Property getOrCreateBooleanProperty(String key, String category, boolean defaultValue) {
      Property prop = this.getOrCreateProperty(key, category, Boolean.toString(defaultValue));
      if(!"true".equals(prop.value.toLowerCase()) && !"false".equals(prop.value.toLowerCase())) {
         prop.value = Boolean.toString(defaultValue);
         return prop;
      } else {
         return prop;
      }
   }

   public Property getOrCreateProperty(String key, String category, String defaultValue) {
      category = category.toLowerCase();
      Object source = (Map)this.categories.get(category);
      if(source == null) {
         source = new TreeMap();
         this.categories.put(category, source);
      }

      if(((Map)source).containsKey(key)) {
         return (Property)((Map)source).get(key);
      } else if(defaultValue != null) {
         Property property = new Property();
         ((Map)source).put(key, property);
         property.name = key;
         property.value = defaultValue;
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
            FileInputStream e = new FileInputStream(this.file);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(e, "8859_1"));
            Object currentMap = null;

            while(true) {
               String line = buffer.readLine();
               if(line == null) {
                  break;
               }

               int nameStart = -1;
               int nameEnd = -1;
               boolean skip = false;

               for(int i = 0; i < line.length() && !skip; ++i) {
                  if(!Character.isLetterOrDigit(line.charAt(i)) && line.charAt(i) != 46) {
                     if(!Character.isWhitespace(line.charAt(i))) {
                        switch(line.charAt(i)) {
                        case 35:
                           skip = true;
                           break;
                        case 61:
                           String propertyName = line.substring(nameStart, nameEnd + 1);
                           if(currentMap == null) {
                              throw new RuntimeException("property " + propertyName + " has no scope");
                           }

                           Property prop = new Property();
                           prop.name = propertyName;
                           prop.value = line.substring(i + 1);
                           i = line.length();
                           ((Map)currentMap).put(propertyName, prop);
                           break;
                        case 123:
                           String scopeName = line.substring(nameStart, nameEnd + 1);
                           currentMap = (Map)this.categories.get(scopeName);
                           if(currentMap == null) {
                              currentMap = new TreeMap();
                              this.categories.put(scopeName, currentMap);
                           }
                           break;
                        case 125:
                           currentMap = null;
                           break;
                        default:
                           throw new RuntimeException("unknown character " + line.charAt(i));
                        }
                     }
                  } else {
                     if(nameStart == -1) {
                        nameStart = i;
                     }

                     nameEnd = i;
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
            FileOutputStream e = new FileOutputStream(this.file);
            BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(e, "8859_1"));
            buffer.write("# Configuration file\r\n");
            buffer.write("# Generated on " + DateFormat.getInstance().format(new Date()) + "\r\n");
            buffer.write("\r\n");
            Iterator i$ = this.categories.entrySet().iterator();

            while(i$.hasNext()) {
               Entry category = (Entry)i$.next();
               buffer.write("####################\r\n");
               buffer.write("# " + (String)category.getKey() + " \r\n");
               buffer.write("####################\r\n\r\n");
               buffer.write((String)category.getKey() + " {\r\n");
               this.writeProperties(buffer, ((Map)category.getValue()).values());
               buffer.write("}\r\n\r\n");
            }

            buffer.close();
            e.close();
         }
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   private void writeProperties(BufferedWriter buffer, Collection props) throws IOException {
      Iterator i$ = props.iterator();

      while(i$.hasNext()) {
         Property property = (Property)i$.next();
         if(property.comment != null) {
            buffer.write("   # " + property.comment + "\r\n");
         }

         buffer.write("   " + property.name + "=" + property.value);
         buffer.write("\r\n");
      }

   }
}
