package jax.cobrautilities.init;

import jax.cobrautilities.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

	public static Item copper_ingot;
	
	public static void init() {
		copper_ingot = new Item().setUnlocalizedName("copper_ingot").setCreativeTab(CreativeTabs.tabMaterials);
		
	}
	
	public static void register() {
		
		registerItem(copper_ingot);
		
	}
	
	public static void registerRenders() {
		registerRender(copper_ingot);
	}
	
	public static void registerItem(Item item) { 
		
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
		System.out.println("registered item: " + item.getUnlocalizedName().substring(5));
	}
	
	public static void registerRender(Item item) { 
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
	
}
