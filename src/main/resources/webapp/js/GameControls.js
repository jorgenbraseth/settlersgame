class GameControls {
    constructor(canvas, gameScreen, playerName) {
        this.playerName = playerName;
        this.canvas = canvas;
        this.buildMode = null;
        this.ctx = this.canvas.getContext("2d");
        this.forwardAllMouseEventsToElement(gameScreen);

        this.buttonCenter = {
            RELAY: [60, -hexRectangleHeight / 2 - 10],
            SIPHON: [60 + hexRectangleWidth + 10, -hexRectangleHeight / 2 - 10],
            WALL: [60 + hexRectangleWidth * 2 + 20, -hexRectangleHeight / 2 - 10],
        };
        this.polys = {
            BACKGROUND: [
                [0, 0],
                [30, -30],
                [SCREEN_WIDTH_PIXELS - 30, -30],
                [SCREEN_WIDTH_PIXELS, 0]
            ],
            RELAY: [
                [hexRadius, 0],
                [hexRectangleWidth, hexHeight],
                [hexRectangleWidth, hexHeight + sideLength],
                [hexRadius, hexRectangleHeight],
                [0, hexHeight + sideLength],
                [0, hexHeight]
            ],
            SIPHON: [
                [hexRadius, 0],
                [hexRectangleWidth, hexHeight],
                [hexRectangleWidth, hexHeight + sideLength],
                [hexRadius, hexRectangleHeight],
                [0, hexHeight + sideLength],
                [0, hexHeight]
            ],
            WALL: [
                [hexRadius, 0],
                [hexRectangleWidth, hexHeight],
                [hexRectangleWidth, hexHeight + sideLength],
                [hexRadius, hexRectangleHeight],
                [0, hexHeight + sideLength],
                [0, hexHeight]
            ]
        }
    }


    chooseBuildMode(mode){
        //overwritten by external for now. TODO: move GameView into its own class and make these to communicate.
    }

    forwardAllMouseEventsToElement(gameScreen) {
        this.canvas.onclick = (e) => {
            if (this.nothingAt(e.offsetX, e.offsetY)) {
                gameScreen.onclick(e);
            } else {
                var buttonClicked = this.getButtonAt(e.offsetX, e.offsetY);
                this.buildMode = buttonClicked;
                this.chooseBuildMode(buttonClicked);
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
            this.buildMode = null;
            this.chooseBuildMode(null);
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
        if (isInside([x - this.buttonCenter.RELAY[0] + hexRectangleWidth / 2, y - this.buttonCenter.RELAY[1] + hexRectangleHeight / 2], this.polys.RELAY)) {
            return "RELAY";
        } else if (isInside([x - this.buttonCenter.SIPHON[0] + hexRectangleWidth / 2, y - this.buttonCenter.SIPHON[1] + hexRectangleHeight / 2], this.polys.SIPHON)) {
            return "SIPHON";
        } else if (isInside([x - this.buttonCenter.WALL[0] + hexRectangleWidth / 2, y - this.buttonCenter.WALL[1] + hexRectangleHeight / 2], this.polys.WALL)) {
            return "WALL";
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

        this.renderButton(ctx, "RELAY", TILE_ICONS.RELAY);
        this.renderButton(ctx, "SIPHON", TILE_ICONS.SIPHON);
        this.renderButton(ctx, "WALL", TILE_ICONS.WALL);

        ctx.restore();

    }

    setPlayerInfo(playerInfo){
        this.playerInfo = playerInfo;
    }

    renderBackground(ctx) {
        ctx.save();

        ctx.beginPath();
        this.polys.BACKGROUND.forEach(point => {
            ctx.lineTo(point[0], point[1]);
        });
        ctx.closePath();

        ctx.fillStyle = hexToRGBA(this.playerInfo.color, 0.1);
        ctx.fill();

        ctx.strokeStyle = this.playerInfo.color;
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

        ctx.fillStyle = this.hoverOnButton == button || this.buildMode == button ? hexToRGBA(this.playerInfo.color, 0.4) : "black";

        ctx.fill();

        ctx.strokeStyle = "white";
        ctx.lineWidth = 2;
        ctx.stroke();

        if (icon) {
            ctx.save();
            var fontSize = TILE_SIZE*0.8;
            ctx.font = `${fontSize}pt FontAwesome`;
            ctx.fillStyle = this.playerInfo.color || "black";
            ctx.fillText(icon, hexRectangleWidth/2-ctx.measureText(icon).width / 2, hexRectangleHeight/2+fontSize/2);
            ctx.restore();
        }

        ctx.restore();
    }
}