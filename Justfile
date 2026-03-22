set dotenv-load := true

help:
    just --list

CSS := "resources/public/assets/css"

watch:
    tailwindcss -i {{ CSS }}/input.css -o {{ CSS }}/output.css --watch=always

minify:
    tailwindcss -i {{ CSS }}/input.css -o {{ CSS }}/output.css --minify

plus:
    clj -X:dev:plus

nrepl:
    clj -M:dev:nrepl

dev:
    just watch >/dev/null 2>&1 &
    just nrepl

kaocha:
    clojure -M:dev -m kaocha.runner

run: minify
    clojure -J--enable-native-access=ALL-UNNAMED -M:run-m

build: minify
    clojure -T:build ci

deploy host: minify build
    scp compose.yaml ${STAGE}:konpy2/compose.yaml
    scp target/io.github.hkimjp/konpy2-*.jar ${STAGE}:konpy2/konpy2.jar
    ssh ${STAGE} 'cd konpy2 && docker compose down && docker compose up -d'

stage:
    just deploy ${STAGE}

prod:
    just deploy ${PROD}

up:
    docker compose up -d

down:
    docker compose down

# start:
#     #!/usr/bin/env bash
#     java -jar konpy.jar >> log/konpy.log 2>> log/error.log
# 
# stop:

restart:
    stop
    start

upgrade:
    clojure -Tantq outdated :upgrade true

clean:
    rm -rf target
    rm resources/public/assets/css/output.css
    fd -I bak --exec rm

#
# docker container
#

TAG := 'hkim0331/konpy2'
VER := '0.8.1'

dockerhub: security manifest

security:
    security -v unlock-keychain ~/Library/Keychains/login.keychain-db

amd64:
    docker buildx build --platform linux/amd64 --push -t {{ TAG }}-amd64 .

arm64:
    docker buildx build --platform linux/arm64 --push -t {{ TAG }}-arm64 .

manifest: arm64 amd64
    docker manifest create --amend {{ TAG }} {{ TAG }}-amd64 {{ TAG }}-arm64
    docker manifest push {{ TAG }}

docker-build:
    docker build --pull -t {{ TAG }} .
    docker tag {{ TAG }} {{ TAG }}:{{ VER }}

docker-push: docker-build
    docker push {{ TAG }}
    docker push {{ TAG }}:{{ VER }}
