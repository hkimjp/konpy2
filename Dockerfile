FROM clojure:latest

ENV DEBIAN_FRONTEND=noninteractive
ENV DEBCONF_NOWARNINGS=yes

RUN set -ex; \
    apt-get -y update; \
#    apt-get -y upgrade; \
    apt-get -y --no-install-recommends install \
            python3 python3-pip python3-pytest curl \
            python3-bottle python3-numpy python3-matplotlib \
#            python3-opencv \
            python3-pygame python3-tk

# don't forget in production
RUN set -ex; apt-get -y autoremove; apt-get -y clean; rm -rf /var/lib/apt/lists/*

# Ruff
RUN set -ex; curl -LsSf https://astral.sh/ruff/install.sh | sh
RUN set -ex; cp /root/.local/bin/ruff /usr/local/bin/ruff


