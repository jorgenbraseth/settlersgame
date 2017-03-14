class GameControls {
    constructor(canvas, gameScreen){
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
}