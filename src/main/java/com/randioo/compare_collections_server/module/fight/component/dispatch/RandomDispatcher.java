/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.dispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @Description: 十点半发牌器
 * @author zsy
 * @date 2017年9月22日 下午3:36:36
 */
@Component
public class RandomDispatcher implements Dispatcher {

    @Override
    public List<CardPart> dispatch(List<Integer> remainCards, int partCount, int everyPartCount) {
        Collections.shuffle(remainCards);

        List<CardPart> cardParts = new ArrayList<>(partCount);
        for (int i = 0; i < partCount; i++) {
            CardPart cardPart = new CardPart();
            cardParts.add(cardPart);
            for (int j = 0; j < everyPartCount; j++) {
                int card = remainCards.remove(j);
                cardPart.add(card);
            }
        }
        return cardParts;
    }

}
