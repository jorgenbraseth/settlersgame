const TILE_SIZE = 25;

const COLORS = {
    "FOREST": "#007700", "MOUNTAIN": "#666666", "WATER": "blue", "PASTURE": "#66aa00", "DESERT": "#aa8800"
};

const IMAGE_MAP = {
    HOME: new PreloadedImage("img/home.png", 256, 224),
    EMITTER: new PreloadedImage("img/emitter.png", 214, 199),
    SIPHON: new PreloadedImage("img/siphon.png", 256, 170),
    WALL: new PreloadedImage("img/wall.png", 153, 153),
};