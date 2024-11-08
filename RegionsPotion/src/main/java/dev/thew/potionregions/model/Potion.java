package dev.thew.potionregions.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Getter
@AllArgsConstructor
public class Potion {
    private String regionTypeId;
    private List<PotionEffect> effects;
}
