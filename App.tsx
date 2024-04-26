import React, { useState } from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  useColorScheme,
  View,
} from 'react-native';

import { Colors } from 'react-native/Libraries/NewAppScreen';
import { Popover } from 'RTNNativeUI';

function App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  const [visible, setVisible] = useState(false);

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView contentInsetAdjustmentBehavior="automatic" style={backgroundStyle}>
        <View style={{ paddingTop: 300, alignItems: 'center', height: 1000 }}>
          <Popover width={100} height={300} visible={visible} onVisibleChange={setVisible}>
            <View collapsable={false} style={{ width: 100, height: 50, backgroundColor: '#F00' }}>
              <TouchableOpacity style={{ flex: 1 }} onPress={() => setVisible(true)}>
                <View style={{ flex: 1 }}>
                  <Text style={styles.highlight}>App.tsx</Text>
                </View>
              </TouchableOpacity>
            </View>
          </Popover>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});

export default App;
