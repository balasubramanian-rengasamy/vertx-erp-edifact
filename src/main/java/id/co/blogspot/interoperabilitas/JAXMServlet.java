package id.co.blogspot.interoperabilitas;

import io.vertx.core.Handler;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import org.milyn.edi.unedifact.d01b.D01BInterchangeFactory;
import org.milyn.edi.unedifact.d01b.ORDERS.Orders;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactInterchange41;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactMessage41;

import java.io.ByteArrayInputStream;

public class JAXMServlet  {
	public static void main(String args[])throws Exception{
        D01BInterchangeFactory factory = D01BInterchangeFactory.getInstance();
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        router.route("/*").handler(BodyHandler.create());
        router.route().consumes("application/EDIFACT").blockingHandler(routingContext -> {
            try {
                String body = routingContext.getBodyAsString();
                routingContext.response().setStatusCode(202).end();
                UNEdifactInterchange41 amplop = (UNEdifactInterchange41) factory.fromUNEdifact(new ByteArrayInputStream(body.getBytes()));
                for (UNEdifactMessage41 surat : amplop.getMessages()) {
                    System.out.println("\tNama topik: " + surat.getMessageHeader().getMessageIdentifier().getId());
                    Orders pesananPembelian = (Orders) surat.getMessage();
                    System.out.println("\tNama mitra " + pesananPembelian.getSegmentGroup2().get(0).getNameAndAddress().getPartyName().getPartyName1());
                    System.out.println("\tJumlah SKU " + pesananPembelian.getSegmentGroup28().size());
                    System.out.println("\tDeskripsi SKU " + pesananPembelian.getSegmentGroup28().get(0).getItemDescription().get(0).getItemDescription().getItemDescription1());
                    System.out.println("\tJumlah " + pesananPembelian.getSegmentGroup28().get(0).getQuantity().get(0).getQuantityDetails().getQuantity());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(5080);
	}
}
