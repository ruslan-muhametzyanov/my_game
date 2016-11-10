package com.gpro.flowergotchi.gamelogic;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ResourceManager extends AssetManager {
    public ResourceManager() {
        super();
    }

    private Texture newTexture(String name) {
        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.genMipMaps = false;
        param.format = Pixmap.Format.RGBA8888;
        this.load(name, Texture.class, param);

        this.finishLoading();
        return getTexture(name);
    }

    public Texture getTexture(String name) {
        if (this.isLoaded(name)) {
            return this.get(name, Texture.class);
        }
        return newTexture(name);
    }

    private Sprite createScaledSprite(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight, float width, float height) {
        Sprite sprite = new Sprite(texture, srcX, srcY, srcWidth, srcHeight);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        sprite.setSize(width,
                height);
        return sprite;
    }

    public Sprite createScaledSprite(Texture texture, float width, float height) {
        return createScaledSprite(texture, 0, 0, texture.getWidth(), texture.getHeight(), width, height);
    }

    public void CleanUp() {
        this.dispose();
    }
}
