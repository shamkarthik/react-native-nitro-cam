import { getHostComponent, type HybridRef } from 'react-native-nitro-modules'
import NitroCamConfig from '../nitrogen/generated/shared/json/NitroCamConfig.json'
import type {
  NitroCamProps,
  NitroCamMethods,
} from './specs/nitro-cam.nitro'


export const NitroCam = getHostComponent<NitroCamProps, NitroCamMethods>(
  'NitroCam',
  () => NitroCamConfig
)

export type NitroCamRef = HybridRef<NitroCamProps, NitroCamMethods>
