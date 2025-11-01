# PrivSis-Project1
Para correr deve se entrar na pasta server e correr os seguintes comandos


```powershell
docker run --name testPostgres -e POSTGRES_PASSWORD=password \ -e POSTGRES˙DB=pds_proj_1 -d -p 5432:5432 postgres
cd Project1Server/server
mvn clean complie package
mvn spring-boot:run
```

O servidor sobe por defeito na porta 8000. Base URL: `http://localhost:8000/api/dp`.

Deve tambem correr o populate 

python3 -m venv .
source bin/activate
pip3 install sqlalchemy
pip3 install psycopg2-binary
python3 populatetables.py

# Diferencial Privacy
Os valores por defeito em `src/main/resources/application.properties` são:

- `privacy.epsilon=1.0` – epsilon usado nas queries com differential privacy.
- `privacy.budget=5.0` – orçamento total inicial gerido por `PrivacyBudgetManager`.

Descrição dos endpoints principais

GET `/api/dp`
Retorna a lista completa de `MedData` .

GET `/api/dp/{id}`
Retorna um registo `MedData` pelo seu id.

GET `/api/dp/budget`
Retorna o orçamento de privacidade restante.

POST `/api/dp/reset`
Reinicia o orçamento de privacidade para o valor passado internamente.

POST `/api/dp/count`
Faz uma contagem com differential privacy. JSON exemplo:

```json
{
	"attribute": "age",
	"value": "45"
}
```

`attribute` pode ser: `age`, `gender`, `diagnosis`, `postal_code`, `address`, `name`, `email`.
Para `age` o `value` deve ser um número.

Resposta: `CountResponse` com o valor com ruido, sensibilidade e budget restante.

POST `/api/dp/grouped-count`
Faz uma contagem agrupada por dois atributos com proteção. JSON exemplo:

```json
{
	"attribute1": "age",
	"value1": "45",
	"attribute2": "gender",
	"value2": "Male"
}
```

As combinações suportadas incluem pares entre `age`, `gender`, `diagnosis`, `postal_code`, `address`, `name`, `email`. A resposta é `GroupedCountResponse` com o count com ruido.

POST `/api/dp/average`
Calcula a média da idade com difeerencial privacy. JSON exemplo para média simples:

```json
{ "attribute": "age" }
```

Exemplo com filtro por género:

```json
{ "attribute": "age:gender:Male" }
```

O endpoint divide o epsilon entre soma e contagem (`epsilonPerQuery = epsilon / 2`) e aplica ruído a ambos. Retorna `AverageResponse` com a média ruidosa, sensibilidade usada e budget restante.

POST `/api/dp/average-raw`
Retorna a média real (sem ruído).

POST `/api/dp/grouped-count-raw`
Retorna contagens reais (sem ruído) utilizado para testes.



# Dados originais

MedData (dados originais)

GET `/api/meddata`
Retorna todos os registos `MedData`.

GET `/api/meddata/{id}`
Retorna um registo por id.

POST `/api/meddata/count`
Conta registos nos dados originais. JSON exemplo:

```json
{
	"attribute": "gender",
	"value": "Male"
}
```

As `attribute` suportadas são: `gender`, `diagnosis`, `age`, `postal_code`.


# De Intentified
GET `/api/deidentified`
Retorna todos os registos deindentified (`MedDataDeidentified`).

GET `/api/deidentified/{id}`
Retorna um registo desidentificado por id.

POST `/api/deidentified/count`
Conta registos nos dados deindentified. JSON igual ao do endpoint `/api/meddata/count`.

# Linkage Attack

GET `/api/linkage/attack`
Executa o ataque de linkage contra os dados originais, cruzando `postal_code` + `gender` entre `MedData` e `WorkData`. Retorna uma lista de `LinkageResult` com pares possíveis.

GET `/api/linkage/attack-di`
Executa o mesmo ataque, mas usando os dados deindentified (`MedDataDeidentified`) em vez dos originais.

GET `/api/linkage/stats`
Retorna estatísticas do ataque (total registos, matches, taxa de sucesso, unicidade).

GET `/api/linkage/stats-di`
Estatísticas do ataque aplicadas aos dados deindentified.


# K Anonymity

GET `/api/kanonymity?k=<valor>`
Gera e retorna uma lista de `KAnonymizedData` aplicando um algoritmo do tipo Mondrian para obter k‑anonymity com o `k` pedido (valor por defeito = 2). Exemplo:

`GET http://localhost:8000/api/kanonymity?k=3`

GET `/api/kanonymity/stats?k=<valor>`
Calcula estatísticas do k‑anonymity (registos totais, retidos, suprimidos, taxa de retenção, número de classes de equivalência, média do tamanho das classes, técnica usada).

GET `/api/kanonymity/test-attack?k=<valor>`
Executa uma teste de linkage entre os dados k‑anonimizados e `WorkData` usando `postal_code prefix` + `gender` para medir quantos registos podem ser ligados.

GET `/api/kanonymity/attack-stats?k=<valor>`
Retorna métricas sobre a vulnerabilidade após aplicação de k‑anonymity (success rate, uniqueness rate, etc.).









