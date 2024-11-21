package com.prizowo.examplemod.items;

import com.prizowo.examplemod.entity.SuperFireworkEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.core.component.DataComponents;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class SuperFireworkItem extends Item {
    public SuperFireworkItem(Properties properties) {
        super(properties.stacksTo(64));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
            List<FireworkExplosion> explosions = new ArrayList<>();
            
            // 第一层爆炸：大型球形
            explosions.add(new FireworkExplosion(
                FireworkExplosion.Shape.LARGE_BALL,
                IntArrayList.of(0xFF0000, 0xFFFF00, 0x00FF00),  // 红、黄、绿
                IntArrayList.of(0xFFFFFF),  // 白色淡出
                true,   // 尾迹
                true    // 闪烁
            ));
            
            // 第二层爆炸：多个小球形
            for (int i = 0; i < 4; i++) {
                explosions.add(new FireworkExplosion(
                    FireworkExplosion.Shape.SMALL_BALL,
                    IntArrayList.of(0x00FFFF, 0x0000FF),  // 青色和蓝色
                    IntArrayList.of(0x87CEEB),  // 天蓝色淡出
                    true,
                    false
                ));
            }
            
            // 第三层爆炸：星形
            explosions.add(new FireworkExplosion(
                FireworkExplosion.Shape.STAR,
                IntArrayList.of(0xFFD700, 0xFFA500, 0xFF4500),  // 金色、橙色、红橙色
                IntArrayList.of(0xFFFF00),  // 黄色淡出
                true,
                true
            ));
            
            // 第四层爆炸：苦力怕形状
            explosions.add(new FireworkExplosion(
                FireworkExplosion.Shape.CREEPER,
                IntArrayList.of(0x32CD32, 0x228B22, 0x006400),  // 不同深浅的绿色
                IntArrayList.of(0x98FB98),  // 淡绿色淡出
                true,
                true
            ));
            
            // 第五层爆炸：爆裂形
            explosions.add(new FireworkExplosion(
                FireworkExplosion.Shape.BURST,
                IntArrayList.of(0xFF1493, 0x9400D3, 0x8B008B),  // 粉色和紫色
                IntArrayList.of(0xDDA0DD),  // 淡紫色淡出
                true,
                true
            ));
            
            // 第六层爆炸：另一个大球形
            explosions.add(new FireworkExplosion(
                FireworkExplosion.Shape.LARGE_BALL,
                IntArrayList.of(0xFFFFFF, 0xC0C0C0, 0x808080),  // 白色、银色、灰色
                IntArrayList.of(0xF0F8FF),  // 淡蓝色淡出
                true,
                true
            ));
            
            // 第七层爆炸：多个星形组合
            for (int i = 0; i < 3; i++) {
                explosions.add(new FireworkExplosion(
                    FireworkExplosion.Shape.STAR,
                    IntArrayList.of(0xE6E6FA, 0xDDA0DD, 0xDA70D6),  // 淡紫色系
                    IntArrayList.of(0xD8BFD8),  // 紫罗兰淡出
                    true,
                    false
                ));
            }
            
            // 创建烟花数据，设置较长的飞行时间
            Fireworks fireworks = new Fireworks(2, explosions);
            fireworkStack.set(DataComponents.FIREWORKS, fireworks);
            
            // 创建超级烟花实体
            SuperFireworkEntity firework = new SuperFireworkEntity(
                level,
                player.getX(),
                player.getY() + 0.5,
                player.getZ(),
                fireworkStack
            );
            
            // 设置更高的初始速度
            firework.setDeltaMovement(
                level.random.triangle(0.0, 0.002297),
                0.25,
                level.random.triangle(0.0, 0.002297)
            );
            
            level.addFreshEntity(firework);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
} 