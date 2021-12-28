package dh0023.springtest.tdd;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MoneyTest {

    @Test
    void testMultiplication() {
        Money five = Money.dollar(5);
        assertThat(five.times(2)).isEqualTo(Money.dollar(10));
        assertThat(five.times(3)).isEqualTo(Money.dollar(15));

        five = Money.franc(5);
        assertThat(five.times(2)).isEqualTo(Money.franc(10));
        assertThat(five.times(3)).isEqualTo(Money.franc(15));
    }

    @Test
    void testEquallity() {
        assertThat(Money.dollar(5).equals(Money.dollar(5))).isTrue();
        assertThat(Money.dollar(5).equals(Money.dollar(6))).isFalse();
        assertThat(Money.franc(5).equals(Money.dollar(5))).isFalse();

        assertThat(new Money(10, "CHF").equals(Money.franc(10))).isTrue();
    }

    @Test
    void testCurrency() {
        assertThat("USD").isEqualTo(Money.dollar(1).currency());
        assertThat("CHF").isEqualTo(Money.franc(1).currency());
    }

    @Test
    void reduceMoney() {
        Bank bank = new Bank();
        Money result = bank.reduce(Money.dollar(1), "USD");

        assertThat(result).isEqualTo(Money.dollar(1));
    }

    @Test
    void reduceMoneyDiffCurrency() {
        Bank bank = new Bank();
        bank.addRate("CHF", "USD", 2);
        Money result = bank.reduce(Money.franc(2), "USD");
        assertThat(result).isEqualTo(Money.dollar(1));
    }

    @Test
    void identityRateTest() {
        assertThat(new Bank().rate("USD", "USD")).isEqualTo(1);
    }

    @Test
    void mixedAddition() {
        Expression fiveDollars = Money.dollar(5);
        Expression tenFrancs = Money.franc(10);

        Bank bank = new Bank();
        bank.addRate("CHF", "USD", 2);

        Money result = bank.reduce(fiveDollars.plus(tenFrancs), "USD");
        assertThat(result).isEqualTo(Money.dollar(10));
    }

    @Test
    void sumPlusMoney() {
        Expression fiveDollars = Money.dollar(5);
        Expression tenFrancs = Money.franc(10);

        Bank bank = new Bank();
        bank.addRate("CHF", "USD", 2);

        Expression sum = new Sum(fiveDollars, tenFrancs).plus(fiveDollars);

        Money result = bank.reduce(sum, "USD");
        assertThat(result).isEqualTo(Money.dollar(15));
    }

    @Test
    void sumTimes() {
        Expression fiveDollars = Money.dollar(5);
        Expression tenFrancs = Money.franc(10);

        Bank bank = new Bank();
        bank.addRate("CHF", "USD", 2);

        Expression sum = new Sum(fiveDollars, tenFrancs).times(5);

        Money result = bank.reduce(sum, "USD");
        assertThat(result).isEqualTo(Money.dollar(50));
    }

}
