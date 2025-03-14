import type {
  HybridView,
  HybridViewProps,
  HybridViewMethods,
} from 'react-native-nitro-modules'

export interface NitroCamProps extends HybridViewProps {
   isRed: boolean
}

export interface NitroCamMethods extends HybridViewMethods {}

export type NitroCam = HybridView<NitroCamProps, NitroCamMethods, { ios: 'swift', android: 'kotlin' }>