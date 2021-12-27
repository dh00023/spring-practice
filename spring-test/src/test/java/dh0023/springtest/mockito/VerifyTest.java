package dh0023.springtest.mockito;

import dh0023.springtest.mockito.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerifyTest {

    @Mock
    ItemService itemService;

    @Test
    void times_테스트() {
        itemService.getItem();
        itemService.getItem();

        // 몇번 호출됐는지 검증
        verify(itemService, times(2)).getItem();
    }

    @Test
    void never_테스트() {

        // 한 번도 호출되지 않았는지 검증
        verify(itemService, never()).getItem();
    }

    @Test
    void atLeastOnce_테스트() {

        itemService.getItem();
        itemService.getItem();
        itemService.getItem();
        itemService.getItem();

        // 최소 1번 호출됐는지 검증
        verify(itemService, atLeastOnce()).getItem();
    }

    @Test
    void atLeast_테스트() {

        itemService.getItem();
        itemService.getItem();
        itemService.getItem();
        itemService.getItem();

        // 최소 N번 호출됐는지 검증
        verify(itemService, atLeast(4)).getItem();
    }

    @Test
    void atMostOnce_테스트() {

        itemService.getItem();

        // 최대 N번 호출됐는지 검증
        verify(itemService, atMostOnce()).getItem();
    }

    @Test
    void atMost_테스트() {

        itemService.getItem();
        itemService.getItem();
        itemService.getItem();
        itemService.getItem();

        // 최대 N번 호출됐는지 검증
        verify(itemService, atMost(4)).getItem();
    }

    @Test
    void inOrder_calls_테스트() {
        itemService.getItem();
        itemService.getItem();
        itemService.getItem();
        itemService.deleteItem();

        InOrder inOrder = inOrder(itemService);

        inOrder.verify(itemService, calls(3)).getItem();
        inOrder.verify(itemService).deleteItem();
    }


}
