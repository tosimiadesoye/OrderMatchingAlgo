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

public class BuyCheapSellHighAlgoLogic implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(BuyCheapSellHighAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        logger.info("[BUYCHEAPSELLHIGHALGO] In Algo Logic....");

        final String orderBookAsString = Util.orderBookToString(state);

        logger.info("[BUYCHEAPSELLHIGHALGO] The state of the order book is:\n" + orderBookAsString);

        BidLevel bid = state.getBidAt(0);
        AskLevel ask = state.getAskAt(0);

        long bidPrice = bid.price;
        long bidQuantity = bid.quantity;
        long askPrice = ask.price;
        long askQuantity = ask.quantity;

        // if the ask price is less than the current value of that share then you know
        // it a good deal
        if (askPrice < bidPrice) {
            logger.info("[BUYCHEAPSELLHIGHALGO] Adding order for" + askQuantity + "@" + askPrice);
            return new CreateChildOrder(Side.BUY, bidQuantity, bidPrice);

            // if the bid price is greater than or equals to 50 percent of the ask price
            // then sell the share
        } else if (bidPrice * 0.50 >= askPrice) {
            logger.info("[BUYCHEAPSELLHIGHALGO] Adding order for" + bidQuantity + "@" + bidPrice);
            return new CreateChildOrder(Side.SELL, askQuantity, askPrice);
            
        } else {
            return NoAction.NoAction;
        }

    }
}
