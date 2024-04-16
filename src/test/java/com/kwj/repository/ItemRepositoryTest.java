package com.kwj.repository;

import com.kwj.constant.ItemSellStatus;
import com.kwj.entity.Item;
import com.kwj.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
//통합 테스트를 위해 스프링 부트에서 제공하는 어노테이션
//실제 어플리케이션을 구동할 때처럼 모든 Bean 을 Ioc 컨테이너에 등록
@TestPropertySource(locations = "classpath:application-test.properties")
//테스트 코드 실행시 application.properties 에 설정해둔 값보다 application-test.properties 에 같은
// 설정이있다면 더 높은 우선순위를 부여. 기존 설정은 MySQL 을 사용하지만 테스트에서는 h2 사용
    
class ItemRepositoryTest {

    @PersistenceContext
    EntityManager em;//영속성 컨텍스트를 사용하기 위해 EntityManager 빈을 주입

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

    @Test
    @DisplayName("Querydsl 조회테스트1")
    public void queryDslTest(){

        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%"+"테스트"+"%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();
        for (Item item:itemList){
            System.out.println(item.toString());
        }//end of for
    }//end of method

    public void createItemList2(){
        for (int i=1; i<=5; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }//end of for
        for (int i=6; i<=10; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }//end of for
    }//end of method

    @Test
    @DisplayName("상품 Querydsl 조회 테스트2")
    public void queryDslTest2(){
        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;

        String itemDetail = "상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%"+itemDetail+"%"));//"상세 설명" 이 제품 상세 설명에 포함된 요소
        booleanBuilder.and(item.price.gt(price));//제품 가격이 10003보다 높은 요소

        if (StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));//제품 판매상태가 "SELL"인 요소
        }//end of if

        Pageable pageable = PageRequest.of(0,5);//첫 번째 페이지를 요청하고, 해당 페이지에서 최대 5개의 항목을 반환하도록 페이지네이션 정보를 설정하는 것입니다.
        Page<Item> itemPagingResult =
                itemRepository.findAll(booleanBuilder, pageable);
        //메서드는 JpaRepository 인터페이스에 정의된 메서드 중 하나입니다. 두 개의 매개변수를 가지고 있습니다. 첫 번째 매개변수는 Querydsl 의
        // BooleanBuilder 를 사용하여 동적으로 생성된 쿼리 조건을 전달하고, 두 번째 매개변수는 페이지네이션 정보를 나타내는 Pageable 객체를 전달합니다.
        System.out.println("total elements : " + itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for (Item resultItem:resultItemList){
            System.out.println(resultItem.toString());
        }//end of for

    }//end of method

}//end of testClass