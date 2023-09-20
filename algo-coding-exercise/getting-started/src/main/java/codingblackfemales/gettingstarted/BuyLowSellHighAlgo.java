package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuyLowSellHighAlgo implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(BuyLowSellHighAlgo.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel bidLevel = state.getBidAt(0);
        final AskLevel askLevel = state.getAskAt(0);
        final var activeOrders = state.getActiveChildOrders();
        var totalOrderCount = state.getChildOrders().size();

        long bidPrice;
        long bidQuantity;

        long askPrice;
        long askQuantity;

        final var option = activeOrders.stream().findFirst();

        // buy at 1000 and sell at 1300
        // buy at 1300 and sell at "1600+"

        // todo - revisit the size

        if (totalOrderCount > 5000) {
            return NoAction.NoAction;
        }
        // Initial buy order
        if (activeOrders.size() < 0) {
            bidPrice = 1000;
            bidQuantity = 800;
            logger.info("[BuyLowSellHighAlgo] Adding buy order for: " + bidQuantity + " @ " +
                    bidPrice);
            new CreateChildOrder(Side.BUY, bidQuantity, bidPrice);

            // Sell order if bid price is greater than or equal to 1300
            if (bidLevel.price >= 1300) {
                askPrice = 1300;
                askQuantity = 800;
                logger.info("[BuyLowSellHighAlgo] Adding sell order for: " + askQuantity + " @ " +
                        askPrice);
                new CreateChildOrder(Side.SELL, askQuantity, askPrice);

                // Then buy order if ask price is less than or equal to 1300
                if (askLevel.price <= 1300) {
                    bidPrice = 1300;
                    bidQuantity = 4000; // todo- revisit the number i want to buy
                    logger.info("[BuyLowSellHighAlgo] Adding buy order for: " + bidQuantity + " @ " +
                            bidPrice);
                    new CreateChildOrder(Side.BUY, bidQuantity, bidPrice);

                    // Then sell order if bid price is greater than or equal to 1300
                    if (bidLevel.price >= 1300) {
                        askPrice = 1670; // todo - this number is just a place holder for now
                        askQuantity = 4000; // todo- revisit the number i want to sell
                        logger.info("[BuyLowSellHighAlgo] Adding sell order for: " + askQuantity + " @ " +
                                askPrice);
                        new CreateChildOrder(Side.SELL, askQuantity, askPrice);

                    }
                }

            }
        }
        return NoAction.NoAction;
    }

}
