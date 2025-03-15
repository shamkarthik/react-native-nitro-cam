import React from 'react';
import { Text, View, StyleSheet } from 'react-native';
import { NitroCam,NitroCamUtil } from 'react-native-nitro-cam';

function App(): React.JSX.Element {
  return (
    <View style={styles.container}>
        <NitroCam isRed={false} style={StyleSheet.absoluteFill} isFrontCamera={false} flash={'off'} zoom={2} />
        <Text>NitroCam {NitroCamUtil.add(2,3)}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  view: {
    width: 200,
    height: 200
  }});

export default App;