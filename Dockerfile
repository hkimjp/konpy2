FROM python:3.14-slim-trixie

ENV DEBIAN_FRONTEND=noninteractive
ENV DEBCONF_NOWARNINGS=yes

RUN set -ex; \
    apt-get -y update; \
    apt-get -y --no-install-recommends install \
            curl \
            openjdk-25-jre-headless \
            redis

# don't forget in production
RUN set -ex; apt-get -y autoremove; apt-get -y clean; rm -rf /var/lib/apt/lists/*

# Ruff
RUN set -ex; curl -LsSf https://astral.sh/ruff/install.sh | sh
RUN set -ex; mv /root/.local/bin/ruff /usr/local/bin/ruff
