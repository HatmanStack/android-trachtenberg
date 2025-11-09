/**
 * Move Tracking Logic for Trachtenberg Hint System
 *
 * Ports the move tracking logic from Android's PracticeActivity.java
 * - setMove() method (lines 310-335)
 * - setIndex() method (lines 277-307)
 *
 * The move tracking system determines:
 * 1. Which "move" range applies to the current digit position (indexCount)
 * 2. Which digit indices to multiply for each move step
 */

/**
 * Move count array from Android implementation (line 311)
 * Maps indexCount (digit position) to move number boundaries
 */
const MOVES_COUNT = [0, 4, 9, 15, 20, 23, 24];

/**
 * Move indexes array from Android implementation (line 281)
 * Used to determine which digits to multiply at each move
 */
const MOVES_INDEXES = [2, 2, 2, 1, 2, 1, 0, 2, 1, 0, 1, 0, 0];

/**
 * Result type for getMoveRange function
 */
export interface MoveRange {
  startMove: number;
  moveCount: number;
}

/**
 * Result type for getDigitIndices function
 */
export interface DigitIndices {
  firstStringIndex: number;
  secondStringIndex: number;
}

/**
 * Determines the move range for a given digit position
 * Ports Android's setMove() logic (lines 310-335)
 *
 * @param indexCount - Current digit position (0-6, right to left)
 * @returns Start move and total move count for this digit
 */
export function getMoveRange(indexCount: number): MoveRange {
  // Handle indexCount 0 case
  if (indexCount === 0) {
    return {
      startMove: 0,
      moveCount: 0,
    };
  }

  // Handle out of range cases
  if (indexCount < 0 || indexCount >= MOVES_COUNT.length) {
    return {
      startMove: 0,
      moveCount: 0,
    };
  }

  // Iterate through movesCount array (Android lines 316-327)
  for (let i = 1; i < MOVES_COUNT.length; i++) {
    if (indexCount === i) {
      // Set move based on previous element (line 320)
      const startMove = MOVES_COUNT[i - 1] !== 0 ? MOVES_COUNT[i - 1] : 1;

      // Set moveCount based on current element minus 1 (line 323)
      const moveCount = MOVES_COUNT[i] - 1;

      return { startMove, moveCount };
    }
  }

  // Fallback (should not reach here with valid indexCount)
  return {
    startMove: 0,
    moveCount: 0,
  };
}

/**
 * Determines which digit indices to multiply for a given move
 * Ports Android's setIndex() logic (lines 277-307)
 *
 * @param move - Current move number (0-23)
 * @returns Indices in the first and second strings to multiply
 */
export function getDigitIndices(move: number): DigitIndices {
  // Default values (Android lines 278-279)
  let firstStringIndex = 3;
  let secondStringIndex = 2;

  // Handle out of range
  if (move < 0 || move >= 24) {
    return { firstStringIndex: 0, secondStringIndex: 0 };
  }

  // Iterate through movesIndexes array (Android lines 284-306)
  for (let i = 0; i < MOVES_INDEXES.length; i++) {
    // Check if move matches current index or index + 4 (line 286)
    if (move === i || move === i + 4) {
      // Calculate fsIndex (line 297)
      // If i is near the end of the array, use special calculation
      if (i > MOVES_INDEXES.length - 3) {
        firstStringIndex = MOVES_INDEXES[MOVES_INDEXES.length - 2] - (i % 2);
      } else {
        firstStringIndex = MOVES_INDEXES[i];
      }

      // Calculate ssIndex (line 298)
      if (i < 6) {
        secondStringIndex = 2;
      } else if (i < 9) {
        secondStringIndex = 1;
      } else if (i < 11) {
        secondStringIndex = 0;
      } else {
        secondStringIndex = MOVES_INDEXES[i + 1];
      }

      // Exit loop (line 303)
      break;
    }
  }

  return { firstStringIndex, secondStringIndex };
}

/**
 * Determines if this move should be displayed as a hint
 * Some moves are skipped based on hint preference (Android line 288)
 *
 * @param _move - Current move number (unused for now)
 * @param _previousMove - Previous move number (unused for now)
 * @param _hintsEnabled - Whether hints are enabled (unused for now)
 * @returns Whether to display this hint step
 */
export function shouldShowHint(
  _move: number,
  _previousMove: number,
  _hintsEnabled: boolean
): boolean {
  // In Android (line 288), moves that match i+4 pattern are skipped when hints enabled
  // For now, return true (this logic is handled in the loop iteration)
  return true;
}
