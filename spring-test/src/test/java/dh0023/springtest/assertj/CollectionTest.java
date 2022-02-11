package dh0023.springtest.assertj;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class CollectionTest {

    @Test
    void test() {
        EnumSet<DayOfWeek> days = EnumSet.allOf(DayOfWeek.class);
        assertThat(days)
                .filteredOn(d -> d.getValue() > 4)
                .containsOnly(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

        List<T1> members = new ArrayList<>();
        members.add(new T1("faker", 26));
        members.add(new T1("bang", 26));
        members.add(new T1("wolf", 26));
        members.add(new T1("keria", 20));

        assertThat(members)
                .filteredOn("age", not(26))
                .containsOnly(members.get(3));
    }

    @Test
    void extracting() {
        List<T1> members = new ArrayList<>();
        members.add(new T1("faker", 26));
        members.add(new T1("bang", 26));
        members.add(new T1("wolf", 26));
        members.add(new T1("keria", 20));

        assertThat(members)
                .extracting("name")
                .contains("faker", "bang", "wolf", "keria");


        assertThat(members)
                .extracting("name", "age")
                .contains(tuple("faker", 26),
                        tuple("bang", 26),
                        tuple("wolf", 26),
                        tuple("keria", 20));
    }

    static class T1 {
        private String name;

        public T1(String name, int age) {
            this.name = name;
            this.age = age;
        }

        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
