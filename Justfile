set dotenv-load

help:
  just --list

CSS := "resources/public/assets/css"

watch:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --watch=always

minify:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --minify

plus:
  clj -X:dev:plus

nrepl:
  clj -M:dev:nrepl

dev:
  just watch >/dev/null 2>&1 &
  just nrepl

kaocha:
  clojure -M:dev -m kaocha.runner

run:
  clojure -J--enable-native-access=ALL-UNNAMED -M:run-m

build:
  if ! test -f target/io.github.hkimjp/konpy2-*.jar; then clojure -T:build ci; fi

deploy: # build
  scp target/io.github.hkimjp/konpy2-*.jar ${DEST}:konpy2/konpy.jar
  ssh ${DEST} 'sudo systemctl restart konpy'
  ssh ${DEST} 'systemctl status konpy'

up:
  docker compose up

down:
  docker compose down

upgrade:
  clojure -Tantq outdated :upgrade true

clean:
  rm -rf target
  fd -I bak --exec rm

#
# test on eq.local
#

eq: build
  # scp compose-prod.yml eq.local:konpy2/compose.yml

  scp target/io.github.hkimjp/konpy2-*.jar eq.local:konpy2/konpy2.jar
  ssh eq.local 'cd konpy2 && docker compose down && docker compose up -d'

#
# docker container
#

TAG := 'hkim0331/konpy2'
VER := '0.3.39'

hub: security manifest

security:
  security -v unlock-keychain ~/Library/Keychains/login.keychain-db

amd64:
  docker buildx build --platform linux/amd64 --push -t {{TAG}}-amd64 .

arm64:
  docker buildx build --platform linux/arm64 --push -t {{TAG}}-arm64 .

manifest: arm64 amd64
  docker manifest create --amend {{TAG}} {{TAG}}-amd64 {{TAG}}-arm64
  docker manifest push {{TAG}}

docker-build:
  docker build --pull -t {{TAG}} .
  docker tag {{TAG}} {{TAG}}:{{VER}}

docker-push:
  docker push {{TAG}}
  docker push {{TAG}}:{{VER}}
