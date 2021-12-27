package dh0023.springtest.mockito;

import dh0023.springtest.mockito.item.domain.Item;
import dh0023.springtest.mockito.item.service.ItemService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StubberTest {

    @Mock
    ItemService itemService;


    @Test
    void doReturn_테스트() {
        List list = new LinkedList();

        List spyList = spy(list);

        doReturn("test").when(spyList).get(0);

        assertThat(spyList.get(0)).isEqualTo("test");

        Item item = new Item("0000000003", "30");
        doReturn(item)
                .when(itemService)
                .getItem();

        assertThat(itemService.getItem()).isEqualTo(item);
    }

    @Test
    void doThrow_테스트() {
        doThrow(new RuntimeException()).when(itemService).getItem();

        assertThatThrownBy(() -> itemService.getItem()).isInstanceOf(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> {
            itemService.getItem();
        });
    }
}
