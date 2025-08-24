open:
	cd frontend && cursor .
	cd backend && idea64.exe .

front-ready:
	npm install && cd frontend && npm install

back-ready:
	cd backend && ./gradlew clean build

ready:
	make front-ready
	make back-ready

front:
	cd frontend && npm run start

build:
	cd backend && ./gradlew build 

# imageをビルドしてコンテナを起動
up:
	cd backend && docker compose up --build

# コンテナの停止・削除
down:
	cd backend && docker compose down

back:
	make build
	make up

# dockerのvolume(DBのデータ)も削除
down-v:
	cd backend && docker compose down -v

# postgreSQL操作
psql:
	cd backend && docker compose exec db psql -U postgres -d nippogen
