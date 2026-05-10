# Proiect T3 – Testare unitară în Java  - Teodor Alexandru-Mihai
## Evaluator de credite – `LoanEvaluator`

Acest proiect face parte din tema T3 („Testare unitară în Java”) la disciplina **Testarea sistemelor software**. Scopul este să definesc o funcționalitate de complexitate medie, să o implementez în Java și să o testez folosind tehnicile discutate la curs: testare funcțională, testare structurală, mutation testing și integrare continuă.

Funcționalitatea aleasă este un **evaluator de eligibilitate pentru credite bancare**. Clasa principală se numește `LoanEvaluator` și conține logica de decizie pentru aprobarea sau respingerea unei cereri de credit.

***

## 1. Specificația funcției `LoanEvaluator`

### 1.1. Descriere generală

Componenta centrală este clasa:

```java
public class LoanEvaluator {
    public String evaluateEligibility(int age,
                                      double monthlyIncome,
                                      int creditScore,
                                      int activeLoans) {
        ...
    }
}
```

Metoda `evaluateEligibility` primește patru parametri:

- `age` – vârsta solicitantului (în ani);  
- `monthlyIncome` – venitul net lunar (în lei);  
- `creditScore` – scorul de credit;  
- `activeLoans` – numărul de credite active curente.

Metoda întoarce unul dintre următoarele string-uri:

- `APPROVED` – cerere aprobată automat;  
- `MANUAL_REVIEW` – cerere care trebuie analizată manual de un ofițer;  
- `REJECTED` – cerere respinsă.

### 1.2. Reguli de business (specificație în cuvinte)

Regulile după care se ia decizia sunt:

1. **Vârstă validă**  
   - Dacă `age < 18` sau `age > 70`, cererea este imediat respinsă (`REJECTED`).  
   - Doar clienții cu vârsta între 18 și 70 de ani (inclusiv) pot merge mai departe în procesul de evaluare.

2. **Filtru minim de venit și credite active**  
   - Dacă `monthlyIncome < 2000` sau `activeLoans >= 3`, cererea este respinsă (`REJECTED`), indiferent de scorul de credit.

3. **Client ideal – aprobare automată**  
   - Dacă `creditScore >= 700`, `monthlyIncome >= 5000` și `activeLoans == 0`, cererea este aprobată automat (`APPROVED`).

4. **Client de risc mediu – analiză manuală**  
   - Dacă `creditScore >= 600` și `activeLoans <= 1`, dar nu sunt îndeplinite condițiile de client ideal, cererea se trimite la revizuire manuală (`MANUAL_REVIEW`).

5. **Cazuri rămase**  
   - În toate celelalte situații (de exemplu scor prea mic, combinații nefavorabile de venit și credite), cererea este respinsă (`REJECTED`).

### 1.3. Parametri și restricții – tabel

| Parametru       | Descriere                        | Domeniu / restricții principale                               |
|-----------------|----------------------------------|----------------------------------------------------------------|
| `age`           | vârsta solicitantului            | `<18` sau `>70` → respins; `18–70` → analiză normală          |
| `monthlyIncome` | venit net lunar (lei)            | `<2000` → respins; `2000–4999` → minim; `>=5000` → venit mare |
| `creditScore`   | scor de credit                   | `<600` → risc mare; `600–699` → risc mediu; `>=700` → foarte bun |
| `activeLoans`   | număr de credite active          | `>=3` → respins; `0` → ideal; `1–2` → acceptabil pentru risc mediu |

***

## 2. Structura proiectului

Proiectul este un **proiect Maven** Java, organizat astfel:

```text
src/
  main/
    java/
      pachet/logica/
        LoanEvaluator.java
  test/
    java/
      pachet/logica/
        LoanEvaluatorTest.java

pom.xml
.github/
  workflows/
    maven.yml
```

- `LoanEvaluator.java` – implementarea logicii de business;  
- `LoanEvaluatorTest.java` – testele unitare JUnit 5;  
- `pom.xml` – configurare Maven, dependențe JUnit 5, JaCoCo, PIT;  
- `.github/workflows/maven.yml` – workflow pentru integrare continuă (GitHub Actions).

***

## 3. Testare funcțională

În testarea funcțională am lucrat după specificație (black-box), fără să mă uit inițial în cod. Am folosit:

- partiționare în clase de echivalență;  
- analiza valorilor de frontieră.

### 3.1. Clase de echivalență individuale

#### 3.1.1. Vârsta (`age`)

- **A1 – vârstă prea mică**: `age < 18` → cererea trebuie respinsă.  
- **A2 – vârstă validă**: `18 ≤ age ≤ 70` → se continuă evaluarea.  
- **A3 – vârstă prea mare**: `age > 70` → cererea trebuie respinsă.

#### 3.1.2. Venitul lunar (`monthlyIncome`)

- **I1 – venit sub prag**: `monthlyIncome < 2000` → respins.  
- **I2 – venit minim/mediu**: `2000 ≤ monthlyIncome < 5000`.  
- **I3 – venit mare**: `monthlyIncome ≥ 5000`.

