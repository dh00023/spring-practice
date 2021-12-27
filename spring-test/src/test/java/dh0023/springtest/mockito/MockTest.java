package dh0023.springtest.mockito;

import dh0023.springtest.mockito.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MockTest {
    @Mock
    ItemService itemService;

    @Test
    void 참조타입_NOT_STUBBING_테스트() {
        assertThat(itemService.getItem()).isNull();
    }

    @Test
    void 기본타입_NOT_STUBBING_테스트() {
        assertThat(itemService.isOnlineMallItem()).isFalse();
    }
}
