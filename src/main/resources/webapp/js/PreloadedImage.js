class PreloadedImage {
    constructor(src, hotspotX = 0, hotspotY = 0) {
        this.loaded = false;
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;

        this.img = new Image;
        this.img.onload = () => {
            console.log("Loaded <" + src + ">");
            this.loaded = true;
        }
        this.img.src = src;
    }

    render(context, scaleToWidth = TILE_SIZE) {
        var scale = scaleToWidth / this.img.width;
        context.save();
        context.scale(scale, scale);
        context.translate(-this.hotspotX, -this.hotspotY);
        context.drawImage(this.img, 0, 0);
        context.restore();
    }
}