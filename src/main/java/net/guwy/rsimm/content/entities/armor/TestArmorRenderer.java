package net.guwy.rsimm.content.entities.armor;

import net.guwy.rsimm.content.items.TestArmorItem;
import net.guwy.rsimm.content.items.armors.Mark1ArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class TestArmorRenderer extends GeoArmorRenderer<TestArmorItem> {
    public TestArmorRenderer() {
        super(new TestArmorModel());

        this.headBone = "armorHead";
        this.bodyBone = "armorBody";
        this.rightArmBone = "armorRightArm";
        this.leftArmBone = "armorLeftArm";
        this.rightLegBone = "armorLeftLeg";
        this.leftLegBone = "armorRightLeg";
        this.rightBootBone = "armorLeftBoot";
        this.leftBootBone = "armorRightBoot";
    }
}
