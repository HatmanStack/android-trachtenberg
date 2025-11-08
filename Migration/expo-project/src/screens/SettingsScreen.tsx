import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Surface, Text } from 'react-native-paper';

export default function SettingsScreen() {
  return (
    <View style={styles.container}>
      <Surface style={styles.surface}>
        <Text variant="headlineMedium">Settings Screen</Text>
        <Text variant="bodyMedium">Settings will be implemented in Phase 5</Text>
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
