class Edge {
    constructor(e) {
        this.data = e;
    }

    render(context) {
    context.save();
    context.translate(this.data.x*100,this.data.y*100);
    context.fillStyle = "rgba(0,0,0,0.8)";
    context.strokeStyle = "rgba(250,250,250,0.75)";
    if("VERTICAL" === this.data.orientation){
        context.fillRect(-4,25,8,50);
        context.strokeRect(-4,25,8,50);
    }else{
        context.fillRect(25,-4,50,8);
        context.strokeRect(25,-4,50,8);
    }
    context.restore();
}
}