class PlayerOverlay {
    constructor(canvas, SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS){
        this.canvas = canvas;
        this.SCREEN_WIDTH_PIXELS = SCREEN_WIDTH_PIXELS;
        this.SCREEN_HEIGHT_PIXELS = SCREEN_HEIGHT_PIXELS;
    }
    
    renderContext() {
        return this.canvas.getContext("2d");
    }
    
    setPlayers(players){
        this.players = players;
    }

    render() {
        var ctx = this.renderContext();
        ctx.clearRect(0, 0, SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS);
        ctx.save();
        ctx.font = "16pt arial";
        ctx.fillStyle = "rgba(0,0,0,0.7)";
        ctx.strokeStyle = "white";
        var width = 50 + Math.max(... this.players.map(p => ctx.measureText(p.name).width + ctx.measureText(p.resources.resource).width));
        var height = this.players.length * 20 + 15;
        ctx.fillRect(-1, -1, width + 1, height + 1);
        ctx.strokeRect(-1, -1, width + 1, height + 1);

        ctx.translate(20, 25);

        this.players.forEach(p => {
            ctx.fillStyle = p.color;
            ctx.fillText(p.name, 0, 0);

            ctx.translate(ctx.measureText(p.name).width + 10, 0);
            ctx.fillStyle = "white";
            var resourceText = "$"+p.resources.resource;
            ctx.fillText(resourceText, 0, 0);
            ctx.translate(-(ctx.measureText(p.name).width + 10), 20);
        });
        ctx.restore();
    }
}