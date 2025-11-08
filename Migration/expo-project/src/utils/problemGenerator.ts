/**
 * Problem generation utilities for Practice mode
 * Ported from Android PracticeActivity.operatorEquation()
 */

export interface PracticeProblem {
  firstNumber: number;  // 4-digit number (1000-9999)
  secondNumber: number; // 3-digit number (100-999)
  answer: number;       // firstNumber * secondNumber
}

/**
 * Generates a random multiplication problem
 * First number: 4 digits (1000-9999)
 * Second number: 3 digits (100-999)
 * Matches Android's operatorEquation() logic (with bug fix)
 */
export function generateProblem(): PracticeProblem {
  let firstNumber = Math.floor(Math.random() * 10000);
  let secondNumber = Math.floor(Math.random() * 1000);

  // Ensure minimum digit counts (corrected from Android bug)
  while (firstNumber < 1000 || secondNumber < 100) {
    firstNumber = Math.floor(Math.random() * 10000);
    secondNumber = Math.floor(Math.random() * 1000);
  }

  const answer = firstNumber * secondNumber;

  return {
    firstNumber,
    secondNumber,
    answer,
  };
}

/**
 * Formats a problem as an equation string
 * Uses × symbol to match tutorial
 */
export function formatEquation(problem: PracticeProblem): string {
  return `${problem.firstNumber} × ${problem.secondNumber}`;
}

/**
 * Validates a problem meets digit requirements
 */
export function isValidProblem(problem: PracticeProblem): boolean {
  return (
    problem.firstNumber >= 1000 &&
    problem.firstNumber < 10000 &&
    problem.secondNumber >= 100 &&
    problem.secondNumber < 1000 &&
    problem.answer === problem.firstNumber * problem.secondNumber
  );
}
