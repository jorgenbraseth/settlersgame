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

        Game gameState = GameList.createGame("gameId");
        Thread gameLoop = new Thread(gameState);
        gameLoop.start();

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        int port = 8080;
        String envPort = System.getenv("PORT");
        if(envPort != null){
            port = Integer.parseInt(envPort);
        }
        connector.setPort(port);
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
        server.setStopAtShutdown(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            gameState.stop();
            try {
                gameLoop.join();
                server.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Game stopped.");
        }));

        try
        {
            server.start();
            server.dump(System.err);
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }



    }
}
