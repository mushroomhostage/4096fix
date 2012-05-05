package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.World;

public class Packet53BlockChange extends Packet {

   public int a;
   public int b;
   public int c;
   public int material;
   public int data;


   public Packet53BlockChange() {
      this.lowPriority = true;
   }

   public Packet53BlockChange(int par1, int par2, int par3, World par4World) {
      this.lowPriority = true;
      this.a = par1;
      this.b = par2;
      this.c = par3;
      this.material = par4World.getTypeId(par1, par2, par3);
      this.data = par4World.getData(par1, par2, par3);
   }

   public void a(DataInputStream par1DataInputStream) throws IOException {
      this.a = par1DataInputStream.readInt();
      this.b = par1DataInputStream.read();
      this.c = par1DataInputStream.readInt();
      this.material = par1DataInputStream.readInt();
      this.data = par1DataInputStream.read();
   }

   public void a(DataOutputStream par1DataOutputStream) throws IOException {
      par1DataOutputStream.writeInt(this.a);
      par1DataOutputStream.write(this.b);
      par1DataOutputStream.writeInt(this.c);
      par1DataOutputStream.writeInt(this.material);
      par1DataOutputStream.write(this.data);
   }

   public void handle(NetHandler par1NetHandler) {
      par1NetHandler.a(this);
   }

   public int a() {
      return 14;
   }
}
