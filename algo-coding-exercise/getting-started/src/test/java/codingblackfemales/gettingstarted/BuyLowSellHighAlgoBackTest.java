package codingblackfemales.gettingstarted;

import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.marketdata.api.MarketDataProviderTest;

import codingblackfemales.orderbook.OrderBook;
import codingblackfemales.orderbook.channel.MarketDataChannel;
import codingblackfemales.orderbook.channel.OrderChannel;
import codingblackfemales.orderbook.consumer.OrderBookInboundOrderConsumer;
import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.consumer.LoggingConsumer;
import codingblackfemales.sequencer.marketdata.SequencerTestCase;
import codingblackfemales.sequencer.net.TestNetwork;
import messages.marketdata.BookUpdateEncoder;
import messages.order.MessageHeaderEncoder;

import org.junit.Test;

public class BuyLowSellHighAlgoBackTest extends SequencerTestCase {
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder encoder = new BookUpdateEncoder();

    private AlgoContainer container;

    @Override
    public Sequencer getSequencer() {
        final TestNetwork network = new TestNetwork();
        final Sequencer sequencer = new DefaultSequencer(network);

        final Actioner actioner = new Actioner(sequencer);

        final MarketDataChannel marketDataChannel = new MarketDataChannel(sequencer);
        final OrderChannel orderChannel = new OrderChannel(sequencer);
        final OrderBook book = new OrderBook(marketDataChannel, orderChannel);

        final OrderBookInboundOrderConsumer orderConsumer = new OrderBookInboundOrderConsumer(book);

        container.setLogic(new BuyLowSellHighAlgo());

        network.addConsumer(new LoggingConsumer());
        network.addConsumer(book);
        network.addConsumer(container.getOrderService());
        network.addConsumer(orderConsumer);
        network.addConsumer(container);

        return sequencer;
    }

    private MarketDataProviderTest createSampleMarketDataTick() {
        MarketDataProviderTest marketDataProviderTest = new MarketDataProviderTest();
        return marketDataProviderTest;
    }

    @Test
    public void testExampleTest() throws Exception {
        var marketDataProviderTest = new MarketDataProviderTest();
        send(createSampleMarketDataTick().should_process_market_data());

    }

}
