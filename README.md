# LoanEvaluator – Proiect Testarea Sistemelor Software (TSS)

Acest depozit conține implementarea și testele pentru componenta **LoanEvaluator**, dezvoltate în cadrul cursului *Testarea sistemelor software*.

Scopul este proiectarea și testarea unei funcții care validează și evaluează cereri de credit pe baza mai multor parametri de intrare, folosind atât **testare funcțională**, cât și **testare structurală**, plus **mutation testing** și **integrare continuă (CI)**.

## Structura proiectului

- `src/main/java/pachet/logica/LoanEvaluator.java`  
  Clasa principală care evaluează cererile de credit pe baza parametrilor de intrare.
- `src/test/java/pachet/logica/LoanEvaluatorTest.java`  
  Suita de teste JUnit care acoperă cazurile de test funcționale și structurale.
- `pom.xml`  
  Configurația Maven, inclusiv plugin‑urile pentru **JaCoCo** (code coverage) și **PIT** (mutation testing).
- `.github/workflows/maven.yml`  
  Workflow GitHub Actions care rulează testele Maven la fiecare push / pull request.

## Tehnologii utilizate

- Java (JDK 11)  
- Maven  
- JUnit  
- JaCoCo – analiză de acoperire a codului  
- PIT – mutation testing  
- GitHub Actions – integrare continuă

## Cum compilezi proiectul și rulezi testele local

Din rădăcina proiectului (unde se află `pom.xml`):

```bash
mvn test
```

Această comandă:

- compilează proiectul;
- rulează toate testele JUnit;
- generează raportul JaCoCo în `target/site/jacoco`.

Poți deschide raportul principal JaCoCo în browser din fișierul:

- `target/site/jacoco/index.html`

## Cum rulezi mutation testing cu PIT

Pentru a rula PIT pe proiectul Maven, folosești:

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

PIT va genera un raport HTML în:

- `target/pit-reports/<timestamp>/index.html`

Deschide `index.html` în browser pentru a vedea:

- scorul global de mutație;
- mutanții uciși și cei supraviețuitori pentru `LoanEvaluator`.

## Integrare continuă (GitHub Actions)

Depozitul folosește un workflow simplu **Java CI with Maven**.

Fișier workflow: `.github/workflows/maven.yml`

La fiecare **push** sau **pull request** în depozit, GitHub Actions:

1. Face checkout la cod.
2. Configurează JDK 11 (Temurin).
3. Rulează:

   ```bash
   mvn -B test --file pom.xml
   ```

Ultimele execuții ale workflow‑ului și statusul lor pot fi vizualizate în tab‑ul **Actions** al depozitului.
