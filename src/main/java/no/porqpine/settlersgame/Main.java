package no.porqpine.settlersgame;

import no.porqpine.settlersgame.api.EventServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

public class Main {

    public static void main(String[] args) throws Exception {

        GameLogic gameState = GameLogic.GAME;
        Thread gameLoop = new Thread(gameState);
        gameLoop.start();

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");



        // Add a websocket to a specific path spec
        EventServlet gameStateServlet = new EventServlet();
        ServletHolder holderEvents = new ServletHolder("ws-events", gameStateServlet);
        context.addServlet(holderEvents, "/game-state/*");

        context.setBaseResource(Resource.newResource(Main.class.getClassLoader().getResource("webapp")));
        DefaultServlet defaultServlet = new DefaultServlet();
        context.addServlet(new ServletHolder("default",defaultServlet),"/");

        server.setHandler(context);

        try
        {
            server.start();
            server.dump(System.err);
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            gameState.stop();
            try {
                server.join();
                gameLoop.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

    }
}
