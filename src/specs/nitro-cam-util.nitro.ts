import type { HybridObject } from "react-native-nitro-modules";


export interface FocalType {
    id: string
    name: string
    focalLength: number
}
export interface CameraType {
    id: string;
    placement: string;
    type: FocalType[];
}
export interface NitroCamUtil extends HybridObject<{ ios: 'swift', android: 'kotlin' }> {
    getCameraDevices():CameraType[];
}