# https://zenn.dev/yyu/articles/3f900eaa2aa860

TAG=hkim0331/clojure
VER=0.5.0

.PHONY: build zip clean hub security manifest amd64 arm64

build:
	docker build --pull -t ${TAG} .
	docker tag ${TAG} ${TAG}:${VER}

push:
	docker push ${TAG}
	docker push ${TAG}:${VER}

zip:
	zip -r clojure.zip Dockerfile Makefile docker-compose.yml .devcontainer

clean:
	${RM} *~ clojure.zip

# amd/arm multi binaries
hub: security clean manifest

security:
	security -v unlock-keychain ~/Library/Keychains/login.keychain-db

manifest: arm64 amd64
	docker manifest create --amend ${TAG} ${TAG}-amd64 ${TAG}-arm64
	docker manifest push ${TAG}
	docker manifest tag ${TAG}:${VER}
	docker manifest push ${TAG}:${VER}

amd64:
	docker buildx build --platform linux/$@ --push -t ${TAG}-amd64 .

arm64:
	docker buildx build --platform linux/$@ --push -t ${TAG}-arm64 .
