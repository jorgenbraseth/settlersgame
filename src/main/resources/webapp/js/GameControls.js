class GameControls {
    constructor(canvas, gameScreen) {
        this.canvas = canvas;
        this.forwardAllMouseEventsToElement(gameScreen);
    }

    forwardAllMouseEventsToElement(gameScreen) {
        this.canvas.onclick = (e) => {
            gameScreen.onclick(e);
        };

        this.canvas.onmousemove = (e) => {
            gameScreen.onmousemove(e);
        };

        this.canvas.oncontextmenu = (e) => {
            gameScreen.oncontextmenu(e);
        };

        this.canvas.onmousewheel = (e)=> {
            gameScreen.onmousewheel(e);
        };
    }

    render() {
        var ctx = this.canvas.getContext("2d");
        ctx.clearRect(0, 0, SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS);
        ctx.translate(0, SCREEN_HEIGHT_PIXELS);

        this.renderBackground(ctx);

        ctx.translate(40,-hexRectangleHeight-10);
        this.renderButton(ctx);
        ctx.translate(hexRectangleWidth + 20,0);
        this.renderButton(ctx);
        ctx.translate(hexRectangleWidth + 20,0);
        this.renderButton(ctx);

        ctx.restore();

    }

    renderBackground(ctx) {
        ctx.save();

        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.lineTo(30, -30);
        ctx.lineTo(SCREEN_WIDTH_PIXELS - 30, -30);
        ctx.lineTo(SCREEN_WIDTH_PIXELS, 0);
        ctx.closePath();

        ctx.fillStyle = hexToRGBA(player.color, 0.3);
        ctx.fill();

        ctx.strokeStyle = player.color;
        ctx.stroke();
    }

    renderButton(ctx) {
        ctx.save();


        var poly = [
            [hexRadius, 0],
            [hexRectangleWidth, hexHeight],
            [hexRectangleWidth, hexHeight + sideLength],
            [hexRadius, hexRectangleHeight],
            [0, hexHeight + sideLength],
            [0, hexHeight]
        ];


        ctx.beginPath();
        poly.forEach(point => {
            ctx.lineTo(point[0], point[1]);
        });
        ctx.closePath();

        ctx.fillStyle = player.color;
        ctx.fill();

        ctx.strokeStyle = "white";
        ctx.stroke();


        ctx.restore();
    }
}