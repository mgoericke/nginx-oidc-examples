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

## SAML
20.11.2023

Einführung eines weiteren Keycloak (siehe: docker compose "samlidp)", der als SAML2.0 Identity Provider (IdP) vorkonfiguriert und im vorhandenen Keycloak eingebunden ist.  

* Eintragen des Hosts "demo-samlidp.external" in die Hosts Datei
* Admin UI ist unter http://demo-samlidp.external:10001 erreichbar
* Admin Login: admin/changeme
* Realm: saml-idp

Für diesen IdP Keycloak ist ein einzelner Benutzer konfiguriert. Der Benutzer kann sich über den Button "SAML IdP" im Keycloak Anmeldeformular anmelden.

* Benutzername: max
* Kennwort: changeme

Nach erfolgreicher Anmeldung leitet der IdP Keycloak auf den als Broker laufenden Keycloak (siehe: docker compose "sso") zurück, dort wird der Benutzer mapped und in der Keycloak Datenbank angelegt.
Anschliessend erfolgt ein Redirect zur Anwendung: http://demo-oidc.localhost:8001/example

Im Admin UI des Keycloak (siehe: docker compose "sso") kann geprüft werden, ob der Benutzer im Broker angelegt und ein Link zum Identity Provider hergestellt wurde


## Export Realm Resources

Realms, einschliesslich Benutzern, können folgendermassen exportiert werden (zuvor natürlich erst einen Terminal in den Container aufmachen :). 
Anschliessend liegen die JSON Dateien der Realms unter keycloak/realms/export bzw. saml/realms/export.

```shell
cd /opt/keycloak
./bin/kc.sh export --dir /opt/keycloak/data/import/export --users realm_file
```
