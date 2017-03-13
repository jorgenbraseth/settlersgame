class PlayerOverlay {

    render(ctx, players) {

        ctx.save();
        ctx.font = "16pt arial";
        ctx.fillStyle = "rgba(0,0,0,0.7)";
        ctx.strokeStyle = "white";
        var width = 50 + Math.max(... players.map(p => ctx.measureText(p.name).width + ctx.measureText(p.resources.resource).width));
        var height = players.length * 20 + 15;
        ctx.fillRect(-1, -1, width + 1, height + 1);
        ctx.strokeRect(-1, -1, width + 1, height + 1);

        ctx.translate(20, 25);

        players.forEach(p => {
            ctx.fillStyle = p.color;
            ctx.fillText(p.name, 0, 0);

            ctx.translate(ctx.measureText(p.name).width + 10, 0);
            ctx.fillStyle = "white";
            ctx.fillText(p.resources.resource, 0, 0);
            ctx.translate(-(ctx.measureText(p.name).width + 10), 20);
        });
        ctx.restore();
    }
}