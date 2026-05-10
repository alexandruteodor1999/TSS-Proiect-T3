package pachet.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class LoanEvaluatorTest {

    LoanEvaluator evaluator = new LoanEvaluator();

    // CE pentru age: A1 (age < 18) -> REJECTED
    @Test
    void testAgeTooYoung() {
        String result = evaluator.evaluateEligibility(
                17,      // age < 18 => REJECTED
                5000.0,  // income ok
                700,     // creditScore ok
                0        // activeLoans ok
        );
        assertEquals("REJECTED", result);
    }

    // CE pentru age: A2 (18 ≤ age ≤ 70) - aici alegem un caz în care restul dă APPROVED
    @Test
    void testAgeValidMiddle() {
        String result = evaluator.evaluateEligibility(
                30,      // age valid
                5000.0,
                700,
                0
        );
        assertEquals("APPROVED", result);
    }

    // CE pentru age: A3 (age > 70) -> REJECTED
    @Test
    void testAgeTooOld() {
        String result = evaluator.evaluateEligibility(
                71,      // age > 70
                5000.0,
                700,
                0
        );
        assertEquals("REJECTED", result);
    }

    // Frontiere pe age: exact 18
    @Test
    void testBoundaryAgeExactly18() {
        String result = evaluator.evaluateEligibility(
                18,
                5000.0,
                700,
                0
        );
        assertEquals("APPROVED", result);
    }

    // Frontiere pe age: exact 70
    @Test
    void testBoundaryAgeExactly70() {
        String result = evaluator.evaluateEligibility(
                70,
                5000.0,
                700,
                0
        );
        assertEquals("APPROVED", result);
    }
    
    //monthlyIncome
    // CE pentru monthlyIncome: venit prea mic (< 2000) -> REJECTED
    @Test
    void testIncomeTooLow() {
        String result = evaluator.evaluateEligibility(
                30,       // age valid
                1500.0,   // income < 2000 => REJECTED
                700,      // creditScore ok (mare)
                0         // activeLoans ok
        );
        assertEquals("REJECTED", result);
    }

    // CE pentru monthlyIncome: venit mediu [2000, 5000) -> MANUAL_REVIEW (în condițiile noastre)
    @Test
    void testIncomeMediumManualReview() {
        String result = evaluator.evaluateEligibility(
                30,       // age valid
                3000.0,   // 2000 <= income < 5000
                650,      // creditScore >= 600 și < 700
                1         // activeLoans <= 1
        );
        assertEquals("MANUAL_REVIEW", result);
    }

    // CE pentru monthlyIncome: venit mare (>= 5000) -> APPROVED (dacă restul e optim)
    @Test
    void testIncomeHighApproved() {
        String result = evaluator.evaluateEligibility(
                30,       // age valid
                6000.0,   // income >= 5000
                720,      // creditScore >= 700
                0         // activeLoans == 0
        );
        assertEquals("APPROVED", result);
    }

    // Frontiere pentru monthlyIncome: exact 2000 (limita inferioară validă)
    @Test
    void testBoundaryIncomeExactly2000() {
        String result = evaluator.evaluateEligibility(
                30,
                2000.0,   // exact la limită
                650,      // scor mediu, cu loans <= 1 => MANUAL_REVIEW
                1
        );
        assertEquals("MANUAL_REVIEW", result);
    }

    // Frontiere pentru monthlyIncome: exact 5000 (limita pentru aprobare automată)
    @Test
    void testBoundaryIncomeExactly5000() {
        String result = evaluator.evaluateEligibility(
                30,
                5000.0,   // exact la pragul de APPROVED
                700,
                0
        );
        assertEquals("APPROVED", result);
    }
    
    //creditScore
    // CE pentru creditScore: score < 600 -> REJECTED (dacă restul e ok)
    @Test
    void testCreditScoreLowRejected() {
        String result = evaluator.evaluateEligibility(
                30,       // age valid
                5000.0,   // venit suficient
                550,      // score < 600
                0         // loans ok
        );
        assertEquals("REJECTED", result);
    }

    // CE pentru creditScore: 600 <= score < 700 -> MANUAL_REVIEW (dacă loans <= 1)
    @Test
    void testCreditScoreMediumManualReview() {
        String result = evaluator.evaluateEligibility(
                30,
                4000.0,   // venit >= 2000, dar < 5000
                650,      // 600 <= score < 700
                1         // loans <= 1
        );
        assertEquals("MANUAL_REVIEW", result);
    }

    // CE pentru creditScore: score >= 700 -> APPROVED (dacă restul e optim)
    @Test
    void testCreditScoreHighApproved() {
        String result = evaluator.evaluateEligibility(
                30,
                6000.0,   // income >= 5000
                720,      // score >= 700
                0         // loans == 0
        );
        assertEquals("APPROVED", result);
    }
    
    //activeLoans
    // CE pentru activeLoans: loans >= 3 -> REJECTED
    @Test
    void testTooManyActiveLoansRejected() {
        String result = evaluator.evaluateEligibility(
                30,       // age valid
                5000.0,   // venit ok
                720,      // scor mare
                3         // activeLoans >= 3
        );
        assertEquals("REJECTED", result);
    }

    // CE pentru activeLoans: loans == 0 -> APPROVED (dacă restul e optim)
    @Test
    void testNoActiveLoansApproved() {
        String result = evaluator.evaluateEligibility(
                30,
                6000.0,
                720,
                0
        );
        assertEquals("APPROVED", result);
    }

    // CE pentru activeLoans: loans == 1 -> MANUAL_REVIEW (dacă scorul este mediu)
    @Test
    void testOneActiveLoanManualReview() {
        String result = evaluator.evaluateEligibility(
                30,
                4000.0,   // venitul nu e suficient pentru APPROVED, dar e peste 2000
                650,      // 600 <= score < 700
                1         // loans <= 1
        );
        assertEquals("MANUAL_REVIEW", result);
    }

    // CE pentru activeLoans: loans == 2 cu scor mediu -> REJECTED (nu intră la revizuire)
    @Test
    void testTwoActiveLoansWithMediumScoreRejected() {
        String result = evaluator.evaluateEligibility(
                30,
                4000.0,
                650,      // scor mediu
                2         // loans == 2 -> nu mai îndeplinește condiția activeLoans <= 1
        );
        assertEquals("REJECTED", result);
    }
    
    // Test structural: aprope APPROVED, dar activeLoans != 0 (condiția din Regula 3 este False)
    @Test
    void testAlmostApprovedButWithActiveLoan() {
        String result = evaluator.evaluateEligibility(
                30,       // age valid
                6000.0,   // monthlyIncome >= 5000.0  (True)
                720,      // creditScore >= 700       (True)
                1         // activeLoans != 0         (False pentru activeLoans == 0)
        );
        // Nu îndeplinește Regula 3 (APPROVED), dar îndeplinește Regula 4 (MANUAL_REVIEW)
        assertEquals("MANUAL_REVIEW", result);
    }
    
    // Test suplimentar: venit minim acceptat, dar prea multe credite (>=3)
    @Test
    void testIncomeAtMinimumButTooManyLoansRejected() {
        String result = evaluator.evaluateEligibility(
                30,       // age valid
                2000.0,   // exact pragul minim de venit
                720,      // scor mare
                3         // activeLoans >= 3 -> REJECTED
        );
        assertEquals("REJECTED", result);
    }
    
    // Test suplimentar: scor exact 600 si un credit activ -> MANUAL_REVIEW
    @Test
    void testCreditScoreAtBoundaryWithOneLoanManualReview() {
        String result = evaluator.evaluateEligibility(
                30,       // age valid
                3000.0,   // venit suficient pentru a nu fi respins de regula 2
                600,      // exact pragul creditScore >= 600
                1         // activeLoans <= 1
        );
        assertEquals("MANUAL_REVIEW", result);
    }
}