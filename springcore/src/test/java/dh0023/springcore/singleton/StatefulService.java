package dh0023.springcore.singleton;

public class StatefulService {

//    private int price; // 상태 유지 필드(공유되는 필드 인데, 특정 클라이언트가 값을 변경한다.)


    public int order(String name, int price){
        System.out.println("name = " + name + " price = " + price);
//        this.price = price; // problem!!

        return price;
    }

//    public int getPrice() {
//        return price;
//    }
}
