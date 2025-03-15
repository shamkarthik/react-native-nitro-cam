import React, {useRef, useState} from 'react';
import {Text, View, StyleSheet} from 'react-native';
import {NitroCam, NitroCamUtil, type NitroCamRef} from 'react-native-nitro-cam';
import Slider from '@react-native-community/slider'; // Import the Slider

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
        <Text style={styles.button}
          onPress={() => {
            const res = camRef.current?.takePhoto();
            console.log("takePhoto",res);
          }}>
          Take Photo
        </Text>        
        <ZoomSlider camRef={camRef} />
      </View>
    </View>
  );
}

const ZoomSlider = ({camRef}: {camRef: React.MutableRefObject<NitroCamRef | null>}) => {
  const [zoomValue, setZoomValue] = useState(1);
  return (
    <View>
      <Slider // Use the imported Slider
        minimumValue={1}
        maximumValue={10}
        value={zoomValue}
        onValueChange={(value: number) => {
          setZoomValue(value);
          console.log("setZoomValue",value)
          camRef.current?.setZoomLevel(value);
        }}
        step={0.1}
      />
      <Text>Zoom: {zoomValue.toFixed(1)}</Text>

      </View>
  );
};


const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  view: {
    width: '100%',
    height: 100,
  },
  button: {
    backgroundColor: '#4CAF50',
    color: 'white',
    padding: 10,
    margin: 5,
    textAlign: 'center',
  },
});

export default App;
