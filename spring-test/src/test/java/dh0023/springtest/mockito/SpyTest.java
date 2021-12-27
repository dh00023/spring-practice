package dh0023.springtest.mockito;

import dh0023.springtest.mockito.item.domain.Item;
import dh0023.springtest.mockito.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpyTest {

    @Spy
    ItemService itemService;

    @Test
    void NOT_STUBBING_테스트() {
        // given

        // when
        Item item = itemService.getItem();

        // then
        assertThat(item).isNotNull();
        assertThat(item.getItemDivCd()).isEqualTo("10");
        assertThat(item.getItemId()).isEqualTo("0000000001");
    }

    @Test
    void STUBBING_테스트() {

        // given
        when(itemService.getItem())
                .thenReturn(new Item("0000000002", "20"));

        // when
        Item item = itemService.getItem();

        // then
        assertThat(item).isNotNull();
        assertThat(item.getItemDivCd()).isEqualTo("20");
        assertThat(item.getItemId()).isEqualTo("0000000002");

    }
}
