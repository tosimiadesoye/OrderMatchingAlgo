package codingblackfemales.gettingstarted;


import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.marketdata.api.MarketDataEncoder;
import codingblackfemales.marketdata.api.MarketDataMessage;
import codingblackfemales.marketdata.api.MarketDataProvider;
import codingblackfemales.marketdata.impl.SimpleFileMarketDataProvider;
import codingblackfemales.orderbook.OrderBook;
import codingblackfemales.orderbook.channel.MarketDataChannel;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.consumer.OrderBookInboundOrderConsumer;
import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.consumer.LoggingConsumer;
import codingblackfemales.sequencer.marketdata.SequencerTestCase;
import codingblackfemales.sequencer.net.TestNetwork;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;


import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

public class BuyLowSellHighAlgoBackTest extends SequencerTestCase {
    private MarketDataService marketDataService;
    private MarketDataProvider provider;
    private MarketDataEncoder encoder;


    private AlgoContainer container;

    @Override
    public Sequencer getSequencer() {
        final TestNetwork network = new TestNetwork();
        final Sequencer sequencer = new DefaultSequencer(network);

        final RunTrigger runTrigger = new RunTrigger();
        final Actioner actioner = new Actioner(sequencer);

        final MarketDataChannel marketDataChannel = new MarketDataChannel(sequencer);
        final OrderChannel orderChannel = new OrderChannel(sequencer);
        final OrderBook book = new OrderBook(marketDataChannel, orderChannel);

        final OrderBookInboundOrderConsumer orderConsumer = new OrderBookInboundOrderConsumer(book);

        provider = new SimpleFileMarketDataProvider("../algo/src/test/resources/marketdata.json");
        encoder = new MarketDataEncoder();
        marketDataService = new MarketDataService(runTrigger);
        container = new AlgoContainer(marketDataService, new OrderService(runTrigger), runTrigger, actioner);

        //set BuyLowSellHighAlgo logic
        container.setLogic(new BuyLowSellHighAlgo());

        network.addConsumer(new LoggingConsumer());
        network.addConsumer(book);
        network.addConsumer(container.getMarketDataService());
        network.addConsumer(container.getOrderService());
        network.addConsumer(orderConsumer);
        network.addConsumer(container);

        return sequencer;

    }

    private UnsafeBuffer createSampleMarketDataTick() {
        MarketDataMessage marketDataMessage;
        while ((marketDataMessage = provider.poll()) != null) {
            UnsafeBuffer encoded = encoder.encode(marketDataMessage);
            marketDataService.onMessage(encoded);
            return encoded;
        }
        return null;
    }

    @Test
    public void testExampleTest() throws Exception {
        send((createSampleMarketDataTick()));
    }

}
