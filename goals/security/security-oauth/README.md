## Spring Security OAuth

### Relevant information:

This module demonstrates OAuth authorization flow using Spring Authorization Server, Spring OAuth Resource Server and
Spring OAuth Client.

- Run the Authorization Server from the `spring-authorization-server` module
    - IMPORTANT:
      - Modify in Linux the `/etc/hosts` file and add the entry `127.0.0.1 auth-server`
      - Modify in Windows the `C:\Windows\System32\drivers\etc\hosts` file and add the entry `127.0.0.1 auth-server`
- Run the Resource Server from `resource-server` module
- Run the client from `client-server` module
- Go to `http://127.0.0.1:8131/articles`
    - Enter the credentials `admin/password`
- The module uses the new OAuth stack with Java 11

### Relevant Articles:

- [OAuth2 Spring Docs](https://docs.spring.io/spring-security/site/docs/5.2.12.RELEASE/reference/html/oauth2.html)
- [The OAuth 2.0 Authorization Framework rfc6749](https://datatracker.ietf.org/doc/html/rfc6749#section-1.1)
- [Spring Security OAuth Authorization Server](https://www.baeldung.com/spring-security-oauth-auth-server)
