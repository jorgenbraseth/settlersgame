class Tile {

    constructor(data) {
        this.data = data;
        this.x = data.x * TILE_SIZE;
        this.y = data.y * TILE_SIZE;
        this.borderWidth = 1;
    }
    
    render(context){
        context.save();
        context.translate(this.x, this.y);

        context.fillStyle = COLORS[this.data.type] || "#ff00ff";
        context.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
        context.fillStyle = "rgba(0,0,0,0.3)";
        context.fillRect(0, 0, TILE_SIZE, TILE_SIZE);

        context.fillStyle = COLORS[this.data.type] || "#ff00ff";
        context.fillRect(this.borderWidth, this.borderWidth, TILE_SIZE-this.borderWidth*2, TILE_SIZE-this.borderWidth*2);

        context.fillStyle = "rgba(250,250,250,0.75)";
        context.beginPath();
        context.moveTo(TILE_SIZE*0.8,TILE_SIZE*0.8);
        context.arc(TILE_SIZE*0.8,TILE_SIZE*0.8,TILE_SIZE/10,0, 2 * Math.PI * (1-this.data.production), false);
        context.closePath();
        context.fill();
        
        context.restore();
    }
}