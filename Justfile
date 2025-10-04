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
  clojure -T:build ci

deploy: build
  scp target/io.github.hkimjp/konpy2-*.jar ${DEST}:konpy2/konpy.jar
  ssh ${DEST} 'sudo systemctl restart konpy'
  ssh ${DEST} 'systemctl status konpy'

container-nrepl:
  clj -M:dev -m nrepl.cmdline -b 0.0.0.0 -p 5555

upgrade:
  clojure -Tantq outdated :upgrade true

clean:
  rm -rf target
  fd -I bak --exec rm

eq: build
  scp target/io.github.hkimjp/konpy2-*.jar eq.local:konpy2/konpy2.jar
  ssh eq.local 'cd wil2 && docker compose restart'
