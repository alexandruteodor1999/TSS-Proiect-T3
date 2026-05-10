package pachet.logica;

public class LoanEvaluator {

    /**
     * Evaluează eligibilitatea unui client pentru un credit.
     * @param age Vârsta solicitantului (ani)
     * @param monthlyIncome Venitul lunar net (RON)
     * @param creditScore Scorul de credit (300-850)
     * @param activeLoans Numărul de credite active în prezent
     * @return Statusul cererii: APPROVED, MANUAL_REVIEW sau REJECTED
     */
    public String evaluateEligibility(int age,
                                      double monthlyIncome,
                                      int creditScore,
                                      int activeLoans) {

        String result = "REJECTED"; // valoare implicită

        // buclă simplă cu 4 "pași" logici
        for (int step = 0; step < 4; step++) {

            if (step == 0) {
                // Condiție compusă (age < 18 || age > 70)
                if (age < 18 || age > 70) {
                    result = "REJECTED";
                    break; // nu mai continuăm evaluarea
                }
            }

            else if (step == 1) {
                // Condiție compusă (monthlyIncome < 2000.0 || activeLoans >= 3)
                if (monthlyIncome < 2000.0 || activeLoans >= 3) {
                    result = "REJECTED";
                    break;
                }
            }

            else if (step == 2) {
                // Condiție compusă pentru APPROVED
                if (creditScore >= 700 && monthlyIncome >= 5000.0 && activeLoans == 0) {
                    result = "APPROVED";
                    break;
                }
            }

            else if (step == 3) {
                // Aici folosim un if cu else (condiție simplă + compusă)
                if (creditScore >= 600 && activeLoans <= 1) {
                    result = "MANUAL_REVIEW";
                } else {
                    // condiție simplă derivată: "nu îndeplinește criteriul de revizuire"
                    // lăsăm result = "REJECTED"
                }
            }
        }

        return result;
    }
}