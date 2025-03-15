import type {
  HybridView,
  HybridViewProps,
  HybridViewMethods,
} from 'react-native-nitro-modules';

type FlashMode = 'auto' | 'on' | 'off';

export interface NitroCamProps extends HybridViewProps {
  isRed: boolean;
  isFrontCamera: boolean;
  flash: FlashMode;
  zoom: number;
}

export interface NitroCamMethods extends HybridViewMethods {
  switchCamera(): void;
  setFlashMode(mode: FlashMode): void;
  setZoomLevel(level: number): void;
}

export type NitroCam = HybridView<
  NitroCamProps,
  NitroCamMethods,
  { ios: 'swift'; android: 'kotlin' }
>;

