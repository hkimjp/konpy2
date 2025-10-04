FROM clojure:latest

ENV DEBIAN_FRONTEND=noninteractive
ENV DEBCONF_NOWARNINGS=yes

RUN set -ex; \
    apt-get -y update; \
    apt-get -y upgrade; \
    apt-get -y --no-install-recommends install \
            python3 python3-pip python3-pytst python3-tk curl

# don't forget in production
RUN set -ex; apt-get -y autoremove; apt-get -y clean; rm -rf /var/lib/apt/lists/*

RUN pip install -y -r requirements.txt

RUN curl -LsSf https://astral.sh/ruff/install.sh | sh


