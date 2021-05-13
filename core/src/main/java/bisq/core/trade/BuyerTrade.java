/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.trade;

import bisq.core.btc.wallet.XmrWalletService;
import bisq.core.offer.Offer;
import bisq.core.trade.protocol.ProcessModel;

import bisq.network.p2p.NodeAddress;

import org.bitcoinj.core.Coin;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public abstract class BuyerTrade extends Trade {
    BuyerTrade(Offer offer,
               Coin tradeAmount,
               Coin txFee,
               Coin takerFee,
               long tradePrice,
               @Nullable NodeAddress takerNodeAddress,
               @Nullable NodeAddress makerNodeAddress,
               @Nullable NodeAddress arbitratorNodeAddress,
               XmrWalletService xmrWalletService,
               ProcessModel processModel,
               String uid) {
        super(offer,
                tradeAmount,
                txFee,
                takerFee,
                tradePrice,
                takerNodeAddress,
                makerNodeAddress,
                arbitratorNodeAddress,
                xmrWalletService,
                processModel,
                uid);
    }

    BuyerTrade(Offer offer,
               Coin txFee,
               Coin takerFee,
               @Nullable NodeAddress takerNodeAddress,
               @Nullable NodeAddress makerNodeAddress,
               @Nullable NodeAddress arbitratorNodeAddress,
               XmrWalletService xmrWalletService,
               ProcessModel processModel,
               String uid) {
        super(offer,
                txFee,
                takerFee,
                takerNodeAddress,
                makerNodeAddress,
                arbitratorNodeAddress,
                xmrWalletService,
                processModel,
                uid);
    }

    @Override
    public Coin getPayoutAmount() {
        checkNotNull(getTradeAmount(), "Invalid state: getTradeAmount() = null");
        return checkNotNull(getOffer()).getBuyerSecurityDeposit().add(getTradeAmount());
    }

    @Override
    public boolean confirmPermitted() {
        return !getDisputeState().isArbitrated();
    }
}