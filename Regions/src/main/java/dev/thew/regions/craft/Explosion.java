package dev.thew.regions.craft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Fluid;
import net.minecraft.server.v1_16_R3.IBlockData;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.block.Block;

import java.util.*;

public record Explosion(ExplosionDamageCalculator explosionDamageCalculator, World world, org.bukkit.World bukkitWorld,
                        double posX, double posY, double posZ, float size, boolean isFirst) {

    public List<Block> explode() {
        if (this.size < 0.1F)
            return Collections.emptyList();

        Set<BlockPosition> set = Sets.newHashSet();

        int i;
        int j;

        for (int k = 0; k < 16; ++k) {
            for (i = 0; i < 16; ++i) {
                for (j = 0; j < 16; ++j) {
                    if (k == 0 || k == 15 || i == 0 || i == 15 || j == 0 || j == 15) {

                        double d0 = (float) k / 15.0F * 2.0F - 1.0F;
                        double d1 = (float) i / 15.0F * 2.0F - 1.0F;
                        double d2 = (float) j / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double d4 = this.posX;
                        double d5 = this.posY;
                        double d6 = this.posZ;

                        for (; f > 0.0F; f -= 0.22500001F) {
                            BlockPosition blockposition = new BlockPosition(d4, d5, d6);
                            IBlockData iblockdata = this.world.getType(blockposition);
                            Fluid fluid = this.world.getFluid(blockposition);

                            Optional<Float> optional = this.explosionDamageCalculator.calculate(iblockdata, fluid);

                            if (optional.isPresent()) f -= (optional.get() + 0.3F) * 0.3F;
                            boolean find = false;

                            if (f > 0.0F && blockposition.getY() < 256 && blockposition.getY() >= 0 && !iblockdata.isAir()) {
                                if (isFirst)
                                    find = true;
                                set.add(blockposition);
                            }

                            d4 += d0 * 0.30000001192092896D;
                            d5 += d1 * 0.30000001192092896D;
                            d6 += d2 * 0.30000001192092896D;

                            if (find && isFirst) break;
                        }
                    }
                }
            }
        }

        List<BlockPosition> blockPositions = Lists.newArrayList();
        blockPositions.addAll(set);

        List<Block> blockList = Lists.newArrayList();
        for (int i1 = set.size() - 1; i1 >= 0; i1--) {
            BlockPosition cpos = blockPositions.get(i1);
            Block bblock = bukkitWorld.getBlockAt(cpos.getX(), cpos.getY(), cpos.getZ());
            if (!bblock.getType().isAir())
                blockList.add(bblock);
        }

        return blockList;
    }
}
