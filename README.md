# demo-oidc
OpenId-Connect Demo Programm mit Nginx (Openresty) und Keycloak

* Nginx (Openresty)
* Keycloak 20.x
* Backend (Spring Boot Anwendung)

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
docker compose up
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
7. Anzeige der geschützte Resource (Diese zeigt alle Header an, die am Backend angekommen sind)
8. Zusätzlich wird das Access Token als **Authorization** Header vom Nginx an das Backend übermittelt: `ngx.req.set_header('Authorization', table.concat({"Bearer", res.access_token}, " "))`



