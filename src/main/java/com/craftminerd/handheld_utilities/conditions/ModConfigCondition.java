package com.craftminerd.handheld_utilities.conditions;

import com.craftminerd.handheld_utilities.HandheldUtilities;
import com.craftminerd.handheld_utilities.config.ModCommonConfigs;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.List;

public class ModConfigCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation(HandheldUtilities.MOD_ID, "config_true");
    private final ForgeConfigSpec.BooleanValue configBool;

    public ModConfigCondition(ForgeConfigSpec.BooleanValue configBool) {
        this.configBool = configBool;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        return this.configBool.get();
    }

    public static class Serializer implements IConditionSerializer<ModConfigCondition> {
        public static final ModConfigCondition.Serializer INSTANCE = new ModConfigCondition.Serializer();

        @Override
        public void write(JsonObject json, ModConfigCondition value) {
            List<String> configPath = value.configBool.getPath();
            String pathString = "";
            for (int i = 0; i < configPath.size(); i++) {
                pathString = pathString.concat(configPath.get(i));
                if (i < configPath.size() - 1) pathString = pathString.concat(".");
            }
            json.addProperty("value", pathString);
        }

        @Override
        public ModConfigCondition read(JsonObject json) {
            String pathString = GsonHelper.getAsString(json, "value");
            ForgeConfigSpec.BooleanValue configBool = ModCommonConfigs.SPEC.getValues().get(pathString);

            return new ModConfigCondition(configBool);
        }

        @Override
        public ResourceLocation getID() {
            return ModConfigCondition.NAME;
        }
    }
}
