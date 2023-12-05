FROM node:latest

WORKDIR /app

RUN npm i -g pnpm@latest

COPY package*.json ./

RUN pnpm i

COPY . .

RUN pnpm run build

EXPOSE 3000

CMD [ "pnpm", "run", "start" ]