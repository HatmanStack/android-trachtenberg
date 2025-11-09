import React, { useEffect, useState, useLayoutEffect } from 'react';
import { StyleSheet, View, Alert } from 'react-native';
import { Surface, Text, IconButton } from 'react-native-paper';
import { useNavigation } from '@react-navigation/native';
import { useAppStore } from '../store/appStore';
import { AnswerButton } from '../components/AnswerButton';
import { HintDisplay } from '../components/HintDisplay';
import { HighlightedText } from '../components/HighlightedText';
import { COLORS, SPACING } from '../theme/constants';

export default function PracticeScreen() {
  const navigation = useNavigation();
  const currentEquation = useAppStore((state) => state.currentEquation);
  const answerProgress = useAppStore((state) => state.answerProgress);
  const answerChoices = useAppStore((state) => state.answerChoices);
  const generateNewProblem = useAppStore((state) => state.generateNewProblem);
  const submitAnswer = useAppStore((state) => state.submitAnswer);

  // Hint state
  const hintsEnabled = useAppStore((state) => state.hintsEnabled);
  const hintHelpShown = useAppStore((state) => state.hintHelpShown);
  const setHintHelpShown = useAppStore((state) => state.setHintHelpShown);
  const hintQuestion = useAppStore((state) => state.hintQuestion);
  const hintResult = useAppStore((state) => state.hintResult);
  const hintHighlightIndices = useAppStore((state) => state.hintHighlightIndices);
  const move = useAppStore((state) => state.move);
  const moveCount = useAppStore((state) => state.moveCount);
  const nextHint = useAppStore((state) => state.nextHint);

  const [feedbackText, setFeedbackText] = useState('');

  // Configure header with Settings navigation button (for stack navigator on mobile)
  useLayoutEffect(() => {
    navigation.setOptions({
      headerRight: () => (
        <IconButton
          icon="cog"
          size={24}
          onPress={() => navigation.navigate('Settings' as never)}
        />
      ),
    });
  }, [navigation]);

  // Generate first problem on mount
  useEffect(() => {
    if (!currentEquation) {
      generateNewProblem();
    }
  }, [currentEquation, generateNewProblem]);

  const handleHintPress = () => {
    // Show help message on first hint click (Android line 291-293)
    if (!hintHelpShown && move === 1) {
      Alert.alert('Hint Help', 'Touch hint to get next step');
      setHintHelpShown(true);
    }

    // Advance to next hint if available
    if (move < moveCount) {
      nextHint();
    }
  };

  const handleAnswerPress = (buttonIndex: number) => {
    // Enforce hint viewing when hints enabled (Android line 347)
    // Must view at least 9 hints before answering
    if (hintsEnabled && move < 9) {
      Alert.alert('View Hints', 'Touch the Hint to Receive More Hints');
      return;
    }

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
        {hintsEnabled && hintHighlightIndices.length > 0 ? (
          <HighlightedText
            text={currentEquation || 'Loading...'}
            highlightIndices={hintHighlightIndices}
            highlightColor={COLORS.accent}
            style={styles.equation}
          />
        ) : (
          <Text variant="headlineLarge" style={styles.equation}>
            {currentEquation || 'Loading...'}
          </Text>
        )}
      </Surface>

      {/* Hint Display */}
      <HintDisplay
        question={hintQuestion}
        result={hintResult}
        visible={hintsEnabled}
        onPress={handleHintPress}
      />

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
