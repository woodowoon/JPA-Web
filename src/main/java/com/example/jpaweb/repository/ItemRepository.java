package com.example.jpaweb.repository;

import com.example.jpaweb.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    // 저장
    public void save(Item item) {
        if(item.getId() == null) {
            em.persist(item);
        } else {
            // merge 넌 누구냐, 근데 실무에서는 잘 사용할 일이 없다.
            // 왜냐, merge 는 모든것을 바꿔쳐준다 근데 그러면 안되기때문에 번거롭더라도 영속성 컨텍스트를 사용해야한다.
            // 그래서 null 이 들어갈 수 있다.
            em.merge(item); // update 랑 비슷한거다~ 라고 생각하자
        }
    }

    // 단건 조회
    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    // 전체 조회
    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
