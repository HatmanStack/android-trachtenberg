import React, { useEffect, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import { Surface, Text } from 'react-native-paper';
import { useAppStore } from '../store/appStore';
import { AnswerButton } from '../components/AnswerButton';
import { COLORS, SPACING } from '../theme/constants';

export default function PracticeScreen() {
  const currentEquation = useAppStore((state) => state.currentEquation);
  const answerProgress = useAppStore((state) => state.answerProgress);
  const answerChoices = useAppStore((state) => state.answerChoices);
  const generateNewProblem = useAppStore((state) => state.generateNewProblem);
  const submitAnswer = useAppStore((state) => state.submitAnswer);

  const [feedbackText, setFeedbackText] = useState('');

  // Generate first problem on mount
  useEffect(() => {
    if (!currentEquation) {
      generateNewProblem();
    }
  }, [currentEquation, generateNewProblem]);

  const handleAnswerPress = (buttonIndex: number) => {
    const result = submitAnswer(buttonIndex);

    if (result.isCorrect) {
      setFeedbackText(result.isComplete ? 'Complete!' : 'Correct!');
    } else {
      setFeedbackText('Wrong');
    }

    // Clear feedback after delay
    setTimeout(() => setFeedbackText(''), 1000);
  };

  return (
    <View style={styles.container}>
      {/* Equation Display */}
      <Surface style={styles.equationSurface}>
        <Text variant="headlineLarge" style={styles.equation}>
          {currentEquation || 'Loading...'}
        </Text>
      </Surface>

      {/* Answer Progress */}
      <Surface style={styles.progressSurface}>
        <Text variant="headlineMedium" style={styles.progress}>
          {answerProgress || '?'}
        </Text>
      </Surface>

      {/* Feedback Text */}
      {feedbackText && (
        <Text
          variant="titleLarge"
          style={[
            styles.feedback,
            feedbackText === 'Wrong' ? styles.feedbackWrong : styles.feedbackCorrect,
          ]}
        >
          {feedbackText}
        </Text>
      )}

      {/* Answer Buttons */}
      <View style={styles.buttonsContainer}>
        <View style={styles.buttonRow}>
          <AnswerButton
            value={answerChoices[0]}
            onPress={() => handleAnswerPress(0)}
          />
          <AnswerButton
            value={answerChoices[1]}
            onPress={() => handleAnswerPress(1)}
          />
        </View>
        <View style={styles.buttonRow}>
          <AnswerButton
            value={answerChoices[2]}
            onPress={() => handleAnswerPress(2)}
          />
          <AnswerButton
            value={answerChoices[3]}
            onPress={() => handleAnswerPress(3)}
          />
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
    padding: SPACING.md,
  },
  equationSurface: {
    padding: SPACING.xl,
    marginBottom: SPACING.md,
    elevation: 4,
    borderRadius: 8,
    alignItems: 'center',
  },
  equation: {
    fontWeight: 'bold',
    textAlign: 'center',
  },
  progressSurface: {
    padding: SPACING.xl,
    marginBottom: SPACING.md,
    elevation: 4,
    borderRadius: 8,
    alignItems: 'center',
    minHeight: 100,
    justifyContent: 'center',
  },
  progress: {
    fontWeight: 'bold',
    color: COLORS.primary,
    textAlign: 'center',
  },
  feedback: {
    textAlign: 'center',
    marginBottom: SPACING.md,
    fontWeight: 'bold',
  },
  feedbackCorrect: {
    color: '#4caf50',
  },
  feedbackWrong: {
    color: COLORS.accent,
  },
  buttonsContainer: {
    flex: 1,
    justifyContent: 'center',
  },
  buttonRow: {
    flexDirection: 'row',
    marginVertical: SPACING.sm,
  },
});
