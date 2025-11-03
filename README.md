# Scorio – GitHub Repository Scoring API

Scorio is a Spring Boot application that ranks GitHub repositories by combining popularity and recent activity.  
It uses the GitHub Search API and applies scoring algorithms to calculate a relevance score for each repository.

---

## Features

- Search repositories by language and creation date
- Two scoring algorithms:
   - **default** – logarithmic weighting with time decay
   - **flat** – simple additive model
- Extendable architecture for new scoring strategies
- Optional GitHub token for higher API rate limits
- Built with **Java 25** and **Spring Boot 3**

---

## Run with Docker

```bash
mvn clean package
docker build -t scorio .
docker run -p 8080:8080 -e GITHUB_TOKEN=ghp_your_token scorio
```

## Rate Limits

Without a token, GitHub limits to **~60 requests/hour**.  
Authenticated requests allow up to **5,000 requests/hour**.

To set up your access token follow [github instruction](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-fine-grained-personal-access-token)

---

## Run Locally (without Docker)

```bash
# optional
export GITHUB_TOKEN=ghp_your_token
# build and run
mvn spring-boot:run
```
Then open:
http://localhost:8080/api/repositories/score?language=Java&created_after=2024-01-01
---

## Example Request

```
GET http://localhost:8080/api/repositories/score?language=Java&created_after=2024-01-01&per_page=1
```
## Example Response

```
{
   "page": 1,
   "perPage": 1,
   "total": 4778855,
   "items": [
      {
            "fullName": "apple/pkl",
            "htmlUrl": "https://github.com/apple/pkl",
            "language": "Java",
            "stars": 10920,
            "forks": 338,
            "createdAt": "2024-01-19T17:28:45Z",
            "pushedAt": "2025-10-31T19:28:03Z",
            "score": 30.22940670287076
      }
   ]
}
```
## Scoring Overview

Default algorithm:
```
score = 2*log(1 + stars) + 1*log(1 + forks) + 3*exp(-days_since_update / 30) + bonus
```

Flat algorithm:
```
score = stars + forks + (100 if updated ≤ 7 days else 0)
```

Add a Custom Scoring Algorithm:

Create a new class implementing ScoringStrategy and annotate it with @Component.
The algorithm will be automatically registered and can be used via:
```
?algo=newName
```
---



## License

MIT License © 2025 – Created by Alexander Schneider as part of a coding task.