package dh0023.springcore.singleton;

/**
 * 테스트용 싱글톤 서비스
 */
public class SingletonService {

    // static 선언으로 하나만 생성
    private static final SingletonService instance = new SingletonService();

    // 객체 인스턴스가 필요한 경우 getInstance()로만 호출 가능
    public static SingletonService getInstance() {
        return instance;
    }

    // 생성자를 private으로 막아서 외부에서 호출할 수 없도록 막음.
    private SingletonService() {
    }

    public void logic() {
        System.out.println("call singleton object logic");
    }
}
