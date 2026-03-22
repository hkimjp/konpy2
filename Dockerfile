FROM clojure:latest

ENV DEBIAN_FRONTEND=noninteractive
ENV DEBCONF_NOWARNINGS=yes

RUN set -ex; \
    apt-get -y update; \
    apt-get -y --no-install-recommends install \
            curl \
            python3 python3-pytest \
            python3-opencv python3-numpy python3-matplotlib \
            redis

# don't forget in production
RUN set -ex; apt-get -y autoremove; apt-get -y clean; rm -rf /var/lib/apt/lists/*

# Ruff
RUN set -ex; curl -LsSf https://astral.sh/ruff/install.sh | sh
RUN set -ex; cp /root/.local/bin/ruff /usr/local/bin/ruff
