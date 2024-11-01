package com.prizowo.examplemod.Reg.music;
import com.prizowo.examplemod.Examplemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
public class JukeboxSongsReg {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Examplemod.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Examplemod.MODID);
    public static final DeferredRegister<JukeboxSong> JUKEBOX_SONGS = DeferredRegister.create(Registries.JUKEBOX_SONG, Examplemod.MODID);
    public static final DeferredHolder<SoundEvent, SoundEvent> CUSTOM_SONG_SOUND = SOUND_EVENTS.register("music_disc.custom_song",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "music_disc.custom_song")));
    public static final DeferredHolder<JukeboxSong, JukeboxSong> CUSTOM_SONG = JUKEBOX_SONGS.register("custom_song",
            () -> new JukeboxSong(CUSTOM_SONG_SOUND, Component.translatable("item.examplemod.music_card"), 200, 13));
    public static final DeferredHolder<Item, Item> MUSIC_CARD = ITEMS.register("music_card", MusicCardItem::new);
}