#### 3.1.3. Scorul de credit (`creditScore`)

- **C1 – scor scăzut**: `creditScore < 600`.  
- **C2 – scor mediu**: `600 ≤ creditScore < 700`.  
- **C3 – scor mare**: `creditScore ≥ 700`.

#### 3.1.4. Numărul de credite active (`activeLoans`)

- **L1 – niciun credit activ**: `activeLoans = 0`.  
- **L2 – puține credite**: `activeLoans = 1` (sau 2, în funcție de interpretare).  
- **L3 – prea multe credite**: `activeLoans ≥ 3` → respins.

### 3.2. Clase de echivalență globale

Pe baza claselor individuale, am definit câteva **clase globale** care corespund scenariilor relevante:

- **G1 – client ideal (APPROVED)**  
  - age în A2, income în I3, score în C3, loans în L1;  
  - așteptat: `APPROVED`.

- **G2 – client de risc mediu (MANUAL_REVIEW)**  
  - age în A2, income în I2 sau I3, score în C2, loans în L2;  
  - așteptat: `MANUAL_REVIEW`.

- **G3 – vârstă invalidă (REJECTED)**  
  - age în A1 sau A3;  
  - așteptat: `REJECTED`.

- **G4 – venit sub prag (REJECTED)**  
  - age în A2, income în I1;  
  - așteptat: `REJECTED`.

- **G5 – prea multe credite active (REJECTED)**  
  - age în A2, loans în L3;  
  - așteptat: `REJECTED`.

- **G6 – scor de credit prea mic (REJECTED)**  
  - age în A2, score în C1;  
  - așteptat: `REJECTED`.

### 3.3. Analiza valorilor de frontieră

Am identificat frontierele:

- pentru vârstă: 18 și 70;  
- pentru venit: 2000 și 5000;  
- pentru scor: 600 și 700;  
- pentru număr de credite: 3.

Exemple de teste de frontieră:

- `age = 17` → respins;  
- `age = 18` cu restul parametrilor buni → nu se respinge pe vârstă;  
- `age = 70` → încă acceptat;  
- `age = 71` → respins.

- `monthlyIncome = 1999` → respins;  
- `monthlyIncome = 2000` → test pentru limita inferioară acceptată;  
- `monthlyIncome = 5000` → prag pentru venit mare.

- `creditScore = 599` vs `600` vs `700` → se testează trecerea între scor scăzut, mediu și mare.

- `activeLoans = 2` vs `3` → se verifică pragul la care încep respingerile automat pe număr de credite.

### 3.4. Tabel de teste funcționale

O selecție de teste (în cod există mai multe, dar ideea este asta):

| ID  | age | income | score | loans | Clase globale acoperite | Rezultat așteptat |
|-----|-----|--------|-------|-------|-------------------------|-------------------|
| T1  | 17  | 5000   | 750   | 0     | G3 (A1)                 | REJECTED          |
| T2  | 71  | 5000   | 750   | 0     | G3 (A3)                 | REJECTED          |
| T3  | 30  | 1500   | 750   | 0     | G4 (I1)                 | REJECTED          |
| T4  | 30  | 5000   | 750   | 3     | G5 (L3)                 | REJECTED          |
| T5  | 30  | 4000   | 550   | 0     | G6 (C1)                 | REJECTED          |
| T6  | 30  | 5500   | 750   | 0     | G1                      | APPROVED          |
| T7  | 30  | 3000   | 650   | 1     | G2                      | MANUAL_REVIEW     |

Toate aceste teste sunt implementate ca metode `@Test` în `LoanEvaluatorTest`.

***

## 4. Testare structurală

După partea de testare funcțională, am trecut la white-box, folosind implementarea efectivă a metodei `evaluateEligibility`.

### 4.1. Graful de flux de control (CFG) – descriere

Metoda este o secvență de condiții `if`:

1. verificarea vârstei (`age < 18 || age > 70`);  
2. verificarea venitului și a numărului de credite (`monthlyIncome < 2000 || activeLoans >= 3`);  
3. verificarea „clientului ideal” (`creditScore >= 700 && monthlyIncome >= 5000 && activeLoans == 0`);  
4. verificarea „clientului de risc mediu” (`creditScore >= 600 && activeLoans <= 1`).

Pentru fiecare `if` există o ramură True și una False, iar fiecare return este un nod de ieșire. CFG-ul respectă convențiile din curs (noduri numerotate și arce între ele).

### 4.2. Statement coverage

Statement coverage cere ca fiecare instrucțiune să fie executată cel puțin o dată. Cu testele T1–T7:

- sunt acoperite toate ramurile de return (`REJECTED`, `APPROVED`, `MANUAL_REVIEW`);  
- toate `if`-urile sunt evaluate cel puțin o dată.

JaCoCo raportează 100% acoperire la nivel de instrucțiune pentru `LoanEvaluator`.

