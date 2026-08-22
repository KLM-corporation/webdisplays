package net.montoyo.wd.client.renderers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class ScreenModelLoader implements IGeometryLoader<ScreenModelLoader.ScreenModelGeometry> {
    public static final ResourceLocation SCREEN_LOADER = ResourceLocation.fromNamespaceAndPath("webdisplays", "screen_loader");

    private static final int SCREEN_TEXTURE_COUNT = 16;

    @Override
    public ScreenModelGeometry read(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new ScreenModelGeometry();
    }

    public static class ScreenModelGeometry implements IUnbakedGeometry<ScreenModelGeometry> {
        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
            TextureAtlasSprite[] textures = new TextureAtlasSprite[SCREEN_TEXTURE_COUNT];

            // Resolve the model's declared texture slots through its baking context. Passing
            // independently-created Materials to spriteGetter can yield its default/unit
            // sprite instead, whose UVs cover the complete block atlas.
            for (int i = 0; i < textures.length; i++) {
                textures[i] = spriteGetter.apply(context.getMaterial("screen" + i));
            }

            return new ScreenBaker(modelState, textures, overrides, context.getTransforms());
        }
    }
}
