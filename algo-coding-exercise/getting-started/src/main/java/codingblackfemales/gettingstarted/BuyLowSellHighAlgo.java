package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

public class BuyLowSellHighAlgo implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(BuyLowSellHighAlgo.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYBUYLOWSELLHIGHALGO] The state of the order book is:\n" + orderBookAsString);

        final BidLevel bidLevel = state.getBidAt(0);
        final AskLevel askLevel = state.getAskAt(0);
        final var activeOrders = state.getActiveChildOrders();
        var totalOrderCount = state.getChildOrders().size();

        //todo- buy at 1000 and sell at 1300
        // buy at 1300 and sell at "1600+"

        // todo - revisit the size

        // Constants for order quantities and counts
        final long MAX_ORDER_COUNT = 5000L;
        final long bidPrice1 = 1000;
        final long bidQuantity1 = 800;
        final long askPrice1 = 1300;
        final long askQuantity1 = 800;
        final long bidPrice2 = 1300;
        final long bidQuantity2 = 4000; // todo- revisit the number i want to buy
        final long askPrice2 = 1670; // todo - this number is just a place holder until i figure out what the actual no is
        final long askQuantity2 = 4000; // todo- revisit the number i want to sell - see above

        var bid1filledQuantity = state.getChildOrders().stream().mapToLong(ChildOrder::getFilledQuantity).sum();

        if (totalOrderCount > MAX_ORDER_COUNT) {
            return NoAction.NoAction;
        }

        // First initial buy order
        if (activeOrders.size() < 0) {
            logger.info("[BuyLowSellHighAlgo] Adding buy order for: " + bidQuantity1 + " @ " +
                    bidPrice1);
            new CreateChildOrder(Side.BUY, bidQuantity1, bidPrice1);
        }

        // Sell order if first initial order has been filled
        if (bid1filledQuantity == bidQuantity1) {
            logger.info("[BuyLowSellHighAlgo] Adding sell order for: " + askQuantity1 + " @ " +
                    askPrice1);
            new CreateChildOrder(Side.SELL, askQuantity1, askPrice1);
        }

        var ask1filledQuantity = state.getChildOrders().stream().mapToLong(ChildOrder::getFilledQuantity).sum();

        // Buy order if ask price 1 has been filled
        if (ask1filledQuantity == askQuantity1) {
            logger.info("[BuyLowSellHighAlgo] Adding buy order for: " + bidQuantity2 + " @ " +
                    bidPrice2);
            new CreateChildOrder(Side.BUY, bidQuantity2, bidPrice2);
        }

        var ask2filledQuantity = state.getChildOrders().stream().mapToLong(ChildOrder::getFilledQuantity).sum();
        // Finally sell order if bid price 2 has been filled
        if (ask2filledQuantity == askQuantity2) {
            logger.info("[BuyLowSellHighAlgo] Adding sell order for: " + askQuantity2 + " @ " +
                    askPrice2);
            new CreateChildOrder(Side.SELL, askQuantity2, askPrice2);

        }
        return NoAction.NoAction;
    }

}
