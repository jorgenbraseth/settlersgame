class GameControls {
    constructor(canvas, gameScreen) {
        this.canvas = canvas;
        this.ctx = this.canvas.getContext("2d");
        this.forwardAllMouseEventsToElement(gameScreen);

        this.buttonCenter = {
            B1: [60, -hexRectangleHeight / 2 - 10],
            B2: [60 + hexRectangleWidth + 10, -hexRectangleHeight / 2 - 10],
            B3: [60 + hexRectangleWidth * 2 + 20, -hexRectangleHeight / 2 - 10],
        };
        this.polys = {
            BACKGROUND: [
                [0, 0],
                [30, -30],
                [SCREEN_WIDTH_PIXELS - 30, -30],
                [SCREEN_WIDTH_PIXELS, 0]
            ],
            B1: [
                [hexRadius, 0],
                [hexRectangleWidth, hexHeight],
                [hexRectangleWidth, hexHeight + sideLength],
                [hexRadius, hexRectangleHeight],
                [0, hexHeight + sideLength],
                [0, hexHeight]
            ],
            B2: [
                [hexRadius, 0],
                [hexRectangleWidth, hexHeight],
                [hexRectangleWidth, hexHeight + sideLength],
                [hexRadius, hexRectangleHeight],
                [0, hexHeight + sideLength],
                [0, hexHeight]
            ],
            B3: [
                [hexRadius, 0],
                [hexRectangleWidth, hexHeight],
                [hexRectangleWidth, hexHeight + sideLength],
                [hexRadius, hexRectangleHeight],
                [0, hexHeight + sideLength],
                [0, hexHeight]
            ]
        }
    }

    forwardAllMouseEventsToElement(gameScreen) {
        this.canvas.onclick = (e) => {
            if (this.nothingAt(e.offsetX, e.offsetY)) {
                gameScreen.onclick(e);
            } else {
                console.log(this.getButtonAt(e.offsetX, e.offsetY));
            }
        };

        this.canvas.onmousemove = (e) => {
            this.hoverOnButton = null;
            if (this.nothingAt(e.offsetX, e.offsetY)) {
                gameScreen.onmousemove(e);
            } else {
                this.hoverOnButton = this.getButtonAt(e.offsetX, e.offsetY);
            }
        };

        this.canvas.oncontextmenu = (e) => {
            e.preventDefault();
            if (this.nothingAt(e.offsetX, e.offsetY)) {
                gameScreen.oncontextmenu(e);
            }
        };

        this.canvas.onmousewheel = (e)=> {
            gameScreen.onmousewheel(e);
        };
    }

    nothingAt(unadjustedX, unadjustedY) {
        var x = unadjustedX - 0;
        var y = unadjustedY - SCREEN_HEIGHT_PIXELS;
        var button = this.getButtonAt(unadjustedX, unadjustedY);
        if (button || isInside([x, y], this.polys.BACKGROUND)) {
            return false;
        } else {
            return true;
        }
    }

    getButtonAt(unadjustedX, unadjustedY) {
        var x = unadjustedX - 0;
        var y = unadjustedY - SCREEN_HEIGHT_PIXELS;
        if (isInside([x - this.buttonCenter.B1[0] + hexRectangleWidth / 2, y - this.buttonCenter.B1[1] + hexRectangleHeight / 2], this.polys.B1)) {
            return "B1";
        } else if (isInside([x - this.buttonCenter.B2[0] + hexRectangleWidth / 2, y - this.buttonCenter.B2[1] + hexRectangleHeight / 2], this.polys.B2)) {
            return "B2";
        } else if (isInside([x - this.buttonCenter.B3[0] + hexRectangleWidth / 2, y - this.buttonCenter.B3[1] + hexRectangleHeight / 2], this.polys.B3)) {
            return "B3";
        } else {
            return null;
        }
    }

    render() {
        var ctx = this.ctx;

        ctx.save();
        ctx.clearRect(0, 0, SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS);
        ctx.translate(0, SCREEN_HEIGHT_PIXELS);

        this.renderBackground(ctx);

        this.renderButton(ctx, "B1", IMAGE_MAP.EMITTER);
        this.renderButton(ctx, "B2", IMAGE_MAP.SIPHON);
        this.renderButton(ctx, "B3", IMAGE_MAP.WALL);

        ctx.restore();

    }

    renderBackground(ctx) {
        ctx.save();


        ctx.beginPath();
        this.polys.BACKGROUND.forEach(point => {
            ctx.lineTo(point[0], point[1]);
        });
        ctx.closePath();

        ctx.fillStyle = hexToRGBA(player.color, 0.3);
        ctx.fill();

        ctx.strokeStyle = player.color;
        ctx.stroke();
        ctx.restore();
    }

    renderButton(ctx, button, icon) {
        var poly = this.polys[button];
        var center = this.buttonCenter[button];
        ctx.save();
        ctx.translate(center[0] - hexRectangleWidth / 2, center[1] - hexRectangleHeight / 2);

        ctx.beginPath();
        poly.forEach(point => {
            ctx.lineTo(point[0], point[1]);
        });
        ctx.closePath();

        ctx.fillStyle = "black";
        ctx.fill();

        ctx.fillStyle = this.hoverOnButton == button ? player.color : hexToRGBA(player.color, 0.4);

        ctx.fill();

        ctx.strokeStyle = "white";
        ctx.lineWidth = 2;
        ctx.stroke();

        if (icon) {
            ctx.save();
            ctx.translate(hexRectangleWidth / 2, hexRectangleHeight / 2);
            icon.render(ctx);
            ctx.restore();
        }

        ctx.restore();
    }
}