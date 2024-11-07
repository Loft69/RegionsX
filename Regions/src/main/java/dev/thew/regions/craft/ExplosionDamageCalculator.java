package dev.thew.regions.craft;

import net.minecraft.server.v1_16_R3.*;

import java.util.Optional;

public class ExplosionDamageCalculator {

    public Optional<Float> calculate(IBlockData var3, Fluid var4) {
        return var3.isAir() && var4.isEmpty() ? Optional.empty() : Optional.of(var4.i());
    }
}
