package dh0023.springcore.lifecycle;

/**
 * 테스트를 위한 가짜 NetworkClient
 */
public class NetworkClientMethod {

    private String url;

    public NetworkClientMethod() {
        System.out.println("생성자 호출, url = " + url);

    }

    public void setUrl(String url) {
        this.url = url;
    }

    // start service
    public void connect() {
        System.out.println("connect: " + url);
    }

    public void call(String message) {
        System.out.println("call: " + url + " message = " + message);
    }

    public void disconnect() {
        System.out.println("close: " + url);
    }

    public void init() {
        System.out.println("NetworkClientMethod.init");
        connect();
        call("초기화 연결 메세지");

    }

    public void close() {
        System.out.println("NetworkClientMethod.close");
        disconnect();
    }

}
