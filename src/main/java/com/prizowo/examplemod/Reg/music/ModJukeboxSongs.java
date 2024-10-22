package com.prizowo.examplemod.Reg.music;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.Util;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModJukeboxSongs {
    public static final ResourceKey<JukeboxSong> CUSTOM_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("examplemod", "custom_song"));
    private static ResourceKey<JukeboxSong> create(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(Examplemod.MOD_ID, name));
    }
    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, CUSTOM_SONG, JukeboxSongsReg.CUSTOM_SONG_SOUND, 180, 15);
    }
    private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, DeferredHolder<SoundEvent, SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        context.register(key, new JukeboxSong(soundEvent, Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), (float)lengthInSeconds, comparatorOutput));
    }
}
