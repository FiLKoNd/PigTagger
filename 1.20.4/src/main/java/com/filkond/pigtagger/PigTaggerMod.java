package com.filkond.pigtagger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class PigTaggerMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PigConfig.load(FabricLoader.getInstance().getConfigDir());
        PigTagger.updateTiers();
    }
}
