package jax.cobrautilities.proxy;

import jax.cobrautilities.init.ModItems;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRenders() {
		ModItems.registerRenders();
	}
}
