# Ads-Online application

Ads-Online is the place where you can easily sell or buy goods. You can place an ad of brand new or previous enjoyed item.To get started first you need to open an account and then login in the application. The authorised user are able to create and edit your ads, change your account information.

### Available function:

1) Create, alter, delete ad;
2) Save, Update, show ad pictures;
3) Comment ad, change or delete it;
4) Show all available ads or all user ads;
5) Show detailed information about selected ad;
6) Show all comments for selected ad.

### Security

Implemented by means of Spring Security.
#### Roles:
1) **Anonymous user** - non-authenticated user has full access to see all available ads;
2) **User** - able to change or delete only his or her ads;
3) **Admin** - able to change or delete all ads.

### Stack of technologies:
* [Hibernate](https://hibernate.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Web Services](https://spring.io/projects/spring-ws)
* [PostgreSQL](https://www.postgresql.org/)
* [Spring Security](https://spring.io/projects/spring-security)
* [Liquibase](https://www.liquibase.org/)
* [Docker](https://www.docker.com/)
* [Java SE 11](https://www.oracle.com/cis/java/technologies/javase/jdk11-archive-downloads.html)

### Installation steps

Installation Docker Desktop:
1) [Download Docker](https://www.docker.com/products/docker-desktop/)
2) [Install WSL 2 for Windows](https://learn.microsoft.com/en-us/windows/wsl/install-manual)
3) [Install Docker Desktop](https://docs.docker.com/desktop/install/windows-install/)

[Install IntelliJ IDEA Ultimate or Community Edition](https://www.jetbrains.com/ru-ru/idea/download/?section=windows)

Create the application fork:
1) Inside IntelliJ IDEA: `File -> New -> Project from Version Control `
2) Paste the project URL in pop up window https://github.com/ProtsD/Ads-Online.git
3) Then press Clone.

Run command `docker run -p 3000:3000 --rm ghcr.io/bizinmitya/front-react-avito:v1.18` in terminal to launch frontend.
Frontend works on: http://localhost:3000

### Project Team
* Sergei Ivanchoglo
* Danil Balakhonov
* Denis Prots 
* Anatoly Yaloza 
* Alexander Demyanov 