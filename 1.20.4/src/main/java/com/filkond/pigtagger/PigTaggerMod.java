package com.filkond.pigtagger;

import net.fabricmc.api.ClientModInitializer;

public class PigTaggerMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PigTagger.updateTiers();
    }
}
