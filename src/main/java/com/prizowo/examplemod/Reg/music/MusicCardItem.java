package com.prizowo.examplemod.Reg.music;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class MusicCardItem extends Item {
    public MusicCardItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON)
                .component(DataComponents.JUKEBOX_PLAYABLE,
                        new JukeboxPlayable(new EitherHolder<>(ModJukeboxSongs.CUSTOM_SONG), true))
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.examplemod.music_card.desc"));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
