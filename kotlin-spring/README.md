# 내용 정리

## cloud-config git 설정

로컬설정 기반으로 설정하는 방법
- https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent

이때 키 생성시 https://github.com/mwiede/jsch/issues/12 jsch는 Ed25519알고리즘을 지원하지 않으므로 rsa로 생성해야함.

- [토리맘의 한글라이즈 프로젝트 Spring Cloud Config](https://godekdls.github.io/Spring%20Cloud%20Config/spring-cloud-config-server/)

