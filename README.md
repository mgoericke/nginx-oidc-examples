# demo-oidc
OpenId-Connect Demo Programm mit Nginx (Openresty) und Keycloak

* Nginx [lua-resty-openidc](https://github.com/zmartzone/lua-resty-openidc)
* Keycloak [keycloak.org](https://www.keycloak.org/)
* Backend ([Spring Boot OAuth2.0 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html))

## Vorbereitung:
Eintrag in der lokalen hosts-Datei:
```
127.0.0.1 demo-oidc.localhost
```

## Einschränkung

**Funktioniert nicht im Safari auf macOS Ventura!**

## Starten der Anwendung

```shell
# baut die Spring Boot Anwendung "backend"
# startet den Nginx (Openresty) als "web"
# startet den Keycloak als "sso" und importiert einen Realm
docker compose up --build
```

### Nginx (Openresty)
* Erreichbar unter [demo-oidc.localhost:8001](demo-oidc.localhost:8001) (etc/hosts Eintrag notwendig)
* Reverse Proxy mit Absicherung von geschützten Ressourcen via Open ID Connect (keycloak)
* Konfiguration befindet sich im Verzeichnis: [openresty](openresty)

### Backend
* Einfache Spring Boot Anwendung mit einem Controller "/example", der die empfangenen Header anzeigt
* Ist nur nach Anmeldung über den Keycloak erreichbar (Nginx als Reverse Proxy)


### Keycloak
* Konfiguration befindet sich im Verzeichnis: [keycloak](keycloak)
* starten des vorkonfigurierten Keycloak
* Erreichbar unter demo-oidc.localhost:9001
* Anmeldung als admin/changeme


## Verwendung

1. Aufruf der **geschützten Resource** [http://demo-oidc.localhost:8001/example](http://demo-oidc.localhost:8001/example) im Webbrowser
2. Weiterleitung auf den Keycloak zur Anmeldung
3. Anmeldung und Authentifizierung am Keycloak als **Erika/erika**
4. Weiterleitung vom Keycloak zum Nginx
5. Nginx tauscht Authorization Code gegen Access Token und ID Token ein
6. Der Zugriff auf geschützte Resource ist jetzt erlaubt
7. Anzeige der geschützte Resource (Diese zeigt alle Header an, die am Backend angekommen sind, sowie die vom Keycloak ermittelten UserAttributes)
8. Zusätzlich wird das Access Token als **Authorization** Header vom Nginx an das Backend übermittelt: `ngx.req.set_header('Authorization', table.concat({"Bearer", res.access_token}, " "))`

## Lokale Entwicklung des Backends

Folgende Anpassungen für die lokale Entwicklung des Backends vornehmen:

### openresty/nginx.conf:
```nginx configuration
upstream backend {
    # Mapping: Container -> localhost  
    server host.docker.internal:8080;
}
```

### src/main/resources/application.yaml:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        opaque:
          # öffentliche Keycloak Adresse
          introspection-uri: http://demo-oidc.localhost:9001/realms/demo-oidc/protocol/openid-connect/token/introspect
```

### Starten von Keycloak und Nginx

```shell
docker compose up web sso
```