### 4.3. Branch coverage

Pentru branch coverage trebuie ca fiecare ramură True/False a fiecărui `if` să fie exercitată de cel puțin un test.

Pe scurt:

- `if (age < 18 || age > 70)`  
  - True: T1, T2;  
  - False: T3, T4, T5, T6, T7.

- `if (monthlyIncome < 2000 || activeLoans >= 3)`  
  - True: T3 (venit mic), T4 (credite multe);  
  - False: T1, T2, T5, T6, T7.

- `if (creditScore >= 700 && monthlyIncome >= 5000 && activeLoans == 0)`  
  - True: T6;  
  - False: T1, T2, T3, T4, T5, T7.

- `if (creditScore >= 600 && activeLoans <= 1)`  
  - True: T7;  
  - False: T1, T2, T3, T4, T5, T6.

Acest set de teste acoperă toate ramurile importante din logică.

### 4.4. Condition coverage – observație

Fiecare condiție compusă are sub-condiții. De exemplu:

- `age < 18` / `age > 70`  
- `monthlyIncome < 2000` / `activeLoans >= 3`  
- `creditScore >= 700`, `monthlyIncome >= 5000`, `activeLoans == 0`  
- `creditScore >= 600`, `activeLoans <= 1`

Prin combinația de teste, fiecare sub-condiție ajunge atât pe True, cât și pe False (ex.: există teste cu vârstă <18 și ≥18, cu venit sub și peste 2000, cu loans ≥3 și <3, etc.). Nu am pus toate într-un tabel detaliat, dar logica este aceeași ca în exemplul de la curs.

***

## 5. Code coverage (JaCoCo)

Plugin-ul **JaCoCo** este configurat în `pom.xml`. După:

```bash
mvn test
```

se generează un raport în:

```text
target/site/jacoco/index.html
```

În acest raport:

- `LoanEvaluator` are 100% acoperire la nivel de instrucțiune;  
- acoperirea la nivel de ramură este foarte bună (ramurile principale sunt acoperite de suitele de teste).

Raportul JaCoCo este util ca verificare independentă că testele scrise chiar trec prin logica de decizie și nu „uită” anumite ramuri.

***

## 6. Mutation testing (PIT)

Pentru a verifica „puterea” testelor, am folosit **PIT** (pitest) ca plugin Maven:

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

Se generează un raport HTML în:

```text
target/pit-reports/<timestamp>/index.html
```

PIT introduce mutanți în cod (de exemplu schimbări de operatori, inversări de condiții etc.) și verifică dacă testele îi detectează.

Rezultatele, pe scurt:

- majoritatea mutanților au fost omorâți de testele existente;  
- pentru câțiva mutanți supraviețuitori (de exemplu schimbări subtile la `>=` vs `>`), am adăugat teste suplimentare pe frontiere (cum ar fi `creditScore = 700` sau `activeLoans = 3`), pentru a-i omorî;  
- după adăugarea testelor de frontieră, mutation score-ul a crescut semnificativ, ceea ce arată că testele sunt sensibile la modificări în logica de decizie.

***

## 7. Integrare continuă (GitHub Actions)

Proiectul folosește un workflow GitHub Actions definit în:

```text
.github/workflows/maven.yml
```

Workflow-ul:

- rulează la `push` și `pull_request`;  
- folosește runner `ubuntu-latest`;  
- instalează JDK 11;  
- rulează `mvn -B test --file pom.xml`.

Astfel, la fiecare modificare în repository, testele sunt rulate automat. Dacă apare o eroare, pipeline-ul devine roșu, iar problema poate fi corectată imediat.

***

## 8. Utilizarea unui asistent AI

Pe parcursul proiectului am folosit un asistent AI de tip chatbot pentru:

- clarificarea cerințelor temei T3;  
- idei de structură pentru teste și README;  
- ajutor la configurarea unor tool-uri (Maven, JaCoCo, PIT, GitHub Actions).

Codul, testele și deciziile finale îmi aparțin. Am folosit AI-ul mai degrabă ca „sparring partner” pentru idei și verificări rapide, nu ca generator automat de proiect complet. Am verificat manual tot ce am integrat în proiect și am validat cu JaCoCo și PIT că testele chiar acoperă logica din `LoanEvaluator`.

***

## 9. Concluzii

În acest proiect am trecut prin toți pașii importanți ai testării unei funcționalități:

- am pornit de la o specificație clară;  
- am proiectat teste funcționale pe baza claselor de echivalență și a valorilor de frontieră;  
- am analizat acoperirea structurală (statement și branch coverage) și am verificat-o cu JaCoCo;  
- am folosit mutation testing (PIT) pentru a evalua cât de robuste sunt testele;  
- am configurat un pipeline de integrare continuă care rulează automat testele la fiecare push.

Rezultatul este o suită de teste care acoperă bine logica `LoanEvaluator` și un README care documentează întregul proces direct în repository, fără documentații suplimentare în altă parte.
