package dh0023.springtest.mockito;

import dh0023.springtest.mockito.item.domain.Item;
import dh0023.springtest.mockito.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OngoingStubbingTest {

    @Mock
    ItemService itemService;

    @Test
    void thenReturn_테스트() {
        Item item = new Item("0000000002", "20");
        when(itemService.getItem())
                .thenReturn(item);

        assertThat(itemService.getItem()).isEqualTo(item);
    }


    @Test
    void thenThrow_테스트() {
        when(itemService.getItem())
                .thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> {
            itemService.getItem();
        });
    }

    @Test
    void thenCallRealMethod_테스트() {
        when(itemService.getItem())
                .thenCallRealMethod();

        assertThat(itemService.getItem()).isNotNull();
        assertThat(itemService.getItem().getItemId()).isEqualTo("0000000001");
    }

    @Test
    void 메소드체이닝_테스트() {
        Item item = new Item("0000000002", "20");
        when(itemService.getItem())
                .thenReturn(item)
                .thenThrow(new RuntimeException());

        assertThat(itemService.getItem()).isEqualTo(item);

        assertThrows(RuntimeException.class, () -> {
            itemService.getItem();
        });
    }
}
