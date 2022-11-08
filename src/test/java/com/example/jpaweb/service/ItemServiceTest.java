package com.example.jpaweb.service;

import com.example.jpaweb.domain.item.Album;
import com.example.jpaweb.domain.item.Book;
import com.example.jpaweb.domain.item.Item;
import com.example.jpaweb.domain.item.Movie;
import com.example.jpaweb.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ItemServiceTest {
    // 상품 등록
    // 상품 조회

    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;
    @Autowired EntityManager em;

    @Test
    public void 영화등록() throws Exception {
        // given
        Item item = new Movie();
        item.setName("영화등록");

        // when
        itemService.saveItem(item);

        // then
        assertEquals(item, itemRepository.findOne(item.getId()));
    }

    @Test
    public void 음반등록() throws Exception {
        // given
        Item item = new Album();
        item.setName("음반등록");

        // when
        itemService.saveItem(item);

        // then
        assertEquals(item, itemRepository.findOne(item.getId()));
    }

    @Test
    public void 책등록() throws Exception {
        // given
        Item item = new Book();
        item.setName("책등록");

        // when
        itemService.saveItem(item);

        // then
        assertEquals(item, itemRepository.findOne(item.getId()));
    }

    @Test
    public void 전체조회() throws Exception {
        // given
        Item item1 = new Book();
        Item item2 = new Book();

        item1.setName("책1");
        item1.setName("책2");

        itemService.saveItem(item1);
        itemService.saveItem(item2);

        // when
        List<Item> result = itemRepository.findAll();

        // then
        Assertions.assertThat(result.size()).isEqualTo(2);

    }

    @Test
    public void 단건조회() throws Exception {
        // given
        Item item1 = new Book();
        item1.setName("책책");
        itemService.saveItem(item1);

        // when
        Item result = itemService.findOne(item1.getId());

        // then
        Assertions.assertThat(result.getId()).isEqualTo(item1.getId());
    }

}
