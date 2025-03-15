import { getHostComponent, NitroModules, type HybridRef } from 'react-native-nitro-modules'
import NitroCamConfig from '../nitrogen/generated/shared/json/NitroCamConfig.json'
import type {
  NitroCamProps,
  NitroCamMethods,
} from './views/nitro-cam.nitro'
import type { NitroCamUtil as NitroCamUtilSpec } from './specs/nitro-cam-util.nitro'


export const NitroCam = getHostComponent<NitroCamProps, NitroCamMethods>(
  'NitroCam',
  () => NitroCamConfig
)

export type NitroCamRef = HybridRef<NitroCamProps, NitroCamMethods>

export const NitroCamUtil =
  NitroModules.createHybridObject<NitroCamUtilSpec>('NitroCamUtil')