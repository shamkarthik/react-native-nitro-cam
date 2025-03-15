import React, {useEffect, useRef} from 'react';
import {Text, View, StyleSheet} from 'react-native';
import {NitroCam, NitroCamUtil, type NitroCamRef} from 'react-native-nitro-cam';

function App(): React.JSX.Element {
  const camRef = useRef<NitroCamRef>(null);
  const details = NitroCamUtil.getCameraDevices();
  console.log(details);

  return (
    <View style={styles.container}>
      <NitroCam
        style={StyleSheet.absoluteFill}
        isFrontCamera={false}
        flash={'off'}
        zoom={0}
        hybridRef={{
          f: ref => {
            camRef.current = ref;
          },
        }}
      />
      <View style={styles.view}>
        <Text
          onPress={() => {
            const res = camRef.current?.takePhoto();
            console.log("takePhoto",res);
          }}>
          Take Photo
        </Text>
      </View>

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
    height: 200,
  },
});

export default App;
