package com.kwj.repository;

import com.kwj.constant.ItemSellStatus;
import com.kwj.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
//통합 테스트를 위해 스프링 부트에서 제공하는 어노테이션
//실제 어플리케이션을 구동할 때처럼 모든 Bean 을 Ioc 컨테이너에 등록
@TestPropertySource(locations = "classpath:application-test.properties")
//테스트 코드 실행시 application.properties 에 설정해둔 값보다 application-test.properties 에 같은
// 설정이있다면 더 높은 우선순위를 부여. 기존 설정은 MySQL 을 사용하지만 테스트에서는 h2 사용
    
class ItemRepositoryTest {

    @Autowired//Bean 을 주입
    ItemRepository itemRepository;
    
    @Test//테스트 메소드로 지정
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){

        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());

    }//end of method

    public void createItemList(){
        for(int i=0; i<=10; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);

        }//end of for
    }//end of method

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNm(){

        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for(Item item:itemList){
            System.out.println(item.toString());
        }//end of for

    }//end of method

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("상품");
        for(Item item:itemList){
            System.out.println(item.toString());
        }//end of for
    }//end of method

}//end of testClass