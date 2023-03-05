package jax.cobrautilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jax.cobrautilities.init.ChatListener;
import jax.cobrautilities.init.ModItems;
import jax.cobrautilities.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class CobraUtilities {
		
	@SidedProxy(serverSide = Reference.SERVER_PROXY_CLASS, clientSide = Reference.CLIENT_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@Mod.Instance("CU")
	public static CobraUtilities instance;
	
	@EventHandler()
	public static void preInit(FMLPreInitializationEvent event) { 
		proxy.registerEventHandlers();
		ModItems.init();
		ModItems.register();
	}
	
	@EventHandler()
	public static void init(FMLInitializationEvent event) throws IOException { 
		URL dictURL = new URL("https://raw.githubusercontent.com/CobraUtilities/blabla/main/moby.txt");
		URL knownWordsURL = new URL("https://raw.githubusercontent.com/CobraUtilities/blabla/main/KnownWords.txt");
		
		proxy.registerRenders();
		
		String mcdir = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
		Path path = Paths.get(mcdir + "/config/cu");
		if (!Files.exists(path)) {
			Files.createDirectory(path);
			System.out.println("directory for dictionary made");
		} else {
			System.out.println("dictionary directory already exists");
		}
		URLConnection connection = dictURL.openConnection();
		URLConnection connection2 = knownWordsURL.openConnection();
		InputStream inputStream = connection.getInputStream();
		InputStream inputStream2 = connection2.getInputStream();
		FileOutputStream outputStream = new FileOutputStream(new File(mcdir + "/config/cu/moby.txt"));
		FileOutputStream outputStream2 = new FileOutputStream(new File(mcdir + "/config/cu/KnownWords.txt"));
		

		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
		    outputStream.write(buffer, 0, bytesRead);
		}
		
		byte[] buffer2 = new byte[4096];
		int bytesRead2;
		while ((bytesRead2 = inputStream2.read(buffer2)) != -1) {
			outputStream2.write(buffer2, 0, bytesRead2);
		}

		inputStream.close();
		inputStream2.close();
		outputStream.close();
		outputStream2.close();
		System.out.println("moby.txt made at: " + mcdir + "/config/cu/moby.txt");
		System.out.println("KnownWords.txt made at: " + mcdir + "/config/cu/knownwords.txt");
		
		
	}
	
	@EventHandler()
	public static void postInit(FMLPostInitializationEvent event) { 
		
	}
	
}
