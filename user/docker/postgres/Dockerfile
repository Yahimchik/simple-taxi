FROM postgres:17

# Устанавливаем необходимые инструменты и компилируем расширение pg_uuidv7
RUN apt-get update && apt-get install -y \
    postgresql-server-dev-17 \
    build-essential \
    git \
 && git clone https://github.com/fboulnois/pg_uuidv7.git \
 && cd pg_uuidv7 && make && make install \
 && cd .. && rm -rf pg_uuidv7 \
 && apt-get clean && rm -rf /var/lib/apt/lists/*
