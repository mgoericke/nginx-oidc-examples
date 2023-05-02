FROM openresty/openresty:1.21.4.1-6-alpine-fat
RUN /usr/local/openresty/luajit/bin/luarocks install lua-resty-openidc 1.7.6-3

### Update curl / libcurl to fix CVE-2023-27536
RUN apk del libcurl
RUN apk update && apk upgrade && apk add curl
