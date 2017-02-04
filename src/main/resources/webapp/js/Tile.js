class Tile {

    constructor(data) {
        this.data = data;
        this.x = data.x * TILE_SIZE;
        this.y = data.y * TILE_SIZE;
        this.borderWidth = 1;
    }

    render(context) {
        context.save();
        context.translate(this.x, this.y);

        context.fillStyle = COLORS[this.data.type] || "#ff00ff";
        context.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
        context.fillStyle = "rgba(0,0,0,0.3)";
        context.fillRect(0, 0, TILE_SIZE, TILE_SIZE);

        context.fillStyle = COLORS[this.data.type] || "#ff00ff";
        context.fillRect(this.borderWidth, this.borderWidth, TILE_SIZE - this.borderWidth * 2, TILE_SIZE - this.borderWidth * 2);

        context.save();
        context.fillStyle = "rgba(250,250,250,0.75)";
        context.translate(TILE_SIZE * 0.8, TILE_SIZE * 0.8);
        context.beginPath();
        context.arc(0, 0, TILE_SIZE / 10, 0, 2 * Math.PI * (this.data.production), false);
        context.closePath();
        context.fill();
        context.restore();

        if (this.data.resourceOn) {
            var fontSize = 24 - 2 * Math.abs(7 - this.data.resourceOn);
            context.font = fontSize + "px arial";
            var textWidth = context.measureText(this.data.resourceOn).width;
            context.translate(TILE_SIZE / 2, TILE_SIZE / 2);
            context.translate(-textWidth / 2, fontSize / 3);
            context.fillStyle = "rgba(0,0,0,0.7)";
            context.fillText(this.data.resourceOn, 0, 0);
            context.translate(-1, -1);
            context.fillStyle = "rgba(255,255,255,1)";
            context.fillText(this.data.resourceOn, 0, 0);
        }

        context.restore();
    }
}