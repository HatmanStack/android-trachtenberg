import React from 'react';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';
import { useTheme } from 'react-native-paper';
import LearnScreen from '@screens/LearnScreen';
import PracticeScreen from '@screens/PracticeScreen';
import SettingsScreen from '@screens/SettingsScreen';

const Tab = createMaterialTopTabNavigator();

export const TabNavigator: React.FC = () => {
  const theme = useTheme();

  return (
    <Tab.Navigator
      screenOptions={{
        tabBarActiveTintColor: theme.colors.primary,
        tabBarInactiveTintColor: '#757575',
        tabBarIndicatorStyle: {
          backgroundColor: theme.colors.primary,
        },
        tabBarStyle: {
          backgroundColor: theme.colors.surface,
        },
        tabBarLabelStyle: {
          fontSize: 14,
          fontWeight: '600',
          textTransform: 'none',
        },
      }}
      initialRouteName="Learn"
    >
      <Tab.Screen name="Learn" component={LearnScreen} />
      <Tab.Screen name="Practice" component={PracticeScreen} />
      <Tab.Screen name="Settings" component={SettingsScreen} />
    </Tab.Navigator>
  );
};
