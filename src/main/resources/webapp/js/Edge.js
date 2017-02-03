class Edge {
    constructor(e) {
        this.data = e;

        if ("VERTICAL" === this.data.orientation) {
            this.width = 8;
            this.height = 50;
            this.x = e.x * TILE_SIZE - this.width / 2;
            this.y = e.y * TILE_SIZE + (TILE_SIZE - this.height) / 2;
        } else {
            this.width = 50;
            this.height = 8;
            this.x = e.x * TILE_SIZE + (TILE_SIZE - this.width) / 2;
            this.y = (e.y * TILE_SIZE) - this.height / 2;
        }
    }

    render(context) {
        context.save();
        context.translate(this.x, this.y);

        context.fillStyle = "rgba(0,0,0,0.9)";
        context.strokeStyle = "rgba(250,250,250,0.75)";

        if(this.data.owner){
            var owningPlayer = gameData.players.filter(p => p.name == this.data.owner)[0];
            console.log(owningPlayer);
            context.fillStyle = owningPlayer.color;
        }

        if (this.isMouseOver) {
            context.fillStyle = "rgba(0,150,150,0.9)";
        }

        context.fillRect(0, 0, this.width, this.height);
        context.strokeRect(0, 0, this.width, this.height);


        context.restore();
    }

    mouseIsOver() {
        this.isMouseOver = true;
    }
    mouseIsNotOver() {
        this.isMouseOver = false;
    }

    containsPoint(x, y) {
        return (x >= this.x && x <= (this.x + this.width)) && (y >= this.y && y <= (this.y + this.height));
    }
}