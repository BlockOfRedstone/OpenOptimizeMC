package com.fazziclay.openoptimizemc.config;

public enum RendererType {
    VANILLA("feature.playerRenderer.vanilla.composed", "feature.playerRenderer.vanilla.description"),
    DIRT_RENDERER("feature.playerRenderer.dirtRenderer.composed", "feature.playerRenderer.dirtRenderer.description"),
    PRIMITIVE_CUBE("feature.playerRenderer.primitiveCube.composed", "feature.playerRenderer.primitiveCube.description");

    private final String name;
    private final String description;

    RendererType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public RendererType next() {
        return switch (this) {
            case VANILLA -> DIRT_RENDERER;
            case DIRT_RENDERER -> PRIMITIVE_CUBE;
            case PRIMITIVE_CUBE -> VANILLA;
        };
    }
}
