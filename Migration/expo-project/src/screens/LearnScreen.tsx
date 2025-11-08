import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Surface, Text } from 'react-native-paper';

export default function LearnScreen() {
  return (
    <View style={styles.container}>
      <Surface style={styles.surface}>
        <Text variant="headlineMedium">Learn Screen</Text>
        <Text variant="bodyMedium">Tutorial system will be implemented in Phase 2</Text>
      </Surface>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  surface: {
    padding: 24,
    borderRadius: 8,
    elevation: 4,
  },
});
